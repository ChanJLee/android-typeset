package me.chan.texas.typesetter;

import android.support.annotation.NonNull;

import java.util.Iterator;

import me.chan.texas.annotations.Hidden;

public class ActiveNodeList implements Iterable<Node> {
	private Node mHeader;
	private Node mTail;

	public ActiveNodeList() {
		mHeader = mTail = Node.obtain();
		mHeader.getData().totals = Sum.obtain();
	}

	public void insertBefore(Node anchor, Node node) {
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
		node.prev = mTail;
		if (mTail != null) {
			mTail.next = node;
			mTail = node;
		} else {
			mHeader = mTail = node;
		}
	}

	public void remove(Node node) {
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

	@Override
	@Hidden
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("ActiveNode{");
		for (Node node : this) {
			stringBuilder.append(node.getData().line)
					.append(',');
		}
		stringBuilder.append("}");
		return stringBuilder.toString();
	}
}
