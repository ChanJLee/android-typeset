package me.chan.texas.text.layout;

/**
 * 可绘制box，可以是图片，表情
 */
public abstract class DrawableSpan extends Span {

	/**
	 * @param width  宽度
	 * @param height 高度
	 */
	protected DrawableSpan(float width, float height) {
		super(width, height);
	}
}
