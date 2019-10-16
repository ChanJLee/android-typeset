package me.chan.te.hypher;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.chan.te.log.Log;


public class Hypher {

	private static Hypher sInstance;
	private static final int UNDER_LINE_CODE_POINT = 95;

	private TrieNode mTrie;
	private int mLeftMin;
	private int mRightMin;

	private Hypher(HyphenationPattern pattern) {
		this.mTrie = createTrie(pattern.patterns);
		this.mLeftMin = pattern.leftMin;
		this.mRightMin = pattern.rightMin;
	}

	public static synchronized Hypher getInstance() {
		if (sInstance == null) {
			sInstance = new Hypher(HyphenationPattern.EN_US);
		}
		return sInstance;
	}

	private static TrieNode createTrie(Map<Integer, String> patternObject) {
		TrieNode tree = new TrieNode();
		for (Map.Entry<Integer, String> entry : patternObject.entrySet()) {
			int key = entry.getKey();
			String value = entry.getValue();
			for (int i = 0; i + key <= value.length(); i = i + key) {
				createTrie(tree, value, i, i + key);
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

		IntArrayList list = new IntArrayList();
		int digitStart = -1;
		for (int p = start; p < end; p++) {
			if (Character.isDigit(value.charAt(p))) {
				if (digitStart < 0) {
					digitStart = p;
				}
				if (p == end - 1) {
					// last number in the pattern
					String number = value.substring(digitStart, end);
					list.add(Integer.valueOf(number));
				}
			} else if (digitStart >= 0) {
				String number = value.substring(digitStart, p);
				list.add(Integer.valueOf(number));
				digitStart = -1;
			} else {
				list.add(0);
			}
		}
		t.points = list.toArray();
	}

	@NonNull
	public List<String> hyphenate(@NonNull String word) {
		List<String> result = new ArrayList<>();
		hyphenate(word, result);
		return result;
	}

	/**
	 * @param word   word
	 * @param result hyphenate
	 */
	public void hyphenate(@NonNull String word, @NonNull List<String> result) {
		hyphenate(word, 0, word.length(), result);
	}

	public void hyphenate(@NonNull String word, int start, int len, @NonNull List<String> result) {
		if (!result.isEmpty()) {
			Log.w("hyphenate result is not empty");
		}

		int lenWithPadding = len + 2;
		int[] points = new int[len + 2];
		TrieNode node, trie = this.mTrie;
		int[] nodePoints;
		for (int i = 0; i < lenWithPadding; i++) {
			node = trie;
			for (int j = i; j < lenWithPadding; j++) {
				int codePoints = UNDER_LINE_CODE_POINT;
				if (j != 0 && j != lenWithPadding - 1) {
					codePoints = word.codePointAt(start + j - 1);
				}

				node = node.codePoint.get(codePoints);
				if (node == null) {
					break;
				}

				nodePoints = node.points;
				if (nodePoints != null) {
					for (int k = 0; k < nodePoints.length && i + k < points.length; k++) {
						points[i + k] = Math.max(points[i + k], nodePoints[k]);
					}
				}
			}
		}

		int first = start;
		int last = start + len;
		for (int i = 1; i < lenWithPadding - 1; i++) {
			if (i > this.mLeftMin && i < (lenWithPadding - this.mRightMin) && points[i] % 2 > 0) {
				int end = first + i - 1;
				if (word.charAt(end - 1) != '-') {
					result.add(word.substring(start, end));
					start = end;
				}
			}
		}

		if (start < last && last - start != len) {
			result.add(word.substring(start, last));
		}
	}
}
