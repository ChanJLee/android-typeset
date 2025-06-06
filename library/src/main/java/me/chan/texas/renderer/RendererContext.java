package me.chan.texas.renderer;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.RectF;
import me.chan.texas.text.layout.Box;
import me.chan.texas.utils.TexasUtils;

public final class RendererContext {

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	BoxMetaInfo currentBoxMetaInfo = new BoxMetaInfo();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	BoxMetaInfo prevBoxMetaInfo = new BoxMetaInfo();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	BoxMetaInfo nextBoxMetaInfo = new BoxMetaInfo();

	/**
	 * @return 获得当前元素的tag
	 */
	@Nullable
	public Object getTag() {
		return currentBoxMetaInfo.box.getTag();
	}

	/**
	 * @return 获得前一个绘制元素的tag
	 */
	@Nullable
	public Object getPrevTag() {
		return prevBoxMetaInfo.box == null ? null : prevBoxMetaInfo.box.getTag();
	}

	/**
	 * @return 获得后一个绘制元素的tag
	 */
	@Nullable
	public Object getNextTag() {
		return nextBoxMetaInfo.box == null ? null : nextBoxMetaInfo.box.getTag();
	}

	/**
	 * 行首
	 */
	public static final int LOCATION_LINE_START = 1;

	/**
	 * 行尾
	 */
	public static final int LOCATION_LINE_END = 2;

	/**
	 * 行中
	 */
	public static final int LOCATION_LINE_MIDDLE = 4;

	/**
	 * 段首
	 */
	public static final int LOCATION_PARAGRAPH_START = 8;

	/**
	 * 段尾
	 */
	public static final int LOCATION_PARAGRAPH_END = 16;

	/**
	 * 段中
	 */
	public static final int LOCATION_PARAGRAPH_MIDDLE = 32;

	public void clear() {
		currentBoxMetaInfo.clear();
		prevBoxMetaInfo.clear();
		nextBoxMetaInfo.clear();
		mParagraphLocationAttribute = 0;
	}

	@IntDef({LOCATION_LINE_START, LOCATION_LINE_END, LOCATION_LINE_MIDDLE,
			LOCATION_PARAGRAPH_START, LOCATION_PARAGRAPH_END, LOCATION_PARAGRAPH_MIDDLE})
	public @interface LocationType {
	}

	private int mParagraphLocationAttribute = 0;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void setParagraphLocationAttribute(int location, boolean enable) {
		if (enable) {
			mParagraphLocationAttribute |= location;
		} else {
			mParagraphLocationAttribute &= ~location;
		}
	}

	/**
	 * @param location {@link #LOCATION_LINE_START} or {@link #LOCATION_LINE_END}
	 * @return 是否包含指定位置的属性
	 */
	public boolean checkLocation(@LocationType int location) {
		if (location == LOCATION_LINE_START) {
			return prevBoxMetaInfo.box == null;
		} else if (location == LOCATION_LINE_END) {
			return nextBoxMetaInfo.box == null;
		} else if (location == LOCATION_LINE_MIDDLE) {
			return prevBoxMetaInfo.box != null && nextBoxMetaInfo.box != null;
		} else if (location == LOCATION_PARAGRAPH_START) {
			return (mParagraphLocationAttribute & LOCATION_PARAGRAPH_START) != 0;
		} else if (location == LOCATION_PARAGRAPH_END) {
			return (mParagraphLocationAttribute & LOCATION_PARAGRAPH_END) != 0;
		} else if (location == LOCATION_PARAGRAPH_MIDDLE) {
			return !checkLocation(LOCATION_PARAGRAPH_START) &&
					!checkLocation(LOCATION_PARAGRAPH_END);
		}

		throw new IllegalArgumentException("unknown location type: " + location);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final class BoxMetaInfo {
		public final RectF inner = new RectF();
		public Box box;
		public int index;

		public void clear() {
			inner.top = inner.left = inner.right = inner.bottom = 0;
			box = null;
			index = -1;
		}

		public boolean isValid() {
			return box != null;
		}

		public void set(Box box, int index, RectF inner) {
			this.box = box;
			this.index = index;
			TexasUtils.copyRect(this.inner, inner);
		}

		public void set(BoxMetaInfo meta) {
			this.box = meta.box;
			this.index = meta.index;
			TexasUtils.copyRect(this.inner, meta.inner);
		}
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public BoxMetaInfo getCurrentBoxMetaInfo() {
		return currentBoxMetaInfo;
	}
}
