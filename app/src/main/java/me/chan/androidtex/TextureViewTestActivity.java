package me.chan.androidtex;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;

import java.util.List;

import me.chan.te.config.Option;
import me.chan.te.config.LineAttribute;
import me.chan.te.config.LineAttributes;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Gravity;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Segment;
import me.chan.te.hypher.Hypher;
import me.chan.te.parser.TextParser;
import me.chan.te.typesetter.TexTypesetter;
import me.chan.te.view.TexTextView;

public class TextureViewTestActivity extends AppCompatActivity {

	private Handler mHandler;
	private Paragraph mParagraph;
	private LineAttributes mLineAttributes;
	private TextPaint mPaint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_texture_view_test);

		final TexTextView texTextView = findViewById(R.id.text);
		texTextView.setSelectionMode(TexTextView.SELECTION_MODE_CLICK);
		findViewById(R.id.debug).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				texTextView.setDebugMode(!texTextView.isDebugMode());
			}
		});

		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				texTextView.render(mParagraph, mPaint);
			}
		};

		texTextView.post(new Runnable() {
			@Override
			public void run() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
						mPaint.setTextAlign(Paint.Align.LEFT);
						mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
						mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));
						Option option = new Option(mPaint);
						LineAttribute defaultAttribute = new LineAttribute(texTextView.getWidth());
						LineAttributes lineAttributes = new LineAttributes(defaultAttribute);
						lineAttributes.add(0, new LineAttribute(
								texTextView.getWidth() - option.indent,
								Gravity.RIGHT,
								(int) option.lineSpacing
						));
						mLineAttributes = new LineAttributes(defaultAttribute);


						ElementFactory factory = new ElementFactory();
						TexTypesetter texTypesetter = new TexTypesetter(mPaint, option, factory);
						TextParser textParser = new TextParser(Hypher.getInstance(), option);
						List<Segment> list = textParser.parser(getResources().getString(R.string.test), factory);
						mParagraph = texTypesetter.typeset(list.get(0), mLineAttributes);

						mHandler.sendEmptyMessage(10);
					}
				}).start();
			}
		});
	}
}
