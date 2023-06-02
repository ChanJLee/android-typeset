package me.chan.texas.renderer.ui.text;

import android.view.MotionEvent;

import androidx.annotation.RestrictTo;

import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface OnSelectedChangedListener {
	boolean onBoxSelected(MotionEvent e, Paragraph paragraph, boolean isLongClicked, Box box);
}