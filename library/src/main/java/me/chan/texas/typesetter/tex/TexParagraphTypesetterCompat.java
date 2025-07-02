package me.chan.texas.typesetter.tex;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;
import static me.chan.texas.Texas.INFINITY_PENALTY;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.Penalty;
import me.chan.texas.typesetter.AbsParagraphTypesetter;
import me.chan.texas.typesetter.utils.ElementStream;
import me.chan.texas.utils.IntArray;

import java.util.Arrays;

@RestrictTo(LIBRARY)
public class TexParagraphTypesetterCompat extends AbsParagraphTypesetter {
	private static final int CLASS_0 = 0;
	private static final int CLASS_1 = 1;
	private static final int CLASS_2 = 2;
	private static final int CLASS_3 = 3;
	private static final float DEMERITS_LINE = 1;

	private static final float DEMERITS_FLAGGED = 100;

	private static final float DEMERITS_FITNESS = 3000;
	private static final int MAX_RELAYOUT_TIMES = 60;
	private static final float MIN_SHRINK_RATIO = -1f;
	private static final float STRETCH_STEP_RATIO = 0.2f;

	@VisibleForTesting
	protected IntArray __intArray;

	@Nullable
	@Override
	public boolean typeset(Paragraph paragraph, BreakStrategy breakStrategy, int width) {

		ActiveNodes activeList = null;
		float tolerance = 1 - STRETCH_STEP_RATIO;
		for (int i = 0; i < MAX_RELAYOUT_TIMES; ++i) {
			tolerance += STRETCH_STEP_RATIO;
			activeList = createActiveNodes(paragraph, tolerance, width);
			if (!activeList.isEmpty()) {
				break;
			}
		}

		if (activeList == null ||
				activeList.isEmpty()) {
			w("can not find active nodes: " + paragraph);
			return false;
		}


		IntArray breakPoints = chooseBreakPoints(activeList);
		if (DEBUG) {
			__intArray = breakPoints;
		}
		if (breakPoints.empty()) {
			return false;
		}


		typesetParagraph(paragraph, breakPoints, breakStrategy, width);
		activeList.recycle();

		return true;
	}

	
	private ActiveNodes createActiveNodes(Paragraph paragraph, float tolerance, int lineWidth) {
		ActiveNodes activeNodes = new ActiveNodes();

		Sum sum = Sum.obtain();
		int size = paragraph.getElementCount();
		Candidate[] candidates = new Candidate[4];
		for (int i = 0; i < size && !activeNodes.isEmpty(); ++i) {
			Element element = paragraph.getElement(i);
			if (element instanceof Box) {
				sum.increase((Box) element);
			} else if (element instanceof Glue) {
				if (i > 0 && paragraph.getElement(i - 1) instanceof Box) {
					typesetLine(i, paragraph, activeNodes, sum, tolerance, candidates, lineWidth);
				}

				Glue glue = (Glue) element;
				sum.increase(glue);
			} else if (element instanceof Penalty &&
					((Penalty) element).getPenalty() != INFINITY_PENALTY) {
				typesetLine(i, paragraph, activeNodes, sum, tolerance, candidates, lineWidth);
			}
		}
		sum.recycle();

		return activeNodes;
	}

