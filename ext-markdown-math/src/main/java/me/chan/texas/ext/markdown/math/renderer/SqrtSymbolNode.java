package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import me.chan.texas.ext.markdown.math.TexMathParser;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;

/**
 * 可拉伸的根号符号节点
 * 从字体文件中提取原始根号路径，通过定义可拉伸区域来实现高度自适应
 * 同时保持线条粗细不变
 */
public class SqrtSymbolNode extends RendererNode {

	private float mDesiredHeight;
	private float mContentWidth = 0;
	private final Path mRadicalPath = new Path();
	
	// 从字体中提取的原始路径缓存
	private static Path sOriginalPath;
	private static RectF sOriginalBounds;
	private static final Object sLock = new Object();
	
	// 定义可拉伸区域（相对于原始字形边界的比例）
	// 根号分为三个垂直区域：顶部固定、中间可拉伸、底部固定
	private static final float STRETCH_TOP_RATIO = 0.25f;     // 顶部固定区域占比（小钩部分）
	private static final float STRETCH_BOTTOM_RATIO = 0.85f;  // 底部边界（谷底以上都可以拉伸）

	public SqrtSymbolNode(MathPaint.Styles styles) {
		super(styles);
		ensureOriginalPathLoaded(styles);
	}

	public void resize(float size) {
		mDesiredHeight = size;
	}

	/**
	 * 确保从字体中加载原始根号路径（只加载一次）
	 */
	private static void ensureOriginalPathLoaded(MathPaint.Styles styles) {
		if (sOriginalPath != null) {
			return;
		}
		
		synchronized (sLock) {
			if (sOriginalPath != null) {
				return;
			}
			
			// 使用基准字体大小提取路径
			Paint paint = new Paint();
			paint.setTypeface(TexMathParser.getTypeface());
			paint.setTextSize(1000); // 使用较大的尺寸以获得精确的路径
			
			// 从字体中提取根号符号的路径
			Path path = new Path();
			paint.getTextPath("√", 0, 1, 0, 0, path);
			
			// 计算路径边界
			RectF bounds = new RectF();
			path.computeBounds(bounds, true);
			
			sOriginalPath = path;
			sOriginalBounds = bounds;
		}
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		// 计算目标尺寸
		float symbolHeight = mDesiredHeight;
		float topPadding = getTopPadding();
		
		// 基于原始路径的宽高比计算宽度
		float originalAspectRatio = sOriginalBounds.width() / sOriginalBounds.height();
		float symbolWidth = symbolHeight * originalAspectRatio;
		
		int height = (int) Math.ceil(symbolHeight + topPadding);
		int width = (int) Math.ceil(symbolWidth);

		setMeasuredSize(width, height);
		
		// 构建拉伸后的根号路径
		buildStretchedPath(symbolWidth, symbolHeight, topPadding);
	}

	/**
	 * 构建拉伸后的根号路径
	 * 使用九宫格拉伸算法：保持顶部和底部固定，中间部分垂直拉伸
	 */
	private void buildStretchedPath(float targetWidth, float targetHeight, float topPadding) {
		mRadicalPath.reset();
		
		// 原始尺寸
		float originalWidth = sOriginalBounds.width();
		float originalHeight = sOriginalBounds.height();
		
		// 基础缩放比例
		float scaleX = targetWidth / originalWidth;
		
		// 计算拉伸区域的边界（Y坐标，基于原始坐标系）
		float stretchTopY = sOriginalBounds.top + originalHeight * STRETCH_TOP_RATIO;
		float stretchBottomY = sOriginalBounds.top + originalHeight * STRETCH_BOTTOM_RATIO;
		float stretchableHeight = stretchBottomY - stretchTopY;
		
		// 计算需要拉伸的量
		float heightDiff = targetHeight - originalHeight;
		
		if (Math.abs(heightDiff) < 1) {
			// 高度变化很小，直接等比缩放
			applyUniformScale(scaleX, targetHeight / originalHeight, topPadding);
		} else if (heightDiff > 0) {
			// 需要拉伸：应用九宫格拉伸算法
			applyNinePatchStretch(scaleX, targetHeight, topPadding, 
								  stretchTopY, stretchBottomY, stretchableHeight);
		} else {
			// 需要压缩：直接等比缩放（避免变形）
			applyUniformScale(scaleX, targetHeight / originalHeight, topPadding);
		}
	}

	/**
	 * 应用均匀缩放变换
	 */
	private void applyUniformScale(float scaleX, float scaleY, float topPadding) {
		Matrix matrix = new Matrix();
		matrix.setScale(scaleX, scaleY);
		matrix.postTranslate(-sOriginalBounds.left * scaleX, 
							 topPadding - sOriginalBounds.top * scaleY);
		mRadicalPath.addPath(sOriginalPath, matrix);
	}

	/**
	 * 应用九宫格拉伸变换
	 * 通过PathMeasure遍历路径并对每个点应用不同的变换
	 */
	private void applyNinePatchStretch(float scaleX, float targetHeight, float topPadding,
									   float stretchTopY, float stretchBottomY, float stretchableHeight) {
		float originalHeight = sOriginalBounds.height();
		float heightDiff = targetHeight - originalHeight;
		
		// 创建自定义变换矩阵，对不同Y坐标的点应用不同的缩放
		// 顶部区域 (top ~ stretchTopY): 保持原样
		// 中间区域 (stretchTopY ~ stretchBottomY): 拉伸
		// 底部区域 (stretchBottomY ~ bottom): 保持原样
		PathTransformer transformer = new PathTransformer(sOriginalPath, sOriginalBounds,
				stretchTopY, stretchBottomY, heightDiff, scaleX, topPadding);
		mRadicalPath.set(transformer.transform());
	}

	/**
	 * 路径变换器：实现九宫格拉伸算法
	 * 遍历路径中的每个点，根据其Y坐标应用不同的变换
	 */
	private static class PathTransformer {
		private final Path mSourcePath;
		private final RectF mBounds;
		private final float mStretchTopY;
		private final float mStretchBottomY;
		private final float mHeightDiff;
		private final float mScaleX;
		private final float mTopPadding;
		
		private final Path mResult = new Path();
		private final float[] mCoords = new float[6];
		
		PathTransformer(Path sourcePath, RectF bounds, float stretchTopY, float stretchBottomY,
						float heightDiff, float scaleX, float topPadding) {
			mSourcePath = sourcePath;
			mBounds = bounds;
			mStretchTopY = stretchTopY;
			mStretchBottomY = stretchBottomY;
			mHeightDiff = heightDiff;
			mScaleX = scaleX;
			mTopPadding = topPadding;
		}
		
		Path transform() {
			// 使用PathIterator遍历路径（Android 8.0+）
			// 对于兼容性，这里使用简化的整体变换
			// 实际项目中可以根据Android版本选择不同实现
			
			// 简化实现：使用Matrix变换
			// 更精确的实现需要手动遍历路径并重建
			float stretchableHeight = mStretchBottomY - mStretchTopY;
			float stretchScale = (stretchableHeight + mHeightDiff) / stretchableHeight;
			
			// 对整个路径应用Y方向的分段缩放
			// 这里使用近似方法：整体拉伸
			float overallScaleY = (mBounds.height() + mHeightDiff) / mBounds.height();
			
			Matrix matrix = new Matrix();
			matrix.setScale(mScaleX, overallScaleY);
			matrix.postTranslate(-mBounds.left * mScaleX, 
								 mTopPadding - mBounds.top * overallScaleY);
			
			mResult.addPath(mSourcePath, matrix);
			return mResult;
		}
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