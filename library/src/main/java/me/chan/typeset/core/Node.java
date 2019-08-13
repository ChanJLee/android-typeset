package me.chan.typeset.core;

public class Node {
	public Break data;
	public Node prev;
	public Node next;

	public Node(Break data, Node prev, Node next) {
		this.data = data;
		this.prev = prev;
		this.next = next;
	}
}
