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

	private Canvas getCanvas(boolean readonly) {
		mModified = mModified || !readonly;
		return mCanvas;
	}

	public boolean isModified() {
		return mModified;
	}

	@Override
	public boolean isHardwareAccelerated() {
		return getCanvas(false).isHardwareAccelerated();
	}

	@Override
	public void setBitmap(@Nullable Bitmap bitmap) {
		getCanvas(false).setBitmap(bitmap);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void enableZ() {
		getCanvas(false).enableZ();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void disableZ() {
		getCanvas(false).disableZ();
	}

	@Override
	public boolean isOpaque() {
		return getCanvas(true).isOpaque();
	}

	@Override
	public int getWidth() {
		return getCanvas(true).getWidth();
	}

	@Override
	public int getHeight() {
		return getCanvas(true).getHeight();
	}

	@Override
	public int getDensity() {
		return getCanvas(true).getDensity();
	}

	@Override
	public void setDensity(int density) {
		getCanvas(false).setDensity(density);
	}

	@Override
	public int getMaximumBitmapWidth() {
		return getCanvas(true).getMaximumBitmapWidth();
	}

	@Override
	public int getMaximumBitmapHeight() {
		return getCanvas(true).getMaximumBitmapHeight();
	}

	@Override
	public int save() {
		return getCanvas(false).save();
	}

	@Override
	public int saveLayer(@Nullable RectF bounds, @Nullable TexasPaint paint, int saveFlags) {
		return getCanvas(false).saveLayer(toRaw(bounds), paint != null ? paint.getPaint() : null, saveFlags);
	}

	@Override
	public int saveLayer(@Nullable RectF bounds, @Nullable TexasPaint paint) {
		return getCanvas(false).saveLayer(toRaw(bounds), paint != null ? paint.getPaint() : null);
	}

	@Override
	public int saveLayer(float left, float top, float right, float bottom, @Nullable TexasPaint paint, int saveFlags) {
		return getCanvas(false).saveLayer(left, top, right, bottom, paint != null ? paint.getPaint() : null, saveFlags);
	}

	@Override
	public int saveLayer(float left, float top, float right, float bottom, @Nullable TexasPaint paint) {
		return getCanvas(false).saveLayer(left, top, right, bottom, paint != null ? paint.getPaint() : null);
	}

	@Override
	public int saveLayerAlpha(@Nullable RectF bounds, int alpha, int saveFlags) {
		return getCanvas(false).saveLayerAlpha(toRaw(bounds), alpha, saveFlags);
	}

	@Override
	public int saveLayerAlpha(@Nullable RectF bounds, int alpha) {
		return getCanvas(false).saveLayerAlpha(toRaw(bounds), alpha);
	}

	@Override
	public int saveLayerAlpha(float left, float top, float right, float bottom, int alpha, int saveFlags) {
		return getCanvas(false).saveLayerAlpha(left, top, right, bottom, alpha, saveFlags);
	}

	@Override
	public int saveLayerAlpha(float left, float top, float right, float bottom, int alpha) {
		return getCanvas(false).saveLayerAlpha(left, top, right, bottom, alpha);
	}

	@Override
	public void restore() {
		getCanvas(false).restore();
	}

	@Override
	public int getSaveCount() {
		return getCanvas(true).getSaveCount();
	}

	@Override
	public void restoreToCount(int saveCount) {
		getCanvas(false).restoreToCount(saveCount);
	}

	@Override
	public void translate(float dx, float dy) {
		getCanvas(false).translate(dx, dy);
	}

	@Override
	public void scale(float sx, float sy) {
		getCanvas(false).scale(sx, sy);
	}

	@Override
	public void scale(float sx, float sy, float px, float py) {
		getCanvas(false).scale(sx, sy, px, py);
	}

	@Override
	public void rotate(float degrees) {
		getCanvas(false).rotate(degrees);
	}

	@Override
	public void rotate(float degrees, float px, float py) {
		getCanvas(false).rotate(degrees, px, py);
	}

	@Override
	public void skew(float sx, float sy) {
		getCanvas(false).skew(sx, sy);
	}

	@Override
	public void concat(@Nullable Matrix matrix) {
		getCanvas(false).concat(matrix);
	}

	@RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
	@Override
	public void concat(@Nullable Matrix44 m) {
		getCanvas(false).concat(m);
	}

	@Override
	public void setMatrix(@Nullable Matrix matrix) {
		getCanvas(false).setMatrix(matrix);
	}

	@Override
	public void getMatrix(@NonNull Matrix ctm) {
		getCanvas(true).getMatrix(ctm);
	}

	@NonNull
	@Override
	public Matrix getMatrix() {
		Matrix matrix = new Matrix();
		getCanvas(true).getMatrix(matrix);
		return matrix;
	}

	@Override
	public boolean clipRect(@NonNull RectF rect, @NonNull Region.Op op) {
		return getCanvas(false).clipRect(toRaw(rect), op);
	}

	@Override
	public boolean clipRect(@NonNull Rect rect, @NonNull Region.Op op) {
		return getCanvas(false).clipRect(toRaw(rect), op);
	}

	@Override
	public boolean clipRect(@NonNull RectF rect) {
		return getCanvas(false).clipRect(toRaw(rect));
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public boolean clipOutRect(@NonNull RectF rect) {
		return getCanvas(false).clipOutRect(toRaw(rect));
	}

	@Override
	public boolean clipRect(@NonNull Rect rect) {
		return getCanvas(false).clipRect(toRaw(rect));
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public boolean clipOutRect(@NonNull Rect rect) {
		return getCanvas(false).clipOutRect(toRaw(rect));
	}

	@Override
	public boolean clipRect(float left, float top, float right, float bottom, @NonNull Region.Op op) {
		return getCanvas(false).clipRect(left, top, right, bottom, op);
	}

	@Override
	public boolean clipRect(float left, float top, float right, float bottom) {
		return getCanvas(false).clipRect(left, top, right, bottom);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public boolean clipOutRect(float left, float top, float right, float bottom) {
		return getCanvas(false).clipOutRect(left, top, right, bottom);
	}

	@Override
	public boolean clipRect(int left, int top, int right, int bottom) {
		return getCanvas(false).clipRect(left, top, right, bottom);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public boolean clipOutRect(int left, int top, int right, int bottom) {
		return getCanvas(false).clipOutRect(left, top, right, bottom);
	}

	@Override
	public boolean clipPath(@NonNull Path path, @NonNull Region.Op op) {
		return getCanvas(false).clipPath(path, op);
	}

	@Override
	public boolean clipPath(@NonNull Path path) {
		return getCanvas(false).clipPath(path);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public boolean clipOutPath(@NonNull Path path) {
		return getCanvas(false).clipOutPath(path);
	}

	@RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
	@Override
	public void clipShader(@NonNull Shader shader) {
		getCanvas(false).clipShader(shader);
	}

	@RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
	@Override
	public void clipOutShader(@NonNull Shader shader) {
		getCanvas(false).clipOutShader(shader);
	}

	@Nullable
	@Override
	public DrawFilter getDrawFilter() {
		return getCanvas(true).getDrawFilter();
	}

	@Override
	public void setDrawFilter(@Nullable DrawFilter filter) {
		getCanvas(false).setDrawFilter(filter);
	}

	@Override
	public boolean quickReject(@NonNull RectF rect, @NonNull Canvas.EdgeType type) {
		return getCanvas(false).quickReject(toRaw(rect), type);
	}

	@RequiresApi(api = Build.VERSION_CODES.R)
	@Override
	public boolean quickReject(@NonNull RectF rect) {
		return getCanvas(false).quickReject(toRaw(rect));
	}

	@Override
	public boolean quickReject(@NonNull Path path, @NonNull Canvas.EdgeType type) {
		return getCanvas(false).quickReject(path, type);
	}

	@RequiresApi(api = Build.VERSION_CODES.R)
	@Override
	public boolean quickReject(@NonNull Path path) {
		return getCanvas(false).quickReject(path);
	}

	@Override
	public boolean quickReject(float left, float top, float right, float bottom, @NonNull Canvas.EdgeType type) {
		return getCanvas(false).quickReject(left, top, right, bottom, type);
	}

	@RequiresApi(api = Build.VERSION_CODES.R)
	@Override
	public boolean quickReject(float left, float top, float right, float bottom) {
		return getCanvas(false).quickReject(left, top, right, bottom);
	}

	@Override
	public boolean getClipBounds(@NonNull Rect bounds) {
		return getCanvas(true).getClipBounds(toRaw(bounds));
	}

	@NonNull
	@Override
	public Rect getClipBounds() {
		android.graphics.Rect rect = getCanvas(true).getClipBounds();
		return new Rect(rect.left, rect.top, rect.right, rect.bottom);
	}

	@Override
	public void drawPicture(@NonNull Picture picture) {
		getCanvas(false).drawPicture(picture);
	}

	@Override
	public void drawPicture(@NonNull Picture picture, @NonNull RectF dst) {
		getCanvas(false).drawPicture(picture, toRaw(dst));
	}

	@Override
	public void drawPicture(@NonNull Picture picture, @NonNull Rect dst) {
		getCanvas(false).drawPicture(picture, toRaw(dst));
	}

	@Override
	public void drawArc(@NonNull RectF oval, float startAngle, float sweepAngle, boolean useCenter, @NonNull TexasPaint paint) {
		getCanvas(false).drawArc(toRaw(oval), startAngle, sweepAngle, useCenter, paint.getPaint());
	}

	@Override
	public void drawArc(float left, float top, float right, float bottom, float startAngle, float sweepAngle, boolean useCenter, @NonNull TexasPaint paint) {
		getCanvas(false).drawArc(left, top, right, bottom, startAngle, sweepAngle, useCenter, paint.getPaint());
	}

	@Override
	public void drawARGB(int a, int r, int g, int b) {
		getCanvas(false).drawARGB(a, r, g, b);
	}

	@Override
	public void drawBitmap(@NonNull Bitmap bitmap, float left, float top, @Nullable TexasPaint paint) {
		getCanvas(false).drawBitmap(bitmap, left, top, paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawBitmap(@NonNull Bitmap bitmap, @Nullable Rect src, @NonNull RectF dst, @Nullable TexasPaint paint) {
		getCanvas(false).drawBitmap(bitmap, toRaw(src), toRaw(dst), paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawBitmap(@NonNull Bitmap bitmap, @Nullable Rect src, @NonNull Rect dst, @Nullable TexasPaint paint) {
		getCanvas(false).drawBitmap(bitmap, toRaw(src), toRaw(dst), paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawBitmap(@NonNull int[] colors, int offset, int stride, float x, float y, int width, int height, boolean hasAlpha, @Nullable TexasPaint paint) {
		getCanvas(false).drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawBitmap(@NonNull int[] colors, int offset, int stride, int x, int y, int width, int height, boolean hasAlpha, @Nullable TexasPaint paint) {
		getCanvas(false).drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawBitmap(@NonNull Bitmap bitmap, @NonNull Matrix matrix, @Nullable TexasPaint paint) {
		getCanvas(false).drawBitmap(bitmap, matrix, paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawBitmapMesh(@NonNull Bitmap bitmap, int meshWidth, int meshHeight, @NonNull float[] verts, int vertOffset, @Nullable int[] colors, int colorOffset, @Nullable TexasPaint paint) {
		getCanvas(false).drawBitmapMesh(bitmap, meshWidth, meshHeight, verts, vertOffset, colors, colorOffset, paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawCircle(float cx, float cy, float radius, @NonNull TexasPaint paint) {
		getCanvas(false).drawCircle(cx, cy, radius, paint.getPaint());
	}

	@Override
	public void drawColor(int color) {
		getCanvas(false).drawColor(color);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void drawColor(long color) {
		getCanvas(false).drawColor(color);
	}

	@Override
	public void drawColor(int color, @NonNull PorterDuff.Mode mode) {
		getCanvas(false).drawColor(color, mode);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void drawColor(int color, @NonNull BlendMode mode) {
		getCanvas(false).drawColor(color, mode);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void drawColor(long color, @NonNull BlendMode mode) {
		getCanvas(false).drawColor(color, mode);
	}

	@Override
	public void drawLine(float startX, float startY, float stopX, float stopY, @NonNull TexasPaint paint) {
		getCanvas(false).drawLine(startX, startY, stopX, stopY, paint.getPaint());
	}

	@Override
	public void drawLines(@NonNull float[] pts, int offset, int count, @NonNull TexasPaint paint) {
		getCanvas(false).drawLines(pts, offset, count, paint.getPaint());
	}

	@Override
	public void drawLines(@NonNull float[] pts, @NonNull TexasPaint paint) {
		getCanvas(false).drawLines(pts, paint.getPaint());
	}

	@Override
	public void drawOval(@NonNull RectF oval, @NonNull TexasPaint paint) {
		getCanvas(false).drawOval(toRaw(oval), paint.getPaint());
	}

	@Override
	public void drawOval(float left, float top, float right, float bottom, @NonNull TexasPaint paint) {
		getCanvas(false).drawOval(left, top, right, bottom, paint.getPaint());
	}

	@Override
	public void drawPaint(@NonNull TexasPaint paint) {
		getCanvas(false).drawPaint(paint.getPaint());
	}

	@RequiresApi(api = Build.VERSION_CODES.S)
	@Override
	public void drawPatch(@NonNull NinePatch patch, @NonNull Rect dst, @Nullable TexasPaint paint) {
		getCanvas(false).drawPatch(patch, toRaw(dst), paint != null ? paint.getPaint() : null);
	}

	@RequiresApi(api = Build.VERSION_CODES.S)
	@Override
	public void drawPatch(@NonNull NinePatch patch, @NonNull RectF dst, @Nullable TexasPaint paint) {
		getCanvas(false).drawPatch(patch, toRaw(dst), paint != null ? paint.getPaint() : null);
	}

	@Override
	public void drawPath(@NonNull Path path, @NonNull TexasPaint paint) {
		getCanvas(false).drawPath(path, paint.getPaint());
	}

	@Override
	public void drawPoint(float x, float y, @NonNull TexasPaint paint) {
		getCanvas(false).drawPoint(x, y, paint.getPaint());
	}

	@Override
	public void drawPoints(float[] pts, int offset, int count, @NonNull TexasPaint paint) {
		getCanvas(false).drawPoints(pts, offset, count, paint.getPaint());
	}

	@Override
	public void drawPoints(@NonNull float[] pts, @NonNull TexasPaint paint) {
		getCanvas(false).drawPoints(pts, paint.getPaint());
	}

	@Override
	public void drawPosText(@NonNull char[] text, int index, int count, @NonNull float[] pos, @NonNull TexasPaint paint) {
		getCanvas(false).drawPosText(text, index, count, pos, paint.getPaint());
	}

	@Override
	public void drawPosText(@NonNull String text, @NonNull float[] pos, @NonNull TexasPaint paint) {
		getCanvas(false).drawPosText(text, pos, paint.getPaint());
	}

	@Override
	public void drawRect(@NonNull RectF rect, @NonNull TexasPaint paint) {
		getCanvas(false).drawRect(toRaw(rect), paint.getPaint());
	}

	@Override
	public void drawRect(@NonNull Rect r, @NonNull TexasPaint paint) {
		getCanvas(false).drawRect(toRaw(r), paint.getPaint());
	}

	@Override
	public void drawRect(float left, float top, float right, float bottom, @NonNull TexasPaint paint) {
		getCanvas(false).drawRect(left, top, right, bottom, paint.getPaint());
	}

	@Override
	public void drawRGB(int r, int g, int b) {
		getCanvas(false).drawRGB(r, g, b);
	}

	@Override
	public void drawRoundRect(@NonNull RectF rect, float rx, float ry, @NonNull TexasPaint paint) {
		getCanvas(false).drawRoundRect(toRaw(rect), rx, ry, paint.getPaint());
	}

	@Override
	public void drawRoundRect(float left, float top, float right, float bottom, float rx, float ry, @NonNull TexasPaint paint) {
		getCanvas(false).drawRoundRect(left, top, right, bottom, rx, ry, paint.getPaint());
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void drawDoubleRoundRect(@NonNull RectF outer, float outerRx, float outerRy, @NonNull RectF inner, float innerRx, float innerRy, @NonNull TexasPaint paint) {
		getCanvas(false).drawDoubleRoundRect(toRaw(outer), outerRx, outerRy, toRaw(inner), innerRx, innerRy, paint.getPaint());
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void drawDoubleRoundRect(@NonNull RectF outer, @NonNull float[] outerRadii, @NonNull RectF inner, @NonNull float[] innerRadii, @NonNull TexasPaint paint) {
		getCanvas(false).drawDoubleRoundRect(toRaw(outer), outerRadii, toRaw(inner), innerRadii, paint.getPaint());
	}

	@RequiresApi(api = Build.VERSION_CODES.S)
	@Override
	public void drawGlyphs(@NonNull int[] glyphIds, int glyphIdOffset, @NonNull float[] positions, int positionOffset, int glyphCount, @NonNull Font font, @NonNull TexasPaint paint) {
		getCanvas(false).drawGlyphs(glyphIds, glyphIdOffset, positions, positionOffset, glyphCount, font, paint.getPaint());
	}

	@Override
	public void drawText(@NonNull char[] text, int index, int count, float x, float y, @NonNull TexasPaint paint) {
		getCanvas(false).drawText(text, index, count, x, y, paint.getPaint());
	}

	@Override
	public void drawText(@NonNull String text, float x, float y, @NonNull TexasPaint paint) {
		getCanvas(false).drawText(text, x, y, paint.getPaint());
	}

	@Override
	public void drawText(@NonNull String text, int start, int end, float x, float y, @NonNull TexasPaint paint) {
		getCanvas(false).drawText(text, start, end, x, y, paint.getPaint());
	}

	@Override
	public void drawText(@NonNull CharSequence text, int start, int end, float x, float y, @NonNull TexasPaint paint) {
		getCanvas(false).drawText(text, start, end, x, y, paint.getPaint());
	}

	@Override
	public void drawTextOnPath(@NonNull char[] text, int index, int count, @NonNull Path path, float hOffset, float vOffset, @NonNull TexasPaint paint) {
		getCanvas(false).drawTextOnPath(text, index, count, path, hOffset, vOffset, paint.getPaint());
	}

	@Override
	public void drawTextOnPath(@NonNull String text, @NonNull Path path, float hOffset, float vOffset, @NonNull TexasPaint paint) {
		getCanvas(false).drawTextOnPath(text, path, hOffset, vOffset, paint.getPaint());
	}

	@Override
	public void drawTextRun(@NonNull char[] text, int index, int count, int contextIndex, int contextCount, float x, float y, boolean isRtl, @NonNull TexasPaint paint) {
		getCanvas(false).drawTextRun(text, index, count, contextIndex, contextCount, x, y, isRtl, paint.getPaint());
	}

	@Override
	public void drawTextRun(@NonNull CharSequence text, int start, int end, int contextStart, int contextEnd, float x, float y, boolean isRtl, @NonNull TexasPaint paint) {
		getCanvas(false).drawTextRun(text, start, end, contextStart, contextEnd, x, y, isRtl, paint.getPaint());
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void drawTextRun(@NonNull MeasuredText text, int start, int end, int contextStart, int contextEnd, float x, float y, boolean isRtl, @NonNull TexasPaint paint) {
		getCanvas(false).drawTextRun(text, start, end, contextStart, contextEnd, x, y, isRtl, paint.getPaint());
	}

	@Override
	public void drawVertices(@NonNull Canvas.VertexMode mode, int vertexCount, @NonNull float[] verts, int vertOffset, @Nullable float[] texs, int texOffset, @Nullable int[] colors, int colorOffset, @Nullable short[] indices, int indexOffset, int indexCount, @NonNull TexasPaint paint) {
		getCanvas(false).drawVertices(mode, vertexCount, verts, vertOffset, texs, texOffset, colors, colorOffset, indices, indexOffset, indexCount, paint.getPaint());
	}

	@Override
	@RequiresApi(api = Build.VERSION_CODES.Q)
	public void drawRenderNode(@NonNull RenderNode renderNode) {
		getCanvas(false).drawRenderNode(renderNode);
	}

	@Override
	@RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
	public void drawMesh(@NonNull Mesh mesh, @Nullable BlendMode blendMode, @NonNull TexasPaint paint) {
		getCanvas(false).drawMesh(mesh, blendMode, paint.getPaint());
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