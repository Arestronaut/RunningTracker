package edu.kit.runningtracker.settings;

import android.bluetooth.BluetoothGattService;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Josh Romanowski
 */

public class DeviceSettings {
    private static DeviceSettings mSettings = null;

    private int numSensors;
    private double maxFrequency;
    private List<BluetoothGattService> services;
    private String deviceAddress;

    private DeviceSettings() {
        this.numSensors = 0;
        this.maxFrequency = 0;
        this.services = new LinkedList<>();
        this.deviceAddress = null;
    }

    public static DeviceSettings getInstance() {
        if (mSettings == null) {
            return new DeviceSettings();
        } else {
            return mSettings;
        }
    }

    public int getNumSensors() {
        return numSensors;
    }

    public double getMaxFrequency() {
        return maxFrequency;
    }

    public void setServices(List<BluetoothGattService> services) {
        this.services = services;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }
}
