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

import com.team3316.bugeyed.DBugNativeBridge;
import com.team3316.bugeyed.DBugPreferences;
import com.team3316.bugeyed.MainActivity;
import com.team3316.bugeyed.PreviewType;
import com.team3316.bugeyed.R;

import java.util.HashMap;
import java.util.Map;

public class CalibrateFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private Map<Integer, String> _sliders = new HashMap<Integer, String>() {{
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
        for (Map.Entry<Integer, String> entry : this._sliders.entrySet()) {
            SeekBar bar = view.findViewById(entry.getKey());
            bar.setOnSeekBarChangeListener(this);

            // REMARK - n is the last character in the word min
            int defaultVal = entry.getValue().charAt(4) == 'n' ? 0 : 255;
            int value = DBugPreferences
                .getInstance()
                .get(entry.getValue() + "-value", defaultVal, true);
            Log.d("CalibrateFragment", "Initial value: " + value);
            bar.setProgress(value);
        }

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        Button doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) this.getActivity()).setPreviewType(PreviewType.THRESHOLDED);
        DBugNativeBridge.setPreviewType(PreviewType.THRESHOLDED);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String name = this._sliders.get(seekBar.getId());
        DBugPreferences.getInstance().set(name + "-value", progress, false);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.doneButton) {
            for (Map.Entry<Integer, String> entry : this._sliders.entrySet()) {
                SeekBar bar = this.getView().findViewById(entry.getKey());
                DBugPreferences
                    .getInstance()
                    .set(entry.getKey() + "-value", bar.getProgress(), true);
            }
        }

        this.getFragmentManager().popBackStack();
    }
}
