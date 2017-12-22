package edu.kit.runningtracker.settings;

import android.bluetooth.BluetoothGattService;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by joshr on 22.12.2017.
 */

public class DeviceSettings {
    private static DeviceSettings mSettings = null;

    private int numSensors;
    private double maxFrequency;
    private List<BluetoothGattService> services;
    private String deviceAddresss;

    private DeviceSettings() {
        this.numSensors = 0;
        this.maxFrequency = 0;
        this.services = new LinkedList<>();
        this.deviceAddresss = null;
    }

    public static DeviceSettings getInstance() {
        if (mSettings == null) {
            return new DeviceSettings();
        } else {
            return mSettings;
        }
    }
}
