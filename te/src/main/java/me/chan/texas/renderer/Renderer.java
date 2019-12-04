package me.chan.texas.renderer;

import android.content.Context;

import androidx.annotation.Nullable;

import android.text.TextPaint;
import android.view.LayoutInflater;

import me.chan.texas.image.ImageLoader;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.parser.Parser;
import me.chan.texas.source.Source;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;

public abstract class Renderer {
	private RenderOption mRenderOption;
	private TextEngineCore mTextEngineCore;

	private ImageLoader mImageLoader;
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	private Listener mListener;

	public Renderer(Context context, RenderOption renderOption) {
		mContext = context;
		mRenderOption = renderOption;
		mImageLoader = new ImageLoader(context);
		mLayoutInflater = LayoutInflater.from(context);
		mTextEngineCore = new TextEngineCore(this, mRenderOption);
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	public LayoutInflater getLayoutInflater() {
		return mLayoutInflater;
	}

	public Context getContext() {
		return mContext;
	}

	void start() {
		onStart();
		if (mListener != null) {
			mListener.onStart();
		}
	}

	protected abstract void onStart();

	void render(Document document, Measurer measurer) {
		onRenderer(document, measurer);
		if (mListener != null) {
			mListener.onRenderer();
		}
	}

	protected abstract void onRenderer(Document document, Measurer measurer);

	public void error(Throwable throwable) {
		onError(throwable);
		if (mListener != null) {
			mListener.onError(throwable);
		}
	}

	protected abstract void onError(Throwable throwable);

	public void release() {
		mTextEngineCore.release();
		mTextEngineCore = null;
	}

	protected RenderOption getRenderOption() {
		return mRenderOption;
	}

	public RenderOption createRendererOption() {
		return new RenderOption(mRenderOption);
	}

	public void refresh(RenderOption renderOption) {
		boolean needReload = checkIfReload(renderOption);
		mRenderOption = renderOption;
		// 如果需要排版那么需要通知底层重新排版
		if (needReload) {
			mTextEngineCore.reload(mRenderOption);
			return;
		}
		onRefresh(renderOption);
	}

	protected abstract void onRefresh(RenderOption renderOption);

	/**
	 * 1. 字体变化了需要重新reload
	 * 2. 字体大小变化了需要重新reload
	 * 3. 开启首行缩进
	 * 4. 更改了断字策略
	 * 5. 渲染模式发生改变
	 * 6. 断字策略发生变化
	 *
	 * @param renderOption 下一个渲染参数
	 * @return 是否需要重新reload
	 */
	private boolean checkIfReload(RenderOption renderOption) {
		if (!renderOption.getTypeface().equals(mRenderOption.getTypeface())) {
			return true;
		}

		if (renderOption.getTextSize() != mRenderOption.getTextSize()) {
			return true;
		}

		if (renderOption.isIndentEnable() != mRenderOption.isIndentEnable()) {
			return true;
		}

		if (renderOption.getBreakStrategy() != mRenderOption.getBreakStrategy()) {
			return true;
		}

		if (renderOption.getHyphenStrategy() != mRenderOption.getHyphenStrategy()) {
			return true;
		}

		return false;
	}

	/**
	 * @param source 源
	 * @param width  期望的宽度
	 */
	public void render(final Source source, int width) {
		mTextEngineCore.typeset(source, width);
	}

	public void setParser(Parser<?> parser) {
		mTextEngineCore.setParser(parser);
	}

	public TextPaint getTextPaint() {
		return mTextEngineCore.getTextPaint();
	}

	public void clearSelection() {
		Document document = mTextEngineCore.getDocument();
		if (document == null) {
			return;
		}

		int segmentCount = document.getSegmentCount();
		for (int segmentIndex = 0; segmentIndex < segmentCount; ++segmentCount) {
			Segment segment = document.getSegment(segmentIndex);
			if (!(segment instanceof Paragraph)) {
				continue;
			}

			Paragraph paragraph = (Paragraph) segment;
			Selection selection = paragraph.getSelection();
			paragraph.setSelection(null);
			if (selection != null) {
				selection.clear();
			}
		}

		invalidate();
	}

	@Nullable
	protected Document getDocument() {
		if (mTextEngineCore == null) {
			return null;
		}
		return mTextEngineCore.getDocument();
	}

	public void setListener(Listener listener) {
		mListener = listener;
	}

	protected abstract void invalidate();

	public interface Listener {
		void onStart();

		void onRenderer();

		void onError(Throwable throwable);
	}
}
