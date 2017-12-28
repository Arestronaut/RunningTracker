package edu.kit.runningtracker.settings;

/**
 * @author Josh Romanowski
 */

public class AppSettings {
    private static AppSettings mSettings = null;

    private double mDesiredSpeed;
    private double mTolerance;
    private boolean mLocal;

    private AppSettings() {
        this.mDesiredSpeed = 0;
        this.mTolerance = 10;
        this.mLocal = true;
    }

    public static AppSettings getInstance() {
        if (mSettings == null) {
            return new AppSettings();
        } else {
            return mSettings;
        }
    }

    public boolean isLocal() {
        return mLocal;
    }

    public void setLocal(boolean local) { mLocal = local; }

    public double getDesiredSpeed() {
        return mDesiredSpeed;
    }

    public void setDesiredSpeed(double desiredSpeed) { mDesiredSpeed = desiredSpeed; }

    public double getTolerance() {
        return mTolerance;
    }

    public void setTolerance(double tolerance) { mTolerance = tolerance; }
}
