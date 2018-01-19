package edu.kit.runningtracker.gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;

import edu.kit.runningtracker.data.LocationRepository;
import edu.kit.runningtracker.settings.AppSettings;

/**
 * Created by joshr on 19.12.2017.
 */

public class LocationService implements LocationListener {
    private static final String TAG = LocationService.class.getSimpleName();

    private ILocationHandler mHandler;
    private AppSettings mAppSettings;
    private Context mContext;
    private LocationManager mLocationManager;

    public LocationService(ILocationHandler handler, Context context) {
        mHandler = handler;
        mAppSettings = AppSettings.getInstance();
        mContext = context;
    }


    public void start() {
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Start receiving location updates");

            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            if (mLocationManager == null) {
                Log.e(TAG, "location manager is null");
                return;
            }

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, this);
        }
    }

    public void stop() {
        System.out.println("Stop receiving location updates");

        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("Location changed");
        Log.d(TAG, "onLocationChanged() : " + location);
        if (location.getSpeed() >= mAppSettings.getDeadzone()) {
            mHandler.onLocationChanged(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public interface ILocationHandler {
        void onLocationChanged(Location location);
    }
}
