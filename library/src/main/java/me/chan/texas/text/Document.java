package me.chan.texas.text;

import me.chan.texas.Texas;
import me.chan.texas.utils.ReferenceCountingPointer;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;


public final class Document {
	private ReferenceCountingPointer<List<Segment>> mSegments;

	private Document(Builder builder) {
		mSegments = builder.mSegments;
	}

	
	public int indexOfSegment(Segment segment) {
		if (segment == null || mSegments == null) {
			return -1;
		}

		return mSegments.get().indexOf(segment);
	}

	
	public int getSegmentCount() {
		return mSegments == null ? 0 : mSegments.get().size();
	}

	@RestrictTo(LIBRARY)
	public boolean isCopy() {
		return mSegments.getRefCount() > 1;
	}

	
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

	public static class Builder {
		private final ReferenceCountingPointer<List<Segment>> mSegments;

		
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
