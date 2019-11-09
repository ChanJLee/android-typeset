package me.chan.te.renderer;

import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;

import me.chan.te.image.ImageLoader;
import me.chan.te.parser.Parser;
import me.chan.te.source.Source;
import me.chan.te.text.Document;

public abstract class Renderer {
	private RenderOption mRenderOption;
	private TextEngineCore mTextEngineCore;

	private ImageLoader mImageLoader;
	private LayoutInflater mLayoutInflater;
	private Context mContext;

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

	void clear() {
		onClear();
	}

	protected abstract void onClear();

	void render(Document document) {
		onRenderer(document);
	}

	protected abstract void onRenderer(Document document);

	public void error(Throwable throwable) {
		onError(throwable);
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

		if (renderOption.getRendererMode() != mRenderOption.getRendererMode()) {
			return true;
		}

		return false;
	}

	/**
	 * @param source 源
	 * @param width  期望的宽度
	 * @param height 期望的高度
	 */
	public void render(final Source source, int width, int height) {
		if (mRenderOption.getRendererMode() == RendererMode.SLIDING) {
			height = Integer.MAX_VALUE;
		}
		mTextEngineCore.typeset(source, width, height);
	}

	public void setParser(Parser<?> parser) {
		mTextEngineCore.setParser(parser);
	}

	public TextPaint getTextPaint() {
		return mTextEngineCore.getTextPaint();
	}
}
