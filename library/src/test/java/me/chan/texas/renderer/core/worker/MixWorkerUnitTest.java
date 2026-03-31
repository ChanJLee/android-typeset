package me.chan.texas.renderer.core.worker;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import org.junit.Test;

import me.chan.texas.misc.Rect;
import me.chan.texas.renderer.ui.RendererHost;
import me.chan.texas.text.Document;
import me.chan.texas.text.Segment;

public class MixWorkerUnitTest {

	@Test
	public void test() {
		Document.Builder builder = new Document.Builder();
		MySegment s1 = new MySegment("s1");
		MySegment s2 = new MySegment("s2");
		MySegment s3 = new MySegment("s3");
		MySegment s4 = new MySegment("s4");

		builder.addSegment(s1).addSegment(s2).addSegment(s3);
		Document lhs = builder.build();

		builder = new Document.Builder();
		builder.addSegment(s1).addSegment(s2).addSegment(s3).addSegment(s4);
		Document rhs = builder.build();

		DiffUtil.DiffResult diff = MixWorker.diff(lhs, rhs);
		System.out.println("=====");
		diff.dispatchUpdatesTo(new LogUpdateCallback(lhs, rhs));

		builder = new Document.Builder();
		builder.addSegment(s1).addSegment(s2);
		rhs = builder.build();

		diff = MixWorker.diff(lhs, rhs);
		System.out.println("=====");
		diff.dispatchUpdatesTo(new LogUpdateCallback(lhs, rhs));

		builder = new Document.Builder();
		builder.addSegment(s1).addSegment(s2).addSegment(s4);
		rhs = builder.build();

		diff = MixWorker.diff(lhs, rhs);
		System.out.println("=====");
		diff.dispatchUpdatesTo(new LogUpdateCallback(lhs, rhs));
	}

	private static class LogUpdateCallback implements ListUpdateCallback {

		private final Document lhs;
		private final Document rhs;

		private LogUpdateCallback(Document lhs, Document rhs) {
			this.lhs = lhs;
			this.rhs = rhs;
		}

		@Override
		public void onInserted(int position, int count) {
			System.out.println("inserted at " + position + " count: " + count);
		}

		@Override
		public void onRemoved(int position, int count) {
			System.out.println("remove at " + position + " count: " + count);
		}

		@Override
		public void onMoved(int fromPosition, int toPosition) {
			System.out.println("move from " + fromPosition + " to " + toPosition);
		}

		@Override
		public void onChanged(int position, int count, @Nullable Object payload) {
			System.out.println("position " + position + " count: " + count);
		}
	}

	private static class MySegment extends Segment {

		private final String name;

		public MySegment(String name) {
			this.name = name;
		}

		@Override
		public void getRect(Rect rect) {

		}

		@Nullable
		@Override
		public Rect getRect() {
			return null;
		}

		@Override
		public void setPadding(Rect rect) {

		}

		@Override
		public void recycle() {

		}

		@Override
		public boolean isRecycled() {
			return false;
		}

		@Override
		public int getId() {
			return 0;
		}

		@Override
		public void bind(RendererHost host) {

		}

		@Override
		public void attachToWindow(RecyclerView.ViewHolder holder) {

		}

		@Override
		public void detachFromWindow(RecyclerView.ViewHolder holder) {

		}

		@Override
		public void requestRedraw() {

		}

		@Override
		public int getIndex() {
			return 0;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
