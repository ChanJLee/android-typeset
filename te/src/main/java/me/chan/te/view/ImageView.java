package me.chan.te.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.TypedValue;

import me.chan.te.R;
import me.chan.te.log.Log;

public class ImageView extends AppCompatImageView {

	private static final String EXTRA_INSTANCE = "state_instance";

	/**
	 * 圆形
	 */
	public static final int TYPE_CIRCLE = 0;
	/**
	 * 圆角
	 */
	public static final int TYPE_ROUND_CORNER = 1;
	/**
	 * 普通
	 */
	public static final int TYPE_NORMAL = 2;

	private Renderer mRenderer;

	public ImageView(Context context) {
		this(context, null);
	}

	public ImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		int cornerRadius = dp2px(10);
		int type = TYPE_NORMAL;
		float ratio = -1;

		if (attrs == null) {
			init(type, ratio, cornerRadius);
			return;
		}

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.me_chan_te_ImageView, defStyleAttr, 0);

		if (a.hasValue(R.styleable.me_chan_te_ImageView_me_chan_te_corner_radius)) {
			cornerRadius = a.getDimensionPixelSize(R.styleable.me_chan_te_ImageView_me_chan_te_corner_radius, cornerRadius);
		}

		if (a.hasValue(R.styleable.me_chan_te_ImageView_me_chan_te_style)) {
			type = a.getInt(R.styleable.me_chan_te_ImageView_me_chan_te_style, TYPE_ROUND_CORNER);
		}

		if (a.hasValue(R.styleable.me_chan_te_ImageView_me_chan_te_ratio)) {
			ratio = a.getFloat(R.styleable.me_chan_te_ImageView_me_chan_te_ratio, -1);
		}

		a.recycle();
		init(type, ratio, cornerRadius);
	}

	private void init(int type, float ratio, int cornerRadius) {
		d("init, type: " + type + " ratio: " + ratio + " corner radius: " + cornerRadius);
		if (type == TYPE_CIRCLE) {
			mRenderer = new CircleRenderer();
		} else if (type == TYPE_ROUND_CORNER) {
			mRenderer = new RoundCornerRenderer(ratio, cornerRadius);
		} else {
			mRenderer = new NormalRenderer(ratio);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mRenderer.measure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (!mRenderer.draw(canvas)) {
			super.onDraw(canvas);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mRenderer.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(EXTRA_INSTANCE, super.onSaveInstanceState());
		mRenderer.saveInstanceState(bundle);
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			mRenderer.restoreInstanceState(bundle);
			super.onRestoreInstanceState(bundle.getParcelable(EXTRA_INSTANCE));
			return;
		}

		super.onRestoreInstanceState(state);
	}

	public void setCornerRadius(int borderRadius) {
		mRenderer.setCornerRadius(borderRadius);
	}

	public void setRatio(float ratio) {
		mRenderer.setRatio(ratio);
	}

	private int dp2px(int dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, getResources().getDisplayMetrics());
	}

	private static void d(String msg) {
		Log.d("ImageView", msg);
	}

	private abstract class Renderer {
		/**
		 * 用来计算是否需要重新计算shader
		 */
		private Drawable mCurrentDrawable;
		private Paint mPaint;

		public abstract void measure(int widthMeasureSpec, int heightMeasureSpec);

		public boolean draw(Canvas canvas) {
			Paint paint = getPaint();
			if (paint == null) {
				return false;
			}

			onDraw(canvas, paint);
			return true;
		}

		protected abstract void onDraw(Canvas canvas, Paint paint);

		private Paint getPaint() {
			// 检查是否需要重新创建
			// 这里 mPaint 可能为空
			// 即没有设置任何渲染内容的情况下
			Drawable drawable = getDrawable();
			if (!checkInRecreate(drawable)) {
				return mPaint;
			}

			Bitmap bitmap = drawableToBitmap(drawable);
			if (bitmap == null) {
				return null;
			}

			mCurrentDrawable = drawable;
			mPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
			mPaint.setShader(onCreateShader(bitmap));
			return mPaint;
		}

		@Nullable
		protected abstract BitmapShader onCreateShader(Bitmap bitmap);

		public abstract void saveInstanceState(Bundle bundle);

		public abstract void restoreInstanceState(Bundle bundle);

		public abstract void setCornerRadius(int borderRadius);

		public abstract void setRatio(float ratio);

		public abstract void onSizeChanged(int w, int h, int ow, int oh);

		private boolean checkInRecreate(Drawable drawable) {
			if (drawable == mCurrentDrawable) {
				return false;
			}

			if (drawable instanceof ColorDrawable &&
					mCurrentDrawable instanceof ColorDrawable) {
				ColorDrawable lhs = (ColorDrawable) drawable;
				ColorDrawable rhs = (ColorDrawable) mCurrentDrawable;
				return lhs.getColor() != rhs.getColor();
			}

			return true;
		}

		@Nullable
		private Bitmap drawableToBitmap(Drawable drawable) {
			if (drawable == null) {
				return null;
			}

			if (drawable instanceof BitmapDrawable) {
				d("drawable instance bitmap");
				BitmapDrawable bd = (BitmapDrawable) drawable;
				return bd.getBitmap();
			}

			if (drawable instanceof ColorDrawable) {
				d("drawable instance bitmap");
				ColorDrawable colorDrawable = (ColorDrawable) drawable;
				Bitmap bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
				bitmap.eraseColor(colorDrawable.getColor());
				return bitmap;
			}

			d("drawable to bitmap");
			int w = drawable.getIntrinsicWidth();
			int h = drawable.getIntrinsicHeight();

			if (w <= 0 || h <= 0) {
				return null;
			}

			Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, w, h);
			drawable.draw(canvas);
			return bitmap;
		}
	}

	private class SquareRenderer extends Renderer {
		private static final String EXTRA_RATIO = "bay_iv_ratio";

		protected float mRatio;

		public SquareRenderer(float ratio) {
			mRatio = ratio;
		}

		@Override
		public void measure(int widthMeasureSpec, int heightMeasureSpec) {
			d("default renderer measure");
			if (mRatio <= 0) {
				return;
			}

			int width = getMeasuredWidth();
			int height = getMeasuredHeight();
			if (width > 0) {
				d("width is specified: " + width);
				height = (int) (width * mRatio + 0.5f);
				setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
			} else if (height > 0) {
				d("height is specified: " + height);
				width = (int) (height / mRatio + 0.5f);
				setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
			}
		}

		@Override
		protected void onDraw(Canvas canvas, Paint paint) {
			/* do nothing */
		}

		@Override
		protected BitmapShader onCreateShader(Bitmap bitmap) {
			/* do nothing */
			return null;
		}


		@Override
		public void saveInstanceState(Bundle bundle) {
			bundle.putFloat(EXTRA_RATIO, mRatio);
		}

		@Override
		public void restoreInstanceState(Bundle bundle) {
			mRatio = bundle.getFloat(EXTRA_RATIO, 1);
		}

		@Override
		public void setCornerRadius(int borderRadius) {
			/* do nothing */
		}

		/**
		 * @param ratio height / width
		 */
		@Override
		public void setRatio(float ratio) {
			mRatio = ratio;
			requestLayout();
		}

		@Override
		public void onSizeChanged(int w, int h, int ow, int oh) {
			/* do nothing */
		}
	}

	private class NormalRenderer extends SquareRenderer {

		public NormalRenderer(float ratio) {
			super(ratio);
		}

		@Override
		public boolean draw(Canvas canvas) {
			return false;
		}
	}

	private class CircleRenderer extends Renderer {
		private static final String EXTRA_RADIUS = "bay_iv_radius";

		/**
		 * 圆的半径
		 */
		private int mRadius;

		@Override
		public void measure(int widthMeasureSpec, int heightMeasureSpec) {
			/* do nothing */
		}

		@Override
		protected void onDraw(Canvas canvas, Paint paint) {
			canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, mRadius, paint);
		}

		@Override
		protected BitmapShader onCreateShader(Bitmap bitmap) {
			BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
			float scale = mRadius * 2.0f / size;

			Matrix matrix = new Matrix();
			matrix.setScale(scale, scale);
			bitmapShader.setLocalMatrix(matrix);
			return bitmapShader;
		}

		@Override
		public void saveInstanceState(Bundle bundle) {
			bundle.putInt(EXTRA_RADIUS, mRadius);
		}

		@Override
		public void restoreInstanceState(Bundle bundle) {
			mRadius = bundle.getInt(EXTRA_RADIUS);
		}

		@Override
		public void setCornerRadius(int borderRadius) {
			/* do nothing */
		}

		@Override
		public void setRatio(float ratio) {
			/* do nothing */
		}

		@Override
		public void onSizeChanged(int w, int h, int ow, int oh) {
			mRadius = Math.min(w, h) / 2;
		}
	}

	private class RoundCornerRenderer extends SquareRenderer {
		private static final String EXTRA_CORNER_RADIUS = "corner_radius";

		/**
		 * 圆角的大小
		 */
		private int mCornerRadius;

		private RectF mRoundRect;

		public RoundCornerRenderer(float ratio, int cornerRadius) {
			super(ratio);
			mCornerRadius = cornerRadius;
		}

		@Override
		protected void onDraw(Canvas canvas, Paint paint) {
			canvas.drawRoundRect(mRoundRect, mCornerRadius, mCornerRadius, paint);
		}

		@Override
		protected BitmapShader onCreateShader(Bitmap bitmap) {
			BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			float scale = Math.max(getWidth() * 1.0f / bitmap.getWidth(), getHeight() * 1.0f / bitmap.getHeight());

			Matrix matrix = new Matrix();
			matrix.setScale(scale, scale);
			bitmapShader.setLocalMatrix(matrix);
			return bitmapShader;
		}

		@Override
		public void saveInstanceState(Bundle bundle) {
			bundle.putInt(EXTRA_CORNER_RADIUS, mCornerRadius);
		}

		@Override
		public void restoreInstanceState(Bundle bundle) {
			mCornerRadius = bundle.getInt(EXTRA_CORNER_RADIUS);
		}

		@Override
		public void setCornerRadius(int cornerRadius) {
			mCornerRadius = cornerRadius;
			invalidate();
		}

		@Override
		public void onSizeChanged(int w, int h, int ow, int oh) {
			mRoundRect = new RectF(0, 0, getWidth(), getHeight());
		}
	}
}
