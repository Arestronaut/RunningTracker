package edu.kit.runningtracker.settings;

import android.bluetooth.BluetoothGattService;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Josh Romanowski
 */

public class DeviceSettings {
    private static DeviceSettings mSettings = null;

    private int mNumSensors;
    private double mMaxFrequency;
    private List<BluetoothGattService> mServices;
    private String mDeviceAddress;

    private DeviceSettings() {
        this.mNumSensors = 0;
        this.mMaxFrequency = 0;
        this.mServices = new LinkedList<>();
        this.mDeviceAddress = null;
    }

    public static DeviceSettings getInstance() {
        if (mSettings == null) {
            return new DeviceSettings();
        } else {
            return mSettings;
        }
    }

    public int getNumSensors() {
        return mNumSensors;
    }

    public double getMaxFrequency() {
        return mMaxFrequency;
    }

    public void addServices(List<BluetoothGattService> services) {
        this.mServices.addAll(services);
    }

    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        mDeviceAddress = deviceAddress;
    }
}
