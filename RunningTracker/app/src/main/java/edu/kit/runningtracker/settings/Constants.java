package edu.kit.runningtracker.settings;

import android.os.ParcelUuid;

/**
 * Created by raoulschwagmeier on 15.01.18.
 */

public class Constants {
    public static ParcelUuid SERVICE_NAME = ParcelUuid.
            fromString("68084313-9757-420f-9f75-bf7f51f1f1bc");
    public static float MPH_IN_METERS_PER_SECOND = 0.44704f;

    public static int LOW_PERIOD = 1000;
    public static int HIGH_PERIOD = 200;
    public static int OFF_PERIOD = 500;

}
