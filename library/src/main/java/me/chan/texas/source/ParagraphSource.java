package me.chan.texas.source;

import androidx.annotation.AnyThread;

import me.chan.texas.TexasOption;
import me.chan.texas.text.Paragraph;

/**
 * 设置paragraph source
 */
public abstract class ParagraphSource extends Source<Paragraph> {

	private TexasOptionLoader mLoader;

	public void setLoader(TexasOptionLoader loader) {
		mLoader = loader;
	}

	@Override
	protected final Paragraph onOpen(LoadingStrategy strategy) throws SourceOpenException {
		return onOpen(mLoader.load(this));
	}

	/**
	 * see {@link Paragraph.Builder#newBuilder(TexasOption)} for more information
	 *
	 * @param option option
	 * @return paragraph
	 */
	@AnyThread
	protected abstract Paragraph onOpen(TexasOption option);

	public interface TexasOptionLoader {
		TexasOption load(ParagraphSource source);
	}
}