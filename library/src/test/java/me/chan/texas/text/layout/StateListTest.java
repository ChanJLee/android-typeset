package me.chan.texas.text.layout;

import static org.junit.Assert.*;

import org.junit.Test;

public class StateListTest {

	@Test
	public void test() {
		StateList stateList = new StateList();
		assertFalse(stateList.isSelected());
		stateList.setSelected(true);
		assertTrue(stateList.isSelected());
		stateList.setSelected(false);
		assertFalse(stateList.isSelected());

		assertFalse(stateList.isHighlighted());
		stateList.setHighlighted(true);
		assertTrue(stateList.isHighlighted());
		stateList.setHighlighted(false);
		assertFalse(stateList.isHighlighted());

		stateList.setSelected(true);
		stateList.setHighlighted(true);
		assertTrue(stateList.isSelected());
		assertTrue(stateList.isHighlighted());
		stateList.clear();
		assertFalse(stateList.isSelected());
		assertFalse(stateList.isHighlighted());
	}
}