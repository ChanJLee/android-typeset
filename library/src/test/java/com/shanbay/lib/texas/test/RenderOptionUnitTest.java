package com.shanbay.lib.texas.test;

import android.graphics.Typeface;

import com.shanbay.lib.texas.TestUtils;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.HyphenStrategy;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RenderOptionUnitTest {

	@Test
	public void test() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Constructor<?> constructor = Typeface.class.getDeclaredConstructor();
		constructor.setAccessible(true);

		Typeface typeface = (Typeface) constructor.newInstance();

		RenderOption renderOption = new RenderOption();
		renderOption.setTextColor(1);
		renderOption.setTypeface(typeface);
		renderOption.setTextSize(2);
		renderOption.setLineSpace(3);
		renderOption.setSelectedBackgroundColor(4);
		renderOption.setSelectedTextColor(5);
		renderOption.setBreakStrategy(BreakStrategy.SIMPLE);
		renderOption.setWordSelectable(true);
		renderOption.setEnableDebug(true);
		renderOption.setSelectedByLongClickBackgroundColor(6);
		renderOption.setSelectedByLongClickTextColor(7);
		renderOption.setHyphenStrategy(HyphenStrategy.UK);
		renderOption.setEnableLazyRender(false);
		renderOption.setLoadingBackgroundColor(8);
		renderOption.setDrawEmoticonSelection(true);

		Assert.assertEquals(renderOption.getTextColor(), 1);
		Assert.assertSame(renderOption.getTypeface(), typeface);
		Assert.assertEquals(renderOption.getTextSize(), 2, 0);
		Assert.assertEquals(renderOption.getLineSpace(), 3, 0);
		Assert.assertEquals(renderOption.getSelectedBackgroundColor(), 4);
		Assert.assertEquals(renderOption.getSelectedTextColor(), 5);
		Assert.assertEquals(renderOption.getBreakStrategy(), BreakStrategy.SIMPLE);
		Assert.assertTrue(renderOption.isWordSelectable());
		Assert.assertTrue(renderOption.isEnableDebug());
		Assert.assertEquals(renderOption.getSelectedByLongClickBackgroundColor(), 6);
		Assert.assertEquals(renderOption.getSelectedByLongClickTextColor(), 7);
		Assert.assertEquals(renderOption.getHyphenStrategy(), HyphenStrategy.UK);
		Assert.assertFalse(renderOption.isEnableLazyRender());
		Assert.assertEquals(renderOption.getLoadingBackgroundColor(), 8);
		Assert.assertTrue(renderOption.isDrawEmoticonSelection());

		RenderOption copy = new RenderOption(renderOption);
		Assert.assertEquals(renderOption.getTextColor(), copy.getTextColor());
		Assert.assertSame(renderOption.getTypeface(), copy.getTypeface());
		Assert.assertEquals(renderOption.getTextSize(), copy.getTextSize(), 0);
		Assert.assertEquals(renderOption.getLineSpace(), copy.getLineSpace(), 0);
		Assert.assertEquals(renderOption.getSelectedBackgroundColor(), copy.getSelectedBackgroundColor());
		Assert.assertEquals(renderOption.getSelectedTextColor(), copy.getSelectedTextColor());
		Assert.assertEquals(renderOption.getBreakStrategy(), copy.getBreakStrategy());
		Assert.assertEquals(renderOption.isWordSelectable(), copy.isWordSelectable());
		Assert.assertEquals(renderOption.isEnableDebug(), copy.isEnableDebug());
		Assert.assertEquals(renderOption.getSelectedByLongClickBackgroundColor(), copy.getSelectedByLongClickBackgroundColor());
		Assert.assertEquals(renderOption.getSelectedByLongClickTextColor(), copy.getSelectedByLongClickTextColor());
		Assert.assertEquals(renderOption.getHyphenStrategy(), copy.getHyphenStrategy());
		Assert.assertEquals(renderOption.isEnableLazyRender(), copy.isEnableLazyRender());
		Assert.assertEquals(renderOption.getLoadingBackgroundColor(), copy.getLoadingBackgroundColor());
		Assert.assertEquals(renderOption, copy);
		Assert.assertEquals(renderOption.isDrawEmoticonSelection(), copy.isDrawEmoticonSelection());
		Assert.assertTrue(TestUtils.reflectCompare(copy, renderOption));

		copy.setTextColor(-1);
		copy.setTypeface((Typeface) constructor.newInstance());
		copy.setTextSize(-2);
		copy.setLineSpace(-3);
		copy.setSelectedBackgroundColor(-4);
		copy.setSelectedTextColor(-5);
		copy.setBreakStrategy(BreakStrategy.BALANCED);
		copy.setWordSelectable(false);
		copy.setEnableDebug(false);
		copy.setSelectedByLongClickBackgroundColor(-6);
		copy.setSelectedByLongClickTextColor(-7);
		copy.setHyphenStrategy(HyphenStrategy.US);
		copy.setEnableLazyRender(true);
		copy.setLoadingBackgroundColor(-8);
		copy.setDrawEmoticonSelection(false);

		Assert.assertNotEquals(renderOption.getTextColor(), copy.getTextColor());
		Assert.assertNotSame(renderOption.getTypeface(), copy.getTypeface());
		Assert.assertNotEquals(renderOption.getTextSize(), copy.getTextSize(), 0);
		Assert.assertNotEquals(renderOption.getLineSpace(), copy.getLineSpace(), 0);
		Assert.assertNotEquals(renderOption.getSelectedBackgroundColor(), copy.getSelectedBackgroundColor());
		Assert.assertNotEquals(renderOption.getSelectedTextColor(), copy.getSelectedTextColor());
		Assert.assertNotEquals(renderOption.getBreakStrategy(), copy.getBreakStrategy());
		Assert.assertNotEquals(renderOption.isWordSelectable(), copy.isWordSelectable());
		Assert.assertNotEquals(renderOption.isEnableDebug(), copy.isEnableDebug());
		Assert.assertNotEquals(renderOption.getSelectedByLongClickBackgroundColor(), copy.getSelectedByLongClickBackgroundColor());
		Assert.assertNotEquals(renderOption.getSelectedByLongClickTextColor(), copy.getSelectedByLongClickTextColor());
		Assert.assertNotEquals(renderOption.getHyphenStrategy(), copy.getHyphenStrategy());
		Assert.assertNotEquals(renderOption.isEnableLazyRender(), copy.isEnableLazyRender());
		Assert.assertNotEquals(renderOption.getLoadingBackgroundColor(), copy.getLoadingBackgroundColor());
		Assert.assertNotEquals(renderOption.isDrawEmoticonSelection(), copy.isDrawEmoticonSelection());
		Assert.assertNotEquals(renderOption, copy);
	}
}
