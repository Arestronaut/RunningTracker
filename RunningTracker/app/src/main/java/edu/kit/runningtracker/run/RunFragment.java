package edu.kit.runningtracker.run;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.kit.runningtracker.R;
import edu.kit.runningtracker.ble.BluetoothConnectionActivity;
import edu.kit.runningtracker.ble.BluetoothLeService;
import edu.kit.runningtracker.ble.SensorCharacteristicAdapter;
import edu.kit.runningtracker.data.LocationRepository;
import edu.kit.runningtracker.gps.LocationService;
import edu.kit.runningtracker.settings.AppSettings;
import edu.kit.runningtracker.settings.Constants;

import static android.app.Activity.RESULT_OK;
import static edu.kit.runningtracker.ble.BluetoothConnectionActivity.REQUEST_SCAN_BLE;

/**
 * @author Josh Romanowski
 */

public class RunFragment extends Fragment implements OnRequestPermissionsResultCallback {
    private static final String TAG = RunFragment.class.getSimpleName();

    private static final int REQUEST_GPS_PERMISSIONS = 16;

    // View
    private TextView mSpeedTextView;
    private TextView mInformationTextView;
    private Button mStartButton;
    private Button mStopButton;

    // Sensors and actors
    private BluetoothLeService mBleService;
    private LocationService mLocationService;
    private LocationRepository mLocationRepository;

    private boolean mBleSetup;
    private boolean mGpsSetup;

    // Internal use
    private String mDeviceAddress;
    private boolean mIsOff;
    private Handler mHandler;

    public RunFragment() {
        mLocationRepository = new LocationRepository();

        mBleSetup = false;
        mGpsSetup = false;

        mIsOff = true;
        mHandler = new Handler();
    }

    // We need to wait for the context to get valid.

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mBleService = new BluetoothLeService(context);
        mLocationService = new LocationService(context);

        setupServices();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCAN_BLE) {
            if (resultCode == RESULT_OK && data.getExtras() != null) {
                Bundle extras = data.getExtras();
                mDeviceAddress = ((String) extras.get(BluetoothConnectionActivity.EXTRA_DEVICE_ADDR));
                mBleSetup = true;
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.run_layout, container, false);

        mStartButton = view.findViewById(R.id.start_button);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartButton.setEnabled(false);
                mStopButton.setEnabled(true);
                startServices();
            }
        });

        mSpeedTextView = view.findViewById(R.id.speedTextView);
        mInformationTextView = view.findViewById(R.id.informationTextView);

        mStopButton = view.findViewById(R.id.stop_button);
        mStopButton.setEnabled(false);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStopButton.setEnabled(false);
                mStartButton.setEnabled(true);
                stopServices();
            }
        });

        return view;
    }

    private Runnable mSpeedPoller = new Runnable() {
        @Override
        public void run() {
            double speed = mLocationService.getSpeed() * Constants.MPH_IN_METERS_PER_SECOND;
            mSpeedTextView.append(String.valueOf(speed));

            int desiredSpeed = (int) AppSettings.getInstance().getDesiredSpeed();
            int tolerance = (int) AppSettings.getInstance().getTolerance();

            int frequency;

            if (speed < desiredSpeed - tolerance) {
                // too slow
                frequency = Constants.LOW_PERIOD;
                mIsOff = !mIsOff;

                mInformationTextView.setText("Too slow");
            } else if (speed > desiredSpeed + tolerance) {
                // too fast
                frequency = Constants.HIGH_PERIOD;
                mIsOff = !mIsOff;

                mInformationTextView.setText("Too fast");
            } else {
                // OFF
                frequency = Constants.OFF_PERIOD;
                mIsOff = true;

                mInformationTextView.setText("Right speed");
            }

            if (!AppSettings.getInstance().isLocal()) {
                if (mBleService == null
                        || mBleService.getConnectionState() != BluetoothLeService.STATE_CONNECTED) {
                    mBleService.writeCharacteristic(SensorCharacteristicAdapter.
                            createCharacteristic(mIsOff));
                    Log.w(TAG, "Service not connected");
                }
            }

            mHandler.postDelayed(this, frequency);
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
        if (!AppSettings.getInstance().isLocal() && !mBleSetup) {
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

    private void startServices() {
        if (!AppSettings.getInstance().isLocal() && mBleSetup) {
            mBleService.connect(mDeviceAddress);
        }

        if (mGpsSetup) {
            mLocationService.start();
            mHandler.post(mSpeedPoller);
        }
    }

    private void stopServices() {
        mHandler.removeCallbacks(mSpeedPoller);
        mLocationService.stop();
        mBleService.disconnect();
    }
}
