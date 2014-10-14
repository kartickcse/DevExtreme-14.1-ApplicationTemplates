package app.id_00000000000000000000000000000000;

import java.io.IOException;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.DroidGap;
import org.xmlpull.v1.XmlPullParserException;

import android.content.pm.ActivityInfo;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class MainActivity extends CordovaActivity {
	private static final String TAG = "APPTEMPL";
	private static final String SHOW_SPLASH_SCREEN_PREFERENCE_NAME = "showsplashscreen";
	private static final int SPLASH_DISPLAY_TIMEOUT = 3000;

	private Bundle preferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(TAG, "onCreate");

		final int manifestOrientation = getRequestedOrientation();

		setRequestedOrientation(getCurentOrientation());

		final Handler handler = new Handler();

		handler.postDelayed(new Runnable() {
			public void run() {
				setRequestedOrientation(manifestOrientation);
			}
		}, SPLASH_DISPLAY_TIMEOUT + 100);

		super.setBooleanProperty("keepRunning", false);
		keepRunning = false;
		super.setIntegerProperty("splashscreen", R.drawable.splash);

		loadConfiguration();

		if (getStringPreference(SHOW_SPLASH_SCREEN_PREFERENCE_NAME, "true").equals("true")) {
			super.loadUrl(Config.getStartUrl(), SPLASH_DISPLAY_TIMEOUT);
		} else {
			super.loadUrl(Config.getStartUrl());
		}

		super.onResume();
	}

	private String getStringPreference(String key, String defaultValue) {
		if (preferences != null) {
			String value = preferences.getString(key);
			if (value != null) {
				return value;
			}
		}
		return defaultValue;

	}

	private int getCurentOrientation() {
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		boolean isWide = display.getWidth() >= display.getHeight();
		switch (display.getRotation()) {
		case Surface.ROTATION_0:
			return isWide ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		case Surface.ROTATION_90:
			return isWide ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
					: ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
		case Surface.ROTATION_180:
			return isWide ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
					: ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
		case Surface.ROTATION_270:
			return isWide ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
					: ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		}
		return -1;
	}

	private void loadConfiguration() {
		if (preferences == null) {
			preferences = new Bundle();
		}

		int id = getResources().getIdentifier("config", "xml", this.getPackageName());

		if (id == 0) {
			id = getResources().getIdentifier("cordova", "xml", this.getPackageName());
			Log.i("CordovaLog", "config.xml missing, reverting to cordova.xml");
		}
		if (id == 0) {
			Log.i("CordovaLog", "cordova.xml missing. Ignoring...");
			return;
		}

		XmlResourceParser xml = getResources().getXml(id);

		int eventType = -1;

		while (eventType != XmlResourceParser.END_DOCUMENT) {
			if (eventType == XmlResourceParser.START_TAG) {
				String strNode = xml.getName();

				if (strNode.equals("preference")) {
					String name = xml.getAttributeValue(null, "name");
					String value = xml.getAttributeValue(null, "value");
					Log.d("CordovaLog", "Found preference for " + name + "=" + value);
					preferences.putString(name, value);
				}
			}
			try {
				eventType = xml.next();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy, before super");
		loadUrl("about:blank");
		super.onDestroy();
	}

}