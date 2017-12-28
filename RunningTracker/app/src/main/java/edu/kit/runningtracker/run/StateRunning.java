package edu.kit.runningtracker.run;

import android.util.Log;

/**
 * @author Josh Romanowski
 */

public class StateRunning implements IState {
    private static final String TAG = StateRunning.class.getSimpleName();

    @Override
    public void enter(RunFragment context) {
        Log.i(TAG, "Entered running state");
    }

    @Override
    public void exit(RunFragment context) {
        Log.i(TAG, "Left running state");
    }
}
