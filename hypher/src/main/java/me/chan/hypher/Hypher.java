package me.chan.hypher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Hypher {

	private static Hypher sInstance;

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

	/**
	 * Returns a list of syllables that indicates at which points the word can
	 * be broken with a hyphen
	 *
	 * @param word Word to hyphenate
	 * @return list of syllables
	 */
	public List<String> hyphenate(String word) {
		word = "_" + word + "_";

		String lowercase = word.toLowerCase();

		int wordLength = lowercase.length();
		int[] points = new int[wordLength];
		int[] characterPoints = new int[wordLength];
		for (int i = 0; i < wordLength; i++) {
			points[i] = 0;
			characterPoints[i] = lowercase.codePointAt(i);
			System.out.println(characterPoints[i]);
		}

		TrieNode node, trie = this.mTrie;
		int[] nodePoints;
		for (int i = 0; i < wordLength; i++) {
			node = trie;
			for (int j = i; j < wordLength; j++) {
				node = node.codePoint.get(characterPoints[j]);
				if (node != null) {
					nodePoints = node.points;
					if (nodePoints != null) {
						for (int k = 0, nodePointsLength = nodePoints.length;
							 k < nodePointsLength; k++) {
							points[i + k] = Math.max(points[i + k], nodePoints[k]);
						}
					}
				} else {
					break;
				}
			}
		}

		List<String> result = new ArrayList<>();
		int start = 1;
		for (int i = 1; i < wordLength - 1; i++) {
			if (i > this.mLeftMin && i < (wordLength - this.mRightMin) && points[i] % 2 > 0) {
				result.add(word.substring(start, i));
				start = i;
			}
		}
		if (start < word.length() - 1) {
			result.add(word.substring(start, word.length() - 1));
		}
		return result;
	}

}
