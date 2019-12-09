package me.chan.texas.typesetter;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class ActiveNodeListUnitTest {

	@Test
	public void test() {
		ActiveNodeList activeNodeList = new ActiveNodeList();
		Assert.assertFalse(activeNodeList.isEmpty());

		Node header = activeNodeList.getHeader();
		Assert.assertNotNull(header);
		Assert.assertNotNull(header.getData());
		Assert.assertSame(activeNodeList.getHeader(), activeNodeList.getTail());

		activeNodeList.remove(header);
		Assert.assertNull(activeNodeList.getHeader());
		Assert.assertSame(activeNodeList.getHeader(), activeNodeList.getTail());

		Node node = createNode(10);
		activeNodeList.pushBack(node);
		header = activeNodeList.getHeader();
		Assert.assertNotNull(header);
		Assert.assertNotNull(header.getData());
		Assert.assertSame(activeNodeList.getHeader(), activeNodeList.getTail());
		checkContent(activeNodeList, 10);

		activeNodeList.remove(header);
		Assert.assertNull(activeNodeList.getHeader());
		Assert.assertSame(activeNodeList.getHeader(), activeNodeList.getTail());

		checkAddRemoveSide(activeNodeList);
		checkAddRemoveRandom(activeNodeList);
	}

	private void checkAddRemoveSide(ActiveNodeList activeNodeList) {
		for (int i = 0; i < 5; ++i) {
			Node node = createNode(i);
			activeNodeList.pushBack(node);
		}

		checkContent(activeNodeList, 0, 1, 2, 3, 4);
		Node node = createNode(100);
		activeNodeList.insertBefore(activeNodeList.getHeader(), node);
		checkContent(activeNodeList, 100, 0, 1, 2, 3, 4);
		Assert.assertNotNull(activeNodeList.getHeader());
		Assert.assertNotNull(activeNodeList.getTail());
		Assert.assertNotSame(activeNodeList.getTail(), activeNodeList.getHeader());

		activeNodeList.remove(activeNodeList.getHeader());
		checkContent(activeNodeList, 0, 1, 2, 3, 4);
		activeNodeList.remove(activeNodeList.getTail());
		checkContent(activeNodeList, 0, 1, 2, 3);
		Assert.assertNotNull(activeNodeList.getHeader());
		Assert.assertNotNull(activeNodeList.getTail());
		Assert.assertNotSame(activeNodeList.getTail(), activeNodeList.getHeader());

		activeNodeList.remove(activeNodeList.getHeader());
		checkContent(activeNodeList, 1, 2, 3);
		activeNodeList.remove(activeNodeList.getTail());
		checkContent(activeNodeList, 1, 2);
		Assert.assertNotNull(activeNodeList.getHeader());
		Assert.assertNotNull(activeNodeList.getTail());
		Assert.assertNotSame(activeNodeList.getTail(), activeNodeList.getHeader());

		activeNodeList.remove(activeNodeList.getHeader());
		checkContent(activeNodeList, 2);
		activeNodeList.remove(activeNodeList.getTail());
		Assert.assertTrue(activeNodeList.isEmpty());
		Assert.assertNull(activeNodeList.getHeader());
		Assert.assertNull(activeNodeList.getTail());
	}

	private void checkAddRemoveRandom(ActiveNodeList activeNodeList) {
		Node node1 = createNode(1);
		Node node2 = createNode(2);
		Node node3 = createNode(3);
		Node node4 = createNode(4);

		activeNodeList.pushBack(node2);
		activeNodeList.insertBefore(node2, node1);
		activeNodeList.pushBack(node4);
		activeNodeList.insertBefore(node4, node3);

		checkContent(activeNodeList, 1, 2, 3, 4);
		Assert.assertSame(node1, activeNodeList.getHeader());
		Assert.assertSame(node4, activeNodeList.getTail());

		activeNodeList.remove(node2);
		checkContent(activeNodeList, 1, 3, 4);
		Assert.assertSame(node1, activeNodeList.getHeader());
		Assert.assertSame(node4, activeNodeList.getTail());

		activeNodeList.remove(node4);
		checkContent(activeNodeList, 1, 3);
		Assert.assertSame(node1, activeNodeList.getHeader());
		Assert.assertSame(node3, activeNodeList.getTail());

		activeNodeList.remove(node1);
		checkContent(activeNodeList, 3);
		Assert.assertSame(node3, activeNodeList.getHeader());
		Assert.assertSame(node3, activeNodeList.getTail());

		activeNodeList.remove(node3);
		Assert.assertTrue(activeNodeList.isEmpty());
		Assert.assertNull(activeNodeList.getHeader());
		Assert.assertNull(activeNodeList.getTail());
	}

	private Node createNode(int value) {
		Node node = Node.obtain();
		node.getData().line = value;
		return node;
	}

	private void checkContent(ActiveNodeList activeNodeList, int... ints) {
		System.out.println(activeNodeList);
		int i = 0;
		Iterator<Node> nodes = activeNodeList.iterator();
		while (nodes.hasNext()) {
			Node node = nodes.next();
			Assert.assertEquals(node.getData().line, ints[i++]);
		}

		if (i < ints.length || nodes.hasNext()) {
			Assert.fail("check content failed, " + i + " " + ints.length + " " + nodes.hasNext());
		}
	}
}
