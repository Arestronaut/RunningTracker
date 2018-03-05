package edu.kit.runningtracker.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.LinkedList;

import edu.kit.runningtracker.settings.Constants;


/**
 * Activity that can be used for scanning BLE devices.
 * Returns the device's address in the return intent extra.
 *
 * @author Josh Romanowski
 */

public class BluetoothConnectionActivity extends Activity {
    private static final String TAG = BluetoothConnectionActivity.class.getSimpleName();

    /**
     * Request code for the activity.
     */
    public static final int REQUEST_SCAN_BLE = 2;
    public static final int REQUEST_NO_DEVICE_FOUND = 3;
    public static final int BLE_NOT_SUPPORTED = 4;
    private static final int REQUEST_ENABLE_BT = 5;
    public static final int BT_PERMISSION_NOT_GRANTED = 6;
    public static final int BT_ERROR = 7;
    private static final long SCAN_PERIOD = 5000;

    /**
     * Intent extra key for the device address.
     */
    public static final String EXTRA_DEVICE_ADDR = "DeviceAddress";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;

    private boolean mScanning;
    private LinkedList<ScanFilter> mFilters;
    private ScanSettings mScanSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // at first check if BLE is supported on the current device
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(TAG, "BLE is not supported");
            setResult(BLE_NOT_SUPPORTED);
            finish();
        }

        mFilters = new LinkedList<>();
        mFilters.add(new ScanFilter.Builder().setDeviceAddress(Constants.DEVICE_ID).build());

        mScanSettings = new ScanSettings.Builder().
                setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build();

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                setResult(BT_ERROR);
                finish();
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            finish();
        }

        // Enable bluetooth
        if (!this.mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            scanLeDevice();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            scanLeDevice();
        } else {
            setResult(BT_PERMISSION_NOT_GRANTED);
            finish();
        }
    }

    public boolean isScanning() {
        return mScanning;
    }

    private void scanLeDevice() {
        final BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();

        final ScanCallback leScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                if (mScanning) {
                    mScanning = false;
                    mDevice = result.getDevice();
                    Intent data = new Intent();
                    data.putExtra(EXTRA_DEVICE_ADDR, mDevice.getAddress());
                    scanner.stopScan(this);
                    setResult(REQUEST_SCAN_BLE, data);
                    finish();
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);

                setResult(BT_ERROR);
                finish();
            }
        };

        // Stops scanning after a pre-defined scan period.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                scanner.stopScan(leScanCallback);
                Log.i(TAG, "Stop scaning BLE");

                if (mDevice == null) {
                    setResult(REQUEST_NO_DEVICE_FOUND);
                }

                finish();
            }
        }, SCAN_PERIOD);

        scanner.startScan(mFilters, mScanSettings, leScanCallback);
        Log.i(TAG, "Start scanning BLE");
        mScanning = true;
    }
}