package me.chan.te;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;

import com.bumptech.glide.request.target.ViewTarget;

import me.chan.te.annotations.Hidden;
import me.chan.te.text.DrawableBox;
import me.chan.te.text.Glue;
import me.chan.te.text.Penalty;
import me.chan.te.text.TextBox;
import me.chan.te.log.Log;
import me.chan.te.text.Background;
import me.chan.te.text.Document;
import me.chan.te.text.Figure;
import me.chan.te.text.Page;
import me.chan.te.text.Paragraph;
import me.chan.te.text.UnderLine;
import me.chan.te.typesetter.BreakPoint;
import me.chan.te.typesetter.Candidate;
import me.chan.te.typesetter.Node;
import me.chan.te.typesetter.Sum;

public class Te {

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
				Te.MemoryOption memoryOption = Te.getMemoryOption();
				memoryOption.setDocumentPageInitialCapacity(4)
						.setDocumentSegmentInitialCapacity(4)
						.setPageSegmentInitialCapacity(4)
						.setParagraphElementInitialCapacity(4)
						.setParagraphLineBoxInitialCapacity(4)
						.setParagraphLineInitialCapacity(4);
				Te.setMemoryOption(memoryOption);
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
		Log.i("Te", "clean text engine memory");
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
		Page.clean();
		TextBox.Attribute.clean();
		System.gc();
	}

	public static class MemoryOption {
		private int mDocumentSegmentInitialCapacity = 512;
		private int mDocumentPageInitialCapacity = 64;
		private int mPageSegmentInitialCapacity = 8;
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

		public int getDocumentPageInitialCapacity() {
			return mDocumentPageInitialCapacity;
		}

		public MemoryOption setDocumentPageInitialCapacity(int documentPageInitialCapacity) {
			mDocumentPageInitialCapacity = documentPageInitialCapacity;
			return this;
		}

		public int getPageSegmentInitialCapacity() {
			return mPageSegmentInitialCapacity;
		}

		public MemoryOption setPageSegmentInitialCapacity(int pageSegmentInitialCapacity) {
			mPageSegmentInitialCapacity = pageSegmentInitialCapacity;
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
