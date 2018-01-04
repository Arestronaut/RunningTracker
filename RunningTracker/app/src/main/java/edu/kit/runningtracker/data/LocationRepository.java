package edu.kit.runningtracker.data;

import android.content.Context;
import android.location.Location;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Josh Romanowski
 */

public class LocationRepository implements IRepository<Location> {
    private final static String TAG = LocationRepository.class.getSimpleName();

    private List<Location> mLocations;

    public LocationRepository() {
        mLocations = new LinkedList<>();
    }

    @Override
    public void put(Location element) {
        mLocations.add(element);
    }

    @Override
    public Location get(int id) {
        return mLocations.get(id);
    }

    @Override
    public Collection<Location> get() {
        return mLocations;
    }

    @Override
    public void update(int id, Location element) {
        mLocations.set(id, element);
    }

    @Override
    public void delete(int id) {
        mLocations.remove(id);
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        mLocations = new LinkedList<>();
    }
}
