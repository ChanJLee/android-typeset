package me.chan.androidtex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.chan.texas.renderer.TexasView;

public class ViewRecorder {
    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1280;
    private static final int VIDEO_BITRATE = 6000000;
    private static final int VIDEO_FRAME_RATE = 30;
    private static final int VIDEO_IFRAME_INTERVAL = 5;
    private static final int REQUEST_CODE_SCREEN_CAPTURE = 111;

    private Activity context;
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private MediaCodec mediaCodec;
    private MediaMuxer mediaMuxer;
    private Surface surface;
    private String videoFilePath;

    public ViewRecorder(Activity context) {
        this.context = context;
        mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public void startRecording(String file) {
        try {
            // 创建视频文件
            videoFilePath = file;
            mediaMuxer = new MediaMuxer(videoFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            // 获取屏幕尺寸
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            int screenHeight = metrics.heightPixels;

            // 创建MediaCodec编码器
            MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_MPEG4, screenWidth, screenHeight);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_BIT_RATE, VIDEO_BITRATE);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, VIDEO_FRAME_RATE);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, VIDEO_IFRAME_INTERVAL);
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_MPEG4);
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            surface = mediaCodec.createInputSurface();
            mediaCodec.start();

            // 创建MediaProjection
            Intent permissionIntent = mediaProjectionManager.createScreenCaptureIntent();
            context.startActivityForResult(permissionIntent, REQUEST_CODE_SCREEN_CAPTURE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SCREEN_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
                startVirtualDisplay();
                return true;
            } else {
                Toast.makeText(context, "录制权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }

        return false;
    }

    private void startVirtualDisplay() {
        virtualDisplay = mediaProjection.createVirtualDisplay("ScreenCapture",
                DISPLAY_WIDTH, DISPLAY_HEIGHT, context.getResources().getDisplayMetrics().densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, surface, null, null);
        mediaCodec.setCallback(new MediaCodec.Callback() {
            @Override
            public void onInputBufferAvailable(MediaCodec codec, int index) {
            }

            @Override
            public void onOutputBufferAvailable(MediaCodec codec, int index, MediaCodec.BufferInfo info) {
                ByteBuffer outputBuffer = codec.getOutputBuffer(index);
                mediaMuxer.writeSampleData(0, outputBuffer, info);
                codec.releaseOutputBuffer(index, false);
            }

            @Override
            public void onError(MediaCodec codec, MediaCodec.CodecException e) {
            }

            @Override
            public void onOutputFormatChanged(MediaCodec codec, MediaFormat format) {
            }
        });
    }

    public void stopRecording() {
        if (mediaProjection != null) {
            mediaProjection.stop();
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
        }
        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec.release();
        }
        if (mediaMuxer != null) {
            mediaMuxer.stop();
            mediaMuxer.release();
        }
        if (surface != null) {
            surface.release();
        }
    }

    public void take(TexasView texasView) {
        Canvas canvas = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ?
                surface.lockHardwareCanvas() : surface.lockCanvas(null);
        texasView.draw(canvas);
        surface.unlockCanvasAndPost(canvas);
    }
}