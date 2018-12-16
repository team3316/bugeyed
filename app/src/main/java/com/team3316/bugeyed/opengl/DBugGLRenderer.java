package com.team3316.bugeyed.opengl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class DBugGLRenderer extends DBugGLRendererBase {
    protected final String TAG = "DBugGLRenderer";
    private CameraDevice _cameraDevice;
    private CameraCaptureSession _captureSession;
    private CaptureRequest.Builder _previewRequestBuilder;
    private String _cameraId;
    private Size _previewSize = new Size(640, 320);

    private HandlerThread _backgroundThread;
    private Handler _backgroundHandler;
    private Semaphore _cameraOpenCloseLock = new Semaphore(1);

    private final CameraDevice.StateCallback _stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            _cameraDevice = camera;
            _cameraOpenCloseLock.release();
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            _cameraDevice = null;
            _cameraOpenCloseLock.release();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            _cameraDevice = null;
            _cameraOpenCloseLock.release();
        }
    };

    public DBugGLRenderer(DBugGLSurfaceView view) {
        super(view);
    }

    @Override
    public void onResume() {
        this.stopBackgroundThread();
        super.onResume();
        this.startBackgroundThread();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.stopBackgroundThread();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void openCamera() {
        Log.i(TAG, "openCamera");

        CameraManager manager = (CameraManager) this._view.getContext().getSystemService(Context.CAMERA_SERVICE);

        try {
            for (String cameraId: manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    this._cameraId = cameraId;
                    break;
                }
            }

            if (!this._cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Timeout waiting to lock camera opening.");
            }
            manager.openCamera(this._cameraId, this._stateCallback, this._backgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "openCamera - Camera Access Exception", e);
        } catch (InterruptedException e) {
            Log.e(TAG, "openCamera - Interrupted Exception", e);
        }
    }

    @Override
    protected void closeCamera() {
        Log.i(TAG, "closeCamera");

        try {
            _cameraOpenCloseLock.acquire();

            if (null != this._captureSession) {
                this._captureSession.close();
                this._captureSession = null;
            }

            if (null != this._cameraDevice) {
                this._cameraDevice.close();
                this._cameraDevice = null;
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "openCamera - Interrupted Exception", e);
        } finally {
            this._cameraOpenCloseLock.release();
        }
    }

    @Override
    protected void setCameraPreviewSize(int width, int height) {
        Log.i(TAG, "setCameraPreviewSize(" + width + "x" + height + ")");

        try {
            this._cameraOpenCloseLock.acquire();

            if (null != this._captureSession) {
                Log.d(TAG, "Closing existing previewSession");
                this._captureSession.close();
                this._captureSession = null;
            }

            this._cameraOpenCloseLock.release();
            this.createCameraPreviewSession();
        } catch (InterruptedException e) {
            this._cameraOpenCloseLock.release();
            Log.e(TAG, "openCamera - Interrupted Exception", e);
        }
    }

    private void startBackgroundThread() {
        Log.i(TAG, "startBackgroundThread");

        this._backgroundThread = new HandlerThread("CameraBackground");
        this._backgroundThread.start();
        this._backgroundHandler = new Handler(this._backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        Log.i(TAG, "stopBackgroundThread");

        if (this._backgroundThread == null) return;
        this._backgroundThread.quitSafely();

        try {
            this._backgroundThread.join();
            this._backgroundThread = null;
            this._backgroundHandler = null;
        } catch (InterruptedException e) {
            Log.e(TAG, "stopBackgroundThread", e);
        }
    }

    private void createCameraPreviewSession() {
        Log.i(TAG, "createCameraPreviewSession");

        try {
            this._cameraOpenCloseLock.acquire();

            if (null == this._cameraDevice) {
                this._cameraOpenCloseLock.release();
                Log.e(TAG, "createCameraPreviewSession - camera isn't opened");
                return;
            }


            if (null != this._captureSession) {
                this._cameraOpenCloseLock.release();
                Log.e(TAG, "createCameraPreviewSession - capture session has already started");
                return;
            }

            if (null == this._surfaceTexture) {
                this._cameraOpenCloseLock.release();
                Log.e(TAG, "createCameraPreviewSession - preview SurfaceTexture is null");
                return;
            }

            Log.d(TAG, "starting preview of size " + this._previewSize.getWidth() + "x" + this._previewSize.getHeight());
            this._surfaceTexture.setDefaultBufferSize(this._previewSize.getWidth(), this._previewSize.getHeight());

            Surface surface = new Surface(this._surfaceTexture);

            this._previewRequestBuilder = this._cameraDevice
                    .createCaptureRequest(CameraDevice.TEMPLATE_MANUAL);
            this._previewRequestBuilder.addTarget(surface);

            this._cameraDevice.createCaptureSession(Arrays.asList(surface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            _captureSession = session;
                            try {
                                _previewRequestBuilder
                                        .set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_OFF);

                                _previewRequestBuilder
                                        .set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
                                _previewRequestBuilder
                                        .set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);

                                _captureSession.setRepeatingRequest(
                                        _previewRequestBuilder.build(), null,
                                        _backgroundHandler
                                );

                                Log.i(TAG, "Camera preview session has been started");
                            } catch (CameraAccessException e) {
                                Log.e(TAG, "createCameraPreviewSession - Camera Access Exception", e);
                            }
                            _cameraOpenCloseLock.release();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Log.e(TAG, "createCameraPreviewSession failed");
                            _cameraOpenCloseLock.release();
                        }
                    }, this._backgroundHandler);
        } catch (InterruptedException e) {
            Log.e(TAG, "createCameraPreviewSession - Interrupted Exception", e);
        } catch (CameraAccessException e) {
            Log.e(TAG, "createCameraPreviewSession - Camera Access Exception", e);
        }
    }
}
