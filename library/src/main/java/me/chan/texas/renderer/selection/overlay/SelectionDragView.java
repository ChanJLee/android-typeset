package me.chan.texas.renderer.selection.overlay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.selection.SelectionManager;
import me.chan.texas.renderer.selection.magnifier.MagnifierView;
import me.chan.texas.renderer.selection.magnifier.MagnifierViewFactory;

/**
 * Created by Otway on 2021/11/16.
 */
@SuppressLint("ViewConstructor")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SelectionDragView extends View {
	private final Paint mPaint;

	private static final int HOT_MOTION_REGION_SIZE = 300;
	private final RectF mP1 = new RectF(0, 0, HOT_MOTION_REGION_SIZE, HOT_MOTION_REGION_SIZE);
	private final RectF mP2 = new RectF(0, 0, HOT_MOTION_REGION_SIZE, HOT_MOTION_REGION_SIZE);


	private SelectionManager mSelectionManager;
	private final MagnifierView mMagnifierView;
	private float mAdviseOffsetY;

	private final Path mPath = new Path();
	private final RectF mFocusPoint = new RectF();
	private final RectF mUnFocusPoint = new RectF();

	private float mLastTouchPoint[] = {0, 0};
	private float mTouchPoint[] = {0, 0};
	private float mTouchSlopThresholdSquare;
	private final LongPressMotionDispatcher mLongPressMotionDispatcher;
	private final Region mMotionRegion = new Region();

	public SelectionDragView(Context context, ViewGroup parent) {
		super(context, null, 0);
		mPaint = new Paint();
		mPaint.setColor(0x88ff0000);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);

		mTouchSlopThresholdSquare = 2 * ViewConfiguration.get(context).getScaledTouchSlop();
		mTouchSlopThresholdSquare = mTouchSlopThresholdSquare * mTouchSlopThresholdSquare;

		mMagnifierView = MagnifierViewFactory.newInstance(parent);

		mLongPressMotionDispatcher = new LongPressMotionDispatcher() {
			@Override
			protected void onMotionReceived(int direction) {
				if (direction == LongPressMotionDispatcher.DIRECTION_UP) {
					mSelectionManager.autoScrollUp();
				} else if (direction == LongPressMotionDispatcher.DIRECTION_DOWN) {
					mSelectionManager.autoScrollDown();
				}
			}
		};
	}

	private final Region mRendererRegion = new Region();
	private static final int HALF_OF_DROPPER_SIZE = 50;

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onDraw(Canvas canvas) {
		// 手指中心
		if (mTouchPoint[0] >= 0 || mTouchPoint[1] >= 0) {
			canvas.drawCircle(mTouchPoint[0], mTouchPoint[1], 10, mPaint);
		}

		getRendererRegion(mRendererRegion);

		canvas.save();

		float cx = mRendererRegion.getTopX();
		float cy = mRendererRegion.getTopY();
		float left = cx - HALF_OF_DROPPER_SIZE;
		float bottom = cy + HALF_OF_DROPPER_SIZE;

		int contentHeight = getHeight();
		mPath.reset();
		mPath.moveTo((left + cx) / 2, cy + mAdviseOffsetY);
		mPath.lineTo(cx, cy + mAdviseOffsetY);
		mPath.lineTo(cx, (bottom + cy) / 2 + mAdviseOffsetY);
		mPath.addArc(left, cy + mAdviseOffsetY, cx, bottom + mAdviseOffsetY, 90f, 360f);
		mPath.close();
		canvas.drawPath(mPath, mPaint);

		cx = mRendererRegion.getBottomX();
		cy = mRendererRegion.getBottomY();
		float right = cx + HALF_OF_DROPPER_SIZE;
		bottom = cy + HALF_OF_DROPPER_SIZE;

		mPath.reset();
		if (bottom <= contentHeight) {
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

	private boolean mHandleDownEvent = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();

		if (action == MotionEvent.ACTION_MOVE && mHandleDownEvent) {
			float dx = mLastTouchPoint[0] - x;
			float dy = mLastTouchPoint[1] - y;
			if (dy * dy + dx * dx >= mTouchSlopThresholdSquare) {
				handleMoveEvent(x, y);
				mLastTouchPoint[0] = x;
				mLastTouchPoint[1] = y;
			} else {
				scheduleAutoScrollEvent(y);
			}
		} else if (action == MotionEvent.ACTION_DOWN) {
			mHandleDownEvent = handleDownEvent(x, y);
			mLastTouchPoint[0] = x;
			mLastTouchPoint[1] = y;
		} else if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) && mHandleDownEvent) {
			mLongPressMotionDispatcher.cancel("手已经抬起");
			mSelectionManager.handleDragEnd(x, y);
		}

		renderPrompt(action, x, y);

		/* 防止事件传递到下面 */
		return true;
	}

	@Override
	protected void onDetachedFromWindow() {
		mLongPressMotionDispatcher.cancel("detach window");
		super.onDetachedFromWindow();
	}

	private void renderPrompt(int action, float x, float y) {
		mTouchPoint[0] = x;
		mTouchPoint[1] = y;

		// render magnifier
		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
			dismissPrompt();
			return;
		}

		if (mHandleDownEvent) {
			mMagnifierView.show(x, y);
		}
	}

	private void scheduleAutoScrollEvent(float y) {
		int height = getHeight();
		if (y < height * 0.1) {
			mLongPressMotionDispatcher.dispatch(LongPressMotionDispatcher.DIRECTION_UP);
		} else if (y > height * 0.9) {
			mLongPressMotionDispatcher.dispatch(LongPressMotionDispatcher.DIRECTION_DOWN);
		} else {
			mLongPressMotionDispatcher.cancel("y超出了范围");
		}
	}

	private void handleMoveEvent(float x, float y) {
		mFocusPoint.offset(x - mFocusPoint.centerX(), y - mFocusPoint.centerY());
		if (mSelectionManager == null) {
			return;
		}

		getMotionRegion(mMotionRegion);
		mSelectionManager.handleMoveToSelection(mMotionRegion.getTopX(), mMotionRegion.getTopY(), mMotionRegion.getBottomX(), mMotionRegion.getBottomY(), mMotionRegion.isTop(mFocusPoint));
	}

	private void getRendererRegion(Region region) {
		getSelectedRegion(region, mP1, mP2);
	}

	private void getMotionRegion(Region region) {
		getSelectedRegion(region, mFocusPoint, mUnFocusPoint);
	}

	private void getSelectedRegion(Region region, RectF p1, RectF p2) {
		if (p1.centerY() < p2.centerY()) {
			region.setup(p1, p2);
			return;
		}

		if (p1.centerY() > p2.centerY()) {
			region.setup(p2, p1);
			return;
		}

		if (p1.centerX() <= p2.centerX()) {
			region.setup(p1, p2);
			return;
		}

		region.setup(p2, p1);
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
		mTouchPoint[0] = mTouchPoint[1] = -100;
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

	public void updateContentScrollY(int dy) {
		if (getVisibility() != VISIBLE) {
			return;
		}

		mP1.offset(0, dy);
		mP2.offset(0, dy);
		mFocusPoint.offset(0, dy);
		mUnFocusPoint.offset(0, dy);
		invalidate();
	}
}