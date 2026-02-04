package me.chan.texas.renderer.ui;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.annotation.SuppressLint;
import android.content.Context;

import me.chan.texas.misc.Rect;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.R;
import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.di.TexasComponent;
import me.chan.texas.di.core.TextEngineCoreComponent;
import me.chan.texas.image.ImageLoader;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.worker.MixWorker;
import me.chan.texas.renderer.selection.SelectionManager;
import me.chan.texas.renderer.ui.figure.FigureView;
import me.chan.texas.renderer.ui.rv.SegmentItemFragmentLayout;
import me.chan.texas.renderer.ui.rv.TexasRecyclerViewImpl;
import me.chan.texas.renderer.ui.text.ParagraphView;
import me.chan.texas.renderer.ui.text.TextureParagraph;
import me.chan.texas.renderer.ui.text.TextureParagraphView0;
import me.chan.texas.renderer.ui.text.TextureParagraphView0Compat;
import me.chan.texas.text.Document;
import me.chan.texas.text.Figure;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.SelectableSegment;
import me.chan.texas.text.ViewSegment;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.utils.concurrency.Worker;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * 点击事件通知约定：
 * 发生一次点击作为一次事务，之后除非发生另外一次点击，后面所有的高亮都算作这一次事务附带的事件
 * 每一次点击需要清除上一次的记录，因此通过点击发生selection后，重新高亮不会触发点击事件的回调
 */
@RestrictTo(LIBRARY)
public class RendererAdapterImpl extends RecyclerView.Adapter<RendererAdapterImpl.Renderer> implements TexasRendererAdapter {
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

	private final TexasRecyclerViewImpl mView;

	private Document mDocument;
	private final LayoutInflater mLayoutInflater;
	private final ImageLoader mImageLoader;
	private RenderOption mRenderOption;

	/**
	 * 选中效果属于编辑器内部的状态了，所有直接由adapter管理而不需要通知外部组件
	 */
	private final ViewSegmentManager mViewSegmentManager = new ViewSegmentManager();
	private SelectionManager mSelectionManager;
	private PaintSet mPaintSet;
	private final RecyclerView.RecycledViewPool mPool;

	private Listener mListener;

	// handler需要设置线程可见性，这样一旦释放了handler，工作线程能立马看到
	// 滞后的消息就不会发到主线程
	@Inject
	@Named("BackgroundWorker")
	Worker mBackgroundWorker;

	private final Worker.Token mToken;

	public RendererAdapterImpl(
			Worker.Token token,
			LayoutInflater layoutInflater,
			ImageLoader imageLoader,
			RecyclerView.RecycledViewPool pool,
			TexasRecyclerViewImpl view) {
		mToken = token;
		mLayoutInflater = layoutInflater;
		mImageLoader = imageLoader;
		mPool = pool;
		mView = view;

		// 前提是Document没有变化
		setHasStableIds(true);

		TexasComponent texasComponent = Texas.getTexasComponent();
		TextEngineCoreComponent textEngineCoreComponent = texasComponent.coreComponent().create();
		textEngineCoreComponent.inject(this);
	}

	public void setListener(Listener listener) {
		mListener = listener;
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
		holder.itemView.setTag(R.id.me_chan_texas_item_tag, null);
	}

	private final SparseArrayCompat<View> mSingletonViewCache = new SparseArrayCompat<>();

	private Renderer createViewSegment(int type) {
		SegmentItemFragmentLayout root = new SegmentItemFragmentLayout(mView);
		boolean disableReuseType = isDisableReuseType(type);
		View content = null;
		if (disableReuseType) {
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
			if (disableReuseType) {
				mSingletonViewCache.put(type, content);
				mPool.setMaxRecycledViews(type, 0);
			}
		}

		root.addView(content);
		return new ViewSegmentRenderer(root);
	}

