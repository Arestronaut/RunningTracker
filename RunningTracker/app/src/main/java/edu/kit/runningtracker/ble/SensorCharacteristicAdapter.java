package edu.kit.runningtracker.ble;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

/**
 * @author Josh Romanowski
 */

public class SensorCharacteristicAdapter {
    /**
     * Create a BLE Characteristic
     * @param value <= 0 = low intesinty, >= 1 = high intensitiy
     * @return
     */
    public BluetoothGattCharacteristic createCharacteristic(int value) {
        if (value <= 0) {
            value = 0;
        } else {
            value = 1;
        }

        UUID CharID = UUID.fromString("lol");

       return null;
    }
}
