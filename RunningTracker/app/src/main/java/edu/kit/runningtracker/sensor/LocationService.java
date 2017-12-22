package edu.kit.runningtracker.sensor;

import android.location.Location;

/**
 * Created by joshr on 19.12.2017.
 */

public class LocationService {
    private ILocationHandler mHandler;

    public LocationService(ILocationHandler handler) {
        mHandler = handler;
    }

    public interface ILocationHandler {
        void onLocationChanged(Location location);
    }
}
