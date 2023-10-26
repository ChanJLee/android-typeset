package me.chan.texas.measurer;

import androidx.annotation.RestrictTo;

import me.chan.texas.misc.PaintSet;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface MeasureFactory {

	Measurer create(PaintSet paintSet);
}
