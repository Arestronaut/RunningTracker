package edu.kit.runningtracker.settings;

/**
 * @author Josh Romanowski
 */

public class AppSettings {
    private static final AppSettings mSettings = new AppSettings();

    private int mDesiredSpeed = 10;
    private int mDeadzone = 0;
    private int mTolerance = 0;
    private int mSpeed = 10;
    private boolean mLocal = false;
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

    public int getDesiredSpeed() {
        return mDesiredSpeed;
    }

    public void setDesiredSpeed(int desiredSpeed) {
        mDesiredSpeed = desiredSpeed;
    }

    public int getTolerance() {
        return mTolerance;
    }

    public void setTolerance(int tolerance) {
        mTolerance = tolerance;
    }

    public int getDeadzone() {
        return mDeadzone;
    }

    public void setDeadzone(int mDeadzone) {
        this.mDeadzone = mDeadzone;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void setSpeed(int mSpeed) {
        this.mSpeed = mSpeed;
    }

    public boolean useLocation() {
        return mUseLocation;
    }

    public void setUseLocation(boolean mUseLocation) {
        this.mUseLocation = mUseLocation;
    }
}
