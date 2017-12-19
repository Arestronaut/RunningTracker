package edu.kit.runningtracker.sensor;

import edu.kit.runningtracker.data.Location;

/**
 * Created by joshr on 19.12.2017.
 */

public class LocationService {
    public LocationService(ILocationHandler handler) {

    }

    public interface ILocationHandler {
        void onLocationChanged(Location location);
    }
}
