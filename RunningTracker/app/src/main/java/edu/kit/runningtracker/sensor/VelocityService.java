package edu.kit.runningtracker.sensor;

/**
 * Created by joshr on 19.12.2017.
 */

public class VelocityService {
    private IVelocityHandler handler;

    public VelocityService(IVelocityHandler handler) {
        this.handler = handler;
    }

    public interface IVelocityHandler {
        void onVelocityUpdate(double velocity);
    }
}
