package com.example.locationdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("ALARM_SERVICE_ACTION")) {
		
//			long last_update_time = Preferences.getUpdateViewTime(context);
//			
////			Utils.log("AlarmReceiver wake" + " now:"
////					+ DCTUtils.format24Date(System.currentTimeMillis())
////					+ " Ticker:"
////					+ DCTUtils.format24Date(last_update_time));
//			
//			if (System.currentTimeMillis() - last_update_time > Constants.ONE_MINUTE * 2) {
//				ViewUtils.startMainService(context);
//				Utils.log("AlarmReceiver need restart service");
//			} else {
////				Utils.log("AlarmReceiver time ticker alive, skip");
//
//			}
			Intent service = new Intent(context,MyService.class);
			context.startService(service);

		}
	}

}
