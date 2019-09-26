package me.chan.te.typesetter;

public class Node {
	public Data data;
	public Node prev;
	public Node next;

	public Node(Data data, Node prev, Node next) {
		this.data = data;
		this.prev = prev;
		this.next = next;
	}

	public static class Data {
		public int position = 0;
		public float demerits = 0;
		public float ratio = 0;
		public int line = -1;
		public int fitnessClazz = 0;
		public Sum totals;
		public Node prev;
	}
}