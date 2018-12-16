package com.team3316.bugeyed.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.team3316.bugeyed.DBugPrefrences;
import com.team3316.bugeyed.R;

import java.util.HashMap;
import java.util.Map;

public class CalibrateFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private Map<Integer, String> _sliders = new HashMap<Integer, String>(){{
        put(R.id.hMinSlide, "h-min");
        put(R.id.hMaxSlide, "h-max");
        put(R.id.sMinSlide, "s-min");
        put(R.id.sMaxSlide, "s-max");
        put(R.id.vMinSlide, "v-min");
        put(R.id.vMaxSlide, "v-max");
    }};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calibrate, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Set the slider listeners to this context
        for (Map.Entry<Integer, String> entry: this._sliders.entrySet()) {
            SeekBar bar = view.findViewById(entry.getKey());
            bar.setOnSeekBarChangeListener(this);
        }

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String name = this._sliders.get(seekBar.getId());
        DBugPrefrences.getInstance().set(name + "-value", progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        this.getFragmentManager().popBackStack();
    }
}