	private float getElementWidth(Element element) {
		if (element instanceof Penalty) {
			Penalty penalty = (Penalty) element;
			return penalty.getWidth();
		}

		if (element instanceof Glue) {
			Glue glue = (Glue) element;
			return glue.getWidth();
		}

		return ((Box) element).getWidth();
	}

	
	private Paragraph typesetParagraph(Paragraph paragraph,
									   IntArray breakPoints,
									   BreakStrategy breakStrategy,
									   int lineWidth) {
		ElementStream stream = new ElementStream(paragraph);
		int size = paragraph.getElementCount();
		Layout layout = Layout.obtain(paragraph.getLayout());

		for (int i = 1; i < breakPoints.size(); ++i) {
			int breakPoint = breakPoints.get(i);
			while (true) {
				if (stream.eof()) {
					return paragraph;
				}

				int save = stream.state();
				Element element = stream.next();
				if (element instanceof Box || (element instanceof Penalty && ((Penalty) element).getPenalty() == -INFINITY_PENALTY)) {
					stream.restore(save);
					break;
				}
			}


			int lineEnd = breakPoint + 1;
			if (lineEnd > size) {
				lineEnd = size;
			}

			Line line = createLine(stream, ElementStream.index2State(lineEnd), breakStrategy, lineWidth);
			if (line == null || line.isEmpty()) {
				break;
			}

			layout.addLine(line);
		}

		layout = paragraph.swap(layout);
		if (layout != null) {
			layout.recycle();
		}

		return paragraph;
	}

	
	private IntArray chooseBreakPoints(ActiveNodes activeNodes) {
		IntArray breaks = new IntArray();
		Node tempNode = null;
		for (Node node : activeNodes) {
			if (tempNode == null) {
				tempNode = node;
				continue;
			}


			if (tempNode.demerits > node.demerits) {
				tempNode = node;
			}
		}


		while (tempNode != null) {
			breaks.add(tempNode.state);
			tempNode = tempNode.link;
		}


		breaks.reverse();

		return breaks;
	}

	
	private void typesetLine(final int index, Paragraph paragraph,
							 ActiveNodes activeNodes,
							 Sum sum,
							 final float tolerance,
							 Candidate[] candidates,
							 final float lineWidth) {
		Element element = paragraph.getElement(index);
		Node active = activeNodes.getHeader();

		Arrays.fill(candidates, null);

		while (active != null) {
			boolean needCreateNode = false;
			while (active != null) {
				Node next = active.next;
				int currentLine = active.line + 1;
				float ratio = computeAdjustRatio(element, active, sum, lineWidth);



				if (ratio < MIN_SHRINK_RATIO || (element instanceof Penalty &&
						((Penalty) element).getPenalty() == -INFINITY_PENALTY)) {
					activeNodes.remove(active);
				}


				if (ratio >= MIN_SHRINK_RATIO && ratio <= tolerance) {

					int currentFitnessClazz = computeFitnessClazz(ratio);

					float demerits = computeDemerits(element, paragraph, ratio, active, currentFitnessClazz);


					if (candidates[currentFitnessClazz] == null || demerits < candidates[currentFitnessClazz].demerits) {
						needCreateNode = true;

						if (candidates[currentFitnessClazz] == null) {
							candidates[currentFitnessClazz] = Candidate.obtain();
						}

						candidates[currentFitnessClazz].active = active;
						candidates[currentFitnessClazz].demerits = demerits;
						candidates[currentFitnessClazz].ratio = ratio;
					}
				}

				active = next;


				if (active != null && active.line >= currentLine) {
					break;
				}
			}


			if (needCreateNode) {

				Sum tmpSum = computeSumAfter(index, paragraph, sum);

				appendActiveNode(active, index, activeNodes, tmpSum, candidates);
				tmpSum.recycle();


				for (int i = 0; i < candidates.length; ++i) {
					Candidate candidate = candidates[i];
					if (candidate != null) {
						candidate.recycle();
						candidates[i] = null;
					}
				}
			}
		}
	}

	
	private void appendActiveNode(Node active, int index, ActiveNodes activeNodes,
								  Sum sum, Candidate[] candidates) {
		for (int i = 0; i < candidates.length; ++i) {
			Candidate candidate = candidates[i];
			if (candidate == null) {
				continue;
			}

			Node node = Node.obtain();
			node.state = index;
			node.demerits = candidate.demerits;
			node.ratio = candidate.ratio;
			node.line = candidate.active.line + 1;
			node.fitness = i;
			node.totals = Sum.obtain(sum);
			node.link = candidate.active;

			if (active != null) {
				activeNodes.insertBefore(active, node);
			} else {
				activeNodes.pushBack(node);
			}
		}
	}

	
	private int computeFitnessClazz(float ratio) {


		if (ratio < -0.5) {
			return CLASS_0;
		} else if (ratio <= 0.5) {
			return CLASS_1;
		} else if (ratio <= 1) {
			return CLASS_2;
		} else {
			return CLASS_3;
		}
	}

	
	private float computeAdjustRatio(Element element, Node node, Sum sum, float lineLength) {
		float width = sum.getWidth() - node.totals.getWidth();
		if (element instanceof Penalty) {
			width += getElementWidth(element);
		}



		if (width < lineLength) {
			float stretch = sum.getStretch() - node.totals.getStretch();
			return stretch > 0 ? (lineLength - width) / stretch : INFINITY_PENALTY;
		} else if (width > lineLength) {
			float shrink = sum.getShrink() - node.totals.getShrink();
			return shrink > 0 ? (lineLength - width) / shrink : INFINITY_PENALTY;
		}

		return 0;
	}

	
	private float computeDemerits(Element element, Paragraph paragraph, float ratio, Node active, int currentFitnessClazz) {
		float badness = (float) (100 * Math.pow(Math.abs(ratio), 3));
		float demerits;

		if (element instanceof Penalty && ((Penalty) element).getPenalty() >= 0) {
			demerits = (float) (
					Math.pow(DEMERITS_LINE + badness + ((Penalty) element).getPenalty(), 2)
			);
		} else if (element instanceof Penalty && ((Penalty) element).getPenalty() != -INFINITY_PENALTY) {
			demerits = (float) (
					Math.pow(DEMERITS_LINE + badness, 2) -
							Math.pow(((Penalty) element).getPenalty(), 2)
			);
		} else {
			demerits = (float) (
					Math.pow(DEMERITS_LINE + badness, 2)
			);
		}

		if (element instanceof Penalty &&
				paragraph.getElement(active.state) instanceof Penalty) {
			Penalty penalty = (Penalty) element;
			Penalty activePenalty = (Penalty) paragraph.getElement(active.state);
			if (penalty.isFlag() && activePenalty.isFlag()) {
				demerits += DEMERITS_FLAGGED;
			}
		}

		if (Math.abs(currentFitnessClazz - active.fitness) > 1) {
			demerits += DEMERITS_FITNESS;
		}


		demerits += active.demerits;

		return demerits;
	}

	
	private Sum computeSumAfter(int index, Paragraph paragraph, Sum sum) {
		Sum result = Sum.obtain(sum);

		int size = paragraph.getElementCount();
		for (int i = index; i < size; ++i) {
			Element element = paragraph.getElement(i);
			if (element instanceof Glue) {
				Glue glue = (Glue) element;
				result.increase(glue);
			} else if (element instanceof Box ||
					(element instanceof Penalty &&
							((Penalty) element).getPenalty() == -INFINITY_PENALTY && i > index)) {
				break;
			}
		}
		return result;
	}

	@Override
	public Object getInternalState() {
		return __intArray;
	}

	private static void w(String msg) {
		Log.w("TextEngineTexTypesetter", msg);
	}
}
