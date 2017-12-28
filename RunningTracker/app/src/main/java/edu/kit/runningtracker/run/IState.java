package edu.kit.runningtracker.run;

/**
 * Created by joshr on 19.12.2017.
 */

public interface IState {
    void enter(RunFragment context);
    void exit(RunFragment context);
}
