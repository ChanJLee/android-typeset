package me.chan.te.data;

import java.util.List;

import me.chan.te.config.LineAttributes;

/**
 * 已经排版好的段落
 */
public class Paragraph {
	private List<Line> mLines;
	private LineAttributes mLineAttributes;

	public void setLines(List<Line> lines) {
		mLines = lines;
	}

	public List<Line> getLines() {
		return mLines;
	}

	public Paragraph(LineAttributes lineAttributes) {
		mLineAttributes = lineAttributes;
	}

	public LineAttributes getLineAttributes() {
		return mLineAttributes;
	}

	public void setLineAttributes(LineAttributes lineAttributes) {
		mLineAttributes = lineAttributes;
	}
}
