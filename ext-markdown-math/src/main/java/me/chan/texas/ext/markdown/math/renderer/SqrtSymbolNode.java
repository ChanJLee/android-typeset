package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;

import me.chan.texas.ext.markdown.math.TexMathParser;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;

/**
 * 混合式根号符号节点
 * 策略：字体钩子 + Canvas 直线
 * - 从字体中提取并保留左侧钩子部分（保持字体美感）
 * - 使用 drawLine 绘制斜线和横线（保持线条粗细可控）
 */
public class SqrtSymbolNode extends RendererNode {

	private float mDesiredHeight;
	private float mContentWidth = 0;
	
	// 钩子部分的路径
	private final Path mHookPath = new Path();
	
	// 从字体中提取的原始钩子路径缓存
	private static Path sOriginalHookPath;
	private static RectF sOriginalHookBounds;
	private static float sHookEndX;  // 钩子结束位置的X坐标
	private static float sHookEndY;  // 钩子结束位置的Y坐标（谷底）
	private static final Object sLock = new Object();
	
	// 钩子截取比例（从字体路径中截取前面这部分作为钩子）
	private static final float HOOK_PATH_RATIO = 0.25f;  // 截取路径的前25%作为钩子

	public SqrtSymbolNode(MathPaint.Styles styles) {
		super(styles);
		ensureHookPathLoaded();
	}

	public void resize(float size) {
		mDesiredHeight = size;
	}

	/**
	 * 确保从字体中加载钩子路径（只加载一次）
	 */
	private static void ensureHookPathLoaded() {
		if (sOriginalHookPath != null) {
			return;
		}
		
		synchronized (sLock) {
			if (sOriginalHookPath != null) {
				return;
			}
			
			// 使用基准字体大小提取路径
			Paint paint = new Paint();
			paint.setTypeface(TexMathParser.getTypeface());
			paint.setTextSize(1000); // 使用较大的尺寸以获得精确的路径
			
			// 从字体中提取根号符号的完整路径
			Path fullPath = new Path();
			paint.getTextPath("√", 0, 1, 0, 0, fullPath);
			
			// 使用 PathMeasure 截取前面的钩子部分
			PathMeasure measure = new PathMeasure(fullPath, false);
			float totalLength = measure.getLength();
			float hookLength = totalLength * HOOK_PATH_RATIO;
			
			// 提取钩子路径
			Path hookPath = new Path();
			measure.getSegment(0, hookLength, hookPath, true);
			
			// 获取钩子结束点的坐标（谷底位置）
			float[] endPos = new float[2];
			measure.getPosTan(hookLength, endPos, null);
			sHookEndX = endPos[0];
			sHookEndY = endPos[1];
			
			// 计算钩子路径边界
			RectF bounds = new RectF();
			hookPath.computeBounds(bounds, true);
			
			sOriginalHookPath = hookPath;
			sOriginalHookBounds = bounds;
		}
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		float symbolHeight = mDesiredHeight;
		float topPadding = getTopPadding();
		
		// 计算钩子的缩放比例（保持钩子的原始比例）
		float hookScale = symbolHeight / sOriginalHookBounds.height();
		
		// 钩子的实际尺寸
		float scaledHookWidth = sOriginalHookBounds.width() * hookScale;
		float scaledHookHeight = sOriginalHookBounds.height() * hookScale;
		
		// 计算从谷底到顶部的斜线距离
		// 这条斜线从钩子结束点（谷底）到达符号顶部
		float slopeStartY = scaledHookHeight; // 谷底Y坐标
		float slopeEndY = topPadding;         // 顶部Y坐标
		float slopeHeight = slopeStartY - slopeEndY;
		
		// 斜线的水平距离（通常是高度的 0.4-0.6 倍，这里使用 0.5）
		float slopeWidth = slopeHeight * 0.5f;
		
		// 总宽度 = 钩子宽度 + 斜线宽度
		float symbolWidth = scaledHookWidth + slopeWidth;
		
		int height = (int) Math.ceil(symbolHeight + topPadding);
		int width = (int) Math.ceil(symbolWidth);

		setMeasuredSize(width, height);
		
		// 构建缩放后的钩子路径
		buildHookPath(hookScale, topPadding);
	}

	/**
	 * 构建缩放后的钩子路径
	 */
	private void buildHookPath(float scale, float topPadding) {
		mHookPath.reset();
		
		// 应用缩放和平移变换
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		
		// 将钩子定位到正确的位置
		// X: 从左边界开始
		// Y: 使钩子底部对齐到符号底部
		float translateX = -sOriginalHookBounds.left * scale;
		float translateY = topPadding + mDesiredHeight - sOriginalHookBounds.bottom * scale;
		
		matrix.postTranslate(translateX, translateY);
		
		mHookPath.addPath(sOriginalHookPath, matrix);
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
		float topPadding = getTopPadding();

		// 设置线条样式
		paint.setStrokeWidth(thickness);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeJoin(Paint.Join.ROUND);

		// 1. 绘制字体钩子（使用描边以保持线条粗细）
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawPath(mHookPath, paint);
		
		// 2. 计算并绘制从谷底到顶部的斜线
		float hookScale = mDesiredHeight / sOriginalHookBounds.height();
		float scaledHookWidth = sOriginalHookBounds.width() * hookScale;
		float scaledHookEndX = sHookEndX * hookScale - sOriginalHookBounds.left * hookScale;
		float scaledHookEndY = topPadding + mDesiredHeight - (sOriginalHookBounds.bottom - sHookEndY) * hookScale;
		
		// 斜线起点：钩子结束位置（谷底）
		float slopeStartX = scaledHookEndX;
		float slopeStartY = scaledHookEndY;
		
		// 斜线终点：符号顶部
		float slopeEndX = getWidth();
		float slopeEndY = topPadding;
		
		canvas.drawLine(slopeStartX, slopeStartY, slopeEndX, slopeEndY, paint);
		
		// 3. 绘制顶部横线
		float horizontalLineStartX = slopeEndX;
		float horizontalLineY = topPadding;
		canvas.drawLine(horizontalLineStartX, horizontalLineY, 
						horizontalLineStartX + mContentWidth, horizontalLineY, paint);
	}

	@Override
	protected String toPretty() {
		return "√";
	}
}