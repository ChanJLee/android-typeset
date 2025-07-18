package me.chan.texas.renderer.core.graphics;

import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Matrix44;
import android.graphics.Mesh;
import android.graphics.NinePatch;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.graphics.fonts.Font;
import android.graphics.text.MeasuredText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

interface TexasCanvas {
	boolean isHardwareAccelerated();

	void setBitmap(@Nullable Bitmap bitmap);

	void enableZ();

	void disableZ();

	boolean isOpaque();

	int getWidth();

	int getHeight();

	int getDensity();

	void setDensity(int density);

	int getMaximumBitmapWidth();

	int getMaximumBitmapHeight();

	int save();

	/**
	 * @deprecated
	 */
	@Deprecated
	int saveLayer(@Nullable RectF bounds, @Nullable TexasPaint paint, int saveFlags);

	int saveLayer(@Nullable RectF bounds, @Nullable TexasPaint paint);

	/**
	 * @deprecated
	 */
	@Deprecated
	int saveLayer(float left, float top, float right, float bottom, @Nullable TexasPaint paint, int saveFlags);

	int saveLayer(float left, float top, float right, float bottom, @Nullable TexasPaint paint);

	/**
	 * @deprecated
	 */
	@Deprecated
	int saveLayerAlpha(@Nullable RectF bounds, int alpha, int saveFlags);

	int saveLayerAlpha(@Nullable RectF bounds, int alpha);

	/**
	 * @deprecated
	 */
	@Deprecated
	int saveLayerAlpha(float left, float top, float right, float bottom, int alpha, int saveFlags);

	int saveLayerAlpha(float left, float top, float right, float bottom, int alpha);

	void restore();

	int getSaveCount();

	void restoreToCount(int saveCount);

	void translate(float dx, float dy);

	void scale(float sx, float sy);

	void scale(float sx, float sy, float px, float py);

	void rotate(float degrees);

	void rotate(float degrees, float px, float py);

	void skew(float sx, float sy);

	void concat(@Nullable Matrix matrix);

	void concat(@Nullable Matrix44 m);

	void setMatrix(@Nullable Matrix matrix);

	/**
	 * @deprecated
	 */
	@Deprecated
	void getMatrix(@NonNull Matrix ctm);

	/**
	 * @deprecated
	 */
	@Deprecated
	@NonNull
	Matrix getMatrix();

	/**
	 * @deprecated
	 */
	@Deprecated
	boolean clipRect(@NonNull RectF rect, @NonNull Region.Op op);

	/**
	 * @deprecated
	 */
	@Deprecated
	boolean clipRect(@NonNull Rect rect, @NonNull Region.Op op);

	boolean clipRect(@NonNull RectF rect);

	boolean clipOutRect(@NonNull RectF rect);

	boolean clipRect(@NonNull Rect rect);

	boolean clipOutRect(@NonNull Rect rect);

	/**
	 * @deprecated
	 */
	@Deprecated
	boolean clipRect(float left, float top, float right, float bottom, @NonNull Region.Op op);

	boolean clipRect(float left, float top, float right, float bottom);

	boolean clipOutRect(float left, float top, float right, float bottom);

	boolean clipRect(int left, int top, int right, int bottom);

	boolean clipOutRect(int left, int top, int right, int bottom);

	/**
	 * @deprecated
	 */
	@Deprecated
	boolean clipPath(@NonNull Path path, @NonNull Region.Op op);

	boolean clipPath(@NonNull Path path);

	boolean clipOutPath(@NonNull Path path);

	void clipShader(@NonNull Shader shader);

	void clipOutShader(@NonNull Shader shader);

	@Nullable
	DrawFilter getDrawFilter();

	void setDrawFilter(@Nullable DrawFilter filter);

	/**
	 * @deprecated
	 */
	@Deprecated
	boolean quickReject(@NonNull RectF rect, @NonNull Canvas.EdgeType type);

	boolean quickReject(@NonNull RectF rect);

	/**
	 * @deprecated
	 */
	@Deprecated
	boolean quickReject(@NonNull Path path, @NonNull Canvas.EdgeType type);

	boolean quickReject(@NonNull Path path);

	/**
	 * @deprecated
	 */
	@Deprecated
	boolean quickReject(float left, float top, float right, float bottom, @NonNull Canvas.EdgeType type);

	boolean quickReject(float left, float top, float right, float bottom);

	boolean getClipBounds(@NonNull Rect bounds);

	@NonNull
	Rect getClipBounds();

	void drawPicture(@NonNull Picture picture);

	void drawPicture(@NonNull Picture picture, @NonNull RectF dst);

	void drawPicture(@NonNull Picture picture, @NonNull Rect dst);

	void drawArc(@NonNull RectF oval, float startAngle, float sweepAngle, boolean useCenter, @NonNull TexasPaint paint);

	void drawArc(float left, float top, float right, float bottom, float startAngle, float sweepAngle, boolean useCenter, @NonNull TexasPaint paint);

	void drawARGB(int a, int r, int g, int b);

	void drawBitmap(@NonNull Bitmap bitmap, float left, float top, @Nullable TexasPaint paint);

	void drawBitmap(@NonNull Bitmap bitmap, @Nullable Rect src, @NonNull RectF dst, @Nullable TexasPaint paint);

	void drawBitmap(@NonNull Bitmap bitmap, @Nullable Rect src, @NonNull Rect dst, @Nullable TexasPaint paint);

