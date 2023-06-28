package me.chan.androidtex;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class Section {
    public List<Chapter> content;
    @Keep
    public static class Chapter {
        public String type;
        public String title;
        public String content;
    }
}
