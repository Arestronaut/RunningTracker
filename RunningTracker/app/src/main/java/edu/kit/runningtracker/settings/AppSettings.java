package edu.kit.runningtracker.settings;

/**
 * @author Josh Romanowski
 */

public class AppSettings {
    private static final AppSettings mSettings = new AppSettings();

    private double mDesiredSpeed = 10;
    private double mDeadzone = 0;
    private double mTolerance = 0;
    private boolean mLocal = true;
    private double mSpeed = 10;
    private boolean mUseLocation = false;

    private AppSettings() {
    }

    public static AppSettings getInstance() {
        return mSettings;
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

    public double getDeadzone() {
        return mDeadzone;
    }

    public void setDeadzone(double mDeadzone) {
        this.mDeadzone = mDeadzone;
    }

    public double getSpeed() {
        return mSpeed;
    }

    public void setSpeed(double mSpeed) {
        this.mSpeed = mSpeed;
    }

    public boolean useLocation() {
        return mUseLocation;
    }

    public void setUseLocation(boolean mUseLocation) {
        this.mUseLocation = mUseLocation;
    }
}
