package com.example.locationdemo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class Utils {
	public static final boolean isDebug = true;
	public static final boolean useOnlineData = true;
	public static final String SAMSUNG_EXTERNAL_SD_DIR_NAME = "external_sd";
	public static final String LOG_FILE_NAME = "LocationDemo_log.txt";

	public static void log(String str) {
		if (isDebug)
			Log.d("plugin", str);
	}

	public static void wl(String str) {
		if (isDebug)
			Log.d("weather service", str);
	}

	public static String exec(String cmd) {
		String output = "";

		try {
			Process process = Runtime.getRuntime().exec(cmd);
			InputStream ins = process.getInputStream();
			byte[] buffer = new byte[1024];
			StringBuilder builder = new StringBuilder();
			int bytesRead = 0;

			while ((bytesRead = ins.read(buffer, 0, buffer.length)) != -1) {
				builder.append(new String(buffer, 0, bytesRead));
			}

			output = builder.toString();
			ins.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output;
	}

	public static String getSDCardDirPath() {
		if (isSamSungDevice()) {
			if (isSamSungExternalSDMounted()) {
				return getExternalStorageDirPath() + "/"
						+ SAMSUNG_EXTERNAL_SD_DIR_NAME;
			} else {
				return getExternalStorageDirPath();
			}
		} else {
			return getExternalStorageDirPath();
		}
	}

	public static boolean isSamSungExternalSDMounted() {
		if (exec("mount").indexOf("external_sd") >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public static String getExternalStorageDirPath() {
		File sdCardDir = Environment.getExternalStorageDirectory();
		return sdCardDir.getAbsolutePath();
	}

	public static boolean isSamSungDevice() {
		File dir = new File(getExternalStorageDirPath() + "/"
				+ SAMSUNG_EXTERNAL_SD_DIR_NAME);
		if (dir.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public static void logAndTxt(Context context, String log) {
		File f = new File(getSDCardDirPath() +"/"+LOG_FILE_NAME);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		log = "[" + format24Date(System.currentTimeMillis()) + "]:" + log
				+ "\r\n";
		try {
			FileWriter fw = new FileWriter(f, true);
			fw.write(log);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			// }
			// l(log);
		}
	}

	public static String format24Date(long time) {
		Date d = new Date(time);
		return new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
				.format(d);
	}
}
