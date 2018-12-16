package com.team3316.bugeyed;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.team3316.bugeyed.fragments.MenuFragment;
import com.team3316.bugeyed.opengl.DBugGLSurfaceView;

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
                            _view = new DBugGLSurfaceView(ctx);
                            setContentView(_view);
                        } else {
                            setContentView(R.layout.activity_no_perm);
                        }
                    }
                });

//        this.setFragment(new MenuFragment());
    }

    @Override
    protected void onPause() {
        this._view.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
