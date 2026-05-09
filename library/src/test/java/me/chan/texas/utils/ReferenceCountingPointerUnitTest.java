package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import me.chan.texas.text.layout.TextSpan;

public class ReferenceCountingPointerUnitTest {

	// 计数 listener 调用次数与最后一次收到的指针
	private static final class RecordingListener<T>
		implements ReferenceCountingPointer.Listener<T> {
		final AtomicInteger calls = new AtomicInteger();
		final AtomicReference<T> last = new AtomicReference<>();

		@Override
		public void onRelease(T v) {
			calls.incrementAndGet();
			last.set(v);
		}
	}

	// 在 copy 构造时会包装 pointer 的子类，用于覆盖 onAcquire
	private static final class WrappingPointer
		extends ReferenceCountingPointer<String> {
		WrappingPointer(String p, Listener<String> l) {
			super(p, l);
		}

		WrappingPointer(ReferenceCountingPointer<String> other) {
			super(other);
		}

		@Override
		protected String onAcquire(String value) {
			return "wrapped:" + value;
		}
	}

	// ============================================================
	// 原始端到端测试 — 与真实 TextSpan 集成
	// ============================================================

	@Test
	public void test() {
		TextSpan span = TextSpan.obtain("1", 0, 1, null, null, null, null);
		Assert.assertFalse(span.isRecycled());

		ReferenceCountingPointer<TextSpan> pointer = new ReferenceCountingPointer<TextSpan>(
			span, new ReferenceCountingPointer.Listener<TextSpan>() {
			@Override
			public void onRelease(TextSpan v) {
				v.recycle();
			}
		});

		ReferenceCountingPointer<TextSpan> copy = new ReferenceCountingPointer<>(pointer);

		pointer.release();
		Assert.assertFalse(span.isRecycled());

		copy.release();
		Assert.assertTrue(span.isRecycled());
	}

	// ============================================================
	// 主构造函数 / get / 初始引用计数
	// ============================================================

	@Test
	public void primaryConstructor_initialRefCountIsOne() {
		ReferenceCountingPointer<String> p = new ReferenceCountingPointer<>(
			"data", new RecordingListener<String>());
		Assert.assertEquals(1, p.getRefCount());
	}

	@Test
	public void get_returnsSameInstanceAsConstructed() {
		String original = "hello";
		ReferenceCountingPointer<String> p = new ReferenceCountingPointer<>(
			original, new RecordingListener<String>());
		Assert.assertSame(original, p.get());
	}

	@Test
	public void primaryConstructor_doesNotInvokeOnAcquire() {
		// onAcquire 仅在 copy 构造时被调用；主构造直接存储指针
		WrappingPointer wp = new WrappingPointer("hello", new RecordingListener<String>());
		Assert.assertEquals("hello", wp.get());
	}

	// ============================================================
	// 单一引用 release
	// ============================================================

	@Test
	public void release_atRefCountOne_invokesListenerWithStoredPointer() {
		String original = "data";
		RecordingListener<String> listener = new RecordingListener<>();
		ReferenceCountingPointer<String> p = new ReferenceCountingPointer<>(original, listener);

		p.release();

		Assert.assertEquals(1, listener.calls.get());
		Assert.assertSame(original, listener.last.get());
		Assert.assertEquals(0, p.getRefCount());
	}

	@Test(expected = IllegalStateException.class)
	public void release_pastZero_throwsIllegalStateException() {
		ReferenceCountingPointer<String> p = new ReferenceCountingPointer<>(
			"data", new RecordingListener<String>());
		p.release(); // 1 -> 0
		p.release(); // 0 -> -1 → 抛
	}

	@Test
	public void release_pastZero_throwsButLeavesCounterAtMinusOne() {
		ReferenceCountingPointer<String> p = new ReferenceCountingPointer<>(
			"data", new RecordingListener<String>());
		p.release();

		try {
			p.release();
			Assert.fail("expected IllegalStateException");
		} catch (IllegalStateException ignore) {
		}
		Assert.assertEquals(-1, p.getRefCount());
	}

	// ============================================================
	// copy 构造 — 引用计数共享
	// ============================================================

	@Test
	public void copyConstructor_incrementsRefCount() {
		ReferenceCountingPointer<String> p = new ReferenceCountingPointer<>(
			"data", new RecordingListener<String>());
		ReferenceCountingPointer<String> copy = new ReferenceCountingPointer<>(p);

		Assert.assertEquals(2, p.getRefCount());
		Assert.assertEquals(2, copy.getRefCount());
	}

	@Test
	public void copyConstructor_sharesRefCounter_acrossInstances() {
		ReferenceCountingPointer<String> p = new ReferenceCountingPointer<>(
			"data", new RecordingListener<String>());
		ReferenceCountingPointer<String> copy = new ReferenceCountingPointer<>(p);

		// 任一实例的 release 都会反映在另一实例的计数上
		copy.release();
		Assert.assertEquals(1, p.getRefCount());
		Assert.assertEquals(1, copy.getRefCount());
	}

	@Test
	public void copyConstructor_defaultOnAcquireIsIdentity() {
		String original = "data";
		ReferenceCountingPointer<String> p = new ReferenceCountingPointer<>(
			original, new RecordingListener<String>());
		ReferenceCountingPointer<String> copy = new ReferenceCountingPointer<>(p);

		Assert.assertSame(original, copy.get());
	}

