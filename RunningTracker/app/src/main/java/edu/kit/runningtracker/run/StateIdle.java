package edu.kit.runningtracker.run;

import android.util.Log;

/**
 * Created by joshr on 19.12.2017.
 */

public class StateIdle implements IState {
    private static final String TAG = StateIdle.class.getSimpleName();

    @Override
    public void enter(RunFragment context) {
        context.resetServices();
    }

    @Override
    public void exit(RunFragment context) {
        context.setupServices();
    }
}
