package com.shanbay.lib.texas.renderer;

import android.content.Context;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.RectF;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.shanbay.lib.texas.R;
import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.image.ImageLoader;
import com.shanbay.lib.log.Log;
import com.shanbay.lib.texas.measurer.Measurer;
import com.shanbay.lib.texas.text.Box;
import com.shanbay.lib.texas.text.Document;
import com.shanbay.lib.texas.text.DrawableBox;
import com.shanbay.lib.texas.text.Figure;
import com.shanbay.lib.texas.text.Line;
import com.shanbay.lib.texas.text.OnClickedListener;
import com.shanbay.lib.texas.text.Paragraph;
import com.shanbay.lib.texas.text.Segment;
import com.shanbay.lib.texas.text.TextBox;
import com.shanbay.lib.texas.text.ViewSegment;

import java.util.List;

@Hidden
class TexasAdapter extends RecyclerView.Adapter<TexasAdapter.Renderer> implements ParagraphView.OnSelectedChangedListener {
	private static final int TYPE_PARAGRAPH = 1;
	private static final int TYPE_FIGURE = 2;
	private static final int TYPE_VIEW_SEGMENT = 3;

	private Document mDocument;
	private LayoutInflater mLayoutInflater;
	private ImageLoader mImageLoader;
	private TextPaint mTextPaint;
	private RenderOption mRenderOption;
	private Measurer mMeasurer;
	/*
	 * 选中效果属于编辑器内部的状态了，所有直接由adapter管理而不需要通知外部组件
	 * */
	private Selection mCurrentSelection;

	TexasAdapter(LayoutInflater layoutInflater, ImageLoader imageLoader) {
		mLayoutInflater = layoutInflater;
		mImageLoader = imageLoader;
	}

