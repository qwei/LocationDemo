package com.example.locationdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationdemo.LocationController.MyLocationListener;

public class ShowLocationDetailActivity extends Activity implements
		OnClickListener {

	private static final int TEXT_MESSAGE_ID = 1;
	private static final int DIALOG_MESSAGE_ID = 2;
	private static final int ENABLE_BUTTON_ID = 3;

	private Button locate,startService;
	private TextView showDetail;
	private ProgressDialog progressDialog;

	private LocationController locationController = null;
	private MyLocationListener myListener = null;
	private StringBuilder resultBuilder = new StringBuilder();
	private boolean isGoing = false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TEXT_MESSAGE_ID:
				String result = (String) msg.obj;
				resultBuilder.append(result);
				showDetail.setText(resultBuilder.toString());
				break;
			case DIALOG_MESSAGE_ID:
				String provider = (String) msg.obj;
				progressDialog.setMessage(provider);
				break;
			case ENABLE_BUTTON_ID:
				startService.setEnabled(true);
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		locate = (Button) findViewById(R.id.locate);
		startService = (Button) findViewById(R.id.start_service);
		locate.setOnClickListener(this);
		startService.setOnClickListener(this);
		showDetail = (TextView) findViewById(R.id.show_detail);
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Locating...");

		myListener = new MyLocationListener() {

			@Override
			public void onProgress(String provider) {
				Message msg = new Message();
				msg.what = DIALOG_MESSAGE_ID;
				msg.obj = provider;
				handler.sendMessage(msg);
			}

			@Override
			public void onLocationResult(Location location, int state) {
				Utils.log("result");
				handler.sendEmptyMessage(ENABLE_BUTTON_ID);
				progressDialog.cancel();
				isGoing = false;
				Message msg = new Message();

				if (location != null) {
					switch (state) {
					case LocationController.RESULT_NORMAL:
						msg.obj = "success,provider:"
								+ location.getProvider() +",lon:" + location.getLongitude() + ",lat="
								+ location.getLatitude() +  "\n\n";
						break;
					case LocationController.RESULT_FROM_LAST_KNOWN:
					case LocationController.RESULT_TIME_OUT:
						msg.obj = "success,from last known=lon:" + location.getLongitude() + ",lat="
								+ location.getLatitude() + ",provider:"
								+ location.getProvider() + "\n\n";
						break;
					}

				} else {
					msg.obj = "gps and network failed, and last known is null, failed!\n";
				}
				msg.what = TEXT_MESSAGE_ID;
				handler.sendMessage(msg);
			}

			@Override
			public void onMessage(String msg) {
				Message message = new Message();
				message.obj = msg;
				message.what = TEXT_MESSAGE_ID;
				handler.sendMessage(message);
			}
		};

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.locate:
			if (!isGoing) {
				isGoing = true;
				locationController = new LocationController(this);
				resultBuilder.delete(0, resultBuilder.length());
				showDetail.setText(resultBuilder.toString());
				if (!progressDialog.isShowing()) {
					progressDialog.show();
				}
				locationController.getLocation(myListener);
			} else {
				Toast.makeText(this, "Locating...", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.start_service:
			if(startService.isClickable()) {
				Toast.makeText(ShowLocationDetailActivity.this, "start service...", Toast.LENGTH_SHORT).show();
				Intent service = new Intent(this,MyService.class);
				startService(service);
			} else {
				Toast.makeText(this, "Locating...", Toast.LENGTH_SHORT).show();
			}
			
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Utils.log("onPause");
	}
	@Override
	protected void onStop() {
		Utils.log("onStop");
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();
		}
		
	}
}
