package me.chan.te.data;

import java.util.List;

import me.chan.te.config.LineAttributes;
import me.chan.te.misc.ObjectFactory;

/**
 * 已经排版好的段落
 */
public class Paragraph implements Recyclable {
	private static final ObjectFactory<Paragraph> POOL = new ObjectFactory<>(1500);

	private List<Line> mLines;
	private LineAttributes mLineAttributes;

	public void setLines(List<Line> lines) {
		mLines = lines;
	}

	public List<Line> getLines() {
		return mLines;
	}

	private Paragraph(LineAttributes lineAttributes) {
		reset(lineAttributes);
	}

	public LineAttributes getLineAttributes() {
		return mLineAttributes;
	}

	private void reset(LineAttributes lineAttributes) {
		mLineAttributes = lineAttributes;
	}

	@Override
	public void recycle() {
		mLineAttributes = null;
		for (int i = 0; mLines != null && i < mLines.size(); ++i) {
			mLines.get(i).recycle();
		}
		mLines = null;
		POOL.release(this);
	}

	public static Paragraph obtain(LineAttributes lineAttributes) {
		Paragraph paragraph = POOL.acquire();
		if (paragraph == null) {
			return new Paragraph(lineAttributes);
		}
		paragraph.reset(lineAttributes);
		return paragraph;
	}
}
