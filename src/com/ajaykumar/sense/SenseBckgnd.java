package com.ajaykumar.sense;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.widget.Toast;

public class SenseBckgnd extends Service implements SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mOrientation;
	private boolean upsidedownCurrentState = false;
	private boolean upsidedownLastState = false;
	private NotificationManager mNotificationManager;
	private AudioManager myaudio; 

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this.getApplicationContext(), "Service started.",
				Toast.LENGTH_LONG).show();
		// Sensor Code
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mSensorManager.registerListener(this, mOrientation,
				SensorManager.SENSOR_DELAY_NORMAL);
		myaudio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this.getApplicationContext(), "Stopping service.",
				Toast.LENGTH_LONG).show();
		mSensorManager.unregisterListener(this);
		mNotificationManager.cancelAll();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		return START_STICKY;
	}

	public void mynotify(String title, String text, String ticker) {
		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = ticker;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		Context context = getApplicationContext();
		CharSequence contentTitle = title;
		CharSequence contentText = text;
		Intent notificationIntent = new Intent(this, SenseBckgnd.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		final int HELLO_ID = 1;

		mNotificationManager.notify(HELLO_ID, notification);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float pitch = event.values[1];

		if (pitch < -120 || pitch > 120) {
			upsidedownCurrentState = true;
			if (upsidedownCurrentState != upsidedownLastState) {
				mynotify("Upside down", "The device is upside down",
						"Sunny side up");
				upsidedownLastState = upsidedownCurrentState;
				myaudio.setSpeakerphoneOn(true);
			}
		} else {
			upsidedownCurrentState = false;
			if (upsidedownCurrentState != upsidedownLastState) {
				mynotify("Normal", "The device is normal", "Normal side up");
				upsidedownLastState = upsidedownCurrentState;
				myaudio.setSpeakerphoneOn(false);
			}
		}
	}
}
