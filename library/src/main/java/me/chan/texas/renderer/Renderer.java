package me.chan.texas.renderer;

import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import me.chan.texas.annotations.Hidden;
import me.chan.texas.image.ImageLoader;
import me.chan.texas.log.Log;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.parser.Parser;
import me.chan.texas.source.Source;
import me.chan.texas.text.Document;

/**
 * 渲染器
 */
@Hidden
abstract class Renderer {
	private RenderOption mRenderOption;
	private TextEngineCore mTextEngineCore;

	private ImageLoader mImageLoader;
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	private TexasView mTexasView;

	Renderer(TexasView texasView, RenderOption renderOption) {
		mTexasView = texasView;
		mContext = texasView.getContext();
		mRenderOption = renderOption;
		mImageLoader = new ImageLoader(mContext);
		mLayoutInflater = LayoutInflater.from(mContext);
		mTextEngineCore = new TextEngineCore(this, mRenderOption);
	}

	ImageLoader getImageLoader() {
		return mImageLoader;
	}

	LayoutInflater getLayoutInflater() {
		return mLayoutInflater;
	}

	public Context getContext() {
		return mContext;
	}

	void start() {
		onStart();
		mTexasView.notifyRenderStart();
	}

	protected abstract void onStart();

	void render(Document document, Measurer measurer) {
		onRenderer(
				document,
				measurer,
				mTextEngineCore.getTextPaint(),
				mRenderOption
		);
		mTexasView.notifyRenderEnd();
	}

	protected abstract void onRenderer(Document document,
									   Measurer measurer,
									   TextPaint textPaint,
									   RenderOption renderOption);

	public void error(Throwable throwable) {
		w(throwable);
		onError(throwable);
		mTexasView.notifyRenderError(throwable);
	}

	protected abstract void onError(Throwable throwable);

	public void release() {
		mTextEngineCore.release();
		mTextEngineCore = null;
	}

	RenderOption createRendererOption() {
		return new RenderOption(mRenderOption);
	}

	void refresh(RenderOption renderOption) {
		d("refresh");
		boolean needReload = checkIfReload(renderOption);
		mRenderOption = renderOption;
		// 如果需要排版那么需要通知底层重新排版
		if (needReload) {
			d("refresh, but need to reload");
			mTextEngineCore.reload(mRenderOption);
			return;
		}
		onRefresh(mTextEngineCore.getTextPaint(), renderOption);
	}

	protected abstract void onRefresh(TextPaint textPaint, RenderOption renderOption);

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

	protected abstract void clearSelection();

	@Nullable
	protected Document getDocument() {
		if (mTextEngineCore == null) {
			w("get document, core is null");
			return null;
		}
		return mTextEngineCore.getDocument();
	}

	protected abstract void invalidate(int position);

	public abstract int getFirstVisibleSegmentIndex();

	private static void d(String msg) {
		Log.d("TexasRenderer", msg);
	}

	private static void w(String msg) {
		Log.w("TexasRenderer", msg);
	}

	private static void w(Throwable throwable) {
		Log.w("TexasRenderer", throwable);
	}

	abstract float getSelectedBottomEdge();

	abstract float getSelectedTopEdge();
}
