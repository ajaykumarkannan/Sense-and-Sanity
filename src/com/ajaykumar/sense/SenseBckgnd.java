package com.ajaykumar.sense;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
	private AudioManager myaudio;
	private NotificationManager mNotificationManager;
	SharedPreferences flags;
	boolean flipForSpeaker;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		flags = getSharedPreferences("myprefs", MODE_PRIVATE);
		flipForSpeaker = flags.getBoolean("flipforspeaker", true);
		if (flipForSpeaker) {
			// Sensor Code
			mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			mOrientation = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			mSensorManager.registerListener(this, mOrientation,
					SensorManager.SENSOR_DELAY_NORMAL);
			myaudio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		}

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification not = new Notification(R.drawable.ic_launcher,
				"Sense Service Started", System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, Main.class), Notification.FLAG_ONGOING_EVENT);
		not.flags = Notification.FLAG_ONGOING_EVENT;
		not.setLatestEventInfo(this, "Sense and Sanity",
				"Speaker Service Running", contentIntent);
		mNotificationManager.notify(1, not);

	}

	@Override
	public void onDestroy() {
		mSensorManager.unregisterListener(this);
		mNotificationManager.cancelAll();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		return START_STICKY;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float pitch = event.values[1];
		float roll = event.values[2];
		if ((pitch < -160 || pitch > 160) && (roll < 20 && roll > -20)) {
			upsidedownCurrentState = true;
			if (upsidedownCurrentState != upsidedownLastState) {
				Toast.makeText(this.getApplicationContext(), "Speaker ON",
						Toast.LENGTH_SHORT).show();
				upsidedownLastState = upsidedownCurrentState;
				myaudio.setSpeakerphoneOn(true);
			}
		} else {
			upsidedownCurrentState = false;
			if (upsidedownCurrentState != upsidedownLastState) {
				Toast.makeText(this.getApplicationContext(), "Speaker OFF",
						Toast.LENGTH_SHORT).show();
				upsidedownLastState = upsidedownCurrentState;
				myaudio.setSpeakerphoneOn(false);
			}
		}
	}
}
