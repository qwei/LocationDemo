package com.example.locationdemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.example.locationdemo.LocationController.MyLocationListener;

public class MyService extends Service {
	private AlarmManager am;
	private final static int INTERVAL = 30;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		LocationController locationController = new LocationController(this);
		locationController.getLocation(new MyLocationListener() {
			
			@Override
			public void onProgress(String provider) {
				
			}
			
			@Override
			public void onMessage(String msg) {
				
			}
			
			@Override
			public void onLocationResult(Location loc, int state) {
				
			}
		});
		am = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent i = new Intent(this, AlarmReceiver.class);
		i.setAction("ALARM_SERVICE_ACTION");
		PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, i, 0);

		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+INTERVAL*LocationController.ONE_MINUTE, INTERVAL*LocationController.ONE_MINUTE,
				pIntent);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		task.cancel();
//		task = null;
	}
}
