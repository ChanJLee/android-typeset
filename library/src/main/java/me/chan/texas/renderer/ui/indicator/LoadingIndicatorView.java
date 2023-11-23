package me.chan.texas.renderer.ui.indicator;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.content.ContextCompat;

import me.chan.texas.R;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class LoadingIndicatorView extends View implements LoadingIndicator {
    private final ValueAnimator mValueAnimator;
    private float mDeltaRatioX = 0;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect mRect = new Rect();
    private int mLoadingColor;
    private int mLoadingBackgroundColor;

    public LoadingIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mValueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        mValueAnimator.addUpdateListener(animation -> {
            mDeltaRatioX = (float) animation.getAnimatedValue();
            invalidate();
        });
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator.setDuration(2000);

        Resources resources = context.getResources();
        TypedArray typedArray = resources.obtainAttributes(attrs, R.styleable.me_chan_texas_LoadingIndicatorView);
        try {
            mLoadingColor = typedArray.getColor(
                    R.styleable.me_chan_texas_LoadingIndicatorView_me_chan_texas_LoadingIndicatorView_loadingColor,
                    ContextCompat.getColor(context, R.color.me_chan_texas_theme_color)
            );

            mLoadingBackgroundColor = typedArray.getColor(
                    R.styleable.me_chan_texas_LoadingIndicatorView_me_chan_texas_LoadingIndicatorView_loadingBackgroundColor,
                    ContextCompat.getColor(context, R.color.me_chan_texas_loading_bg)
            );
        } finally {
            typedArray.recycle();
        }
    }

    public void setLoadingBackgroundColor(int loadingBackgroundColor) {
        mLoadingBackgroundColor = loadingBackgroundColor;
    }

    public void setLoadingColor(int loadingColor) {
        mLoadingColor = loadingColor;
    }

    @Override
    public void renderLoading() {
        setVisibility(VISIBLE);
        if (!isAttachedToWindow()) {
            return;
        }

        mValueAnimator.start();
    }

    @Override
    public void dismiss() {
        setVisibility(GONE);
        mValueAnimator.pause();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getVisibility() == VISIBLE) {
            mValueAnimator.resume();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mValueAnimator.pause();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        if (width <= 0) {
            return;
        }

        int height = getHeight();
        if (height <= 0) {
            return;
        }

        mPaint.setColor(mLoadingBackgroundColor);
        mRect.set(0, 0, width, height);
        canvas.drawRect(mRect, mPaint);

        mPaint.setColor(mLoadingColor);
        mRect.set(0, 0, (int) (width * mDeltaRatioX), height);
        canvas.drawRect(mRect, mPaint);
    }
}
