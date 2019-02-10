package com.team3316.bugeyed;

import android.content.Context;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.TextView;

import org.opencv.android.BetterCamera2Renderer;
import org.opencv.android.BetterCameraGLSurfaceView;


public class DBugGLSurfaceView extends BetterCameraGLSurfaceView implements BetterCameraGLSurfaceView.CameraTextureListener {
    private int _frameCounter = 0;
    private long _lastNanoTime;
    private TextView _fpsTextView, _distTextView;

    public static boolean shouldInit = false;

    private static BetterCamera2Renderer.Settings getSettings() {
        BetterCamera2Renderer.Settings settings = new BetterCamera2Renderer.Settings();

        settings.width = 640;
        settings.height = 360;

        settings.add(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_OFF);
        settings.add(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_OFF);
        settings.add(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_OFF);
        settings.add(CaptureRequest.LENS_FOCUS_DISTANCE, 5.0f);
        settings.add(CaptureRequest.SENSOR_EXPOSURE_TIME, 1000 * 1000L);

        return settings;
    }

    public DBugGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs, getSettings());
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        this._frameCounter = 0;
        this._lastNanoTime = System.nanoTime();
    }

    @Override
    public void onCameraViewStopped() {
        // Nothing here
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
    }

    @Override
    public boolean onCameraTexture(int texIn, int texOut, int width, int height, long system_time_millis) {
        // FPS Counter
        this._frameCounter++;
        if (this._frameCounter >= 30) {
            final int fps = (int) (this._frameCounter * 1e9 / (System.nanoTime() - this._lastNanoTime));
            if (this._fpsTextView != null) {
                Runnable fpsUpdater = new Runnable() {
                    @Override
                    public void run() {
                        _fpsTextView.setText(fps + "FPS");
                    }
                };
                new Handler(Looper.getMainLooper()).post(fpsUpdater);
            } else {
                this._fpsTextView = DBugUtils.unwrap(this.getContext()).findViewById(R.id.fpsTextView);
            }
            this._frameCounter = 0;
            this._lastNanoTime = System.nanoTime();
        }

        int hMin = DBugPreferences.getInstance().get("h-min-value", 0, shouldInit),
            hMax = DBugPreferences.getInstance().get("h-max-value", 255, shouldInit),
            sMin = DBugPreferences.getInstance().get("s-min-value", 0, shouldInit),
            sMax = DBugPreferences.getInstance().get("s-max-value", 255, shouldInit),
            vMin = DBugPreferences.getInstance().get("v-min-value", 0, shouldInit),
            vMax = DBugPreferences.getInstance().get("v-max-value", 255, shouldInit);

        DBugNativeBridge.processFrame(texOut, width, height, hMin, hMax, sMin, sMax, vMin, vMax);

        if (this._distTextView != null) {
            Runnable distUpdater = new Runnable() {
                @Override
                public void run() {
                    _distTextView.setText("Dist: " + DBugNativeBridge.getDistanceToTarget());
                }
            };
            new Handler(Looper.getMainLooper()).post(distUpdater);
        } else {
            this._distTextView = DBugUtils.unwrap(this.getContext()).findViewById(R.id.distanceTextView);
        }
        return true;
    }
}
