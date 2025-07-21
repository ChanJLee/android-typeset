package me.chan.texas.renderer.core.graphics;

import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix44;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.text.MeasuredText;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.misc.PaintSet;
import me.chan.texas.misc.Rect;
import me.chan.texas.misc.RectF;
import me.chan.texas.test.mock.MockTextPaint;

public class CanvasUnitTest {

	/**
	 * 参考 {@link TexasCanvas} 接口，按照顺序编写测试用例，判断包含写的api是否修改了canvas
	 */
	@Test
	public void test() {
		MockTextPaint textPaint = new MockTextPaint();
		textPaint.setMockTextSize(1);
		PaintSet paintSet = new PaintSet(textPaint);
		TexasPaintImpl texasPaint = new TexasPaintImpl();

		TexasCanvasImpl canvas = new TexasCanvasImpl();
		Canvas raw = new Canvas();

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.isHardwareAccelerated();
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.setBitmap(null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.enableZ();
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.disableZ();
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.isOpaque();
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.getWidth();
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.getHeight();
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.getDensity();
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.setDensity(1);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.getMaximumBitmapWidth();
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.getMaximumBitmapHeight();
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.save();
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.saveLayer(null, null, 0);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.saveLayer(null, null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.saveLayer(0, 0, 1, 1, null, 0);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.saveLayer(0, 0, 1, 1, null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.saveLayerAlpha(null, 0, 0);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.saveLayerAlpha(null, 0);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.saveLayerAlpha(0, 0, 1, 1, 0, 0);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.saveLayerAlpha(0, 0, 1, 1, 0);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.restore();
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.getSaveCount();
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.restoreToCount(0);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.translate(0, 0);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.scale(1, 1);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.scale(1, 1, 0, 0);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.rotate(0);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.rotate(0, 0, 0);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.skew(0, 0);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.concat((Matrix) null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.concat((Matrix44) null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.setMatrix((Matrix) null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.getMatrix();
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.getMatrix(new Matrix());
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.clipRect(new Rect(), null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.clipRect(new RectF(), null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.clipRect(new RectF());
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.clipOutRect(new RectF());
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.clipRect(new Rect());
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.clipOutRect(new Rect());
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.clipRect(1f, 1, 1, 1);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.clipOutRect(1f, 1, 1, 1);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.clipRect(1, 1, 1, 1);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.clipOutRect(1, 1, 1, 1);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.clipPath(null, null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.clipPath(null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.clipOutPath(null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.clipShader(null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.clipOutShader(null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.getDrawFilter();
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.setDrawFilter(null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.quickReject(new RectF(), null);
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.quickReject(new RectF());
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.quickReject(new Path());
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.quickReject(1F, 1F, 1F, 1F, null);
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.quickReject(1F, 1F, 1F, 1F);
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.getClipBounds(new Rect());
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		try {
			canvas.getClipBounds();
		} catch (NullPointerException e) {

		}
		Assert.assertFalse(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawPicture(new Picture());
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawPicture(new Picture(), new RectF());
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawPicture(new Picture(), new Rect());
		Assert.assertTrue(canvas.isModified());
		canvas.reset(raw);

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawArc(new RectF(), 0, 0, false, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawArc(0f, 0f, 0f, 0f, 1, 1, false, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawARGB(1, 1, 1, 1);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawBitmap(null, 0, 0, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawBitmap(null, new Rect(), new RectF(), texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawBitmap(null, new Rect(), new Rect(), texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawBitmap(new int[1], 0, 0, 0, 0, 1, 1, false, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawBitmap(null, null, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawBitmapMesh(null, 1, 1, null, 0, null, 0, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawCircle(0, 0, 0, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawColor(0);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawColor(0L);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawColor(0, PorterDuff.Mode.ADD);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawColor(0, BlendMode.CLEAR);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawColor(0L, BlendMode.CLEAR);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawLine(0, 0, 0, 0, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawLines(null, 0, 0, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawLines(null, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawOval(new RectF(), texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawOval(0f, 0f, 0f, 0f, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawPaint(texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawPatch(null, new Rect(), texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawPatch(null, new RectF(), texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawPoint(0f, 0f, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawPoints(null, 0, 0, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawPoints(null, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawPosText((char[]) null, 1, 1, null, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawPosText(null, null, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawRect(new RectF(), texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawRect(new Rect(), texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawRect(1, 1, 1, 1, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawRGB(1, 1, 1);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawRoundRect(new RectF(), 0, 0, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawRoundRect(0, 0, 0, 0, 0, 0, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawDoubleRoundRect(new RectF(), 0, 0, new RectF(), 0, 0, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawDoubleRoundRect(new RectF(), null, new RectF(), null, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawGlyphs(null, 0, null, 0, 0, null, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawText((char[]) null, 0, 0, 0, 0, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawText((String) null, 0, 0, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawText((String) null, 0, 0, 0, 0, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawText((CharSequence) null, 0, 0, 0, 0, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawTextOnPath((char[]) null, 1, 1, null, 0, 0, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawTextOnPath((String) null, null, 0, 0, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawTextRun((char[]) null, 1, 1, 1, 1, 0, 0, false, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawTextRun((CharSequence) null, 1, 1, 1, 1, 0, 0, false, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawTextRun((MeasuredText) null, 1, 1, 1, 1, 0, 0, false, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawVertices(Canvas.VertexMode.TRIANGLE_FAN, 1, new float[1], 1, new float[1], 1, new int[1], 1, new short[1], 1, 1, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawRenderNode(null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.drawMesh(null, null, texasPaint);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.draw(new ColorDrawable(1));
		Assert.assertTrue(canvas.isModified());
	}
}