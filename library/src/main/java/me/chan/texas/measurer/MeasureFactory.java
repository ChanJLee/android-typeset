package me.chan.texas.measurer;

import me.chan.texas.misc.PaintSet;

public interface MeasureFactory {

	Measurer create(PaintSet paintSet);
}
