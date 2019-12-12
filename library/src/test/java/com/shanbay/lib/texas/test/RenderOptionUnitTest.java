package com.shanbay.lib.texas.test;

import android.graphics.Typeface;

import com.shanbay.lib.texas.renderer.RenderOption;
import com.shanbay.lib.texas.text.BreakStrategy;
import com.shanbay.lib.texas.text.HyphenStrategy;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RenderOptionUnitTest {

	@Test
	public void test() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Constructor<?> constructor = Typeface.class.getDeclaredConstructor(long.class);
		constructor.setAccessible(true);

		Typeface typeface = (Typeface) constructor.newInstance(1);

		RenderOption renderOption = new RenderOption();
		renderOption.setTextColor(1);
		renderOption.setTypeface(typeface);
		renderOption.setTextSize(2);
		renderOption.setLineSpace(3);
		renderOption.setIndentEnable(true);
		renderOption.setSelectedBackgroundColor(4);
		renderOption.setSelectedTextColor(5);
		renderOption.setBreakStrategy(BreakStrategy.SIMPLE);
		renderOption.setWordSelectable(true);
		renderOption.setEnableDebug(true);
		renderOption.setSpanSelectedBackgroundColor(6);
		renderOption.setSpanSelectedTextColor(7);
		renderOption.setHyphenStrategy(HyphenStrategy.UK);

		Assert.assertEquals(renderOption.getTextColor(), 1);
		Assert.assertSame(renderOption.getTypeface(), typeface);
		Assert.assertEquals(renderOption.getTextSize(), 2, 0);
		Assert.assertEquals(renderOption.getLineSpace(), 3, 0);
		Assert.assertTrue(renderOption.isIndentEnable());
		Assert.assertEquals(renderOption.getSelectedBackgroundColor(), 4);
		Assert.assertEquals(renderOption.getSelectedTextColor(), 5);
		Assert.assertEquals(renderOption.getBreakStrategy(), BreakStrategy.SIMPLE);
		Assert.assertTrue(renderOption.isWordSelectable());
		Assert.assertTrue(renderOption.isEnableDebug());
		Assert.assertEquals(renderOption.getSpanSelectedBackgroundColor(), 6);
		Assert.assertEquals(renderOption.getSpanSelectedTextColor(), 7);
		Assert.assertEquals(renderOption.getHyphenStrategy(), HyphenStrategy.UK);

		RenderOption copy = new RenderOption(renderOption);
		Assert.assertEquals(renderOption.getTextColor(), copy.getTextColor());
		Assert.assertSame(renderOption.getTypeface(), copy.getTypeface());
		Assert.assertEquals(renderOption.getTextSize(), copy.getTextSize(), 0);
		Assert.assertEquals(renderOption.getLineSpace(), copy.getLineSpace(), 0);
		Assert.assertEquals(renderOption.isIndentEnable(), copy.isIndentEnable());
		Assert.assertEquals(renderOption.getSelectedBackgroundColor(), copy.getSelectedBackgroundColor());
		Assert.assertEquals(renderOption.getSelectedTextColor(), copy.getSelectedTextColor());
		Assert.assertEquals(renderOption.getBreakStrategy(), copy.getBreakStrategy());
		Assert.assertEquals(renderOption.isWordSelectable(), copy.isWordSelectable());
		Assert.assertEquals(renderOption.isEnableDebug(), copy.isEnableDebug());
		Assert.assertEquals(renderOption.getSpanSelectedBackgroundColor(), renderOption.getSpanSelectedBackgroundColor());
		Assert.assertEquals(renderOption.getSpanSelectedTextColor(), renderOption.getSpanSelectedTextColor());
		Assert.assertEquals(renderOption.getHyphenStrategy(), renderOption.getHyphenStrategy());

		copy.setTextColor(-1);
		copy.setTypeface((Typeface) constructor.newInstance(1));
		copy.setTextSize(-2);
		copy.setLineSpace(-3);
		copy.setIndentEnable(false);
		copy.setSelectedBackgroundColor(-4);
		copy.setSelectedTextColor(-5);
		copy.setBreakStrategy(BreakStrategy.BALANCED);
		copy.setWordSelectable(false);
		copy.setEnableDebug(false);
		copy.setSpanSelectedBackgroundColor(-6);
		copy.setSpanSelectedTextColor(-7);
		copy.setHyphenStrategy(HyphenStrategy.US);

		Assert.assertNotEquals(renderOption.getTextColor(), copy.getTextColor());
		Assert.assertNotSame(renderOption.getTypeface(), copy.getTypeface());
		Assert.assertNotEquals(renderOption.getTextSize(), copy.getTextSize(), 0);
		Assert.assertNotEquals(renderOption.getLineSpace(), copy.getLineSpace(), 0);
		Assert.assertNotEquals(renderOption.isIndentEnable(), copy.isIndentEnable());
		Assert.assertNotEquals(renderOption.getSelectedBackgroundColor(), copy.getSelectedBackgroundColor());
		Assert.assertNotEquals(renderOption.getSelectedTextColor(), copy.getSelectedTextColor());
		Assert.assertNotEquals(renderOption.getBreakStrategy(), copy.getBreakStrategy());
		Assert.assertNotEquals(renderOption.isWordSelectable(), copy.isWordSelectable());
		Assert.assertNotEquals(renderOption.isEnableDebug(), copy.isEnableDebug());
		Assert.assertNotEquals(renderOption.getSpanSelectedBackgroundColor(), copy.getSpanSelectedBackgroundColor());
		Assert.assertNotEquals(renderOption.getSpanSelectedTextColor(), copy.getSpanSelectedTextColor());
		Assert.assertNotEquals(renderOption.getHyphenStrategy(), copy.getHyphenStrategy());
	}
}
