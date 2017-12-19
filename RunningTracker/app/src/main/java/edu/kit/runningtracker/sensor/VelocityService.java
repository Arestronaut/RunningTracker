package edu.kit.runningtracker.sensor;

/**
 * Created by joshr on 19.12.2017.
 */

public class VelocityService {
    public VelocityService(IVelocityHandler handler) {
    }

    public interface IVelocityHandler {
        void onVelocityChanged(double velocity);
    }
}
