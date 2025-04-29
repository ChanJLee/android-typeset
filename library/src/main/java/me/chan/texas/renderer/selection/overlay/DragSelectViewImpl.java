package me.chan.texas.renderer.selection.overlay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.misc.PointF;
import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.renderer.selection.SelectionManager;
import me.chan.texas.renderer.selection.magnifier.MagnifierView;
import me.chan.texas.renderer.selection.magnifier.MagnifierViewFactory;

/**
 * Created by Otway on 2021/11/16.
 */
@SuppressLint("ViewConstructor")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class DragSelectViewImpl extends View implements DragSelectView {
	private final Paint mPaint;

	private static final int HOT_MOTION_REGION_SIZE = 300;
	private final PointF mP1 = new PointF(0, 0);
	private final PointF mP2 = new PointF(0, 0);


	private SelectionManager mSelectionManager;
	private final MagnifierView mMagnifierView;
	private float mAdviseOffsetY;

	private final Path mPath = new Path();
	private final PointF mFocusPoint = new PointF();
	private final PointF mUnFocusPoint = new PointF();

	private final PointF mLastTouchPoint = new PointF();
	private final PointF mTouchPoint = new PointF();
	private float mTouchSlopThresholdSquare;
	private final LongPressMotionDispatcher mLongPressMotionDispatcher;
	private final Region mMotionRegion = new Region();
	private boolean mEnable = true;

	public DragSelectViewImpl(Context context, ViewGroup parent) {
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

	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		if (!mEnable) {
			return;
		}

		// 手指中心
		if (mTouchPoint.x >= 0 || mTouchPoint.y >= 0) {
			canvas.drawCircle(mTouchPoint.x, mTouchPoint.y, 10, mPaint);
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

		if (Texas.DEBUG_DRAG && mDebugPaint != null) {
			canvas.drawText(mDebugInfo, 0, 200, mDebugPaint);
		}
	}

	private boolean mHandleDownEvent = false;
	private String mDebugInfo = "";
	private Paint mDebugPaint = null;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mEnable) {
			mSelectionManager.handleDragEnd(TouchEvent.obtain(this, event));
			return true;
		}

		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();

		if (Texas.DEBUG_DRAG) {
			mDebugInfo = MotionEvent.actionToString(action) +
					", [" + x + "," + y +
					"] r[" + event.getRawX() + "," + event.getRawY() + "]";
			if (mDebugPaint == null) {
				mDebugPaint = new Paint();
				mDebugPaint.setColor(Color.RED);
				mDebugPaint.setTextSize(35);
			}
		}

		if (action == MotionEvent.ACTION_MOVE && mHandleDownEvent) {
			float dx = mLastTouchPoint.x - x;
			float dy = mLastTouchPoint.y - y;
			if (dy * dy + dx * dx >= mTouchSlopThresholdSquare) {
				handleMoveEvent(x, y);
				mLastTouchPoint.x = x;
				mLastTouchPoint.y = y;
			} else {
				scheduleAutoScrollEvent(y);
			}
		} else if (action == MotionEvent.ACTION_DOWN) {
			mHandleDownEvent = handleDownEvent(event, x, y);
			mLastTouchPoint.x = x;
			mLastTouchPoint.y = y;
		} else if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) && mHandleDownEvent) {
			mLongPressMotionDispatcher.cancel("手已经抬起");
			mSelectionManager.handleDragEnd(TouchEvent.obtain(this, event));
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
		mTouchPoint.x = x;
		mTouchPoint.y = y;

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
		mFocusPoint.offset(x - mFocusPoint.x, y - mFocusPoint.y);
		if (mSelectionManager == null) {
			return;
		}

		getMotionRegion(mMotionRegion);
		mSelectionManager.handleMoveToSelection(mMotionRegion.getTopX(), mMotionRegion.getTopY(), mMotionRegion.getBottomX(), mMotionRegion.getBottomY());
	}

	private void getRendererRegion(Region region) {
		getSelectedRegion(region, mP1, mP2);
		if (Texas.DEBUG_DRAG) {
			Log.d("drag_debug.view", "getRendererRegion: " + mP1 + " " + mP2 + " " + region);
		}
	}

	private void getMotionRegion(Region region) {
		getSelectedRegion(region, mFocusPoint, mUnFocusPoint);
		if (Texas.DEBUG_DRAG) {
			Log.d("drag_debug.view", "getMotionRegion: " + mFocusPoint + " " + mUnFocusPoint + " " + region);
		}
	}

	private void getSelectedRegion(Region region, PointF p1, PointF p2) {
		if (p1.y < p2.y) {
			region.setup(p1, p2);
			return;
		}

		if (p1.y > p2.y) {
			region.setup(p2, p1);
			return;
		}

		if (p1.x <= p2.x) {
			region.setup(p1, p2);
			return;
		}

		region.setup(p2, p1);
	}

	private boolean handleDownEvent(MotionEvent event, float x, float y) {
		boolean handled = checkIfClickedHotRegion(x, y);

		if (handled) {
			mFocusPoint.offset(x - mFocusPoint.x, y - mFocusPoint.y);
			// 通知上层开始拖拽
			mSelectionManager.handleDragStart(TouchEvent.obtain(this, event));
			return true;
		}

		// 通知上层没有点击到
		mSelectionManager.handleClickNothing();

		dismissPrompt();

		return false;
	}

	private void dismissPrompt() {
		mMagnifierView.dismiss();
		mTouchPoint.x = mTouchPoint.y = -100;
		invalidate();
	}

	private boolean checkIfClickedHotRegion(float x, float y) {
		if (mP1.contains(x, y, HOT_MOTION_REGION_SIZE)) {
			mFocusPoint.set(mP1);
			mUnFocusPoint.set(mP2);
			return true;
		}

		if (mP2.contains(x, y, HOT_MOTION_REGION_SIZE)) {
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
		if (Texas.DEBUG_DRAG) {
			Log.d("drag_debug.view", "update selection: " + x1 + " " + y1 + " " + x2 + " " + y2);
		}

		mP1.set(x1, y1);
		mP2.set(x2, y2);
		mAdviseOffsetY = adviseOffsetY;
		invalidate();
	}

	public void setColor(@ColorInt int color) {
		mPaint.setColor(color);
		invalidate();
	}

	@Override
	public void setEnable(boolean enable) {
		mEnable = enable;
		invalidate();
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