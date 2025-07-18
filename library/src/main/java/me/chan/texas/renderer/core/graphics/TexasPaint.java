package me.chan.texas.renderer.core.graphics;

import android.graphics.Paint;

public interface TexasPaint {
	public static final int ANTI_ALIAS_FLAG = Paint.ANTI_ALIAS_FLAG;
	public static final int CURSOR_AFTER = Paint.CURSOR_AFTER;
	public static final int CURSOR_AT = 4;
	public static final int CURSOR_AT_OR_AFTER = 1;
	public static final int CURSOR_AT_OR_BEFORE = 3;
	public static final int CURSOR_BEFORE = 2;
	public static final int DEV_KERN_TEXT_FLAG = 256;
	public static final int DITHER_FLAG = 4;
	public static final int EMBEDDED_BITMAP_TEXT_FLAG = 1024;
	public static final int END_HYPHEN_EDIT_INSERT_ARMENIAN_HYPHEN = 3;
	public static final int END_HYPHEN_EDIT_INSERT_HYPHEN = 2;
	public static final int END_HYPHEN_EDIT_INSERT_MAQAF = 4;
	public static final int END_HYPHEN_EDIT_INSERT_UCAS_HYPHEN = 5;
	public static final int END_HYPHEN_EDIT_INSERT_ZWJ_AND_HYPHEN = 6;
	public static final int END_HYPHEN_EDIT_NO_EDIT = 0;
	public static final int END_HYPHEN_EDIT_REPLACE_WITH_HYPHEN = 1;
	public static final int FAKE_BOLD_TEXT_FLAG = 32;
	public static final int FILTER_BITMAP_FLAG = 2;
	public static final int HINTING_OFF = 0;
	public static final int HINTING_ON = 1;
	public static final int LINEAR_TEXT_FLAG = 64;
	public static final int START_HYPHEN_EDIT_INSERT_HYPHEN = 1;
	public static final int START_HYPHEN_EDIT_INSERT_ZWJ = 2;
	public static final int START_HYPHEN_EDIT_NO_EDIT = 0;
	public static final int STRIKE_THRU_TEXT_FLAG = 16;
	public static final int SUBPIXEL_TEXT_FLAG = 128;
	public static final int TEXT_RUN_FLAG_LEFT_EDGE = 8192;
	public static final int TEXT_RUN_FLAG_RIGHT_EDGE = 16384;
	public static final int UNDERLINE_TEXT_FLAG = 8;
}