	@VisibleForTesting
	static boolean isDisableReuseType(int type) {
		return type > 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onBindViewHolder(@NonNull Renderer renderer, int position) {
		onBindViewHolder0(renderer, position);
	}

	private void onBindViewHolder0(@NonNull Renderer renderer, int position) {
		Segment segment = getItem(position);
		renderer.itemView.setTag(R.id.me_chan_texas_item_tag, segment);
		updateSegmentFast(renderer, segment);
	}

	private static final Object SIG_UPDATE_SEGMENT = new Object();

	@Override
	public void updateSegment(Object unit, Segment segment) {
		if (segment == null) {
			w("segment is null, ignore updateSegment");
			return;
		}

		if (updateSegmentFast(unit, segment)) {
			return;
		}

		int index = segment.getIndex();
		if (index >= 0 && index < getItemCount()) {
			notifyItemChanged(index, SIG_UPDATE_SEGMENT);
		}
	}

	private boolean updateSegmentFast(Object unit, Segment segment) {
		if (unit == null) {
			w("unit is null, ignore updateSegmentFast");
			return false;
		}

		RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) unit;
		Segment expected = (Segment) holder.itemView.getTag(R.id.me_chan_texas_item_tag);
		if (expected != segment) {
			w("holder information not match, ignore updateSegmentFast");
			return false;
		}

		Renderer renderer = (Renderer) holder;
		renderer.render(segment);
		return true;
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
			return mViewSegmentManager.getType(viewSegment);
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
		Segment segment = getItem(position);
		if (segment == null) {
			throw new IllegalStateException("segment is null");
		}
		return segment.getId();
	}

	@Override
	public void onViewAttachedToWindow(@NonNull Renderer holder) {
		Segment segment = getItem(holder.getAdapterPosition());
		if (segment != null) {
			segment.attachToWindow(holder);
		}
	}

	@Override
	public void onViewDetachedFromWindow(@NonNull Renderer holder) {
		Segment segment = getItem(holder.getAdapterPosition());
		if (segment != null) {
			segment.detachFromWindow(holder);
		}
	}

