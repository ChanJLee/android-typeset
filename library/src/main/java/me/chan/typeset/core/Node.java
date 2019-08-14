package me.chan.typeset.core;

public class Node {
	public Point data;
	public Node prev;
	public Node next;

	public Node(Point data, Node prev, Node next) {
		this.data = data;
		this.prev = prev;
		this.next = next;
	}
}
