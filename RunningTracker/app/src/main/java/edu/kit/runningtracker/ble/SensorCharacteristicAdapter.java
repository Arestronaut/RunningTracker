package edu.kit.runningtracker.ble;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

/**
 * @author Josh Romanowski
 */

public class SensorCharacteristicAdapter {
    private static final byte[] OFF = new byte[]{
            0, 0, 0, 0, 0
    };

    private static final byte[] ON = new byte[] {
            0b1111, 0b1111, 0b1111, 0b1111, 0b1111, 0b1111
    };

    /**
     * Create a BLE Characteristic
     * @param off off?
     * @return characteristic
     */
    public static BluetoothGattCharacteristic createCharacteristic(boolean off) {
        byte[] newValue = off ? OFF : ON;

        UUID CharID = UUID.fromString("68084313-9757-420f-9f75-bf7f51f1f1bc");

        BluetoothGattCharacteristic characteristic =
                new BluetoothGattCharacteristic(
                        CharID,
                        BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
                        BluetoothGattCharacteristic.PERMISSION_READ
                );

        characteristic.setValue(newValue);

        return characteristic;
    }
}