	/**
	 * @deprecated
	 */
	@Deprecated
	void drawBitmap(@NonNull int[] colors, int offset, int stride, float x, float y, int width, int height, boolean hasAlpha, @Nullable TexasPaint paint);

	/**
	 * @deprecated
	 */
	@Deprecated
	void drawBitmap(@NonNull int[] colors, int offset, int stride, int x, int y, int width, int height, boolean hasAlpha, @Nullable TexasPaint paint);

	void drawBitmap(@NonNull Bitmap bitmap, @NonNull Matrix matrix, @Nullable TexasPaint paint);

	void drawBitmapMesh(@NonNull Bitmap bitmap, int meshWidth, int meshHeight, @NonNull float[] verts, int vertOffset, @Nullable int[] colors, int colorOffset, @Nullable TexasPaint paint);

	void drawCircle(float cx, float cy, float radius, @NonNull TexasPaint paint);

	void drawColor(int color);

	void drawColor(long color);

	void drawColor(int color, @NonNull PorterDuff.Mode mode);

	void drawColor(int color, @NonNull BlendMode mode);

	void drawColor(long color, @NonNull BlendMode mode);

	void drawLine(float startX, float startY, float stopX, float stopY, @NonNull TexasPaint paint);

	void drawLines(@NonNull float[] pts, int offset, int count, @NonNull TexasPaint paint);

	void drawLines(@NonNull float[] pts, @NonNull TexasPaint paint);

	void drawOval(@NonNull RectF oval, @NonNull TexasPaint paint);

	void drawOval(float left, float top, float right, float bottom, @NonNull TexasPaint paint);

	void drawPaint(@NonNull TexasPaint paint);

	void drawPatch(@NonNull NinePatch patch, @NonNull Rect dst, @Nullable TexasPaint paint);

	void drawPatch(@NonNull NinePatch patch, @NonNull RectF dst, @Nullable TexasPaint paint);

	void drawPath(@NonNull Path path, @NonNull TexasPaint paint);

	void drawPoint(float x, float y, @NonNull TexasPaint paint);

	void drawPoints(float[] pts, int offset, int count, @NonNull TexasPaint paint);

	void drawPoints(@NonNull float[] pts, @NonNull TexasPaint paint);

	/**
	 * @deprecated
	 */
	@Deprecated
	void drawPosText(@NonNull char[] text, int index, int count, @NonNull float[] pos, @NonNull TexasPaint paint);

	/**
	 * @deprecated
	 */
	@Deprecated
	void drawPosText(@NonNull String text, @NonNull float[] pos, @NonNull TexasPaint paint);

	void drawRect(@NonNull RectF rect, @NonNull TexasPaint paint);

	void drawRect(@NonNull Rect r, @NonNull TexasPaint paint);

	void drawRect(float left, float top, float right, float bottom, @NonNull TexasPaint paint);

	void drawRGB(int r, int g, int b);

	void drawRoundRect(@NonNull RectF rect, float rx, float ry, @NonNull TexasPaint paint);

	void drawRoundRect(float left, float top, float right, float bottom, float rx, float ry, @NonNull TexasPaint paint);

	void drawDoubleRoundRect(@NonNull RectF outer, float outerRx, float outerRy, @NonNull RectF inner, float innerRx, float innerRy, @NonNull TexasPaint paint);

	void drawDoubleRoundRect(@NonNull RectF outer, @NonNull float[] outerRadii, @NonNull RectF inner, @NonNull float[] innerRadii, @NonNull TexasPaint paint);

	void drawGlyphs(@NonNull int[] glyphIds, int glyphIdOffset, @NonNull float[] positions, int positionOffset, int glyphCount, @NonNull Font font, @NonNull TexasPaint paint);

	void drawText(@NonNull char[] text, int index, int count, float x, float y, @NonNull TexasPaint paint);

	void drawText(@NonNull String text, float x, float y, @NonNull TexasPaint paint);

	void drawText(@NonNull String text, int start, int end, float x, float y, @NonNull TexasPaint paint);

	void drawText(@NonNull CharSequence text, int start, int end, float x, float y, @NonNull TexasPaint paint);

	void drawTextOnPath(@NonNull char[] text, int index, int count, @NonNull Path path, float hOffset, float vOffset, @NonNull TexasPaint paint);

	void drawTextOnPath(@NonNull String text, @NonNull Path path, float hOffset, float vOffset, @NonNull TexasPaint paint);

	void drawTextRun(@NonNull char[] text, int index, int count, int contextIndex, int contextCount, float x, float y, boolean isRtl, @NonNull TexasPaint paint);

	void drawTextRun(@NonNull CharSequence text, int start, int end, int contextStart, int contextEnd, float x, float y, boolean isRtl, @NonNull TexasPaint paint);

	void drawTextRun(@NonNull MeasuredText text, int start, int end, int contextStart, int contextEnd, float x, float y, boolean isRtl, @NonNull TexasPaint paint);

	void drawVertices(@NonNull Canvas.VertexMode mode, int vertexCount, @NonNull float[] verts, int vertOffset, @Nullable float[] texs, int texOffset, @Nullable int[] colors, int colorOffset, @Nullable short[] indices, int indexOffset, int indexCount, @NonNull TexasPaint paint);

	void drawRenderNode(@NonNull RenderNode renderNode);

	void drawMesh(@NonNull Mesh mesh, @Nullable BlendMode blendMode, @NonNull TexasPaint paint);
}
