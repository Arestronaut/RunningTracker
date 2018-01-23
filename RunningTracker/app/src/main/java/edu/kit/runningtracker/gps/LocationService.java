package edu.kit.runningtracker.gps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import edu.kit.runningtracker.settings.AppSettings;
import edu.kit.runningtracker.settings.Constants;

import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;

/**
 * @author Raoul Schwagmeier
 */

public class LocationService {
    private static final String TAG = LocationService.class.getSimpleName();

    private AppSettings mAppSettings;
    private Context mContext;
    private LocationManager mLocationManager;

    private double mSpeed;

    public LocationService(Context context) {
        mAppSettings = AppSettings.getInstance();
        mContext = context;
        mSpeed = 0;
    }

    private LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Location changed : " + location);
            if (location.getSpeed() >= mAppSettings.getDeadzone()) {
                mSpeed = location.getSpeed() * Constants.MS_IN_KMH;
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
            if(s.equals(LocationManager.GPS_PROVIDER)) {
                Intent gpsIntent = new Intent(ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(gpsIntent);
            }
        }
    };

    public void start() {
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            if (mLocationManager == null) {
                Log.e(TAG, "location manager is null");
                return;
            }

            Log.i(TAG, "Start receiving location updates");

            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    Constants.HIGH_PERIOD / 2, 0, mListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    Constants.HIGH_PERIOD / 2,
                    0,
                    mListener);
        } else {
            Log.w(TAG, "Missing permissions");
        }
    }

    public void stop() {
        Log.i(TAG, "Stop receiving location updates");
        mLocationManager.removeUpdates(mListener);
    }

    public double getSpeed() {
        return AppSettings.getInstance().useLocation() ? mSpeed : AppSettings.getInstance().getSpeed();
    }
}
