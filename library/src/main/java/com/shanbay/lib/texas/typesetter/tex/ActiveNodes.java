package com.shanbay.lib.texas.typesetter.tex;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import java.util.Iterator;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import com.shanbay.lib.log.Log;


@RestrictTo(LIBRARY)
public class ActiveNodes implements Iterable<Node> {
	private static final boolean DEBUG = false;

	private Node mHeader;
	private Node mTail;

	public ActiveNodes() {
		mHeader = mTail = Node.obtain();
		mHeader.totals = Sum.obtain();
	}

	public void insertBefore(Node anchor, Node node) {
		if (DEBUG) {
			Log.d("ActiveNodes", "insertBefore: " + anchor.state + " <- " + node.state);
		}

		node.prev = anchor.prev;
		if (anchor.prev != null) {
			anchor.prev.next = node;
		} else {
			mHeader = node;
		}

		node.next = anchor;
		anchor.prev = node;
	}

	public void pushBack(Node node) {
		if (DEBUG) {
			Log.d("ActiveNodes", "push: " + node.state);
		}

		node.prev = mTail;
		if (mTail != null) {
			mTail.next = node;
			mTail = node;
		} else {
			mHeader = mTail = node;
		}
	}

	public void remove(Node node) {
		if (DEBUG) {
			Log.d("ActiveNodes", "remove: " + node.state);
		}

		Node prev = node.prev;
		Node next = node.next;

		if (prev != null) {
			prev.next = next;
		}

		if (next != null) {
			next.prev = prev;
		}

		if (node == mHeader) {
			mHeader = next;
		}

		if (node == mTail) {
			mTail = prev;
		}
	}

	public Node getHeader() {
		return mHeader;
	}

	public Node getTail() {
		return mTail;
	}

	public boolean isEmpty() {
		return mHeader == null;
	}

	@NonNull
	@Override
	public Iterator<Node> iterator() {
		return new ActiveNodeIterator(mHeader);
	}

	public void recycle() {
		Node node = mHeader;
		while (node != null) {
			node.recycle();
			node = node.next;
		}
		mTail = mHeader = null;
	}

	private static class ActiveNodeIterator implements Iterator<Node> {
		private Node mNode;

		ActiveNodeIterator(Node node) {
			mNode = node;
		}

		@Override
		public boolean hasNext() {
			return mNode != null;
		}

		@Override
		public Node next() {
			Node node = mNode;
			mNode = mNode.next;
			return node;
		}
	}

	@NonNull
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("{");
		for (Node node : this) {
			stringBuilder.append("[")
					.append(node.line)
					.append(',')
					.append(node.state)
					.append("],");
		}
		stringBuilder.append("}");
		return stringBuilder.toString();
	}
}
