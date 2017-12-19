package edu.kit.runningtracker.data;

import android.location.Location;

import java.util.Collection;

/**
 * Created by joshr on 19.12.2017.
 */

public class LocationRepository implements IRepository<Location> {
    @Override
    public void put(Location element) {

    }

    @Override
    public Location get(int id) {
        return null;
    }

    @Override
    public Collection<Location> get() {
        return null;
    }

    @Override
    public void update(int id, Location element) {

    }

    @Override
    public void delete(int id) {

    }
}
