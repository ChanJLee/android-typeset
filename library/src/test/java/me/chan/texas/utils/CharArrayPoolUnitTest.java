package me.chan.texas.utils;

import org.junit.Assert;
import org.junit.Test;

public class CharArrayPoolUnitTest {

	// ============================================================
	// 原始端到端测试 — 保留作为 smoke test
	// ============================================================

	@Test
	public void test() {
		CharArrayPool pool = new CharArrayPool();
		char[] chars = pool.obtain(10);
		Assert.assertEquals(chars.length, 16);
		char[] tmp = chars;
		pool.release(chars);
		chars = pool.obtain(10);
		Assert.assertSame(chars, tmp);

		try {
			pool.obtain(-1);
			Assert.fail("test alloc -1 array failed");
		} catch (IllegalArgumentException ignore) {
		}

		chars = pool.obtain(2047);
		Assert.assertEquals(chars.length, 2047);
		pool.release(chars);

		chars = pool.obtain(0);
		Assert.assertEquals(chars.length, 4);
		tmp = chars;
		pool.release(chars);
		chars = pool.obtain(0);
		Assert.assertSame(chars, tmp);

		chars = pool.obtain(4);
		Assert.assertEquals(chars.length, 4);
		tmp = chars;
		pool.release(chars);
		chars = pool.obtain(4);
		Assert.assertSame(chars, tmp);
	}

	// ============================================================
	// obtain — 大小到桶的映射
	// ============================================================

	@Test(expected = IllegalArgumentException.class)
	public void obtain_negativeSize_throws() {
		new CharArrayPool().obtain(-1);
	}

	@Test
	public void obtain_zeroSize_returnsSmallestBucketLength() {
		CharArrayPool pool = new CharArrayPool();
		char[] buf = pool.obtain(0);
		Assert.assertEquals(4, buf.length);
	}

	@Test
	public void obtain_exactBucketSize_returnsSameLength() {
		CharArrayPool pool = new CharArrayPool();
		Assert.assertEquals(4, pool.obtain(4).length);
		Assert.assertEquals(8, pool.obtain(8).length);
		Assert.assertEquals(16, pool.obtain(16).length);
		Assert.assertEquals(32, pool.obtain(32).length);
		Assert.assertEquals(64, pool.obtain(64).length);
		Assert.assertEquals(128, pool.obtain(128).length);
		Assert.assertEquals(512, pool.obtain(512).length);
		Assert.assertEquals(1024, pool.obtain(1024).length);
	}

	@Test
	public void obtain_oneOverBucket_promotesToNext() {
		CharArrayPool pool = new CharArrayPool();
		Assert.assertEquals(8, pool.obtain(5).length);    // 4 → 8
		Assert.assertEquals(16, pool.obtain(9).length);   // 8 → 16
		Assert.assertEquals(32, pool.obtain(17).length);  // 16 → 32
		Assert.assertEquals(64, pool.obtain(33).length);  // 32 → 64
		Assert.assertEquals(128, pool.obtain(65).length); // 64 → 128
		Assert.assertEquals(512, pool.obtain(129).length); // 128 → 512
		Assert.assertEquals(1024, pool.obtain(513).length); // 512 → 1024
	}

	@Test
	public void obtain_beyondLargestBucket_returnsExactSize() {
		CharArrayPool pool = new CharArrayPool();
		Assert.assertEquals(1025, pool.obtain(1025).length);
		Assert.assertEquals(2047, pool.obtain(2047).length);
		Assert.assertEquals(8192, pool.obtain(8192).length);
	}

	@Test
	public void obtain_emptyPool_returnsDistinctArrays() {
		CharArrayPool pool = new CharArrayPool();
		char[] a = pool.obtain(16);
		char[] b = pool.obtain(16);
		char[] c = pool.obtain(16);
		Assert.assertNotSame(a, b);
		Assert.assertNotSame(b, c);
		Assert.assertNotSame(a, c);
	}

	// ============================================================
	// release / 缓存命中 / FIFO
	// ============================================================

	@Test
	public void obtainReleaseObtain_returnsCachedInstance() {
		CharArrayPool pool = new CharArrayPool();
		char[] first = pool.obtain(10); // length 16
		pool.release(first);
		char[] second = pool.obtain(10);
		Assert.assertSame(first, second);
	}

