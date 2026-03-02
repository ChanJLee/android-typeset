package me.chan.texas.renderer.core.graphics;

import android.graphics.Picture;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import me.chan.texas.Texas;
import me.chan.texas.utils.concurrency.Worker;
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

		graphicsBuffer.attach(Worker.Token.newInstance());
		Assert.assertTrue(graphicsBuffer.isAttached());

		graphicsBuffer.detach();
		Assert.assertFalse(graphicsBuffer.isAttached());
	}

	@Test
	public void testDraw() {
		GraphicsBuffer.DoubleBuffer buffer = new GraphicsBuffer.DoubleBuffer(Worker.Token.newInstance());
		buffer.lockCanvas(1, 1);
		buffer.unlockCanvas();

		Picture picture = buffer.getPicture();
		Assert.assertNotNull(picture);

		buffer.release();
		Assert.assertNull(buffer.getPicture());
	}

	@Test
	public void testReleaseWhenDraw() {
		GraphicsBuffer.DoubleBuffer buffer = new GraphicsBuffer.DoubleBuffer(Worker.Token.newInstance());
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
		GraphicsBuffer.DoubleBuffer buffer = new GraphicsBuffer.DoubleBuffer(Worker.Token.newInstance());
		buffer.release();
		Assert.assertEquals(0, TexturePicture.ALIVE_COUNT.get());
		new Thread(() -> {
			Assert.assertNull(buffer.lockCanvas(1, 1));
		}).start();
	}

	@Test
	public void testReleaseAfterDraw() {
		GraphicsBuffer.DoubleBuffer buffer = new GraphicsBuffer.DoubleBuffer(Worker.Token.newInstance());
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

	@Test
	public void testConcurrentDrawAndRelease() throws InterruptedException {
		// Run multiple iterations to trigger potential race conditions
		int iterations = 100;

		for (int i = 0; i < iterations; i++) {
			final GraphicsBuffer.DoubleBuffer buffer = new GraphicsBuffer.DoubleBuffer(Worker.Token.newInstance());

			// Latch to start threads simultaneously
			final CountDownLatch startSignal = new CountDownLatch(1);
			// Latch to wait for both threads to finish
			final CountDownLatch doneSignal = new CountDownLatch(2);
			// Container to capture exceptions from threads
			final AtomicReference<Throwable> threadError = new AtomicReference<>();

			// Thread 1: Tries to Draw
			Thread drawThread = new Thread(() -> {
				try {
					startSignal.await();
					// Try to lock. If release() happened first, this might return null.
					// If release() happens during lock, lock mechanism should handle it.
					buffer.lockCanvas(100, 100);
					// Simulate a very tiny amount of work to increase race window
					Thread.sleep((long) (Math.random() * 15 + 10));
					buffer.unlockCanvas();
				} catch (Throwable t) {
					threadError.set(t);
				} finally {
					doneSignal.countDown();
				}
			});

			// Thread 2: Tries to Release
			Thread releaseThread = new Thread(() -> {
				try {
					startSignal.await();
					// Randomly yield to vary the timing relative to the draw thread
					Thread.sleep((long) (Math.random() * 10 + 25));
					buffer.release(true);
				} catch (Throwable t) {
					threadError.set(t);
				} finally {
					doneSignal.countDown();
				}
			});

			drawThread.start();
			releaseThread.start();

			// Start both threads
			startSignal.countDown();
			// Wait for both to finish
			doneSignal.await();

			// Fail if any thread threw an exception
			if (threadError.get() != null) {
				throw new RuntimeException("Error in iteration " + i, threadError.get());
			}

			// Critical Check: Regardless of order, memory must be released.
			Assert.assertEquals("Memory leak detected at iteration " + i, 0, TexturePicture.ALIVE_COUNT.get());
		}
	}
}