package com.team3316.bugeyed;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import net.ralphpina.permissionsmanager.PermissionsManager;
import net.ralphpina.permissionsmanager.PermissionsResult;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.Objects;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity
    implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "BUGEYED";

    private JavaCameraView _cameraView;

    private BaseLoaderCallback _loaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(MainActivity.TAG, "OpenCV Connected!");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.loadLibrary("opencv_java3");

        // Setup fullscreen layout
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_main);

        // Permission handling
        final CameraBridgeViewBase.CvCameraViewListener2 _this = this; // yuk, scoping
        PermissionsManager.get()
                .requestCameraPermission()
                .subscribe(new Action1<PermissionsResult>() {
                    @Override
                    public void call(PermissionsResult permissionsResult) {
                        if (permissionsResult.isGranted()) {
                            _cameraView = (JavaCameraView) findViewById(R.id.camera_view);
                            _cameraView.setVisibility(SurfaceView.VISIBLE);
                            _cameraView.setCvCameraViewListener(_this);
                            _cameraView.enableView();
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this._cameraView != null)
            this._cameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, this._loaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            this._loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }
}