	@Test
	public void releaseTwoArrays_obtainReturnsThemInFifoOrder() {
		// ObjectPool 内部用 ArrayDeque(offer/poll) → 先入先出
		CharArrayPool pool = new CharArrayPool();
		char[] a = pool.obtain(16);
		char[] b = pool.obtain(16);
		Assert.assertNotSame(a, b);

		pool.release(a);
		pool.release(b);

		Assert.assertSame(a, pool.obtain(16));
		Assert.assertSame(b, pool.obtain(16));
	}

	@Test
	public void release_doesNotShareCachesAcrossBuckets() {
		CharArrayPool pool = new CharArrayPool();
		char[] sized16 = pool.obtain(16);
		pool.release(sized16);

		// 在另一桶位获取的实例不应是上一桶位释放的实例
		char[] sized32 = pool.obtain(32);
		Assert.assertNotSame(sized16, sized32);
		Assert.assertEquals(32, sized32.length);

		// 原桶位仍然能取回缓存
		Assert.assertSame(sized16, pool.obtain(16));
	}

	@Test
	public void release_arrayWithForeignLength_isDiscarded() {
		CharArrayPool pool = new CharArrayPool();
		char[] foreign = new char[10]; // 10 不是任何桶大小
		pool.release(foreign);

		// 任何桶尺寸取出的都不应是它
		Assert.assertNotSame(foreign, pool.obtain(8));
		Assert.assertNotSame(foreign, pool.obtain(16));
	}

	@Test
	public void release_arrayLargerThanLargestBucket_isDiscarded() {
		CharArrayPool pool = new CharArrayPool();
		char[] huge = new char[2000];
		pool.release(huge);

		// huge 不会被缓存到任何桶；下次 obtain 走直接分配路径返回新数组
		char[] another = pool.obtain(2000);
		Assert.assertNotSame(huge, another);
		Assert.assertEquals(2000, another.length);
	}

	// ============================================================
	// 缓存容量上限
	// ============================================================

	@Test
	public void release_beyondCacheCapacity_excessIsDiscarded() {
		// 自定义只有一个桶，cache=2
		CharArrayPool.Strategy strategy = new CharArrayPool.Strategy()
			.append("only", 8, 2);
		CharArrayPool pool = new CharArrayPool(strategy);

		char[] a = pool.obtain(8);
		char[] b = pool.obtain(8);
		char[] c = pool.obtain(8);
		Assert.assertNotSame(a, b);
		Assert.assertNotSame(b, c);

		pool.release(a);
		pool.release(b);
		pool.release(c); // 容量已满；c 应被丢弃

		Assert.assertSame(a, pool.obtain(8));
		Assert.assertSame(b, pool.obtain(8));

		// 第三次 obtain 不会拿到 c — c 已经被丢弃，分配新数组
		char[] fresh = pool.obtain(8);
		Assert.assertNotSame(c, fresh);
		Assert.assertEquals(8, fresh.length);
	}

	// ============================================================
	// recycle
	// ============================================================

	@Test
	public void recycle_clearsCaches_subsequentObtainAllocatesFresh() {
		CharArrayPool pool = new CharArrayPool();
		char[] cached = pool.obtain(16);
		pool.release(cached);

		pool.recycle();

		char[] afterRecycle = pool.obtain(16);
		Assert.assertNotSame(cached, afterRecycle);
		Assert.assertEquals(16, afterRecycle.length);
	}

	@Test
	public void recycle_onEmptyPool_isNoOp() {
		CharArrayPool pool = new CharArrayPool();
		pool.recycle(); // 不应抛
		// 仍然可正常工作
		Assert.assertEquals(4, pool.obtain(0).length);
	}

	// ============================================================
	// 自定义 Strategy
	// ============================================================

	@Test
	public void customStrategy_singleBucket_usedForAllObtain() {
		CharArrayPool.Strategy strategy = new CharArrayPool.Strategy()
			.append("only", 100, 4);
		CharArrayPool pool = new CharArrayPool(strategy);

		// 所有 size <= 100 的请求都返回 length=100
		Assert.assertEquals(100, pool.obtain(0).length);
		Assert.assertEquals(100, pool.obtain(1).length);
		Assert.assertEquals(100, pool.obtain(50).length);
		Assert.assertEquals(100, pool.obtain(100).length);

		// size > 100 直接分配
		Assert.assertEquals(101, pool.obtain(101).length);
		Assert.assertEquals(500, pool.obtain(500).length);
	}

