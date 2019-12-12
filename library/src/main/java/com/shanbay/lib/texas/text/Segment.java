package com.shanbay.lib.texas.text;

import com.shanbay.lib.texas.misc.DefaultRecyclable;
import com.shanbay.lib.texas.renderer.RenderOption;

/**
 * 渲染的最小单元
 */
public class Segment extends DefaultRecyclable {

	/**
	 * @param segmentSpace segment的垂直距离 {@link RenderOption#getSegmentSpace()} {@link RenderOption#setSegmentSpace(float)}
	 * @return 距离上一个segment的距离
	 */
	public float getTopMargin(float segmentSpace) {
		return 0;
	}

	/**
	 * @param segmentSpace segment的垂直距离 {@link RenderOption#getSegmentSpace()} {@link RenderOption#setSegmentSpace(float)}
	 * @return 距离下一个segment的距离
	 */
	public float getBottomMargin(float segmentSpace) {
		return segmentSpace;
	}
}
