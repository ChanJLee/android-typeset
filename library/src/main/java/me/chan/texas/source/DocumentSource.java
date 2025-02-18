package me.chan.texas.source;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.TexasOption;
import me.chan.texas.text.Document;

public abstract class DocumentSource extends Source<Document> {

	private Document mPreviousDocument;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void setPreviousDocument(Document previousDocument) {
		mPreviousDocument = previousDocument;
	}

	@Override
	protected final Document onRead(TexasOption option) {
		return onRead(option, mPreviousDocument);
	}

	/**
	 * @param option           当前的option
	 * @param previousDocument 上一次的document
	 * @return 数据
	 * <p>
	 * {@link Document.Builder(Document)} 增量更新内容
	 */
	protected abstract Document onRead(TexasOption option, @Nullable Document previousDocument);
}
