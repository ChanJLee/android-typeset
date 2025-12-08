package me.chan.texas.ext.markdown.math.renderer;

public interface HorizontalCalibratedNode {
	// todo support align api?
	boolean supportAlignBaseline();

	float getBaseline();

	void alignBaseline(float baseline);
}
