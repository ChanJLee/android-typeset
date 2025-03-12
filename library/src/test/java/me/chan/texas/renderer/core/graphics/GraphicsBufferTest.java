package me.chan.texas.renderer.core.graphics;

import android.graphics.Picture;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import me.chan.texas.Texas;
import me.chan.texas.utils.concurrency.TaskQueue;
import me.chan.texas.di.DaggerFakeTexasComponent;

public class GraphicsBufferTest {
	static {
		Texas.setTexasComponent(DaggerFakeTexasComponent.factory().create());
	}

	@Test
	public void testAttach() {
		GraphicsBuffer graphicsBuffer = new GraphicsBuffer();
		try {
			graphicsBuffer.attach(null);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			/* NOOP */
		}

		graphicsBuffer.attach(TaskQueue.Token.newInstance());
		Assert.assertTrue(graphicsBuffer.isAttached());

		graphicsBuffer.detach();
		Assert.assertFalse(graphicsBuffer.isAttached());
	}

	@Test
	public void testDraw() {
		GraphicsBuffer.DoubleBuffer buffer = new GraphicsBuffer.DoubleBuffer(TaskQueue.Token.newInstance());
		buffer.lockCanvas(1, 1);
		buffer.unlockCanvas();

		Picture picture = buffer.getPicture();
		Assert.assertNotNull(picture);

		buffer.release();
		Assert.assertNull(buffer.getPicture());
	}

	@Test
	public void testReleaseWhenDraw() {
		GraphicsBuffer.DoubleBuffer buffer = new GraphicsBuffer.DoubleBuffer(TaskQueue.Token.newInstance());
		CountDownLatch drawLatch = new CountDownLatch(1);
		CountDownLatch releaseLatch = new CountDownLatch(1);
		new Thread(() -> {
			buffer.lockCanvas(1, 1);
			drawLatch.countDown();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			buffer.unlockCanvas();
			releaseLatch.countDown();
		}).start();

		try {
			drawLatch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		Assert.assertEquals(1, TexturePicture.ALIVE_COUNT.get());
		buffer.release();

		try {
			releaseLatch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		Assert.assertEquals(0, TexturePicture.ALIVE_COUNT.get());
	}

	@Test
	public void testReleaseBeforeDraw() {
		GraphicsBuffer.DoubleBuffer buffer = new GraphicsBuffer.DoubleBuffer(TaskQueue.Token.newInstance());
		buffer.release();
		Assert.assertEquals(0, TexturePicture.ALIVE_COUNT.get());
		new Thread(() -> {
			Assert.assertNull(buffer.lockCanvas(1, 1));
		}).start();
	}

	@Test
	public void testReleaseAfterDraw() {
		GraphicsBuffer.DoubleBuffer buffer = new GraphicsBuffer.DoubleBuffer(TaskQueue.Token.newInstance());
		CountDownLatch releaseLatch = new CountDownLatch(1);
		new Thread(() -> {
			buffer.lockCanvas(1, 1);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			buffer.unlockCanvas();
			releaseLatch.countDown();
		}).start();

		try {
			releaseLatch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		buffer.release();
		Assert.assertEquals(0, TexturePicture.ALIVE_COUNT.get());
	}
}