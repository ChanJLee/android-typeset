package me.chan.te.renderer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.chan.te.view.TexTextView;

class TexViewHolder extends RecyclerView.ViewHolder {

	TexTextView texTextView;

	TexViewHolder(@NonNull View itemView) {
		super(itemView);
		texTextView = (TexTextView) itemView;
	}
}