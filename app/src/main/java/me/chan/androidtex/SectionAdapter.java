package me.chan.androidtex;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.chan.texas.TexasOption;
import me.chan.texas.adapter.ParseException;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextStyle;

public class SectionAdapter extends TexasView.Adapter<Section> {

    private Context mContext;

    public SectionAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    protected Document parse(@NonNull Section sections, TexasOption texasOption) throws ParseException {
        Resources resources = mContext.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        float titleTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 24, displayMetrics);
        Document document = Document.createEmptyDocument();
        for (Section.Chapter chapter : sections.content) {
            Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption, Paragraph.TYPESET_POLICY_CN);
            builder.newSpanBuilder()
                    .next(chapter.title)
                    .setTextStyle(new TextStyle() {
                        @Override
                        public void update(@NonNull TextPaint textPaint, @Nullable Object tag) {
                            textPaint.setTextSize(titleTextSize);
                        }
                    })
                    .buildSpan();
            document.addSegment(builder.build());

            builder = Paragraph.Builder.newBuilder(texasOption, Paragraph.TYPESET_POLICY_CN);
            builder.text(chapter.content);
            document.addSegment(builder.build());
        }

        return document;
    }
}
