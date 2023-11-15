package me.chan.texas.renderer.ui.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class LoadingIndicatorView extends View implements LoadingIndicator {

    public LoadingIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void renderStart() {

    }

    @Override
    public void renderError() {

    }

    @Override
    public void renderSuccess() {

    }

    @Override
    public void dismiss() {
        setVisibility(GONE);
    }
}
