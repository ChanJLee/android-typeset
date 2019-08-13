package me.chan.typeset;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.chan.library.R;
import me.chan.typeset.core.Break;
import me.chan.typeset.core.Option;
import me.chan.typeset.core.Typeset;

public class TypesetView extends View {
	private Paint mPaint;
	private Option mOption;
	private TypesetAsyncTask mTypesetAsyncTask;

	public TypesetView(Context context) {
		this(context, null);
	}

	public TypesetView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TypesetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextSize(context.getResources().getDimension(R.dimen.typeset_default_text_size));
		mPaint.setColor(Color.BLACK);

		mOption = new Option(mPaint);
		mTypesetAsyncTask = new TypesetAsyncTask();
	}

	public void setText(CharSequence text) {
		mTypesetAsyncTask.cancel(true);
		mTypesetAsyncTask.execute(text);
	}

	public void release() {
		mTypesetAsyncTask.cancel(true);
		mTypesetAsyncTask = null;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// TODO
	}

	private class TypesetAsyncTask extends AsyncTask<CharSequence, Void, List<Break>> {

		@Override
		protected List<Break> doInBackground(CharSequence... charSequences) {
			if (charSequences == null || charSequences.length != 0) {
				throw new IllegalArgumentException("invalid arguments length");
			}

			float lineLength = getWidth() - getPaddingLeft() - getPaddingRight();
			if (lineLength <= 0) {
				return null;
			}

			List<Float> lineLengths = new ArrayList<>();
			lineLengths.add(lineLength);
			return Typeset.linkBreak(String.valueOf(charSequences[0]), lineLengths, mOption, mPaint);
		}

		@Override
		protected void onPostExecute(List<Break> breaks) {

		}
	}
}
