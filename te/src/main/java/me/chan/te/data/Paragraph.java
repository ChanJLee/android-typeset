package me.chan.te.data;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.config.LineAttributes;
import me.chan.te.misc.ObjectFactory;

/**
 * 已经排版好的段落
 */
public class Paragraph implements Recyclable {
	private static final ObjectFactory<Paragraph> POOL = new ObjectFactory<>(1500);

	private List<Line> mLines = new ArrayList<>(30);
	private LineAttributes mLineAttributes;

	public List<Line> getLines() {
		return mLines;
	}

	public void add(Line line) {
		mLines.add(line);
	}

	private Paragraph(LineAttributes lineAttributes) {
		reset(lineAttributes);
	}

	public LineAttributes getLineAttributes() {
		return mLineAttributes;
	}

	private void reset(LineAttributes lineAttributes) {
		mLineAttributes = lineAttributes;
		mLines.clear();
	}

	@Override
	public void recycle() {
		mLineAttributes = null;
		for (int i = 0; mLines != null && i < mLines.size(); ++i) {
			mLines.get(i).recycle();
		}
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

	public int getLineCount() {
		return mLines.size();
	}
}
