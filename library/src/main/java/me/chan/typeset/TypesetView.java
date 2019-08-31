package me.chan.typeset;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.chan.library.R;
import me.chan.typeset.core.Break;
import me.chan.typeset.core.Line;
import me.chan.typeset.core.Option;
import me.chan.typeset.core.Result;
import me.chan.typeset.core.Typeset;
import me.chan.typeset.elements.Box;
import me.chan.typeset.elements.Element;
import me.chan.typeset.elements.Penalty;

public class TypesetView extends View {
	private Paint mPaint;
	private Option mOption;
	private float mLineWidth;
	private TypesetAsyncTask mTypesetAsyncTask;
	private CharSequence mText;

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
		mText = text;
		invalidate();
	}

	public void release() {
		mTypesetAsyncTask.cancel(true);
		mTypesetAsyncTask = null;
	}

	private Canvas mCanvas;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mLines != null) {
			drawLines(canvas);
			mLines = null;
			return;
		}

		if (mTypesetAsyncTask != null && !TextUtils.isEmpty(mText)) {
			mLineWidth = getWidth() - getPaddingLeft() - getPaddingRight();
			mTypesetAsyncTask.execute(mText);
		}
	}

	private void drawLines(Canvas canvas) {
		int verticalOffset = 0;
		for (int lineNum = 0; lineNum < mLines.size(); ++lineNum) {
			Line line = mLines.get(lineNum);

			List<String> lineContent = new ArrayList<>();
			for (int i = 0; i < line.elements.size(); ++i) {
				Element element = line.elements.get(i);
				if (element instanceof Box) {
					Box box = (Box) element;
					if ((i - 1 >= 0 &&
							(line.elements.get(i - 1) instanceof Penalty || line.elements.get(i - 1) instanceof Box))) {
						appendLast(lineContent, box.content);
					} else {
						lineContent.add(box.content);
					}
				} else if (element instanceof Penalty) {
					if (((Penalty) element).penalty == mOption.hyphenPenalty && i == line.elements.size() - 1) {
						appendLast(lineContent, "-");
					}
				}
			}

			float lineWidth = lineNum >= lineLengths.size() ?
					lineLengths.get(lineLengths.size() - 1) : lineLengths.get(lineNum);

			float currentLineWidth = 0;
			float currentLineHeight = 0;
			Rect textBound = new Rect();
			for (String word : lineContent) {
				mPaint.getTextBounds(word, 0, word.length(), textBound);
				if (textBound.height() > currentLineHeight) {
					currentLineHeight = textBound.height();
				}
				currentLineWidth += textBound.width();
			}

			boolean defaultSpace = lineContent.isEmpty() || lineContent.size() == 1 || lineNum == mLines.size() - 1;
			float space = defaultSpace ? mOption.spaceWidth : (lineWidth - currentLineWidth) / (lineContent.size() - 1);
			Log.d("chan_debug", "space: " + space +
					" shrink: " + (mOption.spaceWidth - mOption.spaceShrink) +
					" stretch: " + (mOption.spaceWidth + mOption.spaceStretch) +
					" space: " + mOption.spaceWidth);
			verticalOffset += (currentLineHeight);

			float horizontalOffset = 0;
			for (String word : lineContent) {
				mPaint.getTextBounds(word, 0, word.length(), textBound);
				canvas.drawText(word, horizontalOffset, verticalOffset, mPaint);
				horizontalOffset += (textBound.width() + space);
			}

			verticalOffset += 20;
		}
		Toast.makeText(getContext(), "used time: " + (SystemClock.elapsedRealtime() - mTimestamp), Toast.LENGTH_LONG).show();
	}

	private static void appendLast(List<String> content, String s) {
		String last = content.isEmpty() ? "" : content.get(content.size() - 1);
		last += s;
		if (!content.isEmpty()) {
			content.remove(content.size() - 1);
		}
		content.add(last);
	}

	private List<Float> lineLengths = null;
	private long mTimestamp = 0;

	private class TypesetAsyncTask extends AsyncTask<CharSequence, Void, List<Line>> {

		@Override
		protected List<Line> doInBackground(CharSequence... charSequences) {
			if (charSequences == null || charSequences.length != 1) {
				throw new IllegalArgumentException("invalid arguments length");
			}

			if (mLineWidth <= 0) {
				return null;
			}

			mTimestamp = SystemClock.elapsedRealtime();
			lineLengths = new ArrayList<>();
			lineLengths.add(mLineWidth);
			Result result = null;

			for (int i = 1; i <= 3; ++i) {
				// TODO 计算一个空格大小的tolerance 防止多次measure
				mOption.tolerance = i + 1;
				result = Typeset.linkBreak(String.valueOf(charSequences[0]), lineLengths, mOption, mPaint);
				if (!result.breaks.isEmpty()) {
					break;
				}
			}

			List<Line> lines = new ArrayList<>();
			int lineStart = 0;
			for (int i = 1; i < result.breaks.size(); ++i) {
				Break b = result.breaks.get(i);
				int pos = b.position;
				float ratio = b.ratio;
				for (int j = lineStart; j != 0 && j < result.elements.size(); ++j) {
					Element element = result.elements.get(j);
					if (element instanceof Box || (element instanceof Penalty && ((Penalty) element).penalty == -mOption.infinity)) {
						lineStart = j;
						break;
					}
				}

				Line line = new Line();
				line.ratio = ratio;
				line.elements = result.elements.subList(lineStart, pos + 1);
				line.pos = pos;
				lines.add(line);
				lineStart = pos;
			}

			return lines;
		}

		@Override
		protected void onPostExecute(List<Line> lines) {
			mLines = lines;
			invalidate();
		}
	}

	private List<Line> mLines;
}
