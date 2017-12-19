package edu.kit.runningtracker.ble;

import android.bluetooth.BluetoothGattServer;

import edu.kit.runningtracker.sensor.VelocityService;

/**
 * Created by joshr on 19.12.2017.
 */

public class BluetoothService {
    private BluetoothConnectionManager manager;
    private BluetoothGattServer server;
    private VelocityService service;


    private boolean checkBLESupport() {
        return false;
    }

    private void requestBluetooth() {
    }

    private void initServer() {

    }

    private void shutdownServer() { }
}
