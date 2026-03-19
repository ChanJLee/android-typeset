package me.chan.texas.renderer;

import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.layout.Box;

/**
 * {@link TexasView.OnClickedListener} 的适配器，提供所有方法的默认空实现。
 * 用户只需重写关心的事件方法即可，无需实现所有回调。
 * <p>
 * 使用示例：
 * <pre>
 * texasView.setOnClickedListener(new OnClickedListenerAdapter() {
 *     {@literal @}Override
 *     public void onSpanClicked(TexasView view, TouchEvent event, Object tag) {
 *         // 只处理 Span 点击
 *     }
 * });
 * </pre>
 */
public abstract class OnClickedListenerAdapter implements TexasView.OnClickedListener {

	@Override
	public void onSpanClicked(TexasView view, Paragraph paragraph, TouchEvent event, Box box) {

	}

	@Override
	public void onSpanLongClicked(TexasView view, Paragraph paragraph, TouchEvent event, Box box) {

	}

	@Override
	public void onSegmentClicked(TexasView view, TouchEvent event, Segment segment) {

	}

	@Override
	public void onSegmentDoubleClicked(TexasView view, TouchEvent event, Segment segment) {

	}

	@Override
	public void onEmptyClicked(TexasView view, TouchEvent event) {

	}
}
