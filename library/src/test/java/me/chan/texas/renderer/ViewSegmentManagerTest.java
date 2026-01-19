package me.chan.texas.renderer;

import android.view.View;

import me.chan.texas.TestUtils;
import me.chan.texas.renderer.ui.RendererAdapterImpl;
import me.chan.texas.text.ViewSegment;

import org.junit.Assert;
import org.junit.Test;

public class ViewSegmentManagerTest {

	@Test
	public void test() {
		RendererAdapterImpl.ViewSegmentManager manager = new RendererAdapterImpl.ViewSegmentManager();
		try {
			Assert.assertEquals(manager.getLayout(1), 1);
			Assert.fail("check get layout failed");
		} catch (Throwable throwable) {
		}

		ViewSegment viewSegment = new ViewSegment(1) {
			@Override
			protected void onRender(View view) {

			}
		};
		Assert.assertEquals(false, viewSegment.isDisableReuse());
		Assert.assertEquals(manager.getType(viewSegment), -5);

		ViewSegment viewSegment2 = new ViewSegment(1) {
			@Override
			protected void onRender(View view) {

			}
		};
		Assert.assertEquals(false, viewSegment2.isDisableReuse());
		Assert.assertEquals(manager.getType(viewSegment2), -5);

		viewSegment.recycle();

		viewSegment = new ViewSegment(1, true) {
			@Override
			protected void onRender(View view) {

			}
		};
		Assert.assertEquals(true, viewSegment.isDisableReuse());
		Assert.assertEquals(manager.getType(viewSegment), viewSegment.getId());
		viewSegment.recycle();
	}
}