	@Test
	public void copyConstructor_doesNotInvokeListener() {
		RecordingListener<String> listener = new RecordingListener<>();
		ReferenceCountingPointer<String> p = new ReferenceCountingPointer<>("data", listener);
		new ReferenceCountingPointer<>(p);
		new ReferenceCountingPointer<>(p);

		Assert.assertEquals(0, listener.calls.get());
	}

	// ============================================================
	// release 链路 — 仅最后一次释放触发 listener
	// ============================================================

	@Test
	public void release_whileStillReferenced_doesNotInvokeListener() {
		RecordingListener<String> listener = new RecordingListener<>();
		ReferenceCountingPointer<String> p = new ReferenceCountingPointer<>("data", listener);
		ReferenceCountingPointer<String> copy = new ReferenceCountingPointer<>(p);

		p.release();
		Assert.assertEquals(0, listener.calls.get());
		Assert.assertEquals(1, copy.getRefCount());

		copy.release();
		Assert.assertEquals(1, listener.calls.get());
	}

	@Test
	public void release_finalReleaseFromCopyFirst_isOrderInvariantForCallCount() {
		RecordingListener<String> listener = new RecordingListener<>();
		ReferenceCountingPointer<String> p = new ReferenceCountingPointer<>("data", listener);
		ReferenceCountingPointer<String> copy = new ReferenceCountingPointer<>(p);

		copy.release();
		Assert.assertEquals(0, listener.calls.get());
		p.release();
		Assert.assertEquals(1, listener.calls.get());
	}

	@Test
	public void release_manyCopies_balancedReleases_listenerInvokedOnce() {
		RecordingListener<String> listener = new RecordingListener<>();
		ReferenceCountingPointer<String> p = new ReferenceCountingPointer<>("data", listener);

		ReferenceCountingPointer<?>[] copies = new ReferenceCountingPointer<?>[10];
		for (int i = 0; i < copies.length; ++i) {
			copies[i] = new ReferenceCountingPointer<>(p);
		}
		Assert.assertEquals(11, p.getRefCount());

		for (ReferenceCountingPointer<?> c : copies) {
			c.release();
		}
		Assert.assertEquals(0, listener.calls.get());

		p.release();
		Assert.assertEquals(1, listener.calls.get());
	}

	// ============================================================
	// 子类 onAcquire 行为
	// ============================================================

	@Test
	public void subclass_onAcquireOverride_transformsPointerOnCopy() {
		WrappingPointer original = new WrappingPointer("hello", new RecordingListener<String>());
		Assert.assertEquals("hello", original.get());

		WrappingPointer copy = new WrappingPointer(original);
		Assert.assertEquals("wrapped:hello", copy.get());
		Assert.assertEquals("hello", original.get());
	}

	@Test
	public void subclass_finalReleaseFromCopy_passesCopyLocalPointer() {
		// 共享的是引用计数，但每个实例拥有自己的 mPointer。
		// 触发 onRelease 的实例决定 listener 收到哪一份指针。
		RecordingListener<String> listener = new RecordingListener<>();
		WrappingPointer original = new WrappingPointer("hello", listener);
		WrappingPointer copy = new WrappingPointer(original);

		original.release();             // count 2 -> 1, 不触发
		copy.release();                 // count 1 -> 0, copy 持有 "wrapped:hello"

		Assert.assertEquals(1, listener.calls.get());
		Assert.assertEquals("wrapped:hello", listener.last.get());
	}

	@Test
	public void subclass_finalReleaseFromOriginal_passesOriginalLocalPointer() {
		RecordingListener<String> listener = new RecordingListener<>();
		WrappingPointer original = new WrappingPointer("hello", listener);
		WrappingPointer copy = new WrappingPointer(original);

		copy.release();                 // count 2 -> 1, 不触发
		original.release();             // count 1 -> 0, original 持有 "hello"

		Assert.assertEquals(1, listener.calls.get());
		Assert.assertEquals("hello", listener.last.get());
	}

	// ============================================================
	// 并发
	// ============================================================

	@Test
	public void concurrentCopiesAndReleases_listenerInvokedExactlyOnce() throws Exception {
		final RecordingListener<String> listener = new RecordingListener<>();
		final ReferenceCountingPointer<String> p = new ReferenceCountingPointer<>(
			"data", listener);

		final int threads = 32;
		final int copiesPerThread = 100;
		final CountDownLatch ready = new CountDownLatch(threads);
		final CountDownLatch start = new CountDownLatch(1);
		final CountDownLatch done = new CountDownLatch(threads);

		for (int i = 0; i < threads; ++i) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					ready.countDown();
					try {
						start.await();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					for (int j = 0; j < copiesPerThread; ++j) {
						ReferenceCountingPointer<String> copy =
							new ReferenceCountingPointer<>(p);
						copy.release();
					}
					done.countDown();
				}
			}).start();
		}

		ready.await();
		start.countDown();
		done.await();

		// 全部 copy 已被自身释放，原始仍持有最后一份引用
		Assert.assertEquals(0, listener.calls.get());
		Assert.assertEquals(1, p.getRefCount());

		p.release();
		Assert.assertEquals(1, listener.calls.get());
		Assert.assertEquals(0, p.getRefCount());
	}
}