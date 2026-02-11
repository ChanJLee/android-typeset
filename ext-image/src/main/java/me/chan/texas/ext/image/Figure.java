package me.chan.texas.ext.image;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.chan.texas.text.ViewSegment;

/**
 * 插图
 */
public final class Figure extends ViewSegment {

	private final ImageLoader.Request mRequest;

	public Figure(@NonNull ImageLoader.Request request) {
		this(request, null);
	}

	public Figure(@NonNull ImageLoader.Request request, @Nullable Object tag) {
		super(new Args(R.layout.me_chan_texas_ext_image).tag(tag));
		mRequest = request;
	}

	@Override
	protected void onRender(View view) {
		mRequest.into((ImageView) view);
	}
}
