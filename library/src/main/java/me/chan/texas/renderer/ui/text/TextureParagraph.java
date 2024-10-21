package me.chan.texas.renderer.ui.text;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.text.Paragraph;
import me.chan.texas.utils.concurrency.TaskQueue;

/**
 * 段落渲染器
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface TextureParagraph {
	/**
	 * @param width  宽度
	 * @param height 高度
	 * @return 获取一个canvas
	 */
	@Nullable
	Canvas lockCanvas(int width, int height);

	/**
	 * @param canvas 完成绘制 canvas 来自 {@link #lockCanvas(int, int)}
	 */
	void unlockCanvasAndPost(Canvas canvas);

	/**
	 * @param location 获取当前对象在屏幕中的位置
	 */
	void getLocationOnScreen(int[] location);

	/**
	 * @param paragraph               绘制的段落
	 * @param paintSet                画笔集合
	 * @param renderOption            绘制选项
	 * @param decor                   装饰的内容
	 * @param spanClickedEventHandler 点击事件处理器
	 */
	void render(@NonNull Paragraph paragraph,
				@NonNull PaintSet paintSet,
				@NonNull RenderOption renderOption,
				@Nullable ParagraphDecor decor,
				@Nullable SpanTouchEventHandler spanClickedEventHandler);

	/**
	 * @param onTextSelectedListener 选中的回调
	 */
	void setOnTextSelectedListener(OnSelectedChangedListener onTextSelectedListener);

	/**
	 * @return 用于标识一个渲染对象
	 */
	TaskQueue.Token getToken();

	/**
	 * 通知刷新UI
	 */
	void syncUI();

	/**
	 * @return 当前的paragraph
	 */
	Paragraph getParagraph();

	void clear();

	int getHeight();
}
