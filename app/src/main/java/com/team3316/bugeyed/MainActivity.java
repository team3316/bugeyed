package com.team3316.bugeyed;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.hardware.camera2.CaptureRequest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.team3316.bugeyed.fragments.MenuFragment;

import net.ralphpina.permissionsmanager.PermissionsManager;
import net.ralphpina.permissionsmanager.PermissionsResult;

import org.opencv.android.BetterCamera2Renderer;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BUGEYED";

    private DBugGLSurfaceView _view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup fullscreen layout
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Permission handling
        final AppCompatActivity ctx = this;
        PermissionsManager.get()
                .requestCameraPermission()
                .subscribe(new Action1<PermissionsResult>() {
                    @Override
                    public void call(PermissionsResult permissionsResult) {
                        if (permissionsResult.isGranted()) {
                            BetterCamera2Renderer.Settings settings = new BetterCamera2Renderer.Settings();
                            settings.add(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_OFF);

                            setContentView(R.layout.activity_main);

                            _view = findViewById(R.id.cameraView);
                            _view.setCameraTextureListener(_view);

                            setFragment(new MenuFragment());
                        } else {
                            setContentView(R.layout.activity_no_perm);
                        }
                    }
                });

    }

    @Override
    protected void onPause() {
        if (this._view != null)
            this._view.onResume();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this._view != null)
            this._view.onResume();
    }

    public void setFragment (Fragment fragment) {
        FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, fragment);
        transaction.commit();

        if (!(fragment instanceof MenuFragment)) // Main menu shouldn't disappear on back
            transaction.addToBackStack(null);
    }
}
