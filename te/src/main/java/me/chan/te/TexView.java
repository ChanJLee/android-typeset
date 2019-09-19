package me.chan.te;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import me.chan.te.renderer.Adapter;

public class TexView extends FrameLayout {

	private Adapter mAdapter;
	private CharSequence mText;
	private Runnable mCmdRunnable = new Runnable() {
		@Override
		public void run() {
			if (mAdapter != null) {
				mAdapter.setText(mText, getWidth());
			}
		}
	};

	public TexView(Context context) {
		this(context, null);
	}

	public TexView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TexView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		RecyclerView recyclerView = new RecyclerView(context);
		recyclerView.setPadding(0, 20, 0, 20);
		recyclerView.setClipToPadding(false);
		recyclerView.setClipChildren(false);
		addView(recyclerView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		mAdapter = new Adapter(context);
		recyclerView.setAdapter(mAdapter);
	}

	public void setText(CharSequence charSequence) {
		mText = charSequence;
		post(mCmdRunnable);
	}

	public CharSequence getText() {
		return mText;
	}

}
