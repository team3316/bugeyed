package com.team3316.bugeyed;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.hardware.camera2.CaptureRequest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;

import com.team3316.bugeyed.fragments.CalibrateFragment;
import com.team3316.bugeyed.fragments.MenuFragment;

import net.ralphpina.permissionsmanager.PermissionsManager;
import net.ralphpina.permissionsmanager.PermissionsResult;

import org.opencv.android.BetterCamera2Renderer;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "BUGEYED";

    private DBugGLSurfaceView _view;
    private PreviewType _currentPreviewType = PreviewType.CAMERA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup fullscreen layout
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Permission handling
        final MainActivity ctx = this;
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

                            AppCompatCheckBox contoursCB = findViewById(R.id.contoursCheckbox);
                            contoursCB.setOnClickListener(ctx);

                            AppCompatCheckBox networkCB = findViewById(R.id.networkCheckbox);
                            networkCB.setOnClickListener(ctx);

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
            this._view.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this._view != null)
            this._view.onResume();
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, fragment);
        transaction.commit();

        if (fragment instanceof CalibrateFragment) // Only the calibrate menu should go back to the main menu
            transaction.addToBackStack(null);
    }

    public void setPreviewType (PreviewType p) {
        this._currentPreviewType = p;
    }

    @Override
    public void onClick(View v) {
        // TODO - Add a preview type selection

        if (v.getId() == R.id.contoursCheckbox) {
            boolean checked = ((AppCompatCheckBox) v).isChecked();
            PreviewType p = this._currentPreviewType.contoursFlag(checked);
            DBugNativeBridge.setPreviewType(p);
        }

        if (v.getId() == R.id.networkCheckbox) {
            boolean checked = ((AppCompatCheckBox) v).isChecked();
            DBugNativeBridge.setNetworkEnable(checked);
        }
    }
}
