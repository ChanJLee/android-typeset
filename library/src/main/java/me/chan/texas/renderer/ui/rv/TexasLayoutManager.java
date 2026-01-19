package me.chan.texas.renderer.ui.rv;


import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.ui.TexasRendererAdapter;
import me.chan.texas.renderer.ui.text.TextureParagraph;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface TexasLayoutManager {

	int findFirstVisibleItemPosition();

	int findLastVisibleItemPosition();

	@Nullable
	TextureParagraph findTextureParagraphByPosition(int index);

	int findFirstCompletelyVisibleItemPosition();

	int findLastCompletelyVisibleItemPosition();
}
