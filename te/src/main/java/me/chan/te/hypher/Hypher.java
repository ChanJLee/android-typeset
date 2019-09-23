package me.chan.te.hypher;

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
		TrieNode t, tree = new TrieNode();

		for (Map.Entry<Integer, String> entry : patternObject.entrySet()) {
			int key = entry.getKey();
			String value = entry.getValue();
			String[] patterns = new String[value.length() / key];
			for (int i = 0; i + key <= value.length(); i = i + key) {
				patterns[i / key] = value.substring(i, i + key);
			}
			for (String pattern : patterns) {
				t = tree;

				for (int c = 0; c < pattern.length(); c++) {
					char chr = pattern.charAt(c);
					if (Character.isDigit(chr)) {
						continue;
					}
					int codePoint = pattern.codePointAt(c);
					if (t.codePoint.get(codePoint) == null) {
						t.codePoint.put(codePoint, new TrieNode());
					}
					t = t.codePoint.get(codePoint);
				}

				IntArrayList list = new IntArrayList();
				int digitStart = -1;
				for (int p = 0; p < pattern.length(); p++) {
					if (Character.isDigit(pattern.charAt(p))) {
						if (digitStart < 0) {
							digitStart = p;
						}
						if (p == pattern.length() - 1) {
							// last number in the pattern
							String number = pattern.substring(digitStart);
							list.add(Integer.valueOf(number));
						}
					} else if (digitStart >= 0) {
						String number = pattern.substring(digitStart, p);
						list.add(Integer.valueOf(number));
						digitStart = -1;
					} else {
						list.add(0);
					}
				}
				t.points = list.toArray();
			}
		}
		return tree;
	}

	public List<String> hyphenate(String word) {
		List<String> result = new ArrayList<>();
		hyphenate(word, result);
		return result;
	}

	/**
	 * @param word   word
	 * @param result hyphenate
	 */
	public void hyphenate(String word, List<String> result) {
		hyphenate(word, 0, word.length(), result);
	}

	public void hyphenate(String word, int start, int len, List<String> result) {
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
					codePoints = Character.toLowerCase(word.codePointAt(start + j - 1));
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
				}
				start = end;
			}
		}

		if (start < last && last - start != len) {
			result.add(word.substring(start, last));
		}
	}
}
