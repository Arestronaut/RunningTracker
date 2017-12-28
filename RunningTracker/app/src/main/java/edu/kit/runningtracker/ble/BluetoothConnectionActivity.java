package edu.kit.runningtracker.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import edu.kit.runningtracker.R;

/**
 * Created by joshr on 19.12.2017.
 */

public class BluetoothConnectionActivity extends Activity {
    private static final String TAG = BluetoothConnectionActivity.class.getSimpleName();

    public static final int REQUEST_SCAN_BLE = 2;
    private static final int REQUEST_ENABLE_BT = 4;
    private static final long SCAN_PERIOD = 10000;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private boolean mScanning;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        scanLeDevice(true);
    }

    private ScanCallback mLeScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice device = result.getDevice();
                    Intent data = new Intent();
                    data.putExtra("DeviceAdress", device.getAddress());
                    setResult(REQUEST_SCAN_BLE, data);
                    finish();
                }
            };

    private void scanLeDevice(final boolean enable) {
        // TODO: Scan filter
        final BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    scanner.stopScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            scanner.startScan(mLeScanCallback);
        } else {
            mScanning = false;
            scanner.stopScan(mLeScanCallback);
        }
    }
}
