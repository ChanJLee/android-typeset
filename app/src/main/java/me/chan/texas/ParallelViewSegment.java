package me.chan.texas;

import android.view.View;
import android.widget.TextView;

import me.chan.texas.debug.R;
import me.chan.texas.renderer.selection.SelectionProvider;
import me.chan.texas.renderer.ui.text.ParagraphView;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.ViewSegment;

public class ParallelViewSegment extends ViewSegment {
	private final Paragraph mParagraph;
	private final String mText;
	private View mView;

	public ParallelViewSegment(Paragraph paragraph, String text) {
		super(
				new Args(me.chan.texas.debug.R.layout.item_parallel)
						.disableReuse(true)
						.selectionProvider(new SelectionProvider(paragraph))
		);
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

		ParagraphView paragraphView = view.findViewById(R.id.en);
		TextView textView = view.findViewById(R.id.cn);
		paragraphView.setParagraph(mParagraph);
		textView.setText(mText);
	}
}
