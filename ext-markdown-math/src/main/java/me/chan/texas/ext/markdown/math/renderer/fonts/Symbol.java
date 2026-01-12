package me.chan.texas.ext.markdown.math.renderer.fonts;

public class Symbol {
	public String c;
	public float[] bbox;

	public Symbol(String c, float[] bbox) {
		this.c = c;
		this.bbox = bbox;
	}

	public Symbol(String c, float xMin, float ascent, float xMax, float descent) {
		this.c = c;
		this.bbox = new float[]{xMin, ascent, xMax, descent};
	}


	public float xMin() {
		return bbox[0];
	}

	public float ascent() {
		return bbox[1];
	}

	public float xMax() {
		return bbox[2];
	}

	public float descent() {
		return bbox[3];
	}


	@Override
	public String toString() {
		return c;
	}
}