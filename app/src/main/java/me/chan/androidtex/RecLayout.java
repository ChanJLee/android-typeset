package me.chan.androidtex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Surface;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

public class RecLayout extends FrameLayout {

    private final ViewVideoRecorder mViewVideoRecorder;

    public RecLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mViewVideoRecorder = new ViewVideoRecorder(this);
    }

    public void start(File file) {
        mViewVideoRecorder.start(file);
    }

    public void stop() {
        mViewVideoRecorder.stop();
    }

    public void take() {
        Surface surface = mViewVideoRecorder.getEncodeSurface();
        if (surface == null) {
            return;
        }

        Canvas canvas = surface.lockCanvas(null);
        canvas.drawColor(Color.TRANSPARENT);
        getChildAt(0).draw(canvas);
        surface.unlockCanvasAndPost(canvas);
    }
}
