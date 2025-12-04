package me.chan.texas.ext.markdown.math.renderer;

import androidx.annotation.NonNull;

public interface OptimizableRendererNode {

	@NonNull
	RendererNode optimize();
}
