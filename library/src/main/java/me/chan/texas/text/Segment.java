package me.chan.texas.text;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.renderer.RenderOption;

/**
 * 渲染的最小单元
 */
public class Segment extends DefaultRecyclable {
	/**
	 * @param normalVertical segment的垂直距离 {@link RenderOption#getSegmentSpace()} {@link RenderOption#setSegmentSpace(float)}
	 * @return 垂直方向的距离
	 */
	public float getVerticalSpace(float normalVertical) {
		return normalVertical;
	}
}
