package edu.kit.runningtracker.view;

import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by joshr on 26.12.2017.
 */

public class DecoratedSeekBar {
    private SeekBar mSeekBar;

    public DecoratedSeekBar(SeekBar seekBar, final TextView valueView) {
        mSeekBar = seekBar;
        valueView.setText(String.valueOf(seekBar.getProgress()));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valueView.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    public double getProgress() {
        return mSeekBar.getProgress();
    }
}
