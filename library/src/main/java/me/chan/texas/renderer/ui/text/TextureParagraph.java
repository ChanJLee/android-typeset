package me.chan.texas.renderer.ui.text;

import android.graphics.Canvas;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.SelectionProvider;
import me.chan.texas.utils.concurrency.Worker;

/**
 * 段落渲染器
 */
public interface TextureParagraph {
	/**
	 * @param width  宽度
	 * @param height 高度
	 * @return 获取一个canvas
	 */
	@Nullable
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	Canvas lockCanvas(int width, int height);

	/**
	 * @param canvas 完成绘制 canvas 来自 {@link #lockCanvas(int, int)}
	 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void unlockCanvasAndPost(Canvas canvas);

	/**
	 * @param location 获取当前对象在屏幕中的位置
	 */
	void getLocationOnScreen(int[] location);

	/**
	 * @param paragraph         绘制的段落
	 * @param paintSet          画笔集合
	 * @param renderOption      绘制选项
	 * @param selectionProvider selection provider
	 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void render(@NonNull Paragraph paragraph,
				@NonNull PaintSet paintSet,
				@NonNull RenderOption renderOption,
				@NonNull SelectionProvider selectionProvider);

	/**
	 * @return 用于标识一个渲染对象
	 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	Worker.Token getToken();

	/**
	 * 通知刷新UI
	 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void syncUI();

	/**
	 * @return 当前的paragraph
	 */
	Paragraph getParagraph();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void clear();

	int getHeight();

	ViewGroup.LayoutParams getLayoutParams();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void setLayoutParams(ViewGroup.LayoutParams layoutParams);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void setOnMeasureInterceptor(OnMeasureInterceptor interceptor);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void setRendererListener(RendererListener rendererListener);

	interface RendererListener {
		void onSyncUI(TextureParagraph view);
	}
}
