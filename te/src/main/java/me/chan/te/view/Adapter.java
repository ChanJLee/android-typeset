package me.chan.te.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import me.chan.te.R;
import me.chan.te.config.Option;
import me.chan.te.config.LineAttributes;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Gravity;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Segment;
import me.chan.te.hypher.Hypher;
import me.chan.te.parser.TextParser;
import me.chan.te.source.Source;
import me.chan.te.source.SourceCloseException;
import me.chan.te.source.SourceOpenException;
import me.chan.te.typesetter.CoreTypesetter;
import me.chan.te.typesetter.Typesetter;

public class Adapter extends RecyclerView.Adapter<TexViewHolder> {
	private static final int DEFAULT_TEXT_SIZE = 18;

	private List<Paragraph> mParagraphs;
	private LayoutInflater mLayoutInflater;
	private TextPaint mTextPaint;
	private Option mOption;
	private Executor mExecutor = Executors.newSingleThreadExecutor();
	private Handler mHandler;
	private boolean mDebugMode;
	private ElementFactory mElementFactory = new ElementFactory();
	private CharSequence mContent;

	public Adapter(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
		mTextPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextSize(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP,
				DEFAULT_TEXT_SIZE,
				context.getResources().getDisplayMetrics())
		);

		mOption = new Option(mTextPaint);
		mHandler = new Handler(Looper.getMainLooper());
	}

	@NonNull
	@Override
	public TexViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
		return new TexViewHolder(mLayoutInflater.inflate(R.layout.me_chan_te_text, viewGroup, false));
	}

	@Override
	public void onBindViewHolder(@NonNull TexViewHolder texViewHolder, int position) {
		texViewHolder.mParagraphView.setDebugMode(mDebugMode);
		texViewHolder.mParagraphView.render(mParagraphs.get(position), mTextPaint);
	}

	@Override
	public int getItemCount() {
		return mParagraphs == null ? 0 : mParagraphs.size();
	}

	public void render(final Source source, final int width) {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					mContent = source.open();
					render(mContent, width);
				} catch (SourceOpenException throwable) {
					// TODO 加入回调
					throwable.printStackTrace();
				} finally {
					try {
						source.close();
					} catch (SourceCloseException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void render(CharSequence charSequence, int width) {
		CoreTypesetter texTypesetter = new CoreTypesetter(mTextPaint, mOption, mElementFactory);
		TextParser textParser = new TextParser(Hypher.getInstance(), mOption);
		List<Segment> segments = textParser.parser(charSequence, mElementFactory);
		final List<Paragraph> paragraphs = new ArrayList<>();
		for (Segment segment : segments) {
			LineAttributes.Attribute defaultAttribute = new LineAttributes.Attribute(width, Gravity.LEFT, (int) mOption.lineSpacing);
			LineAttributes lineAttributes = new LineAttributes(defaultAttribute);
			lineAttributes.add(0, new LineAttributes.Attribute(
					width - mOption.indent,
					Gravity.RIGHT,
					(int) mOption.lineSpacing
			));
			Paragraph paragraph = texTypesetter.typeset(segment, lineAttributes, Typesetter.Policy.FILL);
			paragraphs.add(paragraph);
		}

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mParagraphs = paragraphs;
				notifyDataSetChanged();
			}
		});
	}

	public void setDebugMode(boolean debugMode) {
		mDebugMode = debugMode;
		notifyDataSetChanged();
	}

	public boolean isDebugMode() {
		return mDebugMode;
	}
}