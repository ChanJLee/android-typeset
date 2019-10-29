package me.chan.te.data;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.chan.te.annotations.Hidden;
import me.chan.te.config.Option;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;
import me.chan.te.misc.ObjectFactory;
import me.chan.te.typesetter.Typesetter;

/**
 * 段落
 */
public class Paragraph implements Recyclable {
	private static final ObjectFactory<Paragraph> POOL = new ObjectFactory<>(5000);

	private List<Line> mLines = new ArrayList<>(30);
	private LinkedList<Element> mElements = new LinkedList<>();
	private Object mExtra;

	public Paragraph(Object extra) {
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

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (Element element : mElements) {
			stringBuilder.append(element);
		}
		return stringBuilder.toString();
	}

	@Hidden
	public void addLine(Line line) {
		mLines.add(line);
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

	public static class Builder {
		private static final int MIN_HYPER_LEN = 4;
		private List<Integer> mHyphenated = new ArrayList<>(10);
		private Measurer mMeasurer;
		private Hypher mHypher;
		private Option mOption;
		private Paragraph mParagraph;

		public Builder(Measurer measurer, Hypher hypher, Option option) {
			mMeasurer = measurer;
			mHypher = hypher;
			mOption = option;
		}

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
							extra
					));
					if (j != size - 1 && text.charAt(point - 1) != '-') {
						elements.add(Penalty.obtain(mOption.getHyphenWidth(), mOption.getHyphenHeight(), Typesetter.HYPHEN_PENALTY, true));
					}
					start = point;
				}
			}
			mHyphenated.clear();
			elements.add(Glue.obtain(mOption.getSpaceWidth(), mOption.getSpaceStretch(), mOption.getSpaceShrink()));
			return this;
		}

		public Builder image(String url) {
			return image(url, -1, -1);
		}

		public Builder image(String url, float width, float height) {
			if (mParagraph == null) {
				throw new IllegalStateException("call newParagraph first");
			}

			throw new RuntimeException("Stub");
		}

		public Builder drawable(Drawable drawable) {
			if (mParagraph == null) {
				throw new IllegalStateException("call newParagraph first");
			}

			throw new RuntimeException("Stub");
		}

		public Paragraph build() {
			if (mParagraph == null) {
				throw new IllegalStateException("call newParagraph first");
			}

			if (!mParagraph.mElements.isEmpty() && mParagraph.mElements.getLast() instanceof Glue) {
				mParagraph.mElements.removeLast();
			}

			mParagraph.mElements.add(Glue.obtain(0, Typesetter.INFINITY, 0));
			mParagraph.mElements.add(Penalty.obtain(0, 0, -Typesetter.INFINITY, true));
			Paragraph paragraph = mParagraph;
			mParagraph = null;
			return paragraph;
		}
	}
}
