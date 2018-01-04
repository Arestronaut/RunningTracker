package edu.kit.runningtracker.run;


import android.Manifest;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
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
import edu.kit.runningtracker.data.LocationRepository;
import edu.kit.runningtracker.gps.LocationService;
import edu.kit.runningtracker.settings.AppSettings;
import edu.kit.runningtracker.settings.Constants;
import edu.kit.runningtracker.settings.DeviceSettings;

import static android.app.Activity.RESULT_OK;
import static edu.kit.runningtracker.ble.BluetoothConnectionActivity.REQUEST_SCAN_BLE;

/**
 * @author Josh Romanowski
 */

public class RunFragment extends Fragment implements OnRequestPermissionsResultCallback {
    private static final String TAG = RunFragment.class.getSimpleName();

    private static final int REQUEST_GPS_PERMISSIONS = 16;

    // State
    private IState mCurrentState;

    // View
    private Button mStartButton;
    private Button mPauseButton;
    private Button mStopButton;

    // Sensors and actors
    private BluetoothLeService mBleService;
    private AppSettings mAppSettings;
    private DeviceSettings mDeviceSettings;
    private LocationService mLocationService;
    private LocationRepository mLocationRepository;
    private SensorCharacteristicAdapter mAdapter;

    private boolean mBleSetup;
    private boolean mGpsSetup;

    private Context mContext;

    public RunFragment() {
        mCurrentState = new StateIdle();
        mAppSettings = AppSettings.getInstance();
        mDeviceSettings = DeviceSettings.getInstance();

        mAdapter = new SensorCharacteristicAdapter();
        mBleSetup = false;
        mLocationRepository = new LocationRepository();

        mPauseButton.setEnabled(false);
        mStopButton.setEnabled(false);
    }

    // We need to wait for the context to get valid.

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBleService = new BluetoothLeService(context);
        mLocationService = new LocationService(mLocationHandler, context);
        mContext = context;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCAN_BLE) {
            if (resultCode == RESULT_OK && data.getExtras() != null) {
                Bundle extras = data.getExtras();
                mDeviceSettings.setDeviceAddress((String) extras.get(BluetoothConnectionActivity.EXTRA_DEVICE_ADDR));
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
                mPauseButton.setEnabled(false);
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

    private LocationService.ILocationHandler mLocationHandler = new LocationService.ILocationHandler() {
        @Override
        public void onLocationChanged(Location location) {
            mLocationRepository.put(location);

            double speedInMps = location.getSpeed() * Constants.MPH_IN_METERS_PER_SECOND;
            if (!mAppSettings.isLocal()) {
                if (mBleService == null
                        || mBleService.getConnectionState() != BluetoothLeService.STATE_CONNECTED) {
                    Log.w(TAG, "Service not connected");
                }

                BluetoothGattCharacteristic characteristic = mAdapter.createCharacteristic(speedInMps);
                mBleService.writeCharacteristic(characteristic);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == REQUEST_GPS_PERMISSIONS)
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mGpsSetup = true;
            }
        }

    protected void setupServices() {
        if (!mAppSettings.isLocal() && !mBleSetup) {
            Intent startBleIntent = new Intent(getContext(), BluetoothConnectionActivity.class);
            startActivityForResult(startBleIntent, REQUEST_SCAN_BLE);
        }

        if (!mGpsSetup) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_GPS_PERMISSIONS);
            }
        }
    }

    protected void startServics() {
        if (!mAppSettings.isLocal() && mBleSetup) {
            mBleService.connect(mDeviceSettings.getDeviceAddress());
        }
    }

    protected void pauseServices() {
        mBleService.disconnect();
    }

    protected void resetServices() {
        mLocationRepository.clear();
    }
}
