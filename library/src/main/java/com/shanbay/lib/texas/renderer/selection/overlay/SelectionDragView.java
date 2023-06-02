package com.shanbay.lib.texas.renderer.selection.overlay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.shanbay.lib.texas.renderer.selection.SelectionManager;
import com.shanbay.lib.texas.renderer.selection.magnifier.MagnifierView;
import com.shanbay.lib.texas.renderer.selection.magnifier.MagnifierViewApi28;
import com.shanbay.lib.texas.renderer.selection.magnifier.MagnifierViewApi29;
import com.shanbay.lib.texas.renderer.selection.magnifier.MagnifierViewNoop;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;

/**
 * Created by Otway on 2021/11/16.
 */
@SuppressLint("ViewConstructor")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SelectionDragView extends View {
	private final Paint mPaint;
	private final int mScreenHeight;

	private static final int HOT_MOTION_REGION_SIZE = 300;
	private final RectF mP1 = new RectF(0, 0, HOT_MOTION_REGION_SIZE, HOT_MOTION_REGION_SIZE);
	private final RectF mP2 = new RectF(0, 0, HOT_MOTION_REGION_SIZE, HOT_MOTION_REGION_SIZE);

	private SelectionManager mSelectionManager;
	private final int[] mLocations = new int[2];
	private final MagnifierView mMagnifierView;
	private float mAdviseOffsetY;

	private final Path mPath = new Path();
	private final RectF mFocusPoint = new RectF();
	private final RectF mUnFocusPoint = new RectF();

	private float mLastTouchX, mLastTouchY;
	private float mTouchSlopThresholdSquare = 0;

	public SelectionDragView(Context context, ViewGroup parent) {
		super(context, null, 0);
		mPaint = new Paint();
		mPaint.setColor(0x88ff0000);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);

		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		mScreenHeight = displayMetrics.heightPixels;

		mTouchSlopThresholdSquare = 2 * ViewConfiguration.get(context).getScaledTouchSlop();
		mTouchSlopThresholdSquare = mTouchSlopThresholdSquare * mTouchSlopThresholdSquare;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			mMagnifierView = new MagnifierViewApi29(parent);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			mMagnifierView = new MagnifierViewApi28(parent);
		} else {
			mMagnifierView = new MagnifierViewNoop(parent);
		}
	}

	private final Region mRendererRegion = new Region();
	private static final int HALF_OF_DROPPER_SIZE = 50;

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 手指中心
		canvas.drawCircle(mLastTouchX, mLastTouchY, 10, mPaint);

		getRendererRegion(mRendererRegion);

		canvas.save();
		canvas.translate(-mLocations[0], -mLocations[1]);

		float cx = mRendererRegion.top.centerX();
		float cy = mRendererRegion.top.centerY();
		float left = cx - HALF_OF_DROPPER_SIZE;
		float bottom = cy + HALF_OF_DROPPER_SIZE;

		mPath.reset();
		mPath.moveTo((left + cx) / 2, cy + mAdviseOffsetY);
		mPath.lineTo(cx, cy + mAdviseOffsetY);
		mPath.lineTo(cx, (bottom + cy) / 2 + mAdviseOffsetY);
		mPath.addArc(left, cy + mAdviseOffsetY, cx, bottom + mAdviseOffsetY, 90f, 360f);
		mPath.close();
		canvas.drawPath(mPath, mPaint);

		cx = mRendererRegion.bottom.centerX();
		cy = mRendererRegion.bottom.centerY();

		float right = cx + HALF_OF_DROPPER_SIZE;
		bottom = cy + HALF_OF_DROPPER_SIZE;

		mPath.reset();
		if (bottom <= mScreenHeight) {
			mPath.moveTo((right + cx) / 2, cy);
			mPath.lineTo(cx, cy);
			mPath.lineTo(cx, (bottom + cy) / 2);
			mPath.addArc(cx, cy, right, bottom, -90f, -360f);
		} else {
			float top = cy - HALF_OF_DROPPER_SIZE;
			mPath.moveTo((right + cx) / 2, cy - mAdviseOffsetY);
			mPath.lineTo(cx, cy - mAdviseOffsetY);
			mPath.lineTo(cx, (top + cy) / 2 - mAdviseOffsetY);
			mPath.addArc(cx, top - mAdviseOffsetY, right, cy - mAdviseOffsetY, 90, 360);
		}
		mPath.close();
		canvas.drawPath(mPath, mPaint);

		canvas.restore();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		getLocationOnScreen(mLocations);
	}

	private boolean mHandleDownEvent = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getRawX();
		float y = event.getRawY();

		if (action == MotionEvent.ACTION_MOVE) {
			float dx = mLastTouchX - x;
			float dy = mLastTouchY - y;
			if (dy * dy + dx * dx < mTouchSlopThresholdSquare) {
				return true;
			}
		}

		mLastTouchX = event.getX();
		mLastTouchY = event.getY();

		if (action == MotionEvent.ACTION_DOWN) {
			mHandleDownEvent = handleDownEvent(x, y);
		} else if (action == MotionEvent.ACTION_MOVE && mHandleDownEvent) {
			handleMoveEvent(x, y);
		} else if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) &&
				mHandleDownEvent) {
			mSelectionManager.handleDragEnd(x, y);
		}

		handlePrompt(action);

		/* 防止事件传递到下面 */
		return true;
	}

	private void handlePrompt(int action) {
		// render magnifier
		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
			dismissPrompt();
			return;
		}

		if (mHandleDownEvent) {
			mMagnifierView.show(mLastTouchX, mLastTouchY);
		}
	}

	@SuppressLint("FieldCodeStyle")
	private static class Region {
		private RectF top;
		private RectF bottom;

		public float getTopX() {
			return top.centerX();
		}

		public float getTopY() {
			return top.centerY();
		}

		public float getBottomX() {
			return bottom.centerX();
		}

		public float getBottomY() {
			return bottom.centerY();
		}
	}

	private final Region mMotionRegion = new Region();

	private void handleMoveEvent(float x, float y) {
		mFocusPoint.offset(x - mFocusPoint.centerX(), y - mFocusPoint.centerY());
		if (mSelectionManager != null) {
			getMotionRegion(mMotionRegion);
			mSelectionManager.handleMoveToSelection(mMotionRegion.getTopX(), mMotionRegion.getTopY(),
					mMotionRegion.getBottomX(), mMotionRegion.getBottomY(), mMotionRegion.top == mFocusPoint);
		}
	}

	private void getRendererRegion(Region region) {
		getSelectedRegion(region, mP1, mP2);
	}

	private void getMotionRegion(Region region) {
		getSelectedRegion(region, mFocusPoint, mUnFocusPoint);
	}

	private void getSelectedRegion(Region region, RectF p1, RectF p2) {
		if (p1.centerY() < p2.centerY()) {
			region.top = p1;
			region.bottom = p2;
			return;
		}

		if (p1.centerY() > p2.centerY()) {
			region.top = p2;
			region.bottom = p1;
			return;
		}

		if (p1.centerX() <= p2.centerX()) {
			region.top = p1;
			region.bottom = p2;
			return;
		}

		region.top = p2;
		region.bottom = p1;
	}

	private boolean handleDownEvent(float x, float y) {
		boolean handled = checkIfClickedHotRegion(x, y);

		if (handled) {
			mFocusPoint.offset(x - mFocusPoint.centerX(), y - mFocusPoint.centerY());
			// 通知上层开始拖拽
			mSelectionManager.handleDragStart(x, y);
			return true;
		}

		// 通知上层没有点击到
		mSelectionManager.handleClickNothing();

		dismissPrompt();

		return false;
	}

	private void dismissPrompt() {
		mMagnifierView.dismiss();
		mLastTouchX = mLastTouchY = -100;
		invalidate();
	}

	private boolean checkIfClickedHotRegion(float x, float y) {
		if (mP1.contains(x, y)) {
			mFocusPoint.set(mP1);
			mUnFocusPoint.set(mP2);
			return true;
		}

		if (mP2.contains(x, y)) {
			mFocusPoint.set(mP2);
			mUnFocusPoint.set(mP1);
			return true;
		}

		return false;
	}

	public void setSelectionManager(@NonNull SelectionManager selectionManager) {
		mSelectionManager = selectionManager;
	}

	/**
	 * @param x1            顶点x
	 * @param y1            顶点y
	 * @param x2            底点x
	 * @param y2            底点y
	 * @param adviseOffsetY 绘制方向建议的y偏移量
	 */
	public void renderRegion(float x1, float y1, float x2, float y2, float adviseOffsetY) {
		float offset = HOT_MOTION_REGION_SIZE / 2.0f;
		mP1.set(x1 - offset, y1 - offset, x1 + offset, y1 + offset);
		mP2.set(x2 - offset, y2 - offset, x2 + offset, y2 + offset);
		mAdviseOffsetY = adviseOffsetY;
		invalidate();
	}

	public void setColor(@ColorInt int color) {
		mPaint.setColor(color);
	}
}