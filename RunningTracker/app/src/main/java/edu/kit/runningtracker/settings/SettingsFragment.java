package edu.kit.runningtracker.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
    private CheckBox mLocal;
    private AppSettings mAppSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAppSettings = AppSettings.getInstance();
    }

    @Override
    public void onPause() {
        super.onPause();

        mAppSettings.setDesiredSpeed(mDesiredSpeed.getProgress());
        mAppSettings.setTolerance(mTolerance.getProgress());
        mAppSettings.setLocal(mLocal.isActivated());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_layout, container, false);

        SeekBar desiredSpeedBar = view.findViewById(R.id.desired_speed_bar);
        final TextView desiredSpeedValue = view.findViewById(R.id.desired_speed_value);
        mDesiredSpeed = new DecoratedSeekBar(desiredSpeedBar, desiredSpeedValue);

        SeekBar toleranceBar = view.findViewById(R.id.tolerance_bar);
        final TextView toleranceValue = view.findViewById(R.id.tolerance_value);
        mTolerance = new DecoratedSeekBar(toleranceBar, toleranceValue);

        mLocal = view.findViewById(R.id.local_checkbox);

        return view;
    }
}
