package me.chan.texas.renderer.ui;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.BuildConfig;
import me.chan.texas.image.ImageLoader;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.LoadingStrategy;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.highlight.HighlightManager;
import me.chan.texas.renderer.highlight.ParagraphHighlight;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.selection.SelectionManager;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.renderer.ui.figure.FigureView;
import me.chan.texas.renderer.ui.rv.SegmentItemFragmentLayout;
import me.chan.texas.renderer.ui.rv.TexasRecyclerView;
import me.chan.texas.renderer.ui.text.TextureParagraph;
import me.chan.texas.renderer.ui.text.TextureParagraphView0;
import me.chan.texas.renderer.ui.text.TextureParagraphView0Compat;
import me.chan.texas.text.Document;
import me.chan.texas.text.Figure;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.ViewSegment;
import me.chan.texas.text.layout.Layout;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 点击事件通知约定：
 * 发生一次点击作为一次事务，之后除非发生另外一次点击，后面所有的高亮都算作这一次事务附带的事件
 * 每一次点击需要清除上一次的记录，因此通过点击发生selection后，重新高亮不会触发点击事件的回调
 */
@RestrictTo(LIBRARY)
public class RendererAdapter extends RecyclerView.Adapter<RendererAdapter.Renderer> {
	private static final boolean DEBUG = false;

	// 内部自定义的type必须小于0
	private static final int TYPE_PARAGRAPH = -1;
	private static final int TYPE_FIGURE = -2;
	private static final int TYPE_PARAGRAPH_COMPAT = -3;
	public static final int UNREUSABLE_TYPE_START = -4;

	static {
		// runtime check
		if (TYPE_PARAGRAPH >= 0 || TYPE_FIGURE >= 0 || TYPE_PARAGRAPH_COMPAT >= 0) {
			throw new IllegalStateException("internal view type must be less than 0");
		}
	}

	private final TexasRecyclerView mView;

	private Document mDocument;
	private final LayoutInflater mLayoutInflater;
	private final ImageLoader mImageLoader;
	private RenderOption mRenderOption;

	/**
	 * 选中效果属于编辑器内部的状态了，所有直接由adapter管理而不需要通知外部组件
	 */
	private final ViewSegmentManager mViewSegmentManager = new ViewSegmentManager();
	private SelectionManager mSelectionManager;
	private HighlightManager mHighlightManager;
	private PaintSet mPaintSet;
	private final RecyclerView.RecycledViewPool mPool;
	private ParagraphDecor mParagraphDecor;

	private final SparseArrayCompat<TextureParagraph> mTextureParagraphRecord = new SparseArrayCompat<>();
	private Listener mListener;

	public RendererAdapter(LayoutInflater layoutInflater,
						   ImageLoader imageLoader,
						   RecyclerView.RecycledViewPool pool,
						   TexasRecyclerView view) {
		mLayoutInflater = layoutInflater;
		mImageLoader = imageLoader;
		mPool = pool;
		mView = view;

		// 前提是Document没有变化
		setHasStableIds(true);
	}

	public void setListener(Listener listener) {
		mListener = listener;
	}

	public void setHighlightManager(HighlightManager highlightManager) {
		mHighlightManager = highlightManager;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		mSelectionManager = selectionManager;
	}

