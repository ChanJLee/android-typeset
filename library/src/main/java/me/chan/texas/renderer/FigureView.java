package me.chan.texas.renderer;

import android.content.Context;
import android.util.AttributeSet;

import me.chan.texas.annotations.Hidden;
import me.chan.texas.image.ImageLoader;
import me.chan.texas.text.Figure;

/**
 * 显示插图的控件
 */
@Hidden
public class FigureView extends androidx.appcompat.widget.AppCompatImageView {
	private Figure mFigure;

	public FigureView(Context context) {
		super(context);
	}

	public FigureView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FigureView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void render(ImageLoader imageLoader, Figure figure) {
		mFigure = figure;
		imageLoader.uri(figure.getUrl())
				.size((int) figure.getWidth(), (int) figure.getHeight())
				.into(this);
		requestLayout();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mFigure != null) {
			widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) mFigure.getWidth(), MeasureSpec.EXACTLY);
			heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) mFigure.getHeight(), MeasureSpec.EXACTLY);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
