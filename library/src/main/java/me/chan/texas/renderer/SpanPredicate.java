package me.chan.texas.renderer;

import androidx.annotation.Nullable;


public interface SpanPredicate {
	
	boolean accept(@Nullable Object thiz, @Nullable Object other);
}
