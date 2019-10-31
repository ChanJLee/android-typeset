package me.chan.te.typesetter;

import me.chan.te.annotations.Hidden;
import me.chan.te.misc.Recyclable;
import me.chan.te.misc.ObjectFactory;

@Hidden
public class Node implements Recyclable {
	private static final ObjectFactory<Node> POOL = new ObjectFactory<>(1024);

	private Data mData = new Data();

	public Node prev;
	public Node next;

	private Node(Node prev, Node next) {
		this.prev = prev;
		this.next = next;
	}

	public static void clean() {
		POOL.clean();
	}

	public Data getData() {
		return mData;
	}

	@Override
	public void recycle() {
		mData.demerits = mData.ratio = mData.fitnessClazz = mData.position = 0;
		mData.line = -1;
		if (mData.totals != null) {
			mData.totals.recycle();
			mData.totals = null;
		}
		mData.prev = null;
		next = prev = null;
		POOL.release(this);
	}

	public static Node obtain(Node prev, Node next) {
		Node node = POOL.acquire();
		if (node == null) {
			return new Node(prev, next);
		}
		node.prev = prev;
		node.next = next;
		return node;
	}

	public static class Data {
		public int position = 0;
		public float demerits = 0;
		public float ratio = 0;
		public int line = -1;
		public int fitnessClazz = 0;
		public Sum totals;
		public Node prev;

		private Data() {
		}
	}
}