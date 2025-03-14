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
	}

}