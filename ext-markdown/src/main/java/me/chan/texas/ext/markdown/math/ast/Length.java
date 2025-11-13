package me.chan.texas.ext.markdown.math.ast;

import android.util.DisplayMetrics;
import android.util.TypedValue;

import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;
import me.chan.texas.utils.TexasUtils;

public class Length implements Ast {
	public final NumberAtom size;
	public final SizeUnit unit;

	public Length(NumberAtom size, SizeUnit unit) {
		this.size = size;
		this.unit = unit;
	}

	@Override
	public String toString() {
		return size.toString() + unit.toString();
	}

	public float getSize(MathPaint paint, DisplayMetrics displayMetrics) {
		float value = size.toValue();
		if (TexasUtils.equals(unit.unit, "px")) {
			return value;
		}

		if (TexasUtils.equals("pt", unit.unit)) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, value, displayMetrics);
		}

		if (TexasUtils.equals("in", unit.unit)) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, value, displayMetrics);
		}

		if (TexasUtils.equals("mm", unit.unit)) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, value, displayMetrics);
		}

		if (TexasUtils.equals("cm", unit.unit)) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, value * 10, displayMetrics);
		}

		if (TexasUtils.equals("em", unit.unit)) {
			return value / MathFontOptions.UNITS_PER_EM * paint.getTextSize();
		}

		if (TexasUtils.equals("ex", unit.unit)) {
			return value / MathFontOptions.UNITS_PER_EM * (paint.getTextSize() / 2);
		}

		throw new RuntimeException("Unknown unit: " + unit.unit);
	}
}
