package me.chan.texas.renderer.selection;

import me.chan.texas.text.Paragraph;

/**
 * 可选中的segment协议
 * 1. segment中有多少ParagraphView需要被选中就返回多少
 * 2. segment中的ParagraphView的 {@link me.chan.texas.renderer.SpanTouchEventHandler} 以及 {@link me.chan.texas.renderer.ui.text.OnSelectedChangedListener} 会被替换。
 * 3. 如果你期望自己的ParagraphView的渲染样式和TexasView中一致，需要设置如下属性：
 * <pre>
 * &ltme.chan.texas.renderer.ui.text.ParagraphView
 * android:id="@+id/en"
 * android:layout_width="0dp"
 * android:layout_height="wrap_content"
 * android:layout_weight="1"
 * android:background="#20ff0000"
 * app:me_chan_texas_ParagraphView_overrideStyles="true"
 * tools:me_chan_texas_ParagraphView_text="hello" /&gt
 * </pre>
 * app:me_chan_texas_ParagraphView_overrideStyles属性为true则是允许ParagraphView的渲染样式和TexasView中一致
 */
public class SelectionProvider {

	private final Paragraph[] mParagraphs;

	public SelectionProvider(Paragraph... paragraphs) {
		mParagraphs = paragraphs;
	}

	/**
	 * @return 当前有多少段落需要参与选中
	 */
	public int getParagraphCount() {
		return mParagraphs.length;
	}

	/**
	 * @param index 对应的下标
	 * @return 当前段落
	 */
	public Paragraph getParagraph(int index) {
		return mParagraphs[index];
	}
}
