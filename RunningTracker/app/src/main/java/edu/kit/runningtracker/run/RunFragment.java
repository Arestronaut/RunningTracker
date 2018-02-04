package edu.kit.runningtracker.run;


import android.Manifest;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.kit.runningtracker.R;
import edu.kit.runningtracker.ble.BluetoothConnectionActivity;
import edu.kit.runningtracker.ble.BluetoothLeService;
import edu.kit.runningtracker.gps.LocationService;
import edu.kit.runningtracker.settings.AppSettings;
import edu.kit.runningtracker.settings.Constants;
import edu.kit.runningtracker.view.MainActivity;

import static edu.kit.runningtracker.ble.BluetoothConnectionActivity.BLE_NOT_SUPPORTED;
import static edu.kit.runningtracker.ble.BluetoothConnectionActivity.BT_ERROR;
import static edu.kit.runningtracker.ble.BluetoothConnectionActivity.BT_PERMISSION_NOT_GRANTED;
import static edu.kit.runningtracker.ble.BluetoothConnectionActivity.REQUEST_NO_DEVICE_FOUND;
import static edu.kit.runningtracker.ble.BluetoothConnectionActivity.REQUEST_SCAN_BLE;

/**
 * @author Raoul Schwagmeier
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

    private boolean mBleSetup;
    private boolean mGpsSetup;

    // Internal use
    private String mDeviceAddress = "";
    private boolean mIsOff;
    private Handler mHandler;

    public RunFragment() {
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case REQUEST_SCAN_BLE:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    mDeviceAddress = ((String) extras.get(BluetoothConnectionActivity.EXTRA_DEVICE_ADDR));
                    mBleSetup = true;

                    Toast.makeText(getContext(),
                            "Found device",
                            Toast.LENGTH_LONG)
                            .show();

                    connectToBleDevice();
                }

                break;
            case BLE_NOT_SUPPORTED:
                Toast.makeText(getContext(),
                        "BLE is not supported on this device.",
                        Toast.LENGTH_LONG)
                        .show();
                break;
            case REQUEST_NO_DEVICE_FOUND:
                Toast.makeText(getContext(),
                        "The requested device could not be found",
                        Toast.LENGTH_LONG)
                        .show();

                break;
            case BT_PERMISSION_NOT_GRANTED:
                Toast.makeText(getContext(),
                        "The permission to use Bluetooth is not granted",
                        Toast.LENGTH_LONG)
                        .show();
                break;
            case BT_ERROR:
                Toast.makeText(getContext(),
                        "An error occurred while trying to build up a BT connection",
                        Toast.LENGTH_LONG)
                        .show();
                break;
            default:
                Log.e(TAG, "Activity ended due to an unknown reason");
                break;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.run_layout, container, false);

        mStartButton = view.findViewById(R.id.start_button);
        mStartButton.setEnabled(false);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartButton.setEnabled(false);
                mStopButton.setEnabled(true);

                ((MainActivity) getActivity()).setMenuEnabled(false);

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
                stop();
            }
        });

        return view;
    }

    private void stop() {
        mStopButton.setEnabled(false);
        mStartButton.setEnabled(true);

        ((MainActivity) getActivity()).setMenuEnabled(true);

        stopServices();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupServices();

        mStartButton.setEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopServices();
        mBleService.cleanUp();
    }

    public void reinitiateBluetooth() {
        stop();

        this.mBleSetup = false;

        setupBluetooth();
    }

    private void connectToBleDevice() {
        if (!AppSettings.getInstance().isLocal()
                && mBleSetup) {

            if (mDeviceAddress != "") {
                mBleService.connect(mDeviceAddress);
            }
        }
    }

    private final Runnable mSpeedPoller = new Runnable() {
        @Override
        public void run() {
            double speed = mLocationService.getSpeed();
            mSpeedTextView.setText(String.valueOf(speed));

            int desiredSpeed = (int) AppSettings.getInstance().getDesiredSpeed();
            int tolerance = (int) AppSettings.getInstance().getTolerance();

            int period;

            if (speed < desiredSpeed - tolerance) {
                // too slow
                period = Constants.LOW_PERIOD;
                mIsOff = !mIsOff;

                mInformationTextView.setText("Too slow");
            } else if (speed > desiredSpeed + tolerance) {
                // too fast
                period = Constants.HIGH_PERIOD;
                mIsOff = !mIsOff;

                mInformationTextView.setText("Too fast");
            } else {
                // OFF
                period = Constants.OFF_PERIOD;
                mIsOff = true;

                mInformationTextView.setText("Right speed");
            }

            if (!AppSettings.getInstance().isLocal()) {
                if (mBleService != null
                        && mBleService.getConnectionState() == BluetoothLeService.STATE_CONNECTED) {
                    BluetoothGattCharacteristic characteristic =
                            mBleService.getCharacteristic(Constants.SERVICE_NAME,
                                    Constants.CHARACTERISTIC_ID);
                    int value = mIsOff ? Constants.OFF : Constants.ON;

                    characteristic.setValue(value, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                    mBleService.writeCharacteristic(characteristic);
                }
            }

            mHandler.postDelayed(this, period);
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == REQUEST_GPS_PERMISSIONS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Gps is set up");
                mGpsSetup = true;
            }
        }
    }

    protected void setupServices() {
        setupBluetooth();
        setupGPS();
    }

    private void setupBluetooth() {
        if (!AppSettings.getInstance().isLocal() && !mBleSetup) {
            Intent startBleIntent = new Intent(getContext(), BluetoothConnectionActivity.class);
            startBleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startBleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivityForResult(startBleIntent, REQUEST_SCAN_BLE);
        }
    }

    private void setupGPS() {
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
            } else {
                mGpsSetup = true;
            }
        }
    }

    private void startServices() {
        if (mGpsSetup) {
            mLocationService.start();
            mHandler.post(mSpeedPoller);
        }
    }

    private void stopServices() {
        mHandler.removeCallbacks(mSpeedPoller);

        // Send Stop signal
        if (!AppSettings.getInstance().isLocal()) {
            if (mBleService != null
                    && mBleService.getConnectionState() == BluetoothLeService.STATE_CONNECTED) {
                BluetoothGattCharacteristic characteristic =
                        mBleService.getCharacteristic(Constants.SERVICE_NAME,
                                Constants.CHARACTERISTIC_ID);

                characteristic.setValue(Constants.OFF, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                mBleService.writeCharacteristic(characteristic);
            }
        }

        mLocationService.stop();
    }
}