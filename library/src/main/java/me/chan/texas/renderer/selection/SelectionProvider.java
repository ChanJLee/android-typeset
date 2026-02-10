package me.chan.texas.renderer.selection;

import androidx.annotation.RestrictTo;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.renderer.ui.text.ParagraphView;
import me.chan.texas.text.Paragraph;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SelectionProvider {

	private final List<ParagraphBinding> mBinds = new ArrayList<>();

	public void add(ParagraphBinding bind) {
		mBinds.add(bind);
	}

	/**
	 * @return 当前有多少段落需要参与选中
	 */
	public int size() {
		return mBinds.size();
	}

	/**
	 * @param index 对应的下标
	 * @return 当前段落
	 */
	public ParagraphBinding get(int index) {
		return mBinds.get(index);
	}

	public static class ParagraphBinding {
		private final int mId;
		private final Paragraph mParagraph;
		private ParagraphView mView;

		public ParagraphBinding(int id, Paragraph paragraph) {
			this.mId = id;
			mParagraph = paragraph;
		}

		public void setView(ParagraphView view) {
			mView = view;
		}

		public int getId() {
			return mId;
		}

		public Paragraph getParagraph() {
			return mParagraph;
		}

		public ParagraphView getView() {
			return mView;
		}
	}
}
