package me.chan.texas.typesetter.tex;

import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

@RestrictTo(LIBRARY)
public class Node extends DefaultRecyclable {
	private static final ObjectPool<Node> POOL = new ObjectPool<>(64);

	static {
		Texas.registerLifecycleCallback(new Texas.LifecycleCallback() {
			@Override
			public void onClean() {
				POOL.clean();
			}
		});
	}

	public Node prev;
	public Node next;
	public int state = 0;
	public float demerits = 0;
	public float ratio = 0;
	public int line = -1;
	public int fitness = 0;
	public Sum totals;
	public Node link;

	private Node() {
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		demerits = ratio = fitness = state = 0;
		line = -1;
		if (totals != null) {
			totals.recycle();
			totals = null;
		}
		link = null;
		next = prev = null;
		POOL.release(this);
	}

	public static Node obtain() {
		Node node = POOL.acquire();
		if (node == null) {
			return new Node();
		}
		node.reuse();
		return node;
	}

	@Override
	public String toString() {
		return "Node{" +
				"state=" + state +
				", demerits=" + demerits +
				", ratio=" + ratio +
				", line=" + line +
				", fitness=" + fitness +
				", totals=" + totals +
				'}';
	}
}