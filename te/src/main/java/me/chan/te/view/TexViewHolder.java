package me.chan.te.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class TexViewHolder extends RecyclerView.ViewHolder {

	ParagraphView mParagraphView;

	TexViewHolder(@NonNull View itemView) {
		super(itemView);
		mParagraphView = (ParagraphView) itemView;
	}
}