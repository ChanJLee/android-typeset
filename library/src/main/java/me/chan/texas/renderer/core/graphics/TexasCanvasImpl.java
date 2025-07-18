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
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TexasCanvasImpl implements TexasCanvas {
	private Canvas mCanvas;

	public void reset(Canvas canvas) {
		mCanvas = canvas;
	}

	@Override
	public boolean isHardwareAccelerated() {
		return mCanvas.isHardwareAccelerated();
	}

	@Override
	public void setBitmap(@Nullable Bitmap bitmap) {

	}

	@Override
	public void enableZ() {

	}

	@Override
	public void disableZ() {

	}

	@Override
	public boolean isOpaque() {
		return false;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getDensity() {
		return 0;
	}

	@Override
	public void setDensity(int density) {

	}

	@Override
	public int getMaximumBitmapWidth() {
		return 0;
	}

	@Override
	public int getMaximumBitmapHeight() {
		return 0;
	}

	@Override
	public int save() {
		return 0;
	}

	@Override
	public int saveLayer(@Nullable RectF bounds, @Nullable TexasPaint paint, int saveFlags) {
		return 0;
	}

	@Override
	public int saveLayer(@Nullable RectF bounds, @Nullable TexasPaint paint) {
		return 0;
	}

	@Override
	public int saveLayer(float left, float top, float right, float bottom, @Nullable TexasPaint paint, int saveFlags) {
		return 0;
	}

	@Override
	public int saveLayer(float left, float top, float right, float bottom, @Nullable TexasPaint paint) {
		return 0;
	}

	@Override
	public int saveLayerAlpha(@Nullable RectF bounds, int alpha, int saveFlags) {
		return 0;
	}

	@Override
	public int saveLayerAlpha(@Nullable RectF bounds, int alpha) {
		return 0;
	}

	@Override
	public int saveLayerAlpha(float left, float top, float right, float bottom, int alpha, int saveFlags) {
		return 0;
	}

	@Override
	public int saveLayerAlpha(float left, float top, float right, float bottom, int alpha) {
		return 0;
	}

	@Override
	public void restore() {

	}

	@Override
	public int getSaveCount() {
		return 0;
	}

	@Override
	public void restoreToCount(int saveCount) {

	}

	@Override
	public void translate(float dx, float dy) {

	}

	@Override
	public void scale(float sx, float sy) {

	}

	@Override
	public void scale(float sx, float sy, float px, float py) {

	}

	@Override
	public void rotate(float degrees) {

	}

	@Override
	public void rotate(float degrees, float px, float py) {

	}

	@Override
	public void skew(float sx, float sy) {

	}

	@Override
	public void concat(@Nullable Matrix matrix) {

	}

	@Override
	public void concat(@Nullable Matrix44 m) {

	}

	@Override
	public void setMatrix(@Nullable Matrix matrix) {

	}

	@Override
	public void getMatrix(@NonNull Matrix ctm) {

	}

	@NonNull
	@Override
	public Matrix getMatrix() {
		return null;
	}

	@Override
	public boolean clipRect(@NonNull RectF rect, @NonNull Region.Op op) {
		return false;
	}

	@Override
	public boolean clipRect(@NonNull Rect rect, @NonNull Region.Op op) {
		return false;
	}

	@Override
	public boolean clipRect(@NonNull RectF rect) {
		return false;
	}

	@Override
	public boolean clipOutRect(@NonNull RectF rect) {
		return false;
	}

	@Override
	public boolean clipRect(@NonNull Rect rect) {
		return false;
	}

	@Override
	public boolean clipOutRect(@NonNull Rect rect) {
		return false;
	}

	@Override
	public boolean clipRect(float left, float top, float right, float bottom, @NonNull Region.Op op) {
		return false;
	}

	@Override
	public boolean clipRect(float left, float top, float right, float bottom) {
		return false;
	}

	@Override
	public boolean clipOutRect(float left, float top, float right, float bottom) {
		return false;
	}

	@Override
	public boolean clipRect(int left, int top, int right, int bottom) {
		return false;
	}

	@Override
	public boolean clipOutRect(int left, int top, int right, int bottom) {
		return false;
	}

	@Override
	public boolean clipPath(@NonNull Path path, @NonNull Region.Op op) {
		return false;
	}

	@Override
	public boolean clipPath(@NonNull Path path) {
		return false;
	}

	@Override
	public boolean clipOutPath(@NonNull Path path) {
		return false;
	}

	@Override
	public void clipShader(@NonNull Shader shader) {

	}

	@Override
	public void clipOutShader(@NonNull Shader shader) {

	}

	@Nullable
	@Override
	public DrawFilter getDrawFilter() {
		return null;
	}

	@Override
	public void setDrawFilter(@Nullable DrawFilter filter) {

	}

	@Override
	public boolean quickReject(@NonNull RectF rect, @NonNull Canvas.EdgeType type) {
		return false;
	}

	@Override
	public boolean quickReject(@NonNull RectF rect) {
		return false;
	}

	@Override
	public boolean quickReject(@NonNull Path path, @NonNull Canvas.EdgeType type) {
		return false;
	}

	@Override
	public boolean quickReject(@NonNull Path path) {
		return false;
	}

	@Override
	public boolean quickReject(float left, float top, float right, float bottom, @NonNull Canvas.EdgeType type) {
		return false;
	}

	@Override
	public boolean quickReject(float left, float top, float right, float bottom) {
		return false;
	}

	@Override
	public boolean getClipBounds(@NonNull Rect bounds) {
		return false;
	}

	@NonNull
	@Override
	public Rect getClipBounds() {
		return null;
	}

	@Override
	public void drawPicture(@NonNull Picture picture) {

	}

	@Override
	public void drawPicture(@NonNull Picture picture, @NonNull RectF dst) {

	}

	@Override
	public void drawPicture(@NonNull Picture picture, @NonNull Rect dst) {

	}

	@Override
	public void drawArc(@NonNull RectF oval, float startAngle, float sweepAngle, boolean useCenter, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawArc(float left, float top, float right, float bottom, float startAngle, float sweepAngle, boolean useCenter, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawARGB(int a, int r, int g, int b) {

	}

	@Override
	public void drawBitmap(@NonNull Bitmap bitmap, float left, float top, @Nullable TexasPaint paint) {

	}

	@Override
	public void drawBitmap(@NonNull Bitmap bitmap, @Nullable Rect src, @NonNull RectF dst, @Nullable TexasPaint paint) {

	}

	@Override
	public void drawBitmap(@NonNull Bitmap bitmap, @Nullable Rect src, @NonNull Rect dst, @Nullable TexasPaint paint) {

	}

	@Override
	public void drawBitmap(@NonNull int[] colors, int offset, int stride, float x, float y, int width, int height, boolean hasAlpha, @Nullable TexasPaint paint) {

	}

	@Override
	public void drawBitmap(@NonNull int[] colors, int offset, int stride, int x, int y, int width, int height, boolean hasAlpha, @Nullable TexasPaint paint) {

	}

	@Override
	public void drawBitmap(@NonNull Bitmap bitmap, @NonNull Matrix matrix, @Nullable TexasPaint paint) {

	}

	@Override
	public void drawBitmapMesh(@NonNull Bitmap bitmap, int meshWidth, int meshHeight, @NonNull float[] verts, int vertOffset, @Nullable int[] colors, int colorOffset, @Nullable TexasPaint paint) {

	}

	@Override
	public void drawCircle(float cx, float cy, float radius, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawColor(int color) {

	}

	@Override
	public void drawColor(long color) {

	}

	@Override
	public void drawColor(int color, @NonNull PorterDuff.Mode mode) {

	}

	@Override
	public void drawColor(int color, @NonNull BlendMode mode) {

	}

	@Override
	public void drawColor(long color, @NonNull BlendMode mode) {

	}

	@Override
	public void drawLine(float startX, float startY, float stopX, float stopY, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawLines(@NonNull float[] pts, int offset, int count, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawLines(@NonNull float[] pts, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawOval(@NonNull RectF oval, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawOval(float left, float top, float right, float bottom, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawPaint(@NonNull TexasPaint paint) {

	}

	@Override
	public void drawPatch(@NonNull NinePatch patch, @NonNull Rect dst, @Nullable TexasPaint paint) {

	}

	@Override
	public void drawPatch(@NonNull NinePatch patch, @NonNull RectF dst, @Nullable TexasPaint paint) {

	}

	@Override
	public void drawPath(@NonNull Path path, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawPoint(float x, float y, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawPoints(float[] pts, int offset, int count, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawPoints(@NonNull float[] pts, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawPosText(@NonNull char[] text, int index, int count, @NonNull float[] pos, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawPosText(@NonNull String text, @NonNull float[] pos, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawRect(@NonNull RectF rect, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawRect(@NonNull Rect r, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawRect(float left, float top, float right, float bottom, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawRGB(int r, int g, int b) {

	}

	@Override
	public void drawRoundRect(@NonNull RectF rect, float rx, float ry, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawRoundRect(float left, float top, float right, float bottom, float rx, float ry, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawDoubleRoundRect(@NonNull RectF outer, float outerRx, float outerRy, @NonNull RectF inner, float innerRx, float innerRy, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawDoubleRoundRect(@NonNull RectF outer, @NonNull float[] outerRadii, @NonNull RectF inner, @NonNull float[] innerRadii, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawGlyphs(@NonNull int[] glyphIds, int glyphIdOffset, @NonNull float[] positions, int positionOffset, int glyphCount, @NonNull Font font, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawText(@NonNull char[] text, int index, int count, float x, float y, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawText(@NonNull String text, float x, float y, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawText(@NonNull String text, int start, int end, float x, float y, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawText(@NonNull CharSequence text, int start, int end, float x, float y, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawTextOnPath(@NonNull char[] text, int index, int count, @NonNull Path path, float hOffset, float vOffset, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawTextOnPath(@NonNull String text, @NonNull Path path, float hOffset, float vOffset, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawTextRun(@NonNull char[] text, int index, int count, int contextIndex, int contextCount, float x, float y, boolean isRtl, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawTextRun(@NonNull CharSequence text, int start, int end, int contextStart, int contextEnd, float x, float y, boolean isRtl, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawTextRun(@NonNull MeasuredText text, int start, int end, int contextStart, int contextEnd, float x, float y, boolean isRtl, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawVertices(@NonNull Canvas.VertexMode mode, int vertexCount, @NonNull float[] verts, int vertOffset, @Nullable float[] texs, int texOffset, @Nullable int[] colors, int colorOffset, @Nullable short[] indices, int indexOffset, int indexCount, @NonNull TexasPaint paint) {

	}

	@Override
	public void drawRenderNode(@NonNull RenderNode renderNode) {

	}

	@Override
	public void drawMesh(@NonNull Mesh mesh, @Nullable BlendMode blendMode, @NonNull TexasPaint paint) {

	}
}
