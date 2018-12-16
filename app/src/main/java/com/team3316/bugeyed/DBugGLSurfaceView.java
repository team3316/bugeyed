package com.team3316.bugeyed;

import android.content.Context;
import android.hardware.camera2.CaptureRequest;
import android.util.AttributeSet;

import org.opencv.android.BetterCamera2Renderer;
import org.opencv.android.BetterCameraGLSurfaceView;

public class DBugGLSurfaceView extends BetterCameraGLSurfaceView implements BetterCameraGLSurfaceView.CameraTextureListener {
    private static BetterCamera2Renderer.Settings getSettings() {
        BetterCamera2Renderer.Settings settings = new BetterCamera2Renderer.Settings();

        settings.width = 640;
        settings.height = 480;

        settings.add(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_OFF);
        settings.add(CaptureRequest.LENS_FOCUS_DISTANCE, .2f);
        settings.add(CaptureRequest.SENSOR_EXPOSURE_TIME, 1000000L);

        return settings;
    }

    public DBugGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs, getSettings());
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public boolean onCameraTexture(int texIn, int texOut, int width, int height, long system_time_millis) {
        return false;
    }
}
