package me.chan.te.renderer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.chan.te.view.ParagraphView;

class TexViewHolder extends RecyclerView.ViewHolder {

	ParagraphView mParagraphView;

	TexViewHolder(@NonNull View itemView) {
		super(itemView);
		mParagraphView = (ParagraphView) itemView;
	}
}