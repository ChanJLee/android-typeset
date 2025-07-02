package me.chan.texas.utils;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.misc.ObjectPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class CharArrayPool {
	public CharArrayPool() {
		this(new Strategy()
				.append("l4", 4, 4)
				.append("l8", 8, 4)
				.append("l16", 16, 4)
				.append("l32", 32, 4)
				.append("l64", 64, 4)
				.append("l128", 128, 2)
				.append("l512", 512, 2)
				.append("l1k", 1024, 2));
	}

	public CharArrayPool(Strategy strategy) {
		this.mStrategy = strategy;
	}

	private static final Stats STATS = new Stats();

	private static class Stats {
		public AtomicInteger alloc = new AtomicInteger();
		public AtomicInteger allocCached = new AtomicInteger();

		public AtomicInteger free = new AtomicInteger();
		public AtomicInteger freeDiscard = new AtomicInteger();
	}

	private static final boolean DEBUG = false;

	public char[] obtain(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("size must >= 0");
		}

		STATS.alloc.incrementAndGet();

		char[] chunk = null;
		int detailCount = mStrategy.mDetails.size();
		for (int i = 0; i < detailCount; ++i) {
			StrategyDetail detail = mStrategy.mDetails.get(i);
			chunk = detail.alloc(size);
			if (chunk == null) {
				continue;
			}

			if (chunk != StrategyDetail.INVALID) {
				STATS.allocCached.incrementAndGet();
				return chunk;
			}


			return new char[detail.getChunkSize()];
		}


		return new char[size];
	}

	public void release(char[] buf) {
		STATS.free.incrementAndGet();
		int detailCount = mStrategy.mDetails.size();
		for (int i = 0; i < detailCount; ++i) {
			StrategyDetail detail = mStrategy.mDetails.get(i);
			if (detail.free(buf)) {
				return;
			}
		}

		STATS.freeDiscard.incrementAndGet();
	}

	@SuppressLint("DefaultLocale")
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public String stats() {
		final int alloc = STATS.alloc.get();
		final int allocCached = STATS.allocCached.get();
		final int free = STATS.free.get();
		final int freeDiscard = STATS.freeDiscard.get();

		int detailCount = mStrategy.mDetails.size();
		StringBuilder stringBuilder = new StringBuilder(128);

		for (int i = 0; i < detailCount; ++i) {
			StrategyDetail detail = mStrategy.mDetails.get(i);
			int detailAlloc = detail.getAlloc();
			int detailFree = detail.getFree();

			stringBuilder.append('[')
					.append(detail.getName())
					.append(", a:")
					.append(alloc == 0 ? 0 : detailAlloc * 100f / alloc)
					.append("%, f:")
					.append(free == 0 ? 0 : detailFree * 100f / free)
					.append("%] ");
		}

		stringBuilder.append("alloc hit: ")
				.append(alloc == 0 ? 0 : allocCached * 100f / alloc)
				.append("%, free discard: ")
				.append(free == 0 ? 0 : freeDiscard * 100f / free)
				.append("%");

		return stringBuilder.toString();
	}

	
	public void recycle() {
		mStrategy.recycle();
	}

	private static class StrategyDetail {
		private static final char[] INVALID = new char[0];

		private final String mName;
		private final int mChunkSize;
		private final ObjectPool<char[]> mPool;
		private final AtomicInteger mAlloc = new AtomicInteger();
		private final AtomicInteger mFree = new AtomicInteger();

		private StrategyDetail(@NonNull String name, int chunkSize, int cacheSize) {
			mName = name;
			mChunkSize = chunkSize;
			mPool = new ObjectPool<>(cacheSize);
		}

		public char[] alloc(int size) {
			if (size > mChunkSize) {
				return null;
			}

			char[] chunk = mPool.acquire();
			if (chunk == null) {
				return INVALID;
			}

			mAlloc.incrementAndGet();
			return chunk;
		}

		public boolean free(char[] chunk) {
			if (chunk.length != mChunkSize) {
				return false;
			}

			boolean rtn = mPool.release(chunk);
			if (rtn) {
				mFree.incrementAndGet();
			}

			return rtn;
		}

		public void recycle() {
			mPool.clean();
		}

		@VisibleForTesting
		String getName() {
			return mName;
		}

		@VisibleForTesting
		int getChunkSize() {
			return mChunkSize;
		}

		@VisibleForTesting
		ObjectPool<char[]> getPool() {
			return mPool;
		}

		public int getAlloc() {
			return mAlloc.get();
		}

		public int getFree() {
			return mFree.get();
		}
	}

	public static class Strategy {
		private final List<StrategyDetail> mDetails = new ArrayList<>(8);

		public Strategy append(@NonNull String name, int chunkSize, int cacheSize) {
			if (mDetails.isEmpty()) {
				mDetails.add(new StrategyDetail(name, chunkSize, cacheSize));
				return this;
			}

			StrategyDetail last = mDetails.get(mDetails.size() - 1);
			if (last.mName.equals(name)) {
				throw new IllegalArgumentException("name conflict");
			}

			if (last.mChunkSize >= chunkSize) {
				throw new IllegalArgumentException("chunk size conflict");
			}

			mDetails.add(new StrategyDetail(name, chunkSize, cacheSize));
			return this;
		}

		public void recycle() {
			for (StrategyDetail strategyDetail : mDetails) {
				strategyDetail.recycle();
			}
		}
	}

	private Strategy mStrategy;


	@VisibleForTesting
	Strategy getChunkStrategy() {
		return mStrategy;
	}

	@VisibleForTesting
	void resetForTest(Strategy strategy) {
		mStrategy = strategy;
	}
}
