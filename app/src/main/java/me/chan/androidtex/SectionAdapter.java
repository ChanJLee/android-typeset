package me.chan.androidtex;

import androidx.annotation.NonNull;

import me.chan.texas.TexasOption;
import me.chan.texas.adapter.ParseException;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.text.Document;

public class SectionAdapter extends TexasView.Adapter<Section> {
    
    @NonNull
    @Override
    protected Document parse(@NonNull Section content, TexasOption texasOption) throws ParseException {
        return null;
    }
}
