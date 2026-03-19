package me.chan.texas.renderer;

import androidx.annotation.NonNull;

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
public class OnClickedListenerAdapter implements TexasView.OnClickedListener {

	@Override
	public void onSpanClicked(@NonNull TexasView view, @NonNull Paragraph paragraph, @NonNull TouchEvent event, @NonNull Box box) {

	}

	@Override
	public void onSpanLongClicked(@NonNull TexasView view, @NonNull Paragraph paragraph, @NonNull TouchEvent event, @NonNull Box box) {

	}

	@Override
	public void onSegmentClicked(@NonNull TexasView view, @NonNull TouchEvent event, @NonNull Segment segment) {

	}

	@Override
	public void onEmptyClicked(@NonNull TexasView view, @NonNull TouchEvent event) {

	}

	@Override
	public void onSegmentDoubleClicked(@NonNull TexasView view, @NonNull TouchEvent event, @NonNull Segment segment) {

	}
}
