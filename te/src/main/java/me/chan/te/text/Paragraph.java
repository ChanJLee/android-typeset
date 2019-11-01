package me.chan.te.text;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.annotations.Hidden;
import me.chan.te.config.Option;
import me.chan.te.data.DrawableBox;
import me.chan.te.data.Element;
import me.chan.te.data.Glue;
import me.chan.te.data.Penalty;
import me.chan.te.misc.Recyclable;
import me.chan.te.data.TextBox;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;
import me.chan.te.misc.ObjectFactory;
import me.chan.te.typesetter.ParagraphTypesetter;
import me.chan.te.typesetter.Typesetter;

/**
 * 段落
 */
public class Paragraph implements Recyclable, Segment {
	private static final ObjectFactory<Paragraph> POOL = new ObjectFactory<>(4096);

	private List<Line> mLines = new ArrayList<>(32);
	private List<Element> mElements = new ArrayList<>(512);
	private Object mExtra;
	private boolean mEmpty;

	public Paragraph(Object extra) {
		mExtra = extra;
	}

	public void setExtra(Object extra) {
		mExtra = extra;
	}

	@Nullable
	public Object getExtra() {
		return mExtra;
	}

	public Line getLine(int index) {
		return mLines.get(index);
	}

	public int getLineCount() {
		return mLines.size();
	}

	@Hidden
	public void addLine(Line line) {
		mLines.add(line);
	}

	public boolean isEmpty() {
		return mEmpty;
	}

	@Override
	public void recycle() {
		for (int i = 0; i < mLines.size(); ++i) {
			mLines.get(i).recycle();
		}
		mLines.clear();
		for (int i = 0; i < mElements.size(); ++i) {
			mElements.get(i).recycle();
		}
		mElements.clear();
		mExtra = null;
		POOL.release(this);
	}

	private static Paragraph obtain(Object extra) {
		Paragraph paragraph = POOL.acquire();
		if (paragraph == null) {
			return new Paragraph(extra);
		}
		paragraph.mExtra = extra;
		return paragraph;
	}

	public static void clean() {
		POOL.clean();
	}

	// TODO remove
	@Hidden
	public List<? extends Element> getElements() {
		return mElements;
	}

	/**
	 * 需要避免多次创建
	 */
	public static class Builder {
		private static final int MIN_HYPER_LEN = 4;
		private List<Integer> mHyphenated = new ArrayList<>(10);
		private Measurer mMeasurer;
		private Hypher mHypher;
		private Option mOption;
		private Paragraph mParagraph;
		private boolean mEmpty = true;

		public Builder(Measurer measurer, Hypher hypher, Option option) {
			mMeasurer = measurer;
			mHypher = hypher;
			mOption = option;
		}

		// TODO remove
		public Builder newParagraph() {
			return newParagraph(null);
		}

		public Builder newParagraph(Object extra) {
			if (mParagraph != null) {
				throw new IllegalStateException("call newParagraph twice");
			}

			mParagraph = obtain(extra);
			return this;
		}

		public Builder text(CharSequence text) {
			return text(text, 0, text.length());
		}

		public Builder text(CharSequence text, Object extra) {
			return text(text, 0, text.length(), extra);
		}

		public Builder text(CharSequence text, int start, int end) {
			return text(text, start, end, null);
		}

		public Builder text(CharSequence text, int start, int end, Object extra) {
			if (mParagraph == null) {
				throw new IllegalStateException("call newParagraph first");
			}

			int len = end - start;
			List<Element> elements = mParagraph.mElements;
			mHypher.hyphenate(text, start, end, mHyphenated);
			int size = mHyphenated.size();
			if (size == 0 || len < MIN_HYPER_LEN) {
				elements.add(TextBox.obtain(text, start, end,
						mMeasurer.getDesiredWidth(text, start, end),
						mMeasurer.getDesiredHeight(text, start, end),
						null,
						null,
						null,
						extra
				));
			} else {
				for (int j = 0; j < size; ++j) {
					int point = mHyphenated.get(j);
					if (point == start) {
						continue;
					}

					elements.add(TextBox.obtain(text, start, point,
							mMeasurer.getDesiredWidth(text, start, point),
							mMeasurer.getDesiredHeight(text, start, point),
							null,
							null,
							null,
							extra
					));
					if (j != size - 1 && text.charAt(point - 1) != '-') {
						elements.add(Penalty.obtain(mOption.getHyphenWidth(), mOption.getHyphenHeight(), ParagraphTypesetter.HYPHEN_PENALTY, true));
					}
					start = point;
				}
			}
			mHyphenated.clear();
			elements.add(Glue.obtain(mOption.getSpaceWidth(), mOption.getSpaceStretch(), mOption.getSpaceShrink()));
			mEmpty = false;
			return this;
		}

		public Builder drawable(Drawable drawable, float width, float height, Object extra) {
			if (mParagraph == null) {
				throw new IllegalStateException("call newParagraph first");
			}

			List<Element> elements = mParagraph.mElements;
			elements.add(DrawableBox.obtain(drawable, width, height, extra));
			elements.add(Glue.obtain(mOption.getSpaceWidth(), mOption.getSpaceStretch(), mOption.getSpaceShrink()));
			mEmpty = false;
			return this;
		}

		public Paragraph build() {
			if (mParagraph == null) {
				throw new IllegalStateException("call newParagraph first");
			}

			int elementSize = mParagraph.mElements.size();
			if (elementSize != 0 && mParagraph.mElements.get(elementSize - 1) instanceof Glue) {
				mParagraph.mElements.remove(elementSize - 1);
			}

			mParagraph.mElements.add(Glue.obtain(0, ParagraphTypesetter.INFINITY, 0));
			mParagraph.mElements.add(Penalty.obtain(0, 0, -ParagraphTypesetter.INFINITY, true));
			mParagraph.mEmpty = mEmpty;
			Paragraph paragraph = mParagraph;
			mParagraph = null;
			mEmpty = true;
			return paragraph;
		}
	}
}
