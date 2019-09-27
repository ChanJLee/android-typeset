package me.chan.te.data;

import java.util.List;

import me.chan.te.config.SegmentAttributes;

public class Paragraph {
	private List<Line> mLines;
	private SegmentAttributes mSegmentAttributes;

	public void setLines(List<Line> lines) {
		mLines = lines;
	}

	public List<Line> getLines() {
		return mLines;
	}

	public Paragraph(SegmentAttributes segmentAttributes) {
		mSegmentAttributes = segmentAttributes;
	}

	public SegmentAttributes getSegmentAttributes() {
		return mSegmentAttributes;
	}

	public void setSegmentAttributes(SegmentAttributes segmentAttributes) {
		mSegmentAttributes = segmentAttributes;
	}
}
