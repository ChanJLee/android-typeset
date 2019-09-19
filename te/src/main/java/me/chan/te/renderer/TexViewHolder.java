package me.chan.te.renderer;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.chan.te.data.Paragraph;
import me.chan.te.view.TexTextView;

class TexViewHolder extends RecyclerView.ViewHolder {

	private TexTextView mTexTextView;

	public TexViewHolder(@NonNull View itemView) {
		super(itemView);
		mTexTextView = (TexTextView) itemView;
	}

	public void render(Paragraph paragraph, Paint paint) {
		mTexTextView.render(paragraph, paint);
	}
}