package com.team3316.bugeyed.opengl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.team3316.bugeyed.DBugNativeBridge;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class DBugGLRendererBase implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    protected final String TAG = "DBugGLRendererBase";
    protected int frameCounter;
    protected long lastNanoTime;

    protected SurfaceTexture _surfaceTexture;
    protected DBugGLSurfaceView _view;
    protected TextView _fpsText;

    protected boolean _glInit = false;
    protected boolean _texUpdate = false;

    public DBugGLRendererBase(DBugGLSurfaceView view) {
        this._view = view;
    }

    protected abstract void openCamera();
    protected abstract void closeCamera();
    protected abstract void setCameraPreviewSize(int width, int height);

    public void setFpsTextView(TextView fpsTextView) {
        this._fpsText = fpsTextView;
    }

    public void onResume() {
        Log.i(TAG, "onResume");

        this.frameCounter = 0;
        this.lastNanoTime = System.nanoTime();
    }

    public void onPause() {
        Log.i(TAG, "onPause");

        this._glInit = false;
        this._texUpdate = false;
        this.closeCamera();

        if (this._surfaceTexture != null) {
            this._surfaceTexture.release();
            this._surfaceTexture = null;
            DBugNativeBridge.closeGL();
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this._texUpdate = true;
        this._view.requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");

        String strGLVersion = GLES20.glGetString(GLES20.GL_VERSION);
        if (strGLVersion != null)
            Log.i(TAG, "OpenGL ES version: " + strGLVersion);

        int hTex = DBugNativeBridge.initGL();
        this._surfaceTexture = new SurfaceTexture(hTex);
        this._surfaceTexture.setOnFrameAvailableListener(this);

        this.openCamera();
        this._glInit = true;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged(" + width + "x" + height);
        DBugNativeBridge.changeSize(width, height);
        this.setCameraPreviewSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (!this._glInit) return;

        synchronized (this) {
            if (this._texUpdate) {
                this._surfaceTexture.updateTexImage();
                this._texUpdate = false;
            }
        }
        DBugNativeBridge.drawFrame();

        // Log FPS
        this.frameCounter++;
        if (this.frameCounter >= 10) {
            final int fps = (int) (frameCounter * 1e9 / (System.nanoTime() - lastNanoTime));
            Log.i(TAG, "drawFrame() FPS: " + fps);

            if (this._fpsText != null) {
                Runnable fpsUpdater = new Runnable() {
                    @Override
                    public void run() {
                        _fpsText.setText("FPS: " + fps);
                    }
                };
                new Handler(Looper.getMainLooper()).post(fpsUpdater);
            }

            this.frameCounter = 0;
            this.lastNanoTime = System.nanoTime();
        }
    }
}
