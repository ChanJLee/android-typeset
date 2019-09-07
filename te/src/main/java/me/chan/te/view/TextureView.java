package me.chan.te.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import me.chan.te.annotations.Hidden;
import me.chan.te.data.Line;
import me.chan.te.data.LineAttributes;
import me.chan.te.data.Paragraph;

@Hidden
public class TextureView extends View {
	private Paragraph mParagraph;
	private LineAttributes mLineAttributes;

	public TextureView(Context context) {
		super(context);
	}

	public TextureView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TextureView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void render(@NonNull Paragraph paragraph, @NonNull LineAttributes lineAttributes) {
		mParagraph = paragraph;
		mLineAttributes = lineAttributes;
		requestLayout();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mParagraph != null && !mParagraph.getLines().isEmpty()) {
			List<Line> lines = mParagraph.getLines();
			int height = 0;
			for (int i = 0; i < lines.size(); ++i) {
				Line line = lines.get(i);
				height += line.getLineHeight();
				height += mLineAttributes.get(i).getLineSpace();
			}

			heightMeasureSpec = MeasureSpec.makeMeasureSpec(
					height,
					MeasureSpec.AT_MOST
			);
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mParagraph == null || mLineAttributes == null) {
			return;
		}

		List<Line> lines = mParagraph.getLines();
		for (int i = 0; i < lines.size(); ++i) {
			Line line = lines.get(i);
		}
	}

	private void draw(Line line, int x, int y) {

	}
}
