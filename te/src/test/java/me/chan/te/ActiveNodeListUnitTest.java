package me.chan.te;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

import me.chan.te.typesetter.ActiveNodeList;
import me.chan.te.typesetter.Node;

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

		checkInsert1(activeNodeList);
	}

	private void checkInsert1(ActiveNodeList activeNodeList) {
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
		checkContent(activeNodeList, 1, 2, 3, 4);
		activeNodeList.remove(activeNodeList.getTail());
		checkContent(activeNodeList, 1, 2, 3);
		Assert.assertNotNull(activeNodeList.getHeader());
		Assert.assertNotNull(activeNodeList.getTail());
		Assert.assertNotSame(activeNodeList.getTail(), activeNodeList.getHeader());

		activeNodeList.remove(activeNodeList.getHeader());
		checkContent(activeNodeList, 2, 3, 4);
		activeNodeList.remove(activeNodeList.getTail());
		checkContent(activeNodeList, 2, 3);
		Assert.assertNotNull(activeNodeList.getHeader());
		Assert.assertNotNull(activeNodeList.getTail());
		Assert.assertNotSame(activeNodeList.getTail(), activeNodeList.getHeader());
	}

	private void checkRemove1() {

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

		if (i != ints.length - 1 || nodes.hasNext()) {
			Assert.fail("check content failed");
		}
	}
}
