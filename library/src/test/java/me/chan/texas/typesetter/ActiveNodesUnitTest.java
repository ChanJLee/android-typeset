package me.chan.texas.typesetter;

import me.chan.texas.typesetter.tex.ActiveNodes;
import me.chan.texas.typesetter.tex.Node;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class ActiveNodesUnitTest {

	@Test
	public void test() {
		ActiveNodes activeNodes = new ActiveNodes();
		Assert.assertFalse(activeNodes.isEmpty());

		Node header = activeNodes.getHeader();
		Assert.assertNotNull(header);
		Assert.assertSame(activeNodes.getHeader(), activeNodes.getTail());

		activeNodes.remove(header);
		Assert.assertNull(activeNodes.getHeader());
		Assert.assertSame(activeNodes.getHeader(), activeNodes.getTail());

		Node node = createNode(10);
		activeNodes.pushBack(node);
		header = activeNodes.getHeader();
		Assert.assertNotNull(header);
		Assert.assertSame(activeNodes.getHeader(), activeNodes.getTail());
		checkContent(activeNodes, 10);

		activeNodes.remove(header);
		Assert.assertNull(activeNodes.getHeader());
		Assert.assertSame(activeNodes.getHeader(), activeNodes.getTail());

		checkAddRemoveSide(activeNodes);
		checkAddRemoveRandom(activeNodes);
	}

	private void checkAddRemoveSide(ActiveNodes activeNodes) {
		for (int i = 0; i < 5; ++i) {
			Node node = createNode(i);
			activeNodes.pushBack(node);
		}

		checkContent(activeNodes, 0, 1, 2, 3, 4);
		Node node = createNode(100);
		activeNodes.insertBefore(activeNodes.getHeader(), node);
		checkContent(activeNodes, 100, 0, 1, 2, 3, 4);
		Assert.assertNotNull(activeNodes.getHeader());
		Assert.assertNotNull(activeNodes.getTail());
		Assert.assertNotSame(activeNodes.getTail(), activeNodes.getHeader());

		activeNodes.remove(activeNodes.getHeader());
		checkContent(activeNodes, 0, 1, 2, 3, 4);
		activeNodes.remove(activeNodes.getTail());
		checkContent(activeNodes, 0, 1, 2, 3);
		Assert.assertNotNull(activeNodes.getHeader());
		Assert.assertNotNull(activeNodes.getTail());
		Assert.assertNotSame(activeNodes.getTail(), activeNodes.getHeader());

		activeNodes.remove(activeNodes.getHeader());
		checkContent(activeNodes, 1, 2, 3);
		activeNodes.remove(activeNodes.getTail());
		checkContent(activeNodes, 1, 2);
		Assert.assertNotNull(activeNodes.getHeader());
		Assert.assertNotNull(activeNodes.getTail());
		Assert.assertNotSame(activeNodes.getTail(), activeNodes.getHeader());

		activeNodes.remove(activeNodes.getHeader());
		checkContent(activeNodes, 2);
		activeNodes.remove(activeNodes.getTail());
		Assert.assertTrue(activeNodes.isEmpty());
		Assert.assertNull(activeNodes.getHeader());
		Assert.assertNull(activeNodes.getTail());
	}

	private void checkAddRemoveRandom(ActiveNodes activeNodes) {
		Node node1 = createNode(1);
		Node node2 = createNode(2);
		Node node3 = createNode(3);
		Node node4 = createNode(4);

		activeNodes.pushBack(node2);
		activeNodes.insertBefore(node2, node1);
		activeNodes.pushBack(node4);
		activeNodes.insertBefore(node4, node3);

		checkContent(activeNodes, 1, 2, 3, 4);
		Assert.assertSame(node1, activeNodes.getHeader());
		Assert.assertSame(node4, activeNodes.getTail());

		activeNodes.remove(node2);
		checkContent(activeNodes, 1, 3, 4);
		Assert.assertSame(node1, activeNodes.getHeader());
		Assert.assertSame(node4, activeNodes.getTail());

		activeNodes.remove(node4);
		checkContent(activeNodes, 1, 3);
		Assert.assertSame(node1, activeNodes.getHeader());
		Assert.assertSame(node3, activeNodes.getTail());

		activeNodes.remove(node1);
		checkContent(activeNodes, 3);
		Assert.assertSame(node3, activeNodes.getHeader());
		Assert.assertSame(node3, activeNodes.getTail());

		activeNodes.remove(node3);
		Assert.assertTrue(activeNodes.isEmpty());
		Assert.assertNull(activeNodes.getHeader());
		Assert.assertNull(activeNodes.getTail());
	}

	private Node createNode(int value) {
		Node node = Node.obtain();
		node.line = value;
		return node;
	}

	private void checkContent(ActiveNodes activeNodes, int... ints) {
		System.out.println(activeNodes);
		int i = 0;
		Iterator<Node> nodes = activeNodes.iterator();
		while (nodes.hasNext()) {
			Node node = nodes.next();
			Assert.assertEquals(node.line, ints[i++]);
		}

		if (i < ints.length || nodes.hasNext()) {
			Assert.fail("check content failed, " + i + " " + ints.length + " " + nodes.hasNext());
		}
	}
}
