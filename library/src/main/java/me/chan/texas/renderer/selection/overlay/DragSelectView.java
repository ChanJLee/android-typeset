package me.chan.texas.renderer.selection.overlay;

import me.chan.texas.renderer.selection.SelectionMethodImpl;

public interface DragSelectView {

	void setVisibility(int visible);

	void setSelectionMethod(SelectionMethodImpl selectionMethod);

	void updateContentScrollY(int y);

	void renderRegion(float x1, float y1, float x2, float y2, float adviseOffsetY);

	void setColor(int color);

	void setEnable(boolean enable);
}
