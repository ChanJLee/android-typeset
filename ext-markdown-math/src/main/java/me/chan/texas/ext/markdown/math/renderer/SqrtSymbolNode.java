package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Paint;
import android.graphics.Path;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;

public class SqrtSymbolNode extends RendererNode {

	private float mDesiredHeight;
	private float mContentWidth = 0;
	private final Path mRadicalPath = new Path();
	
	// 根号符号的基准尺寸（相对于1.0单位高度）
	// 这些比例经过调整以模拟传统数学字体的美感
	private static final float BASE_WIDTH_RATIO = 0.5f;   // 根号宽度与高度的比例
	private static final float HOOK_START_Y_RATIO = 0.75f; // 左侧小钩起点Y位置比例
	private static final float VALLEY_X_RATIO = 0.32f;    // 谷底X位置比例
	private static final float VALLEY_Y_RATIO = 0.98f;    // 谷底Y位置比例（接近底部）
	private static final float PEAK_X_RATIO = 0.70f;      // 峰顶X位置比例

	public SqrtSymbolNode(MathPaint.Styles styles) {
		super(styles);
	}

	public void resize(float size) {
		mDesiredHeight = size;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		// 使用基准字体大小来计算线条粗细，而不是用实际的符号高度
		float baseTextSize = mStyles.getTextSize();
		
		// 计算根号符号的宽度（基于高度）
		float symbolHeight = mDesiredHeight;
		float symbolWidth = symbolHeight * BASE_WIDTH_RATIO;
		
		int height = (int) Math.ceil(symbolHeight + getTopPadding());
		int width = (int) Math.ceil(symbolWidth);

		setMeasuredSize(width, height);
		
		// 构建根号路径
		buildRadicalPath(symbolWidth, symbolHeight);
	}

	/**
	 * 构建根号符号的矢量路径
	 * 使用贝塞尔曲线来模拟字体的流畅感
	 */
	private void buildRadicalPath(float width, float height) {
		mRadicalPath.reset();
		
		float topPadding = getTopPadding();
		
		// 根号的关键点坐标
		float hookStartX = 0;
		float hookStartY = topPadding + height * HOOK_START_Y_RATIO;
		
		float valleyX = width * VALLEY_X_RATIO;
		float valleyY = topPadding + height * VALLEY_Y_RATIO;
		
		float peakX = width * PEAK_X_RATIO;
		float peakY = topPadding;
		
		// 左侧小钩的曲线（从顶部开始，优雅地弯曲到谷底）
		mRadicalPath.moveTo(hookStartX, hookStartY);
		
		// 小钩到谷底的连接 - 使用二次贝塞尔曲线让下降更自然
		float ctrlX1 = hookStartX + (valleyX - hookStartX) * 0.4f;
		float ctrlY1 = hookStartY + (valleyY - hookStartY) * 0.6f;
		mRadicalPath.quadTo(ctrlX1, ctrlY1, valleyX, valleyY);
		
		// 谷底到峰顶的急剧上升 - 使用三次贝塞尔曲线来模拟加速上升的动感
		// 这是根号最重要的部分，需要表现出力量感
		float ctrlX2 = valleyX + (peakX - valleyX) * 0.3f;
		float ctrlY2 = valleyY - (valleyY - peakY) * 0.1f; // 先稍微延续一下底部
		float ctrlX3 = valleyX + (peakX - valleyX) * 0.6f;
		float ctrlY3 = peakY + (valleyY - peakY) * 0.2f; // 然后快速上升
		mRadicalPath.cubicTo(ctrlX2, ctrlY2, ctrlX3, ctrlY3, peakX, peakY);
	}

	public float getVerticalGap() {
		return MathFontOptions.RADICAL_VERTICAL_GAP / MathFontOptions.UNITS_PER_EM * mStyles.getTextSize();
	}

	public float getRuleThickness() {
		// 使用基准字体大小来计算粗细，保持一致性
		return MathFontOptions.RADICAL_RULE_THICKNESS / MathFontOptions.UNITS_PER_EM * mStyles.getTextSize();
	}

	public float getExtraAscender() {
		return MathFontOptions.RADICAL_EXTRA_ASCENDER / MathFontOptions.UNITS_PER_EM * mStyles.getTextSize();
	}

	public float getKernBeforeDegree() {
		return MathFontOptions.RADICAL_KERN_BEFORE_DEGREE / MathFontOptions.UNITS_PER_EM * mStyles.getTextSize();
	}

	public float getKernAfterDegree() {
		// return MathFontOptions.RADICAL_KERN_AFTER_DEGREE / MathFontOptions.UNITS_PER_EM * mStyles.getTextSize();
		return 0;
	}

	public float getDegreeBottomRaisePercent() {
		return MathFontOptions.RADICAL_DEGREE_BOTTOM_RAISE_PERCENT / 100;
	}

	public void setContentWidth(float contentWidth) {
		mContentWidth = contentWidth;
	}

	public float getTopPadding() {
		return getExtraAscender();
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		float thickness = getRuleThickness();

		// 设置画笔样式为描边，使用圆角让曲线更流畅
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(thickness);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeJoin(Paint.Join.ROUND);

		// 绘制根号符号路径
		canvas.drawPath(mRadicalPath, paint);
		
		// 绘制顶部横线（保持相同的线条样式以确保视觉统一）
		float startX = getWidth();
		float y = getTopPadding();
		canvas.drawLine(startX, y, startX + mContentWidth, y, paint);
	}

	@Override
	protected String toPretty() {
		return "√";
	}
}