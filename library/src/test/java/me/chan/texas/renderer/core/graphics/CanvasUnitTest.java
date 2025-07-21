package me.chan.texas.renderer.core.graphics;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix44;
import android.graphics.Path;
import android.graphics.Picture;

import org.junit.Assert;
import org.junit.Test;

import me.chan.texas.misc.Rect;
import me.chan.texas.misc.RectF;

public class CanvasUnitTest {

	/**
	 * 参考 {@link TexasCanvas} 接口，按照顺序编写测试用例，判断包含写的api是否修改了canvas
	 */
	@Test
	public void test() {
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
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.quickReject(new RectF());
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.quickReject(new Path());
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.quickReject(1F, 1F, 1F, 1F, null);
		Assert.assertTrue(canvas.isModified());

		canvas.reset(raw);
		Assert.assertFalse(canvas.isModified());
		canvas.quickReject(1F, 1F, 1F, 1F);
		Assert.assertTrue(canvas.isModified());

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

	}
}