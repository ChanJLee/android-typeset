package me.chan.texas.renderer.ui;

import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.Document;
import me.chan.texas.text.Segment;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface TexasRendererAdapter {
	int getItemCount();

	Segment getItem(int position);

	Document getDocument();

	RenderOption getRenderOption();

	int indexOf(Segment segment);

	void updateSegment(RecyclerView.ViewHolder holder, Segment segment);
}
