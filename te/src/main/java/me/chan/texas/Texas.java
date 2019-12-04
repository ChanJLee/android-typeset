package me.chan.texas;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;

import com.bumptech.glide.request.target.ViewTarget;

import me.chan.texas.annotations.Hidden;
import me.chan.texas.text.DrawableBox;
import me.chan.texas.text.Foot;
import me.chan.texas.text.Glue;
import me.chan.texas.text.Penalty;
import me.chan.texas.text.TextBox;
import me.chan.texas.log.Log;
import me.chan.texas.text.Background;
import me.chan.texas.text.Document;
import me.chan.texas.text.Figure;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.UnderLine;
import me.chan.texas.typesetter.BreakPoint;
import me.chan.texas.typesetter.Candidate;
import me.chan.texas.typesetter.Node;
import me.chan.texas.typesetter.Sum;

public class Texas {

	private static MemoryOption sMemoryOption = new MemoryOption();

	@Hidden
	public static MemoryOption getMemoryOption() {
		return sMemoryOption;
	}

	@Hidden
	public static void setMemoryOption(MemoryOption memoryOption) {
		sMemoryOption = memoryOption;
	}

	public static void init(Application application) {
		application.registerComponentCallbacks(new ComponentCallbacks() {
			@Override
			public void onConfigurationChanged(Configuration newConfig) {

			}

			@Override
			public void onLowMemory() {
				Texas.MemoryOption memoryOption = Texas.getMemoryOption();
				memoryOption.setDocumentSegmentInitialCapacity(4)
						.setParagraphElementInitialCapacity(4)
						.setParagraphLineBoxInitialCapacity(4)
						.setParagraphLineInitialCapacity(4);
				Texas.setMemoryOption(memoryOption);
				clean();
			}
		});

		try {
			ViewTarget.setTagId(R.id.me_chan_te_glide);
		} catch (Exception ignore) {
		}
	}

	/**
	 * do clean
	 */
	@Hidden
	public static void clean() {
		Log.i("Texas", "clean text engine memory");
		// do clean
		// add engine clean code
		TextBox.clean();
		Glue.clean();
		Paragraph.Line.clean();
		Paragraph.clean();
		Penalty.clean();
		BreakPoint.clean();
		Candidate.clean();
		Node.clean();
		Sum.clean();
		Document.clean();
		Figure.clean();
		DrawableBox.clean();
		Background.clean();
		UnderLine.clean();
		Paragraph.Builder.clean();
		TextBox.Attribute.clean();
		Foot.clean();
		System.gc();
	}

	public static class MemoryOption {
		private int mDocumentSegmentInitialCapacity = 512;
		private int mParagraphLineInitialCapacity = 32;
		private int mParagraphElementInitialCapacity = 128;
		private int mParagraphLineBoxInitialCapacity = 32;

		public int getDocumentSegmentInitialCapacity() {
			return mDocumentSegmentInitialCapacity;
		}

		public MemoryOption setDocumentSegmentInitialCapacity(int documentSegmentInitialCapacity) {
			mDocumentSegmentInitialCapacity = documentSegmentInitialCapacity;
			return this;
		}

		public int getParagraphLineInitialCapacity() {
			return mParagraphLineInitialCapacity;
		}

		public MemoryOption setParagraphLineInitialCapacity(int paragraphLineInitialCapacity) {
			mParagraphLineInitialCapacity = paragraphLineInitialCapacity;
			return this;
		}

		public int getParagraphElementInitialCapacity() {
			return mParagraphElementInitialCapacity;
		}

		public MemoryOption setParagraphElementInitialCapacity(int paragraphElementInitialCapacity) {
			mParagraphElementInitialCapacity = paragraphElementInitialCapacity;
			return this;
		}

		public int getParagraphLineBoxInitialCapacity() {
			return mParagraphLineBoxInitialCapacity;
		}

		public MemoryOption setParagraphLineBoxInitialCapacity(int paragraphLineBoxInitialCapacity) {
			mParagraphLineBoxInitialCapacity = paragraphLineBoxInitialCapacity;
			return this;
		}
	}
}
