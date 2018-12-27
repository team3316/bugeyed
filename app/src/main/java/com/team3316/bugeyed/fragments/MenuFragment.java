package com.team3316.bugeyed.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.team3316.bugeyed.DBugNativeBridge;
import com.team3316.bugeyed.MainActivity;
import com.team3316.bugeyed.PreviewType;
import com.team3316.bugeyed.R;

public class MenuFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button calibrateButton = view.findViewById(R.id.calibrateButton);
        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.setFragment(new CalibrateFragment());
            }
        });

        Button matchVisionButton = view.findViewById(R.id.matchVisionButton);
        matchVisionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.setFragment(new MatchVisionFragment());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) this.getActivity()).setPreviewType(PreviewType.CAMERA);
        DBugNativeBridge.setPreviewType(PreviewType.CAMERA);
    }
}
