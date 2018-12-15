package com.team3316.bugeyed;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.team3316.bugeyed.fragments.MenuFragment;

import net.ralphpina.permissionsmanager.PermissionsManager;
import net.ralphpina.permissionsmanager.PermissionsResult;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.Objects;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity
    implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "BUGEYED";

    private JavaCamera2View _cameraView;

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

    static {
        if (BuildConfig.DEBUG) {
        }
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                            _cameraView = findViewById(R.id.camera_view);
                            _cameraView.setVisibility(SurfaceView.VISIBLE);
                            _cameraView.setCvCameraViewListener(_this);
                            _cameraView.setFocusableInTouchMode(false);
                            _cameraView.setFocusable(false);
                            _cameraView.enableView();
                        }
                    }
                });

        this.setFragment(new MenuFragment());
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

    public void setFragment (Fragment fragment) {
        FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, fragment);
        transaction.commit();

        if (!(fragment instanceof MenuFragment)) // Main menu shouldn't disappear on back
            transaction.addToBackStack(null);
    }
}
