package me.chan.te.renderer;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import me.chan.te.R;
import me.chan.te.config.LineAttribute;
import me.chan.te.config.LineAttributes;
import me.chan.te.config.Option;
import me.chan.te.data.Element;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Gravity;
import me.chan.te.data.Paragraph;
import me.chan.te.hypher.Hypher;
import me.chan.te.parser.TextParser;
import me.chan.te.typesetter.TexTypesetter;

public class Adapter extends RecyclerView.Adapter<TexViewHolder> {
	private static final int DEFAULT_TEXT_SIZE = 16;
	private static final Pattern LINE_BREAK_PATTERN = Pattern.compile("\n");

	private List<Paragraph> mParagraphs;
	private LayoutInflater mLayoutInflater;
	private TextPaint mTextPaint;
	private Option mOption;
	private Executor mExecutor = Executors.newSingleThreadExecutor();
	private Handler mHandler;
	private boolean mDebugMode;
	private ElementFactory mElementFactory = new ElementFactory();

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
		texViewHolder.texTextView.setDebugMode(mDebugMode);
		texViewHolder.texTextView.render(mParagraphs.get(position), mTextPaint);
	}

	@Override
	public int getItemCount() {
		return mParagraphs == null ? 0 : mParagraphs.size();
	}

	public void setText(final CharSequence charSequence, final int width) {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				render(charSequence, width);
			}
		});
	}

	private void render(CharSequence charSequence, int width) {

		TexTypesetter texTypesetter = new TexTypesetter(mTextPaint, mOption);
		TextParser textParser = new TextParser(Hypher.getInstance(), mOption);

		String[] lines = LINE_BREAK_PATTERN.split(charSequence);
		final List<Paragraph> paragraphs = new ArrayList<>();
		for (String line : lines) {
			if (TextUtils.isEmpty(line)) {
				continue;
			}

			LineAttribute defaultAttribute = new LineAttribute(width, Gravity.LEFT, (int) mOption.lineSpacing);
			LineAttributes lineAttributes = new LineAttributes(defaultAttribute);
			lineAttributes.add(0, new LineAttribute(
					width - mOption.indent,
					Gravity.RIGHT,
					(int) mOption.lineSpacing
			));
			List<? extends Element> list = textParser.parser(line, mElementFactory);
			Paragraph paragraph = texTypesetter.typeset(list, lineAttributes, mElementFactory);
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