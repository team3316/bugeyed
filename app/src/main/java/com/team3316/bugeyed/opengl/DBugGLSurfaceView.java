package com.team3316.bugeyed.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Parcelable;
import android.view.SurfaceHolder;
import android.widget.TextView;

public class DBugGLSurfaceView extends GLSurfaceView {
    DBugGLRendererBase _renderer;

    public DBugGLSurfaceView(Context context) {
        super(context);

        this._renderer = new DBugGLRenderer(this);
        this.setEGLContextClientVersion(2);
        this.setRenderer(this._renderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setFpsTextView (TextView textView) {
        this._renderer.setFpsTextView(textView);
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
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);
    }

    @Override
    public void onResume() {
        super.onResume();
        this._renderer.onResume();
    }

    @Override
    public void onPause() {
        this._renderer.onPause();
        super.onPause();
    }
}
