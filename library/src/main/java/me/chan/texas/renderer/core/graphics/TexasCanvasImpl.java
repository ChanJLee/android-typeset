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

import me.chan.texas.misc.RectF;
import me.chan.texas.misc.Rect;

import android.graphics.Region;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.graphics.fonts.Font;
import android.graphics.text.MeasuredText;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TexasCanvasImpl implements TexasCanvas {
	private Canvas mCanvas;
	private android.graphics.RectF mRawRectF;
	private android.graphics.Rect mRawRect;
	private boolean mModified = false;

	public void reset(Canvas canvas) {
		mCanvas = canvas;
		mModified = false;
	}

	public boolean isModified() {
		return mModified;
	}

	@Override
	public boolean isHardwareAccelerated() {
		return mCanvas.isHardwareAccelerated();
	}

	@Override
	public void setBitmap(@Nullable Bitmap bitmap) {
		mCanvas.setBitmap(bitmap);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void enableZ() {
		mCanvas.enableZ();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void disableZ() {
		mCanvas.disableZ();
	}

	@Override
	public boolean isOpaque() {
		return mCanvas.isOpaque();
	}

	@Override
	public int getWidth() {
		return mCanvas.getWidth();
	}

	@Override
	public int getHeight() {
		return mCanvas.getHeight();
	}

	@Override
	public int getDensity() {
		return mCanvas.getDensity();
	}

	@Override
	public void setDensity(int density) {
		mCanvas.setDensity(density);
	}

	@Override
	public int getMaximumBitmapWidth() {
		return mCanvas.getMaximumBitmapWidth();
	}

	@Override
	public int getMaximumBitmapHeight() {
		return mCanvas.getMaximumBitmapHeight();
	}

	@Override
	public int save() {
		return mCanvas.save();
	}

	@Override
	public int saveLayer(@Nullable RectF bounds, @Nullable TexasPaint paint, int saveFlags) {
		return mCanvas.saveLayer(toRaw(bounds), paint != null ? paint.getPaint() : null, saveFlags);
	}

	@Override
	public int saveLayer(@Nullable RectF bounds, @Nullable TexasPaint paint) {
		return mCanvas.saveLayer(toRaw(bounds), paint != null ? paint.getPaint() : null);
	}

	@Override
	public int saveLayer(float left, float top, float right, float bottom, @Nullable TexasPaint paint, int saveFlags) {
		return mCanvas.saveLayer(left, top, right, bottom, paint != null ? paint.getPaint() : null, saveFlags);
	}

	@Override
	public int saveLayer(float left, float top, float right, float bottom, @Nullable TexasPaint paint) {
		return mCanvas.saveLayer(left, top, right, bottom, paint != null ? paint.getPaint() : null);
	}

	@Override
	public int saveLayerAlpha(@Nullable RectF bounds, int alpha, int saveFlags) {
		return mCanvas.saveLayerAlpha(toRaw(bounds), alpha, saveFlags);
	}

	@Override
	public int saveLayerAlpha(@Nullable RectF bounds, int alpha) {
		return mCanvas.saveLayerAlpha(toRaw(bounds), alpha);
	}

	@Override
	public int saveLayerAlpha(float left, float top, float right, float bottom, int alpha, int saveFlags) {
		return mCanvas.saveLayerAlpha(left, top, right, bottom, alpha, saveFlags);
	}

	@Override
	public int saveLayerAlpha(float left, float top, float right, float bottom, int alpha) {
		return mCanvas.saveLayerAlpha(left, top, right, bottom, alpha);
	}

	@Override
	public void restore() {
		mCanvas.restore();
	}

	@Override
	public int getSaveCount() {
		return mCanvas.getSaveCount();
	}

	@Override
	public void restoreToCount(int saveCount) {
		mCanvas.restoreToCount(saveCount);
	}

	@Override
	public void translate(float dx, float dy) {

		mCanvas.translate(dx, dy);
	}

	@Override
	public void scale(float sx, float sy) {

		mCanvas.scale(sx, sy);
	}

	@Override
	public void scale(float sx, float sy, float px, float py) {

		mCanvas.scale(sx, sy, px, py);
	}

	@Override
	public void rotate(float degrees) {
		mCanvas.rotate(degrees);
	}

	@Override
	public void rotate(float degrees, float px, float py) {
		mCanvas.rotate(degrees, px, py);
	}

	@Override
	public void skew(float sx, float sy) {
		mCanvas.skew(sx, sy);
	}

	@Override
	public void concat(@Nullable Matrix matrix) {
		mCanvas.concat(matrix);
	}

	@RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
	@Override
	public void concat(@Nullable Matrix44 m) {
		mCanvas.concat(m);
	}

	@Override
	public void setMatrix(@Nullable Matrix matrix) {
		mCanvas.setMatrix(matrix);
	}

	@Override
	public void getMatrix(@NonNull Matrix ctm) {
		mCanvas.getMatrix(ctm);
	}

	@NonNull
	@Override
	public Matrix getMatrix() {
		Matrix matrix = new Matrix();
		mCanvas.getMatrix(matrix);
		return matrix;
	}

	@Override
	public boolean clipRect(@NonNull RectF rect, @NonNull Region.Op op) {

		return mCanvas.clipRect(toRaw(rect), op);
	}

	@Override
	public boolean clipRect(@NonNull Rect rect, @NonNull Region.Op op) {
		return mCanvas.clipRect(toRaw(rect), op);
	}

	@Override
	public boolean clipRect(@NonNull RectF rect) {
		return mCanvas.clipRect(toRaw(rect));
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public boolean clipOutRect(@NonNull RectF rect) {
		return mCanvas.clipOutRect(toRaw(rect));
	}

	@Override
	public boolean clipRect(@NonNull Rect rect) {
		return mCanvas.clipRect(toRaw(rect));
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public boolean clipOutRect(@NonNull Rect rect) {
		return mCanvas.clipOutRect(toRaw(rect));
	}

	@Override
	public boolean clipRect(float left, float top, float right, float bottom, @NonNull Region.Op op) {
		return mCanvas.clipRect(left, top, right, bottom, op);
	}

	@Override
	public boolean clipRect(float left, float top, float right, float bottom) {
		return mCanvas.clipRect(left, top, right, bottom);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public boolean clipOutRect(float left, float top, float right, float bottom) {
		return mCanvas.clipOutRect(left, top, right, bottom);
	}

	@Override
	public boolean clipRect(int left, int top, int right, int bottom) {
		return mCanvas.clipRect(left, top, right, bottom);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public boolean clipOutRect(int left, int top, int right, int bottom) {
		return mCanvas.clipOutRect(left, top, right, bottom);
	}

	@Override
	public boolean clipPath(@NonNull Path path, @NonNull Region.Op op) {
		return mCanvas.clipPath(path, op);
	}

	@Override
	public boolean clipPath(@NonNull Path path) {
		return mCanvas.clipPath(path);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public boolean clipOutPath(@NonNull Path path) {
		return mCanvas.clipOutPath(path);
	}

	@RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
	@Override
	public void clipShader(@NonNull Shader shader) {
		mCanvas.clipShader(shader);
	}

	@RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
	@Override
	public void clipOutShader(@NonNull Shader shader) {
		mCanvas.clipOutShader(shader);
	}

	@Nullable
	@Override
	public DrawFilter getDrawFilter() {
		return mCanvas.getDrawFilter();
	}

	@Override
	public void setDrawFilter(@Nullable DrawFilter filter) {
		mCanvas.setDrawFilter(filter);
	}

	@Override
	public boolean quickReject(@NonNull RectF rect, @NonNull Canvas.EdgeType type) {
		return mCanvas.quickReject(toRaw(rect), type);
	}

	@RequiresApi(api = Build.VERSION_CODES.R)
	@Override
	public boolean quickReject(@NonNull RectF rect) {
		return mCanvas.quickReject(toRaw(rect));
	}

	@Override
	public boolean quickReject(@NonNull Path path, @NonNull Canvas.EdgeType type) {
		return mCanvas.quickReject(path, type);
	}

	@RequiresApi(api = Build.VERSION_CODES.R)
	@Override
	public boolean quickReject(@NonNull Path path) {
		return mCanvas.quickReject(path);
	}

	@Override
	public boolean quickReject(float left, float top, float right, float bottom, @NonNull Canvas.EdgeType type) {
		return mCanvas.quickReject(left, top, right, bottom, type);
	}

	@RequiresApi(api = Build.VERSION_CODES.R)
	@Override
	public boolean quickReject(float left, float top, float right, float bottom) {
		return mCanvas.quickReject(left, top, right, bottom);
	}

	@Override
	public boolean getClipBounds(@NonNull Rect bounds) {
		return mCanvas.getClipBounds(toRaw(bounds));
	}

	@NonNull
	@Override
	public Rect getClipBounds() {
		android.graphics.Rect rect = mCanvas.getClipBounds();
		return new Rect(rect.left, rect.top, rect.right, rect.bottom);
	}

	@Override
	public void drawPicture(@NonNull Picture picture) {
		mCanvas.drawPicture(picture);
	}

	@Override
	public void drawPicture(@NonNull Picture picture, @NonNull RectF dst) {
		mCanvas.drawPicture(picture, toRaw(dst));
	}

	@Override
	public void drawPicture(@NonNull Picture picture, @NonNull Rect dst) {
		mCanvas.drawPicture(picture, toRaw(dst));
	}

	@Override
	public void drawArc(@NonNull RectF oval, float startAngle, float sweepAngle, boolean useCenter, @NonNull TexasPaint paint) {
		mCanvas.drawArc(toRaw(oval), startAngle, sweepAngle, useCenter, paint.getPaint());
	}

	@Override
	public void drawArc(float left, float top, float right, float bottom, float startAngle, float sweepAngle, boolean useCenter, @NonNull TexasPaint paint) {
		mCanvas.drawArc(left, top, right, bottom, startAngle, sweepAngle, useCenter, paint.getPaint());
	}

	@Override
	public void drawARGB(int a, int r, int g, int b) {
		mCanvas.drawARGB(a, r, g, b);
	}

	@Override
	public void drawBitmap(@NonNull Bitmap bitmap, float left, float top, @Nullable TexasPaint paint) {
		mCanvas.drawBitmap(bitmap, left, top, paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawBitmap(@NonNull Bitmap bitmap, @Nullable Rect src, @NonNull RectF dst, @Nullable TexasPaint paint) {
		mCanvas.drawBitmap(bitmap, toRaw(src), toRaw(dst), paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawBitmap(@NonNull Bitmap bitmap, @Nullable Rect src, @NonNull Rect dst, @Nullable TexasPaint paint) {
		mCanvas.drawBitmap(bitmap, toRaw(src), toRaw(dst), paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawBitmap(@NonNull int[] colors, int offset, int stride, float x, float y, int width, int height, boolean hasAlpha, @Nullable TexasPaint paint) {
		mCanvas.drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawBitmap(@NonNull int[] colors, int offset, int stride, int x, int y, int width, int height, boolean hasAlpha, @Nullable TexasPaint paint) {
		mCanvas.drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawBitmap(@NonNull Bitmap bitmap, @NonNull Matrix matrix, @Nullable TexasPaint paint) {
		mCanvas.drawBitmap(bitmap, matrix, paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawBitmapMesh(@NonNull Bitmap bitmap, int meshWidth, int meshHeight, @NonNull float[] verts, int vertOffset, @Nullable int[] colors, int colorOffset, @Nullable TexasPaint paint) {
		mCanvas.drawBitmapMesh(bitmap, meshWidth, meshHeight, verts, vertOffset, colors, colorOffset, paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawCircle(float cx, float cy, float radius, @NonNull TexasPaint paint) {
		mCanvas.drawCircle(cx, cy, radius, paint.getPaint());
	}

	@Override
	public void drawColor(int color) {
		mCanvas.drawColor(color);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void drawColor(long color) {
		mCanvas.drawColor(color);
	}

	@Override
	public void drawColor(int color, @NonNull PorterDuff.Mode mode) {
		mCanvas.drawColor(color, mode);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void drawColor(int color, @NonNull BlendMode mode) {
		mCanvas.drawColor(color, mode);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void drawColor(long color, @NonNull BlendMode mode) {
		mCanvas.drawColor(color, mode);
	}

	@Override
	public void drawLine(float startX, float startY, float stopX, float stopY, @NonNull TexasPaint paint) {
		mCanvas.drawLine(startX, startY, stopX, stopY, paint.getPaint());
	}

	@Override
	public void drawLines(@NonNull float[] pts, int offset, int count, @NonNull TexasPaint paint) {
		mCanvas.drawLines(pts, offset, count, paint.getPaint());
	}

	@Override
	public void drawLines(@NonNull float[] pts, @NonNull TexasPaint paint) {
		mCanvas.drawLines(pts, paint.getPaint());
	}

	@Override
	public void drawOval(@NonNull RectF oval, @NonNull TexasPaint paint) {
		mCanvas.drawOval(toRaw(oval), paint.getPaint());
	}

	@Override
	public void drawOval(float left, float top, float right, float bottom, @NonNull TexasPaint paint) {
		mCanvas.drawOval(left, top, right, bottom, paint.getPaint());
	}

	@Override
	public void drawPaint(@NonNull TexasPaint paint) {
		mCanvas.drawPaint(paint.getPaint());
	}

	@RequiresApi(api = Build.VERSION_CODES.S)
	@Override
	public void drawPatch(@NonNull NinePatch patch, @NonNull Rect dst, @Nullable TexasPaint paint) {
		mCanvas.drawPatch(patch, toRaw(dst), paint != null ? paint.getPaint() : null);
	}

	@RequiresApi(api = Build.VERSION_CODES.S)
	@Override
	public void drawPatch(@NonNull NinePatch patch, @NonNull RectF dst, @Nullable TexasPaint paint) {
		mCanvas.drawPatch(patch, toRaw(dst), paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawPath(@NonNull Path path, @NonNull TexasPaint paint) {
		mCanvas.drawPath(path, paint.getPaint());
	}

	@Override
	public void drawPoint(float x, float y, @NonNull TexasPaint paint) {
		mCanvas.drawPoint(x, y, paint.getPaint());
	}

	@Override
	public void drawPoints(float[] pts, int offset, int count, @NonNull TexasPaint paint) {
		mCanvas.drawPoints(pts, offset, count, paint.getPaint());
	}

	@Override
	public void drawPoints(@NonNull float[] pts, @NonNull TexasPaint paint) {
		mCanvas.drawPoints(pts, paint.getPaint());
	}

	@Override
	public void drawPosText(@NonNull char[] text, int index, int count, @NonNull float[] pos, @NonNull TexasPaint paint) {
		mCanvas.drawPosText(text, index, count, pos, paint.getPaint());
	}

	@Override
	public void drawPosText(@NonNull String text, @NonNull float[] pos, @NonNull TexasPaint paint) {
		mCanvas.drawPosText(text, pos, paint.getPaint());
	}

	@Override
	public void drawRect(@NonNull RectF rect, @NonNull TexasPaint paint) {
		mCanvas.drawRect(toRaw(rect), paint.getPaint());
	}

	@Override
	public void drawRect(@NonNull Rect r, @NonNull TexasPaint paint) {
		mCanvas.drawRect(toRaw(r), paint.getPaint());
	}

	@Override
	public void drawRect(float left, float top, float right, float bottom, @NonNull TexasPaint paint) {
		mCanvas.drawRect(left, top, right, bottom, paint.getPaint());
	}

	@Override
	public void drawRGB(int r, int g, int b) {
		mCanvas.drawRGB(r, g, b);
	}

	@Override
	public void drawRoundRect(@NonNull RectF rect, float rx, float ry, @NonNull TexasPaint paint) {
		mCanvas.drawRoundRect(toRaw(rect), rx, ry, paint.getPaint());
	}

	@Override
	public void drawRoundRect(float left, float top, float right, float bottom, float rx, float ry, @NonNull TexasPaint paint) {
		mCanvas.drawRoundRect(left, top, right, bottom, rx, ry, paint.getPaint());
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void drawDoubleRoundRect(@NonNull RectF outer, float outerRx, float outerRy, @NonNull RectF inner, float innerRx, float innerRy, @NonNull TexasPaint paint) {
		mCanvas.drawDoubleRoundRect(toRaw(outer), outerRx, outerRy, toRaw(inner), innerRx, innerRy, paint.getPaint());
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void drawDoubleRoundRect(@NonNull RectF outer, @NonNull float[] outerRadii, @NonNull RectF inner, @NonNull float[] innerRadii, @NonNull TexasPaint paint) {
		mCanvas.drawDoubleRoundRect(toRaw(outer), outerRadii, toRaw(inner), innerRadii, paint.getPaint());
	}

	@RequiresApi(api = Build.VERSION_CODES.S)
	@Override
	public void drawGlyphs(@NonNull int[] glyphIds, int glyphIdOffset, @NonNull float[] positions, int positionOffset, int glyphCount, @NonNull Font font, @NonNull TexasPaint paint) {
		mCanvas.drawGlyphs(glyphIds, glyphIdOffset, positions, positionOffset, glyphCount, font, paint.getPaint());
	}

	@Override
	public void drawText(@NonNull char[] text, int index, int count, float x, float y, @NonNull TexasPaint paint) {
		mCanvas.drawText(text, index, count, x, y, paint.getPaint());
	}

	@Override
	public void drawText(@NonNull String text, float x, float y, @NonNull TexasPaint paint) {
		mCanvas.drawText(text, x, y, paint.getPaint());
	}

	@Override
	public void drawText(@NonNull String text, int start, int end, float x, float y, @NonNull TexasPaint paint) {
		mCanvas.drawText(text, start, end, x, y, paint.getPaint());
	}

	@Override
	public void drawText(@NonNull CharSequence text, int start, int end, float x, float y, @NonNull TexasPaint paint) {
		mCanvas.drawText(text, start, end, x, y, paint.getPaint());
	}

	@Override
	public void drawTextOnPath(@NonNull char[] text, int index, int count, @NonNull Path path, float hOffset, float vOffset, @NonNull TexasPaint paint) {
		mCanvas.drawTextOnPath(text, index, count, path, hOffset, vOffset, paint.getPaint());
	}

	@Override
	public void drawTextOnPath(@NonNull String text, @NonNull Path path, float hOffset, float vOffset, @NonNull TexasPaint paint) {
		mCanvas.drawTextOnPath(text, path, hOffset, vOffset, paint.getPaint());
	}

	@Override
	public void drawTextRun(@NonNull char[] text, int index, int count, int contextIndex, int contextCount, float x, float y, boolean isRtl, @NonNull TexasPaint paint) {
		mCanvas.drawTextRun(text, index, count, contextIndex, contextCount, x, y, isRtl, paint.getPaint());
	}

	@Override
	public void drawTextRun(@NonNull CharSequence text, int start, int end, int contextStart, int contextEnd, float x, float y, boolean isRtl, @NonNull TexasPaint paint) {
		mCanvas.drawTextRun(text, start, end, contextStart, contextEnd, x, y, isRtl, paint.getPaint());
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void drawTextRun(@NonNull MeasuredText text, int start, int end, int contextStart, int contextEnd, float x, float y, boolean isRtl, @NonNull TexasPaint paint) {
		mCanvas.drawTextRun(text, start, end, contextStart, contextEnd, x, y, isRtl, paint.getPaint());
	}

	@Override
	public void drawVertices(@NonNull Canvas.VertexMode mode, int vertexCount, @NonNull float[] verts, int vertOffset, @Nullable float[] texs, int texOffset, @Nullable int[] colors, int colorOffset, @Nullable short[] indices, int indexOffset, int indexCount, @NonNull TexasPaint paint) {
		mCanvas.drawVertices(mode, vertexCount, verts, vertOffset, texs, texOffset, colors, colorOffset, indices, indexOffset, indexCount, paint.getPaint());
	}

	@Override
	@RequiresApi(api = Build.VERSION_CODES.Q)
	public void drawRenderNode(@NonNull RenderNode renderNode) {
		mCanvas.drawRenderNode(renderNode);
	}

	@Override
	@RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
	public void drawMesh(@NonNull Mesh mesh, @Nullable BlendMode blendMode, @NonNull TexasPaint paint) {
		mCanvas.drawMesh(mesh, blendMode, paint.getPaint());
	}

	private android.graphics.RectF toRaw(@Nullable RectF rect) {
		if (rect == null) {
			return null;
		}

		if (mRawRectF == null) {
			mRawRectF = new android.graphics.RectF();
		}

		mRawRectF.set(rect.left, rect.top, rect.right, rect.bottom);
		return mRawRectF;
	}

	private android.graphics.Rect toRaw(@Nullable Rect rect) {
		if (rect == null) {
			return null;
		}

		if (mRawRect == null) {
			mRawRect = new android.graphics.Rect();
		}

		mRawRect.set(rect.left, rect.top, rect.right, rect.bottom);
		return mRawRect;
	}
}