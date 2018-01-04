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
import android.widget.Toast;

import java.util.LinkedList;

import edu.kit.runningtracker.R;
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
    private static final int REQUEST_ENABLE_BT = 4;
    private static final long SCAN_PERIOD = 10000;

    /**
     * Intent extra key for the device address.
     */
    public static final String EXTRA_DEVICE_ADDR = "DeviceAddress";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private boolean mScanning;
    private LinkedList<ScanFilter> mFilters;
    private ScanSettings mScanSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFilters = new LinkedList<>();
        mFilters.add(new ScanFilter.Builder().setServiceUuid(Constants.SERVICE_NAME).build());
        mScanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build();

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Enable bluetooth
        if (this.mBluetoothAdapter == null || !this.mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Check for BLE support
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        scanLeDevice();
    }

    public boolean isScanning() {
        return mScanning;
    }

    private ScanCallback mLeScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice device = result.getDevice();
                    Intent data = new Intent();
                    data.putExtra(EXTRA_DEVICE_ADDR, device.getAddress());
                    setResult(REQUEST_SCAN_BLE, data);
                    finish();
                }
            };

    private void scanLeDevice() {
        final BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();

        // Stops scanning after a pre-defined scan period.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                scanner.stopScan(mLeScanCallback);
            }
        }, SCAN_PERIOD);

        mScanning = true;
        scanner.startScan(mFilters, mScanSettings, mLeScanCallback);
    }
}
