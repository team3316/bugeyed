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

import com.team3316.bugeyed.DBugNativeBridge;
import com.team3316.bugeyed.MainActivity;
import com.team3316.bugeyed.PreviewType;
import com.team3316.bugeyed.R;

public class MatchVisionFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_matchvision, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button connectedButton = view.findViewById(R.id.connectedButton);
        int notConnectedColor = ContextCompat.getColor(this.getContext(), R.color.not_connected);
        connectedButton.getBackground().setColorFilter(notConnectedColor, PorterDuff.Mode.DARKEN);
        connectedButton.setText(R.string.not_connected);

        connectedButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(getClass().getSimpleName(), "onlongclick");
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) this.getActivity()).setPreviewType(PreviewType.MATCH);
        DBugNativeBridge.setPreviewType(PreviewType.MATCH);
    }
}
