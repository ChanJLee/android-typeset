package me.chan.typeset;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

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

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mTypesetAsyncTask != null && !TextUtils.isEmpty(mText)) {
			mLineWidth = getWidth() - getPaddingLeft() - getPaddingRight();
			mTypesetAsyncTask.execute(mText);
		}
	}

	private class TypesetAsyncTask extends AsyncTask<CharSequence, Void, List<Line>> {

		@Override
		protected List<Line> doInBackground(CharSequence... charSequences) {
			if (charSequences == null || charSequences.length != 1) {
				throw new IllegalArgumentException("invalid arguments length");
			}

			if (mLineWidth <= 0) {
				return null;
			}

			List<Float> lineLengths = new ArrayList<>();
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
			for (Line line : lines) {
				boolean intent = false;
				int spaces = 0;
				float totalAdjuatment = 0;
				float wordSpace = line.ratio * (line.ratio < 0 ? mOption.spaceShrink : mOption.spaceStretch);
				int integerWordSpace = Math.round(wordSpace);
				float adjustment = wordSpace - integerWordSpace;
				int integerAdjustment = (int) (adjustment < 0 ? Math.floor(adjustment) : Math.ceil(adjustment));

				for (int i = 0; i < line.elements.size(); ++i) {
					Element element = line.elements.get(i);
					if (element instanceof Box) {
						Box box = (Box) element;
						if (TextUtils.isEmpty(box.mContent)) {
							continue;
						}


					}
				}

//				// Iterate over the elements in each line and build a temporary array containing just words, spaces, and soft-hyphens.
//				line.elements.forEach(function(n, index, array) {
//					// normal boxes
//					if (n.type == = 'box' && n.value != = '') {
//						if (tmp.length != = 0 && tmp[tmp.length - 1] != = '&nbsp;') {
//							tmp[tmp.length - 1] += n.value;
//						} else {
//							tmp.push(n.value);
//						}
//						// empty boxes (indentation for example)
//					} else if (n.type == = 'box' && n.value == = '') {
//						output.push('<span style="margin-left: 30px;"></span>');
//						// glue inside a line
//					} else if (n.type == = 'glue' && index != = array.length - 1) {
//						tmp.push('&nbsp;');
//						spaces += 1;
//						// glue at the end of a line
//					} else if (n.type == = 'glue') {
//						tmp.push(' ');
//						// hyphenated word at the end of a line
//					} else if (n.type == = 'penalty' && n.penalty == = hyphenPenalty && index == = array.length - 1) {
//						tmp.push('&shy;');
//						// Remove trailing space at the end of a paragraph
//					} else if (n.type == = 'penalty' && index == = array.length - 1 && tmp[tmp.length - 1] == = '&nbsp;') {
//						tmp.pop();
//					}
//				}
			}
		}
	}
