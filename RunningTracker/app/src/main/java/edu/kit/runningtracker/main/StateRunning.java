package edu.kit.runningtracker.main;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.util.Log;

import edu.kit.runningtracker.ble.BluetoothLeService;
import edu.kit.runningtracker.ble.SensorCharacteristicAdapter;
import edu.kit.runningtracker.sensor.VelocityService;
import edu.kit.runningtracker.settings.AppSettings;

/**
 * @author Josh Romanowski
 */

public class StateRunning extends AState {
    private static final String TAG = StateRunning.class.getSimpleName();

    private BluetoothLeService mBleService;
    private AppSettings mAppSettings;
    private boolean mConnected;
    private VelocityService mVelocityService;
    private SensorCharacteristicAdapter mAdapter;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBleService = new BluetoothLeService(getActivity());

        mAppSettings = AppSettings.getInstance();
        mVelocityService = new VelocityService(mVelocityHandler);
        mAdapter = new SensorCharacteristicAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBleService != null) {
            final boolean result = mBleService.connect();
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBleService = null;
    }
}