	@NonNull
	@Override
	public Renderer onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
		Context context = viewGroup.getContext();
		if (type == TYPE_PARAGRAPH) {
			return new ParagraphRenderer(new TextureParagraphView0(context));
		} else if (type == TYPE_PARAGRAPH_COMPAT) {
			return new ParagraphRenderer(new TextureParagraphView0Compat(context));
		} else if (type == TYPE_FIGURE) {
			FigureView figureView = new FigureView(context);
			figureView.setAdjustViewBounds(true);
			figureView.setScaleType(ImageView.ScaleType.FIT_XY);
			SegmentItemFragmentLayout root = new SegmentItemFragmentLayout(mView);
			root.addView(figureView);
			return new FigureRenderer(root);
		} else {
			return createViewSegment(type);
		}
	}

	@Override
	public void onViewRecycled(@NonNull Renderer holder) {
		if (holder instanceof ParagraphRenderer) {
			ParagraphRenderer paragraphRenderer = (ParagraphRenderer) holder;
			mTextureParagraphRecord.remove(paragraphRenderer.mRender.getToken().getId());
			paragraphRenderer.mRender.clear();
			if (DEBUG) {
				d("onViewRecycled: " + paragraphRenderer.mRender.getToken());
			}
		}
	}

	private Renderer createViewSegment(int type) {
		SegmentItemFragmentLayout root = new SegmentItemFragmentLayout(mView);
		boolean incrementalUpdateView = isIncrementalUpdateView(type);
		View content = null;
		if (incrementalUpdateView) {
			content = mSingletonViewCache.get(type);
			ViewGroup viewGroup = null;
			if (content != null) {
				if ((viewGroup = (ViewGroup) content.getParent()) != null) {
					viewGroup.removeView(content);
				}
			}
		}

		if (content == null) {
			content = mLayoutInflater.inflate(mViewSegmentManager.getLayout(type), root, false);
			if (incrementalUpdateView) {
				mSingletonViewCache.put(type, content);
				mPool.setMaxRecycledViews(type, 0);
			}
		}

		root.addView(content);
		return new ViewSegmentRenderer(root);
	}

	private boolean isIncrementalUpdateView(int type) {
		return type <= UNREUSABLE_TYPE_START;
	}

	private final SparseArrayCompat<View> mSingletonViewCache = new SparseArrayCompat<>();

	@Override
	@SuppressWarnings("unchecked")
	public void onBindViewHolder(@NonNull Renderer renderer, int position) {
		if (position == getItemCount() - 1) {
			mListener.onLoadingMore(position);
		} else if (position == 0) {
			mListener.onLoadingPrevious();
		}

		Segment segment = getItem(position);
		if (segment == null) {
			w("segment is null, ignore onBindViewHolder");
			return;
		}

		renderer.render(segment);
	}

	@Override
	public int getItemViewType(int position) {
		Segment content = getItem(position);
		if (content == null) {
			w("segment is null, ignore getItemViewType");
			return TYPE_PARAGRAPH;
		}

		if (content instanceof Paragraph) {
			return mRenderOption.isCompatMode() ? TYPE_PARAGRAPH_COMPAT : TYPE_PARAGRAPH;
		} else if (content instanceof Figure) {
			return TYPE_FIGURE;
		} else if (content instanceof ViewSegment) {
			ViewSegment viewSegment = (ViewSegment) content;
			boolean isIncremental = viewSegment.isIncremental();
			int type = mViewSegmentManager.getType(viewSegment.getLayout(), position, isIncremental);
			return type;
		}

		throw new RuntimeException("unknown segment type");
	}

	@Nullable
	public Segment getItem(int position) {
		if (mDocument == null) {
			return null;
		}

		try {
			return mDocument.getSegment(position);
		} catch (Throwable throwable) {
			w(throwable);
		}
		return null;
	}

	@Override
	public int getItemCount() {
		return mDocument == null ? 0 : mDocument.getSegmentCount();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NotifyDataSetChanged")
	public void clear() {
		d("clear");
		mView.stopScroll();

		int count = 0;
		if (mDocument != null) {
			count = mDocument.getSegmentCount();
		}

		mDocument = null;
		if (count <= 0) {
			return;
		}

		try {
			notifyItemRangeRemoved(0, count);
		} catch (Throwable ignore) {
			/* ignore */
		}
	}

	@SuppressLint("NotifyDataSetChanged")
	public void render(Document document,
					   PaintSet context,
					   RenderOption renderOption) {
		d("render");
		mView.stopScroll();
		mDocument = document;
		mPaintSet = context;
		mRenderOption = renderOption;
		notifyDataSetChanged();
	}

	@SuppressLint("NotifyDataSetChanged")
	public void render(LoadingStrategy strategy,
					   int start,
					   int end) {
		d("render");
		mView.stopScroll();
		if (strategy == LoadingStrategy.LOAD_PREVIOUS) {
			notifyItemRangeInserted(0, end - start);
		} else if (strategy == LoadingStrategy.LOAD_MORE) {
			notifyItemRangeInserted(start, end - start);
		} else {
			throw new IllegalArgumentException("unknown loading strategy");
		}
	}

	public void updateRenderOption(RenderOption renderOption) {
		d("update");
		if (mPaintSet == null) {
			w("ignore refresh");
			return;
		}

		float expectedLineSpace = renderOption.getLineSpace();
		boolean isLineSpaceChanged = mRenderOption != null && expectedLineSpace != mRenderOption.getLineSpace();

		mPaintSet.refresh(renderOption);
		mRenderOption = renderOption;

		if (isLineSpaceChanged && mDocument != null) {
			for (int i = 0; i < mDocument.getSegmentCount(); i++) {
				Segment segment = mDocument.getSegment(i);
				if (segment instanceof Paragraph) {
					Paragraph paragraph = (Paragraph) segment;
					Layout layout = paragraph.getLayout();
					float lineSpace = layout.getAdvise().getLineSpace();
					if (lineSpace < 0) {
						layout.setLineSpace(expectedLineSpace);
					}
				}
			}
		}
	}

	@SuppressLint("NotifyDataSetChanged")
	public void release() {
		mView.stopScroll();
		mDocument = null;

		try {
			int count = mTextureParagraphRecord.size();
			for (int i = 0; i < count; i++) {
				TextureParagraph textureParagraph = mTextureParagraphRecord.valueAt(i);
				textureParagraph.clear();
				if (DEBUG) {
					d("release texture paragraph record " + textureParagraph.getToken());
				}
			}
		} catch (Throwable throwable) {
			if (BuildConfig.DEBUG) {
				throw new RuntimeException(throwable);
			}
		}
	}

	public RenderOption getRenderOption() {
		return mRenderOption;
	}

	public Document getDocument() {
		return mDocument;
	}

	public void setParagraphDecor(ParagraphDecor decor) {
		mParagraphDecor = decor;
	}

	abstract class Renderer<T extends Segment> extends RecyclerView.ViewHolder {

		private float mX, mY;

		Renderer(@NonNull View view) {
			super(view);
			view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

			if (view instanceof SegmentItemFragmentLayout) {
				SegmentItemFragmentLayout layout = (SegmentItemFragmentLayout) view;
				layout.setOnClickedListener(new SegmentItemFragmentLayout.OnClickedListener() {
					@Override
					public void onClicked(float x, float y) {
						if (mListener == null) {
							return;
						}

						int position = getAdapterPosition();
						if (position < 0 || position >= getItemCount()) {
							return;
						}

						Segment segment = getItem(position);
						mListener.onSegmentClicked(x, y, segment.getTag());
					}

					@Override
					public void onDoubleClicked(float x, float y) {
						if (mListener == null) {
							return;
						}

						int position = getAdapterPosition();
						if (position < 0 || position >= getItemCount()) {
							return;
						}

						Segment segment = getItem(position);
						mListener.onSegmentDoubleClicked(x, y, segment.getTag());
					}
				});
				view = layout.getChildAt(0);
			}

			onCreate(view);
		}

		protected abstract void onCreate(View view);

		public final void render(T data) {
			onRender(data);
		}

		protected abstract void onRender(T data);

		protected Context getContext() {
			return itemView.getContext();
		}

		protected final <T extends View> T findViewById(@IdRes int id) {
			return itemView.findViewById(id);
		}
	}

	private class ParagraphRenderer extends Renderer<Paragraph> {

		private TextureParagraph mRender;

		ParagraphRenderer(@NonNull View root) {
			super(root);
		}

		@Override
		protected void onCreate(View view) {
			mRender = (TextureParagraph) view;
			if (mRender != null) {
				mRender.setOnTextSelectedListener(mSelectionManager.getOnTextSelectedListener());
			}
		}

		@Override
		protected void onRender(Paragraph data) {
			ParagraphSelection paragraphSelection = mSelectionManager.getParagraphSelection(data);
			ParagraphHighlight highlight = mHighlightManager.getParagraphHighlight(data);

			mRender.render(
					data,
					mPaintSet,
					mRenderOption,
					paragraphSelection,
					highlight,
					mParagraphDecor);

			mTextureParagraphRecord.put(mRender.getToken().getId(), mRender);
			if (DEBUG) {
				d("onCreateViewHolder: " + mRender.getToken());
			}
		}
	}

	class FigureRenderer extends Renderer<Figure> {
		private FigureView mFigureView;

		FigureRenderer(View root) {
			super(root);
		}

		@Override
		protected void onCreate(View view) {
			mFigureView = (FigureView) view;
		}

		@Override
		protected void onRender(Figure figure) {
			mFigureView.render(mImageLoader, figure);
		}
	}

	class ViewSegmentRenderer extends Renderer<ViewSegment> {

		ViewSegmentRenderer(@NonNull View root) {
			super(root);
		}

		@Override
		protected void onCreate(View view) {
			/* do nothing */
		}

		@Override
		protected void onRender(final ViewSegment data) {
			data.render(itemView);

			// 当内容被设置为GONE后，当前的item还是会在rv里占用一个格子，这会导致界面上出现大片空白
			// 因此当发现内容为gone要把当前item高度设置为0
			ViewGroup.LayoutParams params = itemView.getLayoutParams();
			if (params == null) {
				return;
			}

			params.height = itemView.getVisibility() == View.GONE ? 0 : ViewGroup.LayoutParams.WRAP_CONTENT;
		}
	}

	@RestrictTo(LIBRARY)
	public static class ViewSegmentManager {
		/**
		 * view segment type计算算法：
		 * {@link ViewSegment#ViewSegment(int)} 接受R.layout作为构造函数参数
		 * R.layout是唯一的。当检测到R.layout没有创建过type，就通atomic int自增一来产生一个唯一的type
		 * 这个自增的id从1开始 {@link RendererAdapter#getItemViewType(int)}
		 * 而内部保留id则是小于0的 {@link RendererAdapter#TYPE_FIGURE} etc.
		 */
		private final SparseArrayCompat<Integer> mTypeBuffer = new SparseArrayCompat<>(4);
		private final SparseArrayCompat<Integer> mLayoutBuffer = new SparseArrayCompat<>(4);
		private final AtomicInteger mViewUUID = new AtomicInteger(0);

		public int getType(int layout, int position, boolean incremental) {
			return incremental ? getIncrementalType(position, layout) : getNonIncrementalType(layout);
		}

		private int getNonIncrementalType(int layout) {
			Integer t = mTypeBuffer.get(layout);
			if (t != null) {
				return t;
			}

			int type = mViewUUID.incrementAndGet();
			mTypeBuffer.put(layout, type);
			mLayoutBuffer.put(type, layout);
			return type;
		}

		private int getIncrementalType(int position, int layout) {
			int type = -position + UNREUSABLE_TYPE_START;
			Integer prevValue = mLayoutBuffer.get(type);
			if (prevValue != null && prevValue != layout) {
				throw new IllegalStateException("illegal state");
			}
			if (prevValue == null) {
				mLayoutBuffer.put(type, layout);
			}
			return type;
		}

		public int getLayout(int type) {
			Integer layout = mLayoutBuffer.get(type);
			if (layout == null) {
				throw new IllegalStateException("can not get type: " + type + "'s layout");
			}
			return layout;
		}
	}

	private static void d(String msg) {
		Log.d("TexasAdapter", msg);
	}

	private static void w(Throwable throwable) {
		Log.w("TexasAdapter", throwable);
	}

	private static void w(String msg) {
		Log.w("TexasAdapter", msg);
	}

	/**
	 * {@link  me.chan.texas.renderer.ui.text.ParagraphViewMotion}
	 * 处理paragraph的事件
	 * 其它由 {@link SegmentItemFragmentLayout}
	 */
	public interface Listener {
		void onSegmentClicked(float x, float y, Object tag);

		void onSegmentDoubleClicked(float x, float y, Object tag);

		void onLoadingMore(int count);

		void onLoadingPrevious();
	}
}
