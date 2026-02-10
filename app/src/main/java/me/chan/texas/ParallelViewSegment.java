package me.chan.texas;

import android.view.View;
import android.widget.TextView;

import me.chan.texas.debug.R;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.ViewSegment;

public class ParallelViewSegment extends ViewSegment {
	private final String mText;
	private View mView;

	public ParallelViewSegment(Paragraph paragraph, String text) {
		super(new Args(me.chan.texas.debug.R.layout.item_parallel)
				.disableReuse(true)
				// 将当前layout中的ParagraphView参与到TexasView的自由选中效果
				// 上层无需再手动设置ParagraphView的数据源以及点击事件响应
				// 通过设置ParagraphView的me_chan_texas_ParagraphView_overrideStyles你也可以使得当前ParagraphView渲染样式也跟随TexasView
				.addSelectionProvider(R.id.en, paragraph));
		mText = text;
	}

	@Override
	protected void onRender(View view) {
		if (mView == null) {
			mView = view;
		} else {
			if (view != mView) {
				throw new RuntimeException("view not match");
			}
		}

		TextView textView = view.findViewById(R.id.cn);
		textView.setText(mText);
	}
}
