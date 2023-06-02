package me.chan.texas.renderer.ui.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.shanbay.lib.log.Log;
import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class ParagraphViewMotion implements GestureDetector.OnGestureListener {
	private Paragraph mParagraph;
	private GestureDetector mGestureDetector = null;

	private RenderOption mRenderOption;
	private OnSelectedChangedListener mOnTextSelectedListener;
	private Box mLastTouchBox = null;
	private final Context mContext;
	@Nullable
	private ParagraphDecor mParagraphDecor;
	private final View mView;

	public ParagraphViewMotion(Context context, View view) {
		mContext = context;
		mView = view;
	}

	public void setup(@NonNull Paragraph paragraph,
					  @NonNull RenderOption renderOption,
					  @Nullable ParagraphDecor paragraphDecor) {
		mParagraph = paragraph;
		mRenderOption = renderOption;
		mParagraphDecor = paragraphDecor;
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
			mGestureDetector = new GestureDetector(mContext, this);
		}
		mGestureDetector.setIsLongpressEnabled(true);
		return mGestureDetector.onTouchEvent(event);
	}

	private boolean handleMotion(MotionEvent e, boolean isLongClicked) {
		if (mMode == MODE_BOX) {
			return handleBoxModeMotion(e, isLongClicked);
		}

		if (mMode == MODE_DECOR) {
			return handleDecorModeMotion(e, isLongClicked);
		}

		return false;
	}

	private boolean handleDecorModeMotion(MotionEvent e, boolean isLongClicked) {
		if (mParagraphDecor == null || isLongClicked) {
			return false;
		}

		return mParagraphDecor.handleTouchEvent(e, mParagraph, mRenderOption, mView.getWidth(), mView.getHeight());
	}

	private boolean handleBoxModeMotion(MotionEvent e, boolean isLongClicked) {
		if (mLastTouchBox == null || mOnTextSelectedListener == null) {
			return false;
		}

		// 通知上层有元素被选中
		return mOnTextSelectedListener.onBoxSelected(e, mParagraph, isLongClicked, mLastTouchBox);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		mLastTouchBox = null;
		mMode = 0;

		if (mParagraph == null) {
			return false;
		}

		Layout layout = mParagraph.getLayout();
		if (layout.getLineCount() == 0) {
			return false;
		}

		float x = e.getX();
		float y = e.getY();
		mLastTouchBox = checkIfClicked(x, y);
		if (mLastTouchBox != null) {
			mMode = MODE_BOX;
			return true;
		}

		if (mParagraphDecor != null && mParagraphDecor.handleTouchEvent(e, mParagraph, mRenderOption, mView.getWidth(), mView.getHeight())) {
			mMode = MODE_DECOR;
			return true;
		}

		return false;
	}

	private int mMode = 0;
	private static final int MODE_BOX = 1;
	private static final int MODE_DECOR = 2;

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

	@Override
	public void onShowPress(MotionEvent e) {
		/* do nothing */
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return handleMotion(e, false);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		handleMotion(e, true);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		/* do nothing */
		return false;
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
		public void onVisitBox(Box box, RectF inner, RectF outer) {
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
}
