package me.chan.te.renderer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.TypedValue;

import java.lang.ref.WeakReference;

import me.chan.te.R;
import me.chan.te.parser.Parser;
import me.chan.te.source.Source;
import me.chan.te.text.BreakStrategy;
import me.chan.te.text.Document;

public abstract class Renderer {
	private static final int DEFAULT_TEXT_SIZE = 18;
	private static final int DEFAULT_LINE_SPACE = 12;
	private static final int DEFAULT_SEGMENT_SPACE = 28;
	private static WeakReference<Typeface> DEFAULT_TYPEFACE;

	private RenderOption mRenderOption;
	private TextEngineCore mTextEngineCore;

	public Renderer(Context context) {
		Resources resources = context.getResources();
		mRenderOption = new RenderOption();
		mRenderOption.setTextColor(ContextCompat.getColor(context, R.color.me_chan_te_text_color));

		Typeface typeface = null;
		if (DEFAULT_TYPEFACE != null && (typeface = DEFAULT_TYPEFACE.get()) != null) {
			mRenderOption.setTypeface(typeface);
		} else {
			typeface = Typeface.createFromAsset(context.getAssets(), "typeface/SourceSerifPro-Regular.ttf");
			DEFAULT_TYPEFACE = new WeakReference<>(typeface);
			mRenderOption.setTypeface(typeface);
		}

		mRenderOption.setTextSize(
				TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_SP,
						DEFAULT_TEXT_SIZE,
						resources.getDisplayMetrics()
				)
		);
		mRenderOption.setLineSpace(
				TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP,
						DEFAULT_LINE_SPACE,
						resources.getDisplayMetrics()
				)
		);
		mRenderOption.setIndentEnable(false);
		mRenderOption.setSelectedBgColor(ContextCompat.getColor(context, R.color.me_chan_te_theme_color));
		mRenderOption.setSelectedTextColor(Color.WHITE);
		mRenderOption.setSegmentSpace(
				TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP,
						DEFAULT_SEGMENT_SPACE,
						resources.getDisplayMetrics()
				)
		);
		mRenderOption.setBreakStrategy(BreakStrategy.BALANCED);
		mRenderOption.setRendererMode(RendererMode.SLIDING);

		mTextEngineCore = new TextEngineCore(this, mRenderOption);
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

	public RenderOption getRenderOption() {
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

	public void render(final Source source, final int width, final int height) {
		mTextEngineCore.typeset(source, width, height);
	}

	public void setParser(Parser parser) {
		mTextEngineCore.setParser(parser);
	}

	public TextPaint getTextPaint() {
		return mTextEngineCore.getTextPaint();
	}
}
