package com.shanbay.lib.texas.typesetter;

import androidx.annotation.Keep;

import com.shanbay.lib.texas.Texas;
import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.misc.DefaultRecyclable;
import com.shanbay.lib.texas.misc.ObjectFactory;

@Hidden
class Node extends DefaultRecyclable {
	private static final ObjectFactory<Node> POOL = new ObjectFactory<>(1024);
	static {
		Texas.register(Node.class);
	}

	private Data mData = new Data();

	public Node prev;
	public Node next;

	private Node() {
	}

	public Data getData() {
		return mData;
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
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

	public static Node obtain() {
		Node node = POOL.acquire();
		if (node == null) {
			return new Node();
		}
		node.prev = node.next = null;
		node.reuse();
		return node;
	}

	@Keep
	public static void clean() {
		POOL.clean();
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