package me.chan.texas.hyphenation;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;
import static me.chan.texas.utils.TexasUtils.parseInt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.collection.SparseArrayCompat;

import java.util.concurrent.atomic.AtomicInteger;

import me.chan.texas.Texas;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.utils.IntArray;
import me.chan.texas.utils.Lazy;

/**
 * 断字器
 */
@RestrictTo(LIBRARY)
public class Hyphenation {
	private static final ObjectPool<IntArray> POOL = new ObjectPool<>(4);

	public static final int NONE_GROUP_ID = 0;

	private static final AtomicInteger UUID = new AtomicInteger(0);

	static {
		Texas.registerLifecycleCallback(new Texas.LifecycleCallback() {
			@Override
			public void onClean() {
				POOL.clean();
			}
		});
	}

	private static final boolean DEBUG = false;

	private static final Lazy<Hyphenation> EN_US = new Lazy<Hyphenation>() {
		@Override
		protected Hyphenation onCreate() {
			return new Hyphenation(HyphenationPattern.newInstance(HyphenationPattern.EN_US));
		}
	};

	private static final Lazy<Hyphenation> EN_GB = new Lazy<Hyphenation>() {
		@Override
		protected Hyphenation onCreate() {
			return new Hyphenation(HyphenationPattern.newInstance(HyphenationPattern.EN_GB));
		}
	};
	private static final Lazy<Hyphenation> NONE = new Lazy<Hyphenation>() {
		@Override
		protected Hyphenation onCreate() {
			return new Hyphenation();
		}
	};

	private static final int UNDER_LINE_CODE_POINT = 95;

	@Nullable
	private final TrieNode mTrie;
	private final int mLeftMin;
	private final int mRightMin;

	private Hyphenation() {
		mTrie = null;
		mLeftMin = 0;
		mRightMin = 0;
	}

	private Hyphenation(@NonNull HyphenationPattern pattern) {
		this.mTrie = createTrie(pattern.patterns);
		this.mLeftMin = pattern.leftMin;
		this.mRightMin = pattern.rightMin;
	}

	public static Hyphenation getInstance() {
		return getInstance(HyphenationPattern.EN_US);
	}

	public static Hyphenation getInstance(@HyphenationPattern.PatternType int type) {
		if (type == HyphenationPattern.EN_US) {
			return EN_US.value();
		} else if (type == HyphenationPattern.EN_GB) {
			return EN_GB.value();
		} else if (type == HyphenationPattern.NONE) {
			return NONE.value();
		}

		throw new IllegalArgumentException("unknown pattern");
	}

	private static int nextId() {
		int id = UUID.incrementAndGet();
		if (id == NONE_GROUP_ID) {
			id = UUID.incrementAndGet();
		}
		return id;
	}

	private static TrieNode createTrie(SparseArrayCompat<String> pattern) {
		TrieNode tree = new TrieNode();
		int count = pattern.size();
		for (int keyIndex = 0; keyIndex < count; ++keyIndex) {
			int key = pattern.keyAt(keyIndex);
			String value = pattern.get(key);
			if (value == null) {
				continue;
			}
			for (int i = 0; i + key <= value.length(); i = i + key) {
				createTrie(tree, value, i, i + key);
			}
		}

		if (DEBUG) {
			int capacity = tree.capacity();
			int used = tree.used();
			System.out.println("capacity: " + capacity);
			System.out.println("used: " + used);
			System.out.println("usage: " + (used * 1.0f / capacity));
			System.out.println("max: " + tree.max());

			SparseArrayCompat<Integer> arrayCompat = tree.status();
			for (int i = 0; i < arrayCompat.size(); ++i) {
				System.out.println("detail: " + arrayCompat.keyAt(i) + " -> " + arrayCompat.valueAt(i));
			}
		}

		return tree;
	}

	private static void createTrie(TrieNode root, String value, int start, int end) {
		TrieNode t = root;
		for (int c = start; c < end; c++) {
			char chr = value.charAt(c);
			if (Character.isDigit(chr)) {
				continue;
			}
			int codePoint = value.codePointAt(c);
			if (t.codePoint.get(codePoint) == null) {
				t.codePoint.put(codePoint, new TrieNode());
			}
			t = t.codePoint.get(codePoint);
		}

		IntArray array = new IntArray();
		int digitStart = -1;
		for (int p = start; p < end; p++) {
			if (Character.isDigit(value.charAt(p))) {
				if (digitStart < 0) {
					digitStart = p;
				}
				if (p == end - 1) {
					// last number in the pattern
					array.add(parseInt(value, digitStart, end));
				}
			} else if (digitStart >= 0) {
				array.add(parseInt(value, digitStart, p));
				digitStart = -1;
			} else {
				array.add(0);
			}
		}
		t.points = array;
	}

	public int hyphenate(@NonNull CharSequence text, int start, int end, @NonNull IntArray result) {
		if (start == end || mTrie == null) {
			return NONE_GROUP_ID;
		}

		String word = String.valueOf(text);

		int len = end - start;
		int lenWithPadding = len + 2;
		IntArray points = obtain(len + 2);
		TrieNode node, trie = this.mTrie;
		IntArray nodePoints;
		for (int i = 0; i < lenWithPadding; i++) {
			node = trie;
			for (int j = i; j < lenWithPadding; j++) {
				int codePoints = UNDER_LINE_CODE_POINT;
				if (j != 0 && j != lenWithPadding - 1) {
					codePoints = word.charAt(start + j - 1);
				}

				node = node.codePoint.get(codePoints);
				if (node == null) {
					break;
				}

				nodePoints = node.points;
				if (nodePoints != null) {
					int size = nodePoints.size();
					for (int k = 0; k < size && i + k < points.size(); k++) {
						points.set(i + k, Math.max(points.get(i + k), nodePoints.get(k)));
					}
				}
			}
		}

		int first = start;
		int last = start + len;
		for (int i = 1; i < lenWithPadding - 1; i++) {
			if (i > this.mLeftMin && i < (lenWithPadding - this.mRightMin) && points.get(i) % 2 > 0) {
				int point = first + i - 1;
				if (start != first &&
						start < last &&
						point - start == 1 &&
						word.charAt(start) == '-' &&
						!result.empty()) {
					// 修复 self-complacency 这个单词
					// 如果不这么改
					// 会是这样的序列：self, -, com, pla, cency
					// 我们期望的 self-, com, pla, cency
					result.set(result.size() - 1, point);
				} else {
					result.add(point);
				}
				start = point;
			}
		}

		if (start < last && last - start != len) {
			result.add(last);
		}

		release(points);

		return nextId();
	}

	private static IntArray obtain(int size) {
		IntArray array = POOL.acquire();
		if (array == null) {
			array = new IntArray(size);
		}
		array.zero(size);
		return array;
	}

	private static void release(IntArray array) {
		POOL.release(array);
	}
}
