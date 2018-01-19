package edu.kit.runningtracker.view;

import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by joshr on 26.12.2017.
 */

public class DecoratedSeekBar {
    private SeekBar mSeekBar;

    public DecoratedSeekBar(SeekBar seekBar, final TextView valueView, int initial) {
        mSeekBar = seekBar;
        mSeekBar.setProgress(initial);
        valueView.setText(String.valueOf(initial));

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

    public int getProgress() {
        return mSeekBar.getProgress();
    }

    public void setEnabled(boolean enabled) {
        mSeekBar.setEnabled(enabled);
    }
}
