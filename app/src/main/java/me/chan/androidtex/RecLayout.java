package me.chan.androidtex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.Surface;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

public class RecLayout extends FrameLayout implements Runnable {

    private final ViewVideoRecorder mViewVideoRecorder;

    public RecLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mViewVideoRecorder = new ViewVideoRecorder(this);
    }

    public void start(File file) {
        mViewVideoRecorder.start(file);
        getChildAt(0).getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                Surface surface = mViewVideoRecorder.getEncodeSurface();
                if (surface == null) {
                    return;
                }

                Canvas canvas = surface.lockCanvas(null);
                draw(canvas);
                surface.unlockCanvasAndPost(canvas);
            }
        });
    }

    public void stop() {
        mViewVideoRecorder.stop();
    }

    public void take() {
    }

    @Override
    public void run() {

    }
}
