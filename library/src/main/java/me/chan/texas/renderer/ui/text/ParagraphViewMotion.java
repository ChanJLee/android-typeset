package me.chan.texas.renderer.ui.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.text.Paragraph;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class ParagraphViewMotion {
	private Paragraph mParagraph;
	private GestureDetector mGestureDetector = null;

	private RenderOption mRenderOption;
	private OnSelectedChangedListener mOnTextSelectedListener;
	private Box mLastTouchBox = null;
	private final Context mContext;
	@Nullable
	private ParagraphDecor mParagraphDecor;
	private final View mView;

	private EventListener mEventListener;
	private SpanTouchEventHandler mSpanClickedEventHandler;

	public ParagraphViewMotion(Context context, View view) {
		mContext = context;
		mView = view;
	}

	public void setup(@NonNull Paragraph paragraph,
					  @NonNull RenderOption renderOption,
					  @Nullable ParagraphDecor paragraphDecor,
					  @Nullable SpanTouchEventHandler spanClickedEventHandler) {
		mParagraph = paragraph;
		mRenderOption = renderOption;
		mParagraphDecor = paragraphDecor;
		mSpanClickedEventHandler = spanClickedEventHandler;
	}

	public void clear() {
		mParagraph = null;
		mRenderOption = null;
		mParagraphDecor = null;
	}

	public void setOnTextSelectedListener(OnSelectedChangedListener onTextSelectedListener) {
		mOnTextSelectedListener = onTextSelectedListener;
	}

	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent event) {
		if (mRenderOption == null || !mRenderOption.isWordSelectable()) {
			return false;
		}

		if (mGestureDetector == null) {
			mGestureDetector = new GestureDetector(mContext, mEventListener = new EventListener());
		}
		mGestureDetector.setIsLongpressEnabled(true);
		mGestureDetector.setOnDoubleTapListener(mEventListener);
		return mGestureDetector.onTouchEvent(event);
	}

	private boolean handleMotion(MotionEvent e, @OnSelectedChangedListener.EventType int eventType) {
		if (mMode == MODE_BOX) {
			return handleBoxModeMotion(e, eventType);
		}

		if (mMode == MODE_DECOR) {
			return handleDecorModeMotion(e, eventType);
		}

		return handleEmptyModeMotion(e, eventType);
	}

	private boolean handleEmptyModeMotion(MotionEvent e, int eventType) {
		if (mOnTextSelectedListener == null) {
			return false;
		}

		// 通知上层有元素被选中
		return mOnTextSelectedListener.onSegmentClicked(mView, e, mParagraph, eventType);
	}

	private boolean handleDecorModeMotion(MotionEvent e, @OnSelectedChangedListener.EventType int eventType) {
		if (mParagraphDecor == null || eventType != OnSelectedChangedListener.EVENT_CLICKED) {
			return false;
		}

		return mParagraphDecor.handleTouchEvent(e, mParagraph, mRenderOption, mView.getWidth(), mView.getHeight());
	}

	private boolean handleBoxModeMotion(MotionEvent e, @OnSelectedChangedListener.EventType int eventType) {
		if (mLastTouchBox == null || mOnTextSelectedListener == null) {
			return false;
		}

		// 通知上层有元素被选中
		return mOnTextSelectedListener.onBoxSelected(mView, e, mParagraph, eventType, mLastTouchBox);
	}

	private int mMode = 0;
	private static final int MODE_BOX = 1;
	private static final int MODE_DECOR = 2;
	private static final int MODE_EMPTY = 3;

	public Box checkIfClicked(float x, float y) {
		mMotionEventVisitor.setMotionLocation(x, y);
		mMotionEventVisitor.setLineSpace(mRenderOption.getLineSpace());
		try {
			mMotionEventVisitor.visit(mParagraph, mRenderOption);
			return mMotionEventVisitor.getBox();
		} catch (ParagraphVisitor.VisitException ex) {
			w(ex);
		} finally {
			mMotionEventVisitor.clear();
		}
		return null;
	}

	public Paragraph getParagraph() {
		return mParagraph;
	}

	private final MotionEventVisitor mMotionEventVisitor = new MotionEventVisitor();

	private static class MotionEventVisitor extends ParagraphVisitor {

		private Box mBox;
		private float mX;
		private float mY;
		private float mHalfLineSpace;
		private float mLastLineTop;
		private float mLastLineBottom;

		public void setMotionLocation(float x, float y) {
			mX = x;
			mY = y;
		}

		public void setLineSpace(float lineSpace) {
			mHalfLineSpace = lineSpace / 2;
		}

		public void clear() {
			mBox = null;
			mX = mY = -1;
			mHalfLineSpace = 0;
			mLastLineTop = mLastLineBottom = 0;
		}

		public Box getBox() {
			return mBox;
		}

		@Override
		protected void onVisitParagraphStart(Paragraph paragraph) {

		}

		@Override
		protected void onVisitParagraphEnd(Paragraph paragraph) {

		}

		@Override
		protected void onVisitLineStart(Line line, float x, float y) {
			mLastLineTop = y - line.getLineHeight() - mHalfLineSpace;
			mLastLineBottom = y + mHalfLineSpace;

			if (mLastLineTop > mY ||
					mLastLineBottom < mY) {
				sendVisitSig(SIG_STOP_LINE_VISIT);
			}
		}

		@Override
		protected void onVisitLineEnd(Line line, float x, float y) {

		}

		@Override
		public void onVisitBox(Box box, RectF inner, RectF outer, RendererContext context) {
			// 增大点击热区
			if (outer.left <= mX &&
					outer.right >= mX) {
				mBox = box;
				sendVisitSig(SIG_STOP_PARA_VISIT);
			}
		}
	}

	private static void d(String msg) {
		Log.d("TexasParaView", msg);
	}

	private static void w(Throwable throwable) {
		Log.w("TexasParaView", throwable);
	}

	private static void i(String msg) {
		Log.i("TexasParaView", msg);
	}

	private class EventListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

		@Override
		public boolean onDown(@NonNull MotionEvent e) {
			mLastTouchBox = null;
			mMode = 0;

			if (mParagraph == null || mSpanClickedEventHandler == null) {
				return false;
			}

			Layout layout = mParagraph.getLayout();
			if (layout.getLineCount() == 0) {
				return false;
			}

			float x = e.getX();
			float y = e.getY();
			Box box = checkIfClicked(x, y);
			if (box != null) {
				if (mSpanClickedEventHandler.isSpanClickable(box.getTag())) {
					mLastTouchBox = box;
					mMode = MODE_BOX;
					return true;
				}

				return false;
			}

			if (mParagraphDecor != null && mParagraphDecor.handleTouchEvent(e, mParagraph, mRenderOption, mView.getWidth(), mView.getHeight())) {
				mMode = MODE_DECOR;
				return true;
			}

			mMode = MODE_EMPTY;
			return true;
		}


		@Override
		public void onShowPress(MotionEvent e) {
			/* do nothing */
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			handleMotion(e, OnSelectedChangedListener.EVENT_LONG_CLICKED);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			/* do nothing */
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return handleMotion(e, OnSelectedChangedListener.EVENT_CLICKED);
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			mMode = MODE_EMPTY;
			return handleMotion(e, OnSelectedChangedListener.EVENT_DOUBLE_CLICKED);
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return true;
		}
	}
}