	@Test(expected = IllegalArgumentException.class)
	public void Strategy_appendDuplicateNameAsLast_throws() {
		new CharArrayPool.Strategy()
			.append("a", 4, 4)
			.append("a", 8, 4);
	}

	@Test(expected = IllegalArgumentException.class)
	public void Strategy_appendEqualChunkSizeAsLast_throws() {
		new CharArrayPool.Strategy()
			.append("a", 8, 4)
			.append("b", 8, 4);
	}

	@Test(expected = IllegalArgumentException.class)
	public void Strategy_appendSmallerChunkSizeThanLast_throws() {
		new CharArrayPool.Strategy()
			.append("a", 16, 4)
			.append("b", 8, 4);
	}

	@Test
	public void Strategy_validAscendingAppends_succeed() {
		// 不应抛
		CharArrayPool.Strategy strategy = new CharArrayPool.Strategy()
			.append("a", 4, 4)
			.append("b", 8, 4)
			.append("c", 16, 4);

		CharArrayPool pool = new CharArrayPool(strategy);
		Assert.assertEquals(4, pool.obtain(3).length);
		Assert.assertEquals(8, pool.obtain(5).length);
		Assert.assertEquals(16, pool.obtain(9).length);
	}

	@Test
	public void Strategy_emptyFirstAppend_doesNotValidate() {
		// 第一次 append 没有 last 可比，任意 chunkSize 都允许
		CharArrayPool.Strategy strategy = new CharArrayPool.Strategy()
			.append("first", 1, 4);
		CharArrayPool pool = new CharArrayPool(strategy);
		Assert.assertEquals(1, pool.obtain(1).length);
	}

	// ============================================================
	// resetForTest
	// ============================================================

	@Test
	public void resetForTest_replacesStrategy_andAffectsObtain() {
		CharArrayPool pool = new CharArrayPool();
		// 默认策略：obtain(50) → 64
		Assert.assertEquals(64, pool.obtain(50).length);

		CharArrayPool.Strategy custom = new CharArrayPool.Strategy()
			.append("custom", 256, 4);
		pool.resetForTest(custom);

		// 替换后：obtain(50) → 256
		Assert.assertEquals(256, pool.obtain(50).length);
	}

	// ============================================================
	// stats — 不依赖具体计数，只验证结构
	// ============================================================

	@Test
	public void stats_returnsNonEmptyString_includingEachBucketName() {
		CharArrayPool pool = new CharArrayPool();
		String stats = pool.stats();

		Assert.assertNotNull(stats);
		Assert.assertFalse(stats.isEmpty());
		// 默认策略中各桶的名字
		String[] names = {"l4", "l8", "l16", "l32", "l64", "l128", "l512", "l1k"};
		for (String name : names) {
			Assert.assertTrue("stats should contain bucket " + name + " but was: " + stats,
				stats.contains(name));
		}
		Assert.assertTrue(stats.contains("alloc hit:"));
		Assert.assertTrue(stats.contains("free discard:"));
	}

	@Test
	public void stats_withCustomStrategy_includesCustomNames() {
		CharArrayPool.Strategy strategy = new CharArrayPool.Strategy()
			.append("xs", 4, 4)
			.append("md", 64, 4)
			.append("xl", 1024, 4);
		CharArrayPool pool = new CharArrayPool(strategy);

		String stats = pool.stats();
		Assert.assertTrue(stats.contains("xs"));
		Assert.assertTrue(stats.contains("md"));
		Assert.assertTrue(stats.contains("xl"));
	}

	// ============================================================
	// 综合：release foreign size 后桶不被污染
	// ============================================================

	@Test
	public void release_foreignThenLegitimate_legitimateStillCached() {
		CharArrayPool pool = new CharArrayPool();

		// 释放一个外来尺寸的数组——应被丢弃
		pool.release(new char[7]);
		// 释放一个合法尺寸——应进入对应桶
		char[] legit = pool.obtain(16);
		pool.release(legit);

		Assert.assertSame(legit, pool.obtain(16));
	}
}