	@SuppressLint("NotifyDataSetChanged")
	public void clear(String reason) {
		d("clear: " + reason);
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
	public void render(MixWorker.TypesetResult result) {
		d("render");
		mView.stopScroll();
		TexasOption option = result.texasOption;
		mPaintSet = option.getPaintSet();
		mRenderOption = option.getRenderOption();
		Document prev = mDocument;
		Document document = result.doc;
		mDocument = document;
		for (int i = 0; i < document.getSegmentCount(); i++) {
			Segment segment = document.getSegment(i);
			segment.bind(this);
		}

		if (prev == result.base && result.diff != null) {
			result.diff.dispatchUpdatesTo(this);
			return;
		}

		notifyDataSetChanged();
	}

	public void updateRenderOption(RenderOption renderOption) {
		d("update");
		if (mPaintSet == null) {
			w("ignore refresh");
			return;
		}

		mPaintSet.refresh(renderOption);
		mRenderOption = renderOption;

		if (mDocument != null) {
			for (int i = 0; i < mDocument.getSegmentCount(); i++) {
				Segment segment = mDocument.getSegment(i);
				if (segment instanceof Paragraph) {
					Paragraph paragraph = (Paragraph) segment;
					Layout layout = paragraph.getLayout();
					Layout.Advise advise = layout.getAdvise();
					advise.copy(renderOption);
				}
			}
		}
	}

	@SuppressLint("NotifyDataSetChanged")
	public void release() {
		mView.stopScroll();
		Document prev = mDocument;
		mDocument = null;

		if (prev != null) {
			WorkerScheduler.odd().submit(mToken, mBackgroundWorker, prev::release);
		}
	}

	public RenderOption getRenderOption() {
		return mRenderOption;
	}

	public Document getDocument() {
		return mDocument;
	}

	public int indexOf(Segment segment) {
		if (mDocument == null) {
			return -1;
		}

		return mDocument.indexOfSegment(segment);
	}

	public void redraw(int start, int end) {
		while (start < end) {
			Segment segment = getItem(start);
			if (segment != null) {
				segment.requestRedraw();
			}
			start++;
		}
	}

	abstract class Renderer<T extends Segment> extends RecyclerView.ViewHolder {

		Renderer(@NonNull View view) {
			super(view);
			view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

			if (view instanceof SegmentItemFragmentLayout) {
				SegmentItemFragmentLayout layout = (SegmentItemFragmentLayout) view;
				layout.setOnClickedListener(new SegmentItemFragmentLayout.OnClickedListener() {
					@Override
					public void onClicked(TouchEvent event) {
						if (mListener == null) {
							return;
						}

						int position = getAdapterPosition();
						if (position < 0 || position >= getItemCount()) {
							return;
						}

						Segment segment = getItem(position);
						if (segment == null) {
							return;
						}

						mListener.onSegmentClicked(event, segment.getTag());
					}

					@Override
					public void onDoubleClicked(TouchEvent event) {
						if (mListener == null) {
							return;
						}

						int position = getAdapterPosition();
						if (position < 0 || position >= getItemCount()) {
							return;
						}

						Segment segment = getItem(position);
						if (segment == null) {
							return;
						}

						mListener.onSegmentDoubleClicked(event, segment.getTag());
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
				mRender.setOnTextSelectedListener(mSelectionManager.getOnSelectedChangedListener());
			}
		}

		@Override
		protected void onRender(Paragraph data) {
			mRender.render(
					data,
					mPaintSet,
					mRenderOption,
					mSelectionManager.getSpanTouchEventHandler());
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

		ViewSegmentRenderer(@NonNull SegmentItemFragmentLayout root) {
			super(root);
			root.setDisableDecoration(true);
		}

		@Override
		protected void onCreate(View view) {
			/* do nothing */
		}

		@Override
		protected void onRender(final ViewSegment data) {
			SegmentItemFragmentLayout layout = (SegmentItemFragmentLayout) itemView;

			View content = layout.getContent();
			data.render(content);

			// 当内容被设置为GONE后，当前的item还是会在rv里占用一个格子，这会导致界面上出现大片空白
			// 因此当发现内容为gone要把当前item高度设置为0
			if (content.getVisibility() == View.GONE) {
				layout.setPadding(0, 0, 0, 0);
			} else {
				Rect rect = data.getRect();
				assert rect != null;
				layout.setPadding(rect.left, rect.top, rect.right, rect.bottom);
			}

			if (data instanceof SelectableSegment) {
				SelectableSegment selectableSegment = (SelectableSegment) data;
				for (int i = 0; i < selectableSegment.getParagraphCount(); i++) {
					ParagraphView paragraphView = selectableSegment.getParagraphView(i);
					if (paragraphView != null) {
						paragraphView.setSpanTouchEventHandler(mSelectionManager.getSpanTouchEventHandler());
						paragraphView.setOnSelectedChangedListener(mSelectionManager.getOnSelectedChangedListener());
					}

					Paragraph paragraph = selectableSegment.getParagraph(i);
					if (paragraph != null) {
						paragraph.setTag(R.id.me_chan_texas_paragraph_selection_tag, selectableSegment);
					}
				}
			}
		}
	}

	@RestrictTo(LIBRARY)
	public static class ViewSegmentManager {
		/**
		 * view segment type计算算法：
		 * {@link ViewSegment#ViewSegment(int)} 接受R.layout作为构造函数参数
		 * R.layout是唯一的。当检测到R.layout没有创建过type，就通atomic int自增一来产生一个唯一的type
		 * 这个自增的id从1开始 {@link RendererAdapterImpl#getItemViewType(int)}
		 * 而内部保留id则是小于0的 {@link RendererAdapterImpl#TYPE_FIGURE} etc.
		 */
		private final SparseArrayCompat<Integer> mTypeBuffer = new SparseArrayCompat<>(4);
		private final SparseArrayCompat<Integer> mLayoutBuffer = new SparseArrayCompat<>(4);
		private final AtomicInteger mViewUUID = new AtomicInteger(UNREUSABLE_TYPE_START);

		public int getType(ViewSegment viewSegment) {
			return getType(viewSegment, viewSegment.isDisableReuse());
		}

		public int getType(ViewSegment viewSegment, boolean disableReuse) {
			return disableReuse ? getUniqueType(viewSegment) : getReusableType(viewSegment);
		}

		private int getReusableType(ViewSegment viewSegment) {
			int layout = viewSegment.getLayout();
			Integer t = mTypeBuffer.get(layout);
			if (t != null) {
				return t;
			}

			int type = mViewUUID.decrementAndGet();
			if (type >= UNREUSABLE_TYPE_START) {
				throw new IllegalStateException("view segment type must be less than " + UNREUSABLE_TYPE_START);
			}

			mTypeBuffer.put(layout, type);
			mLayoutBuffer.put(type, layout);
			return type;
		}

		private int getUniqueType(ViewSegment viewSegment) {
			int type = viewSegment.getId();
			if (type <= 0) {
				throw new IllegalStateException("view segment id must be greater than 0, check Segment#nextId()");
			}

			int layout = viewSegment.getLayout();
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
		void onSegmentClicked(TouchEvent event, Object tag);

		void onSegmentDoubleClicked(TouchEvent event, Object tag);
	}

	public int sendSignal(Segment segment, Object signal) {
		int index = indexOf(segment);
		return sendSignal(index, signal);
	}

	public int sendSignal(int index, Object signal) {
		if (index >= 0) {
			notifyItemChanged(index, signal);
		}
		return index;
	}

	@Override
	public void onBindViewHolder(@NonNull Renderer renderer, int position, @NonNull List<Object> payloads) {
		onBindViewHolder0(renderer, position);
	}
}
