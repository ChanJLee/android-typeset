package me.chan.texas.renderer.ui.figure;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.RestrictTo;

import me.chan.texas.image.ImageLoader;
import me.chan.texas.text.Figure;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;


@RestrictTo(LIBRARY)
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
		ImageLoader.Request request = imageLoader.uri(figure.getUrl());

		int width = (int) mFigure.getWidth();
		int height = (int) mFigure.getHeight();

		if (width > 0 && height > 0) {
			request.size(width, height);
		}

		request.into(this);

		if (width > 0 && height > 0) {
			requestLayout();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mFigure == null) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}

		int width = (int) mFigure.getWidth();
		int height = (int) mFigure.getHeight();
		if (width <= 0 || height <= 0) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			mFigure.resize(getMeasuredWidth(), getMeasuredHeight());
			return;
		}

		widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
