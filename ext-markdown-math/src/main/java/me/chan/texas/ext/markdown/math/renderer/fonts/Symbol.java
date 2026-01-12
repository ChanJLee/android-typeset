package me.chan.texas.ext.markdown.math.renderer.fonts;

public class Symbol {
	public String c;
	public float[] bbox;

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