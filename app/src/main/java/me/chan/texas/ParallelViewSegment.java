package me.chan.texas;

import android.view.View;
import android.widget.TextView;

import me.chan.texas.debug.R;
import me.chan.texas.renderer.ui.RendererHost;
import me.chan.texas.renderer.ui.text.ParagraphView;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.ViewSegment;

public class ParallelViewSegment extends ViewSegment {
	private final Paragraph mParagraph;
	private final String mText;
	private ParagraphView mParagraphView;

	public ParallelViewSegment(Paragraph paragraph, String text) {
		super(me.chan.texas.debug.R.layout.item_parallel);
		mParagraph = paragraph;
		mText = text;
	}

	@Override
	protected void onRender(View view) {
		ParagraphView paragraphView = mParagraphView = view.findViewById(R.id.en);
		TextView textView = view.findViewById(R.id.cn);
		paragraphView.setParagraph(mParagraph);
		textView.setText(mText);

		RendererHost rendererHost = getRendererHost();
		mParagraphView.setSpanTouchEventHandler(rendererHost.getSpanTouchEventHandler());
		mParagraphView.getTextureParagraph().setOnTextSelectedListener(rendererHost.getOnSelectedChangedListener());
	}
}
