package com.example.wlw.signin.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;

import java.util.Collection;
import java.util.Map;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by WLW on 2017/5/16.
 */

public class utils {
    public final static String BASE = "http://39.108.63.108";

    public static String getBSSID(Context context) {
        WifiManager wifi_service = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifi_service.getConnectionInfo();
        String Bssid = wifiInfo.getBSSID();
        return Bssid;
    }

    public static String getIMEI(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String id = manager.getDeviceId();
        return id;
    }

    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null)
            return true;

        if (obj instanceof CharSequence)
            return ((CharSequence) obj).length() == 0;

        if (obj instanceof Collection)
            return ((Collection) obj).isEmpty();

        if (obj instanceof Map)
            return ((Map) obj).isEmpty();

        if (obj instanceof Object[]) {
            Object[] object = (Object[]) obj;
            if (object.length == 0) {
                return true;
            }
            boolean empty = true;
            for (int i = 0; i < object.length; i++) {
                if (!isNullOrEmpty(object[i])) {
                    empty = false;
                    break;
                }
            }
            return empty;
        }
        return false;
    }

}
