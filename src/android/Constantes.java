package com.tcloud.util;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.security.MessageDigest;

@SuppressLint("DefaultLocale")
public class Constantes {

	// URL del App
	public static String URL = "http://www.omegatelecom.cl:8100";

	public static long UPDATE_INTERVAL = 1000 * 60 * 5;
	public static long FASTEST_INTERVAL = 1000 * 60 * 2;

	// Google GCM - Numero de Proyecto Google API
	public static String SENDER_ID = "314048987912";


    // Constantes LocationService
    public static String geofence_enter = "Now entering %1$s";
    public static String geofence_dwell = "Dwelling in %1$s";
    public static String geofence_exit = "Now exiting %1$s";
    public static String geofence_not_available = "Geofence service is not available now";
    public static String geofence_too_many_geofences = "App registered too many geofences";
    public static String geofence_too_many_pending_intents = "Too many PendingIntents provided";
    public static String unknown_geofence_error = "Unknown error";
    public static String geofences_added = "Geofences added";
    public static String geofences_removed = "Geofences removed";



	public static final String md5(final String toEncrypt) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("md5");
			digest.update(toEncrypt.getBytes());
			final byte[] bytes = digest.digest();
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(String.format("%02X", bytes[i]));
			}
			return sb.toString().toLowerCase();
		} catch (Exception exc) {
			return ""; // Impossibru!
		}
	}

}