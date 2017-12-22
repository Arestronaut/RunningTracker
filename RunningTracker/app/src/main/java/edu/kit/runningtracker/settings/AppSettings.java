package edu.kit.runningtracker.settings;

/**
 * @author Josh Romanowski
 */

public class AppSettings {
    private static AppSettings mSettings = null;

    private double desiredSpeed;
    private double tolerance;
    private boolean local;

    private AppSettings() {
        this.desiredSpeed = 0;
        this.tolerance = 10;
        this.local = false;
    }

    public static AppSettings getInstance() {
        if (mSettings == null) {
            return new AppSettings();
        } else {
            return mSettings;
        }
    }

    public boolean isLocal() {
        return local;
    }

    public double getDesiredSpeed() {
        return desiredSpeed;
    }

    public double getTolerance() {
        return tolerance;
    }
}
