package com.team3316.bugeyed.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.team3316.bugeyed.DBugGLSurfaceView;
import com.team3316.bugeyed.DBugNativeBridge;
import com.team3316.bugeyed.DBugRobotConnection;
import com.team3316.bugeyed.MainActivity;
import com.team3316.bugeyed.PreviewType;
import com.team3316.bugeyed.R;

public class MatchVisionFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        DBugGLSurfaceView.shouldInit = true;
        return inflater.inflate(R.layout.fragment_matchvision, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        this.setConnectionState(view, false);
        CheckBox netcb = this.getActivity().findViewById(R.id.networkCheckbox);
        netcb.setChecked(true);
        netcb.setEnabled(false);
        netcb.callOnClick();

        Runnable updateUI = new Runnable() {
            @Override
            public void run() {
                setConnectionState(view, DBugNativeBridge.getConnectionStatus());
            }
        };
        (new Thread(updateUI)).start();
    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) this.getActivity()).setPreviewType(PreviewType.MATCH);
        DBugNativeBridge.setPreviewType(PreviewType.MATCH);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setConnectionState(View view, boolean isConnected) {
        Button connectedButton = view.findViewById(R.id.connectedButton);
        int color = ContextCompat.getColor(this.getContext(), isConnected ? R.color.connected : R.color.not_connected);
        connectedButton.getBackground().setColorFilter(color, PorterDuff.Mode.DARKEN);
        connectedButton.setText(isConnected ? R.string.connected : R.string.not_connected);
    }
}
