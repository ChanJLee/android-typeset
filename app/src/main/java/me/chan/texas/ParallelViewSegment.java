package me.chan.texas;

import android.view.View;
import android.widget.TextView;

import me.chan.texas.debug.R;
import me.chan.texas.renderer.ui.text.ParagraphView;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.ViewSegment;

public class ParallelViewSegment extends ViewSegment {
	private final Paragraph mParagraph;
	private final String mText;
	private ParagraphView mParagraphView;
	private View mView;

	public ParallelViewSegment(Paragraph paragraph, String text) {
		super(me.chan.texas.debug.R.layout.item_parallel, true, false);
		mParagraph = paragraph;
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

		ParagraphView paragraphView = mParagraphView = view.findViewById(R.id.en);
		TextView textView = view.findViewById(R.id.cn);
		paragraphView.setParagraph(mParagraph);
		textView.setText(mText);
	}
}
