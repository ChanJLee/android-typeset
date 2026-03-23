package me.chan.texas.text;

import me.chan.texas.R;
import me.chan.texas.Texas;
import me.chan.texas.compat.Predicate;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.renderer.selection.SelectionProvider;
import me.chan.texas.text.util.TexasIterator;
import me.chan.texas.utils.ReferenceCountingPointer;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * 文档
 */
public final class Document {
	private ReferenceCountingPointer<List<Segment>> mSegments;
	private Selection mSelection;
	private Selection mHighlightSelection;

	private Document(Builder builder) {
		mSegments = builder.mSegments;
		List<Segment> segments = builder.mSegments.get();
		for (int i = 0; i < segments.size(); i++) {
			Segment segment = segments.get(i);
			if (segment instanceof ViewSegment) {
				ViewSegment viewSegment = (ViewSegment) segment;
				SelectionProvider provider = viewSegment.getSelectionProvider();
				if (provider != null) {
					for (int j = 0; j < provider.size(); j++) {
						SelectionProvider.ParagraphBinding binding = provider.get(j);
						Paragraph paragraph = binding.getParagraph();
						paragraph.setTag(R.id.me_chan_texas_paragraph_outer_segment, segment);
					}
				}
			}
		}
		mSelection = builder.mSelection;
		mHighlightSelection = builder.mHighlightSelection;
	}

	/**
	 * @param segment segment
	 * @return segment在document中的下标
	 */
	public int indexOfSegment(Segment segment) {
		if (segment == null || mSegments == null) {
			return -1;
		}

		if (segment instanceof Paragraph) {
			Paragraph paragraph = (Paragraph) segment;
			Segment outer = paragraph.getTag(R.id.me_chan_texas_paragraph_outer_segment, null);
			if (outer != null) {
				segment = outer;
			}
		}

		List<Segment> segments = mSegments.get();
		return segments.indexOf(segment);
	}

	/**
	 * 获取段落数目
	 *
	 * @return 段落数目
	 */
	public int getSegmentCount() {
		return mSegments == null ? 0 : mSegments.get().size();
	}

	@RestrictTo(LIBRARY)
	public boolean isCopy() {
		return mSegments.getRefCount() > 1;
	}

	/**
	 * 获取segment
	 *
	 * @param index 下标
	 * @return segment
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *                                   (<tt>index &lt; 0 || index &gt;= size()</tt>)
	 */
	public Segment getSegment(int index) {
		return mSegments.get().get(index);
	}

	@RestrictTo(LIBRARY)
	public void release() {
		clear();
		mSegments = null;
	}

	@RestrictTo(LIBRARY)
	public void clear() {
		mSegments.release();
	}

	public TexasIterator<Segment> iterator() {
		return new TexasIterator<Segment>() {
			private int mIndex = -1;

			@Override
			public Segment next() {
				return restore(mIndex + 1);
			}

			@Override
			public Segment prev() {
				return restore(mIndex - 1);
			}

			@Nullable
			@Override
			public Segment current() {
				return restore(mIndex);
			}

			@Override
			public Segment restore(int state) {
				if (state < 0 || state >= getSegmentCount()) {
					return null;
				}

				return getSegment(mIndex = state);
			}

			@Override
			public int save() {
				return mIndex;
			}
		};
	}

	public Selection getSelection(Selection.Type type) {
		if (type == Selection.Type.SELECTION) {
			return mSelection;
		} else if (type == Selection.Type.HIGHLIGHT) {
			return mHighlightSelection;
		} else {
			throw new IllegalArgumentException("unknown type: " + type);
		}
	}

	public void setSelection(Selection.Type type, Selection selection) {
		if (type == Selection.Type.SELECTION) {
			mSelection = selection;
		} else if (type == Selection.Type.HIGHLIGHT) {
			mHighlightSelection = selection;
		} else {
			throw new IllegalArgumentException("unknown type: " + type);
		}
	}

	public static class Builder {
		private final ReferenceCountingPointer<List<Segment>> mSegments;
		private Selection mSelection;
		private Selection mHighlightSelection;

		public Builder() {
			this(null);
		}

		/**
		 * 拷贝这个document的内容，并且可以编辑
		 *
		 * @param document document
		 */
		public Builder(@Nullable Document document) {
			if (document == null) {
				Texas.MemoryOption memoryOption = Texas.getMemoryOption();
				mSegments = new ReferenceCountingPointer<>(new ArrayList<>(memoryOption.getDocumentSegmentInitialCapacity()), segments -> {
					final int count = segments.size();
					for (int i = 0; i < count; ++i) {
						Segment segment = segments.get(i);
						segment.recycle();
					}
				});
				mSelection = null;
				mHighlightSelection = null;
			} else {
				mSegments = new ReferenceCountingPointer<List<Segment>>(document.mSegments) {
					@Override
					protected List<Segment> onAcquire(List<Segment> value) {
						return new ArrayList<>(value);
					}
				};
				mSelection = document.mSelection;
				mHighlightSelection = document.mHighlightSelection;
			}
		}

		public Builder addSegment(Segment segment) {
			mSegments.get().add(segment);
			return this;
		}

		public Builder addSegment(int index, Segment segment) {
			mSegments.get().add(index, segment);
			return this;
		}

		public Builder addSegments(int index, List<Segment> segments) {
			mSegments.get().addAll(index, segments);
			return this;
		}

		public Builder addSegments(List<Segment> segments) {
			mSegments.get().addAll(segments);
			return this;
		}

		public Builder removeSegment(int index) {
			mSegments.get().remove(index);
			return this;
		}

		public Builder removeSegment(Segment segment) {
			mSegments.get().remove(segment);
			return this;
		}

		public Builder updateSegment(int index, Segment segment) {
			mSegments.get().set(index, segment);
			return this;
		}

		public Segment getSegment(int index) {
			return mSegments.get().get(index);
		}

		public int getSegmentCount() {
			return mSegments.get().size();
		}

		public int indexOfSegment(Segment segment) {
			return mSegments.get().indexOf(segment);
		}

		public void removeIf(Predicate<Segment> predicate) {
			mSegments.get().removeIf(predicate::test);
		}

		private Selection updateSelection(@Nullable Selection selection) {
			if (selection == null) {
				return null;
			}

			boolean rebuild = false;
			Selection copy = Selection.obtain(selection.getType(), selection.getContainer(), selection.getStyles());
			for (int i = 0; i < getSegmentCount(); ++i) {
				Segment segment = getSegment(i);
				if (segment instanceof Paragraph) {
					Paragraph paragraph = (Paragraph) segment;
					ParagraphSelection paragraphSelection = paragraph.getSelection(selection.getType());
					if (paragraphSelection != null) {
						if (paragraphSelection.isBackgroundInvalid()) {
							rebuild = true;
						}
						copy.add(paragraphSelection);
					}
				}
			}

			if (!rebuild) {
				copy.recycle();
				return selection;
			}

			return copy;
		}

		public Document build() {
			mSelection = updateSelection(mSelection);
			mHighlightSelection = updateSelection(mHighlightSelection);
			return new Document(this);
		}
	}
}
