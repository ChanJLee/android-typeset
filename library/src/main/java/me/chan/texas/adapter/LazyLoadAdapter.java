package me.chan.texas.adapter;

import androidx.annotation.NonNull;

import me.chan.texas.TexasOption;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.text.Document;

public abstract class LazyLoadAdapter<T> extends TexasView.Adapter<T> {

    private final Document mDocument;

    public LazyLoadAdapter() {
        mDocuemnt = Document.createEmptyDocument();
    }

    @NonNull
    @Override
    protected final Document parse(@NonNull T content, TexasOption texasOption) throws ParseException {
        return mDocuemnt;
    }

    protected abstract boolean onLoad();
}
