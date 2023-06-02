package com.shanbay.lib.texas.renderer.ui.text;

import android.view.MotionEvent;

import androidx.annotation.RestrictTo;

import com.shanbay.lib.texas.text.Paragraph;
import com.shanbay.lib.texas.text.layout.Box;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface OnSelectedChangedListener {
	boolean onBoxSelected(MotionEvent e, Paragraph paragraph, boolean isLongClicked, Box box);
}