package me.chan.texas.renderer.core.worker;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.BatchingListUpdateCallback;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import me.chan.texas.TexasOption;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.test.mock.MockTextPaint;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.TextAttribute;

public class DiffUtilsUnitTest {

	private Measurer mMeasurer;
	private TextAttribute mTextAttribute;
	private PaintSet mPaintSet;


	@Before
	public void setup() {
		MockTextPaint mockTextPaint = new MockTextPaint(20);
		mMeasurer = new MockMeasurer(mockTextPaint);
		mTextAttribute = new TextAttribute(mMeasurer);
		mPaintSet = new PaintSet(mockTextPaint);
	}

	@Test
	public void test() {
		TexasOption texasOption = new TexasOption(mPaintSet, Hyphenation.getInstance(), mMeasurer, mTextAttribute, new RenderOption());
		Document old = new Document.Builder()
				.addSegment(Paragraph.Builder.newBuilder(texasOption)
						.text("0")
						.tag("0")
						.build())
				.addSegment(Paragraph.Builder.newBuilder(texasOption)
						.text("1")
						.tag("1")
						.build())
				.addSegment(Paragraph.Builder.newBuilder(texasOption)
						.text("2")
						.tag("2")
						.build())
				.addSegment(Paragraph.Builder.newBuilder(texasOption)
						.text("5")
						.tag("5")
						.build())
				.build();


		Document.Builder builder = new Document.Builder(old)
				.addSegment(Paragraph.Builder.newBuilder(texasOption)
						.text("3")
						.tag("3")
						.build());

		Document copy = builder.build();
//		diff(old, copy, new HashSet<>(Arrays.asList(3)));

		builder = new Document.Builder(old)
				.updateSegment(1, Paragraph.Builder.newBuilder(texasOption)
						.text("3")
						.tag("3")
						.build());

		copy = builder.build();
//		diff(old, copy, new HashSet<>(Arrays.asList(1)));

		builder = new Document.Builder(old);
		Segment tmp = builder.getSegment(0);
		builder.updateSegment(0, builder.getSegment(1));
		builder.updateSegment(1, tmp);
		builder.updateSegment(1, Paragraph.Builder.newBuilder(texasOption)
				.text("4")
				.tag("4")
				.build());

		copy = builder.build();
		diff(old, copy, new HashSet<>(Arrays.asList(1)));
	}

	private void diff(Document lhs, Document rhs, Set<Integer> updates) {
		System.out.println("============" + toStr(lhs) + "-" + toStr(rhs));


		DiffUtil.DiffResult diff = MixWorker.diff(lhs, rhs);
		diff.dispatchUpdatesTo(new BatchingListUpdateCallback(new ListUpdateCallback() {
			@Override
			public void onInserted(int position, int count) {
				for (int i = 0; i < count; i++) {
					int v = position + i;
					System.out.println("+ insert: " + (v) + "(" + diff.convertNewPositionToOld(v) + "," + diff.convertOldPositionToNew(v) + ")");
					updates.remove(v);
				}
			}

			@Override
			public void onRemoved(int position, int count) {
				for (int i = 0; i < count; i++) {
					System.out.println("- remove: " + (position + i));
				}
			}

			@Override
			public void onMoved(int fromPosition, int toPosition) {
				System.out.println("> _move_: " + fromPosition + "-" + toPosition);
			}

			@Override
			public void onChanged(int position, int count, @Nullable Object payload) {
				for (int i = 0; i < count; i++) {
					int v = position + i;
					System.out.println("* change: " + (v));
					updates.remove(v);
				}
			}
		}));
		Assert.assertTrue(updates.isEmpty());
	}

	private static String toStr(Document document) {
		StringBuilder builder = new StringBuilder("[");
		for (int i = 0; i < document.getSegmentCount(); ++i) {
			builder.append(document.getSegment(i).toString());
			if (i < document.getSegmentCount() - 1) {
				builder.append(", ");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}
