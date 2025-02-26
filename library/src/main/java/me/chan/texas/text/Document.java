package me.chan.texas.text;

import me.chan.texas.Texas;
import me.chan.texas.utils.ReferenceCountingPointer;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * 文档
 */
public final class Document {
	private ReferenceCountingPointer<List<Segment>> mSegments;

	private Document(Builder builder) {
		mSegments = builder.mSegments;
	}

	/**
	 * @param segment segment
	 * @return segment在document中的下标
	 */
	public int indexOfSegment(Segment segment) {
		if (segment == null || mSegments == null) {
			return -1;
		}

		return mSegments.get().indexOf(segment);
	}

	/**
	 * 获取段落数目
	 *
	 * @return 段落数目
	 */
	public int getSegmentCount() {
		return mSegments == null ? 0 : mSegments.get().size();
	}

	/**
	 * 获取segment
	 *
	 * @param index 下标
	 * @return segment
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *                                   (<tt>index &lt; 0 || index &gt;= size()</tt>)
	 */
	public Segment getSegment(int index) {
		return mSegments.get().get(index);
	}

	@RestrictTo(LIBRARY)
	public void release() {
		clear();
		mSegments = null;
	}

	@RestrictTo(LIBRARY)
	public void clear() {
		mSegments.release();
	}

	@RestrictTo(LIBRARY)
	public boolean isCopy() {
		return mSegments.getRefCount() > 1;
	}

	public static class Builder {
		private final ReferenceCountingPointer<List<Segment>> mSegments;

		/**
		 * 拷贝这个document的内容，并且可以编辑
		 *
		 * @param document document
		 */
		public Builder(@Nullable Document document) {
			mSegments = new ReferenceCountingPointer<List<Segment>>(document.mSegments) {
				@Override
				protected List<Segment> onAcquire(List<Segment> value) {
					return new ArrayList<>(value);
				}
			};
		}

		public Builder() {
			Texas.MemoryOption memoryOption = Texas.getMemoryOption();
			mSegments = new ReferenceCountingPointer<>(new ArrayList<>(memoryOption.getDocumentSegmentInitialCapacity()), segments -> {
				final int count = segments.size();
				for (int i = 0; i < count; ++i) {
					Segment segment = segments.get(i);
					segment.recycle();
				}
			});
		}

		public Builder addSegment(Segment segment) {
			mSegments.get().add(segment);
			return this;
		}

		public Builder addSegment(int index, Segment segment) {
			mSegments.get().add(index, segment);
			return this;
		}

		public Builder addSegments(int index, List<Segment> segments) {
			mSegments.get().addAll(index, segments);
			return this;
		}

		public Builder addSegments(List<Segment> segments) {
			mSegments.get().addAll(segments);
			return this;
		}

		public Builder removeSegment(int index) {
			mSegments.get().remove(index);
			return this;
		}

		public Builder removeSegment(Segment segment) {
			mSegments.get().remove(segment);
			return this;
		}

		public Builder updateSegment(int index, Segment segment) {
			mSegments.get().set(index, segment);
			return this;
		}

		public Segment getSegment(int index) {
			return mSegments.get().get(index);
		}

		public int getSegmentCount() {
			return mSegments.get().size();
		}

		public int indexOfSegment(Segment segment) {
			return mSegments.get().indexOf(segment);
		}

		public Document build() {
			return new Document(this);
		}
	}
}
