package edu.kit.runningtracker.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import edu.kit.runningtracker.R;
import edu.kit.runningtracker.view.DecoratedSeekBar;

/**
 * Created by joshr on 19.12.2017.
 */

public class SettingsFragment extends Fragment {
    private DecoratedSeekBar mDesiredSpeed;
    private DecoratedSeekBar mTolerance;
    private DecoratedSeekBar mSpeed;
    private CheckBox mLocal;
    private CheckBox mUseLocation;
    private Button mSaveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_layout, container, false);

        SeekBar desiredSpeedBar = view.findViewById(R.id.desired_speed_bar);
        final TextView desiredSpeedValue = view.findViewById(R.id.desired_speed_value);
        mDesiredSpeed = new DecoratedSeekBar(desiredSpeedBar, desiredSpeedValue);

        SeekBar toleranceBar = view.findViewById(R.id.tolerance_bar);
        final TextView toleranceValue = view.findViewById(R.id.tolerance_value);
        mTolerance = new DecoratedSeekBar(toleranceBar, toleranceValue);

        SeekBar speedBar = view.findViewById(R.id.speed_bar);
        final TextView speedValue = view.findViewById(R.id.speed_value);
        mSpeed = new DecoratedSeekBar(speedBar, speedValue);

        mLocal = view.findViewById(R.id.local_checkbox);
        mUseLocation = view.findViewById(R.id.uselocation_checkbox);
        mUseLocation.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mSpeed.setEnabled(!b);
                    }
                }
        );

        mSaveButton = view.findViewById(R.id.button_save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppSettings settings = AppSettings.getInstance();
                
                settings.setDesiredSpeed(mDesiredSpeed.getProgress());
                settings.setTolerance(mTolerance.getProgress());
                settings.setLocal(mLocal.isChecked());
                settings.setSpeed(mSpeed.getProgress());
                settings.setUseLocation(mUseLocation.isChecked());
            }
        });

        return view;
    }
}
