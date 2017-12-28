package edu.kit.runningtracker.run;


import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.kit.runningtracker.R;
import edu.kit.runningtracker.ble.BluetoothConnectionActivity;
import edu.kit.runningtracker.ble.BluetoothLeService;
import edu.kit.runningtracker.ble.SensorCharacteristicAdapter;
import edu.kit.runningtracker.sensor.VelocityService;
import edu.kit.runningtracker.settings.AppSettings;

import static android.app.Activity.RESULT_OK;
import static edu.kit.runningtracker.ble.BluetoothConnectionActivity.REQUEST_SCAN_BLE;

/**
 * @author Josh Romanowski
 */

public class RunFragment extends Fragment {
    private static final String TAG = RunFragment.class.getSimpleName();

    // State
    private IState mCurrentState;

    // View
    private Button mStartButton;
    private Button mPauseButton;
    private Button mStopButton;

    // Sensors and actors
    private BluetoothLeService mBleService;
    private AppSettings mAppSettings;
    private VelocityService mVelocityService;
    private SensorCharacteristicAdapter mAdapter;

    private boolean mBleSetup;

    public RunFragment() {
        mCurrentState = new StateIdle();
        mAppSettings = AppSettings.getInstance();

        mAdapter = new SensorCharacteristicAdapter();
        mBleSetup = false;

        mVelocityService = new VelocityService(mVelocityHandler);
    }

    // We need to wait for the context to get valid.

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mBleService = new BluetoothLeService(context);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCAN_BLE) {
            if (resultCode == RESULT_OK) {
                mBleSetup = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.run_layout, container, false);

        mStartButton = view.findViewById(R.id.start_button);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(new StateRunning());
                mStartButton.setEnabled(false);
                mPauseButton.setEnabled(true);
                mStopButton.setEnabled(true);
            }
        });

        mPauseButton = view.findViewById(R.id.pause_button);
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(new StatePaused());
                mPauseButton.setEnabled(false);
                mStartButton.setEnabled(true);
                mStopButton.setEnabled(true);
            }
        });

        mStopButton = view.findViewById(R.id.stop_button);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(new StateIdle());
                mStopButton.setEnabled(false);
                mPauseButton.setEnabled(true);
                mStartButton.setEnabled(true);
            }
        });

        return view;
    }

    public void setState(IState newSate) {
        mCurrentState.exit(this);
        mCurrentState = newSate;
        mCurrentState.enter(this);
    }

    private VelocityService.IVelocityHandler mVelocityHandler = new VelocityService.IVelocityHandler() {
        @Override
        public void onVelocityUpdate(double velocity) {
            Log.i(TAG, "Velocity received: " + velocity);
            if (!mAppSettings.isLocal()) {
                if (mBleService == null
                        || mBleService.getConnectionState() != BluetoothLeService.STATE_CONNECTED) {
                    Log.w(TAG, "Service not connected");
                }

                BluetoothGattCharacteristic characteristic = mAdapter.createCharacteristic(velocity);
                mBleService.writeCharacteristic(characteristic);
            }
        }
    };

    protected void setupServices() {
        if (!mBleSetup) {
            Intent startBleIntent = new Intent(getContext(), BluetoothConnectionActivity.class);
            startActivityForResult(startBleIntent, REQUEST_SCAN_BLE);
        }
    }

    protected void pauseServices() {
        mBleService.disconnect();
    }

    protected void startServics() {
        if (!mAppSettings.isLocal() && mBleSetup) {
            mBleService.connect();
        }
    }
}
