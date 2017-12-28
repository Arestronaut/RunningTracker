package edu.kit.runningtracker.run;

import android.util.Log;

/**
 * Created by joshr on 19.12.2017.
 */

public class StatePaused implements IState {
    private static final String TAG = StatePaused.class.getSimpleName();

    @Override
    public void enter(RunFragment context) {
        Log.i(TAG, "Entered paused state");
    }

    @Override
    public void exit(RunFragment context) {
        Log.i(TAG, "Left paused state");
    }
}
