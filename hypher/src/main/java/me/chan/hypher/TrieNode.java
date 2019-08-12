package me.chan.hypher;

import java.io.Serializable;

class TrieNode implements Serializable {

	private static final long serialVersionUID = 1L;

	IntTrieNodeArrayMap codePoint = new IntTrieNodeArrayMap();

	int[] points;
}
