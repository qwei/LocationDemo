package com.example.locationdemo;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

public class LocationController {

	public static final int ONE_MINUTE = 1000 * 60;
	private static final int NETWORK_TIME_OUT = ONE_MINUTE / 4;
	private static final int GPS_TIME_OUT = ONE_MINUTE / 2;
	
	public static final int RESULT_NORMAL = 1;
	public static final int RESULT_TIME_OUT = 2;
	public static final int RESULT_FROM_LAST_KNOWN = 3;

	private LocationManager locationManager;
	private LocationListener locationListener;
	private MyLocationListener myListener;
	private Context mContext;
	private boolean isGPSEnable, isNETEnable;
	private TimerTask task = null;
	private static boolean isScheduled = false;

	public LocationController(Context context) {
		this.mContext = context;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		isGPSEnable = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNETEnable = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		locationListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				switch (status) {
				case LocationProvider.AVAILABLE:
					break;
				case LocationProvider.OUT_OF_SERVICE:
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					locationManager.removeUpdates(this);
					break;
				}
			}

			@Override
			public void onProviderEnabled(String provider) {

			}

			@Override
			public void onProviderDisabled(String provider) {

			}

			@Override
			public void onLocationChanged(Location location) {
				Utils.logAndTxt(mContext,
						"location changed,provider=" + location.getProvider()
								+ ",lon=" + location.getLongitude() + ",lat="
								+ location.getLatitude());
				locationManager.removeUpdates(locationListener);
				myListener.onLocationResult(location,RESULT_NORMAL);
				task.cancel();
				isScheduled = false;
			}
		};
	}

	public void getLocation(MyLocationListener listener) {
		this.myListener = listener;
		getNetWorkLocation();
	}
	
	private void getNetWorkLocation() {
		task = new TimerTask() {
			public void run() {
				Utils.logAndTxt(mContext, "network time out");
				myListener.onMessage("network time out.\n");
				isScheduled = false;
				getGpsLocation();
			}
		};
		if (isNETEnable) {
			Utils.logAndTxt(mContext, "network is available");
			myListener.onMessage("network is available.\n");
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			myListener.onProgress(LocationManager.NETWORK_PROVIDER);
			if (!isScheduled) {
				try {
					new Timer().schedule(task, NETWORK_TIME_OUT);
					isScheduled = true;
				} catch (IllegalStateException e) {
					isScheduled = false;
				}
			}
		} else {
			Utils.logAndTxt(mContext, "network is not available");
			myListener.onMessage("network is not available.\n");
			getGpsLocation();
		}
	}

	private void getGpsLocation() {
		task.cancel();
		task = new TimerTask() {
			public void run() {
				Location loc = getLastLocationFromLocationManager(mContext);
				Utils.logAndTxt(mContext, "gps time out");
				myListener.onMessage("gps time out.\n");
				locationManager.removeUpdates(locationListener);
				myListener.onLocationResult(loc,RESULT_TIME_OUT);
				
				if(loc != null) {
					Utils.logAndTxt(mContext, "last known: lon:"+loc.getLongitude()+",lat="+loc.getLatitude()+",provider="+loc.getProvider());
				} else {
					Utils.logAndTxt(mContext, "last known: null");
				}
				isScheduled = false;
			}
		};
		if (isGPSEnable) {
			Utils.logAndTxt(mContext, "gps is available");
			myListener.onMessage("gps is available.\n");
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			myListener.onProgress(LocationManager.GPS_PROVIDER);
			if (!isScheduled) {
				Utils.log("gps request");
				try {
					new Timer().schedule(task, GPS_TIME_OUT);
					isScheduled = true;
				} catch (IllegalStateException e) {
					isScheduled = false;
				}
			}
		} else {
			Location loc = getLastLocationFromLocationManager(mContext);
			Utils.logAndTxt(mContext, "gps is not available.");
			myListener.onMessage("gps is not available.\n");
			Utils.logAndTxt(mContext, "last known:");
			if(loc != null) {
				Utils.logAndTxt(mContext, "lon:"+loc.getLongitude()+",lat="+loc.getLatitude()+",provider="+loc.getProvider());
			} else {
				Utils.logAndTxt(mContext, "null");
			}
			myListener.onLocationResult(loc,RESULT_FROM_LAST_KNOWN);
		}
	}
	
	public Location getLastLocationFromLocationManager(Context c) {
		LocationManager locationManager;
		String serviceName = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) c.getSystemService(serviceName);

		List<String> providers = locationManager.getAllProviders();
		Location location = null;
		long now = System.currentTimeMillis();
		long minInterval = now;
		Location lastUpdateLocation = null;
		for (int i = 0; i < providers.size(); i++) {
			location = locationManager.getLastKnownLocation(providers.get(i));
			if (location != null) {
				if (now - location.getTime() < minInterval) {
					lastUpdateLocation = location;
					minInterval = now - location.getTime();
				}
			}
		}
		return lastUpdateLocation;
	}

	public interface MyLocationListener {
		public void onLocationResult(Location loc,int state);

		public void onProgress(String provider);
		
		public void onMessage(String msg);
		
	}
}
