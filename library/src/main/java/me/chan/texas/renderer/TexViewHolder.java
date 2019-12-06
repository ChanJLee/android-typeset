package me.chan.texas.renderer;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

class TexViewHolder extends RecyclerView.ViewHolder {

	ParagraphView mParagraphView;

	TexViewHolder(@NonNull View itemView) {
		super(itemView);
		mParagraphView = (ParagraphView) itemView;
	}
}