	@NonNull
	@Override
	public Renderer onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
		if (type == TYPE_PARAGRAPH) {
			return new ParagraphRenderer(mLayoutInflater.inflate(R.layout.com_shanbay_lib_texas_text, viewGroup, false));
		} else if (type == TYPE_FIGURE) {
			return new FigureRenderer(mLayoutInflater.inflate(R.layout.com_shanbay_lib_texas_figure, viewGroup, false));
		} else {
			return new ViewSegmentRenderer(mLayoutInflater.inflate(R.layout.com_shanbay_lib_view_segment, viewGroup, false));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onBindViewHolder(@NonNull Renderer renderer, int position) {
		renderer.render(getItem(position));
	}

	@Override
	public int getItemViewType(int position) {
		Segment segment = getItem(position);
		if (segment instanceof Paragraph) {
			return TYPE_PARAGRAPH;
		} else if (segment instanceof Figure) {
			return TYPE_FIGURE;
		} else if (segment instanceof ViewSegment) {
			return TYPE_VIEW_SEGMENT;
		}

		throw new RuntimeException("unknown segment type");
	}

	Segment getItem(int position) {
		return mDocument.getSegment(position);
	}

	@Override
	public int getItemCount() {
		return mDocument == null ? 0 : mDocument.getSegmentCount();
	}

	public void clear() {
		d("clear");
		mDocument = null;
		clearSelection();
		notifyDataSetChanged();
	}

	public void render(Document document,
					   TextPaint textPaint,
					   RenderOption renderOption,
					   Measurer measurer) {
		d("render");
		mDocument = document;
		mTextPaint = textPaint;
		mRenderOption = renderOption;
		mMeasurer = measurer;
		clearSelection();
		notifyDataSetChanged();
	}

	void update(TextPaint textPaint, RenderOption renderOption) {
		d("update");
		mTextPaint = textPaint;
		mRenderOption = renderOption;
		notifyDataSetChanged();
	}


	private void notifySegmentChanged(Segment segment) {
		if (mDocument == null || segment == null) {
			return;
		}

		int index = mDocument.indexOf(segment);
		if (index < 0 || index >= getItemCount()) {
			return;
		}
		d("notify item changed: " + index);
		notifyItemChanged(index);
	}

	@Nullable
	Selection getCurrentSelection() {
		return mCurrentSelection;
	}

	@Override
	public void onTextSelected(MotionEvent e,
							   Paragraph paragraph,
							   boolean isLongClicked,
							   OnClickedListener onClickedListener,
							   int width) {
		clearSelection();
		mSelectedTextByListenerVisitor.setLongClicked(isLongClicked);
		mSelectedTextByListenerVisitor.setLastYOnScreen(e.getRawY());
		mSelectedTextByListenerVisitor.setLastYInView(e.getY());
		mSelectedTextByListenerVisitor.setOnClickedListener(onClickedListener);
		mSelectedTextByListenerVisitor.setWidth(width);
		mSelectedTextByListenerVisitor.visit(paragraph, width, mRenderOption, mMeasurer.getFontTopPadding());
		TextParagraphSelection selection = mSelectedTextByListenerVisitor.getSelection();
		mSelectedTextByListenerVisitor.clear();
		handleParagraphSelected(new SelectionImpl(selection));
		if (onClickedListener != null) {
			onClickedListener.onClicked(e.getRawX(), e.getRawY());
		}
	}

	@Override
	public void onDrawSelected(MotionEvent e,
							   Paragraph paragraph,
							   boolean isLongClicked,
							   DrawableBox box,
							   OnClickedListener onClickedListener,
							   int width) {
		clearSelection();
		DrawableParagraphSelection selection = new DrawableParagraphSelection(
				paragraph,
				isLongClicked,
				e.getY(),
				e.getRawY(),
				width,
				box
		);

		box.setSelected(true);
		handleParagraphSelected(new SelectionImpl(selection));
		if (onClickedListener != null) {
			onClickedListener.onClicked(e.getRawX(), e.getRawY());
		}
	}

	private void clearSelection() {
		if (mCurrentSelection != null) {
			mCurrentSelection.clear();
			mCurrentSelection = null;
		}
	}

	private void handleSelectedParagraphByTags(ParagraphSelection paragraphSelection, List<?> tags) {
		mSelectedTextByTagVisitor.setLongClicked(paragraphSelection.isSelectedByLongClick());
		mSelectedTextByTagVisitor.setLastYOnScreen(paragraphSelection.getTouchYOnScreen());
		mSelectedTextByTagVisitor.setLastYInView(paragraphSelection.getTouchYInView());
		mSelectedTextByTagVisitor.setTags(tags);
		mSelectedTextByTagVisitor.setWidth(paragraphSelection.getViewWidth());
		mSelectedTextByTagVisitor.visit(paragraphSelection.getParagraph(), paragraphSelection.getViewWidth(), mRenderOption, mMeasurer.getFontTopPadding());
		TextParagraphSelection textParagraphSelection = mSelectedTextByTagVisitor.getSelection();
		Selection selection = new SelectionImpl(textParagraphSelection);
		handleParagraphSelected(selection);
		mSelectedTextByTagVisitor.clear();
	}

	private void handleParagraphSelected(Selection selection) {
		notifySegmentChanged(selection.getParagraph());
		if (mCurrentSelection == null) {
			mCurrentSelection = selection;
			return;
		}

		mCurrentSelection.update(selection);
	}

	static abstract class Renderer<T> extends RecyclerView.ViewHolder {

		Renderer(@NonNull View itemView) {
			super(itemView);
			onCreate(itemView);
		}

		protected abstract void onCreate(View view);

		public abstract void render(T data);

		protected Context getContext() {
			return itemView.getContext();
		}

		protected final <T extends View> T findViewById(@IdRes int id) {
			return itemView.findViewById(id);
		}
	}

	private class ParagraphRenderer extends Renderer<Paragraph> {

		private ParagraphView mParagraphView;

		ParagraphRenderer(@NonNull View itemView) {
			super(itemView);
		}

		@Override
		protected void onCreate(View view) {
			mParagraphView = (ParagraphView) itemView;
			mParagraphView.setOnTextSelectedListener(TexasAdapter.this);
		}

		@Override
		public void render(Paragraph data) {
			ParagraphSelection paragraphSelection = null;
			if (mCurrentSelection != null && mCurrentSelection.getParagraph() == data) {
				paragraphSelection = mCurrentSelection.getParagraphSelection();
			}

			mParagraphView.render(
					data,
					mTextPaint,
					mRenderOption,
					paragraphSelection,
					mMeasurer.getFontTopPadding(),
					mMeasurer.getFontBottomPadding());
		}
	}

	class FigureRenderer extends Renderer<Figure> {
		private FigureView mFigureView;

		FigureRenderer(View view) {
			super(view);
		}

		@Override
		protected void onCreate(View view) {
			mFigureView = findViewById(R.id.image);
		}

		@Override
		public void render(Figure figure) {
			mFigureView.render(mImageLoader, figure);
		}
	}

	class ViewSegmentRenderer extends Renderer<ViewSegment> {

		private FrameLayout mRootView;

		ViewSegmentRenderer(@NonNull View itemView) {
			super(itemView);
		}

		@Override
		protected void onCreate(View view) {
			mRootView = (FrameLayout) view;
		}

		@Override
		public void render(final ViewSegment data) {
			data.attach(mLayoutInflater, mRootView);
			data.render();
		}
	}


	private abstract static class SelectedVisitor extends ParagraphVisitor {

		private TextParagraphSelection mSelection;
		private RectF mRectF;
		private float mLastLineBottom;
		private float mLastLineTop;
		private float mTopEdgeOnScreen = -1;
		private float mBottomEdgeOnScreen = -1;
		private boolean mHasContent = false;

		private float mLastYOnScreen;
		private float mLastYInView;
		boolean mIsLongClicked;
		private float mWidth;

		public void setWidth(float width) {
			mWidth = width;
		}

		public void setLongClicked(boolean longClicked) {
			mIsLongClicked = longClicked;
		}

		public void setLastYInView(float lastYInView) {
			mLastYInView = lastYInView;
		}

		public void setLastYOnScreen(float lastYOnScreen) {
			mLastYOnScreen = lastYOnScreen;
		}

		@Override
		public void onVisitParagraph(Paragraph paragraph) {
			mSelection = new TextParagraphSelection(paragraph, mIsLongClicked, mLastYInView, mLastYOnScreen, mWidth);
		}

		@Override
		public void onVisitParagraphEnd(Paragraph paragraph) {
			if (mSelection != null) {
				mSelection.setTopEdgeOnScreen(mTopEdgeOnScreen);
				mSelection.setBottomEdgeOnScreen(mBottomEdgeOnScreen);
			}
		}

		public void clear() {
			mSelection = null;
			mIsLongClicked = false;
			mLastLineBottom = mLastLineTop = -1;
			mRectF = null;
			mTopEdgeOnScreen = -1;
			mBottomEdgeOnScreen = -1;
			mHasContent = false;
		}

		public TextParagraphSelection getSelection() {
			return mSelection;
		}

		@Override
		public void onVisitLine(Line line, float bottomX, float bottomY) {
			mLastLineBottom = bottomY;
			mLastLineTop = bottomY - line.getLineHeight();
			mHasContent = false;

			if (mSelection.isEmpty()) {
				mTopEdgeOnScreen = mLastYOnScreen - (mLastYInView - mLastLineTop);
			}
		}

		@Override
		public void onVisitLineEnd(Line line, float x, float y) {
			if (mRectF != null) {
				mSelection.addSelectArea(mRectF);
				mRectF = null;
			}

			if (mHasContent) {
				mBottomEdgeOnScreen = mLastLineBottom - mLastYInView + mLastYOnScreen;
			}
		}

		@Override
		public void onVisitBox(Box box, float left, float top, float right, float bottom) {
			if (selected(box)) {
				if (mRectF == null) {
					mRectF = new RectF(left, mLastLineTop, right, mLastLineBottom);
				}

				mHasContent = true;
				mRectF.right = right;
				mSelection.addBox(box);
				box.setSelected(true);
			} else {
				if (mRectF != null) {
					mSelection.addSelectArea(mRectF);
					mRectF = null;
				}

				box.setSelected(false);
			}
		}

		abstract boolean selected(Box box);
	}

	private class SelectionImpl extends Selection {

		SelectionImpl(ParagraphSelection paragraphSelection) {
			super(paragraphSelection);
		}

		@Override
		void onSelectedByTags(@NonNull ParagraphSelection paragraphSelection, @NonNull List<?> tags) {
			handleSelectedParagraphByTags(paragraphSelection, tags);
		}

		@Override
		void onClear(Paragraph paragraph) {
			notifySegmentChanged(paragraph);
			mCurrentSelection = null;
		}
	}

	private SelectedTextByTagVisitor mSelectedTextByTagVisitor = new SelectedTextByTagVisitor();

	private static class SelectedTextByTagVisitor extends SelectedVisitor {

		private List<?> mTags;

		void setTags(List<?> tags) {
			mTags = tags;
		}

		@Override
		boolean selected(Box box) {
			if (!(box instanceof TextBox)) {
				return false;
			}

			TextBox textBox = (TextBox) box;
			Object rhs = textBox.getTag();
			if (rhs == null) {
				return false;
			}

			for (Object lhs : mTags) {
				if (lhs == null) {
					continue;
				}

				if (lhs.equals(rhs)) {
					return true;
				}
			}

			return false;
		}
	}

	private SelectedTextByListenerVisitor mSelectedTextByListenerVisitor = new SelectedTextByListenerVisitor();

	private static class SelectedTextByListenerVisitor extends SelectedVisitor {

		private OnClickedListener mOnClickedListener;

		public void setOnClickedListener(OnClickedListener onClickedListener) {
			mOnClickedListener = onClickedListener;
		}

		@Override
		boolean selected(Box box) {
			return mOnClickedListener == ParagraphView.getBoxOnClickedListener(box, mIsLongClicked);
		}
	}

	private static void d(String msg) {
		Log.d("TexasAdapter", msg);
	}
}