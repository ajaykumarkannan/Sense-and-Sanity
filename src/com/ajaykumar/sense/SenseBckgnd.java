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
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class SenseBckgnd extends Service implements SensorEventListener {
	private SensorManager mSensorManager;
	private boolean upsidedownCurrentState = false;
	private boolean upsidedownLastState = false;
	private AudioManager myaudio;
	private NotificationManager mNotificationManager;
	SharedPreferences flags;
	private boolean flipForSpeaker;
	private boolean seenPhone = false;
	private String currentState = TelephonyManager.EXTRA_STATE_RINGING;
	private boolean silenced = false;
	private int ringerState;
	float pitch = 0;
	float roll = 0;
	float prox = 0;
	private boolean orientationInitialized = false;
	private boolean proxInitialzed = false;
	private boolean sensorsIntialized = false;

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
			mSensorManager.registerListener(this,
					mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
					SensorManager.SENSOR_DELAY_GAME);
			mSensorManager.registerListener(this,
					mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
					SensorManager.SENSOR_DELAY_GAME);

			myaudio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			ringerState = myaudio.getRingerMode();
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
		myaudio.setRingerMode(ringerState);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		Bundle extras = intent.getExtras();
		currentState = extras.getString(TelephonyManager.EXTRA_STATE);
		return START_STICKY;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor;
		if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
			pitch = event.values[1];
			roll = event.values[2];
			orientationInitialized = true;
			// Log.v("PROX", "Pitch " + pitch + ", Roll " + roll);
		} else if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
			prox = event.values[0];
			Log.v("PROX", "Prox " + prox);
			proxInitialzed = true;
		}

		if (orientationInitialized && proxInitialzed) {
			sensorsIntialized = true;
		}

		if (sensorsIntialized) {
			if (currentState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
				// Log.v("DEBUG", "Phone off hook in service");
				if ((pitch < -160 || pitch > 160) && (roll < 20 && roll > -20)) {
					upsidedownCurrentState = true;
					if (upsidedownCurrentState != upsidedownLastState) {
						Toast.makeText(this.getApplicationContext(),
								"Speaker ON", Toast.LENGTH_SHORT).show();
						upsidedownLastState = upsidedownCurrentState;
						myaudio.setSpeakerphoneOn(true);
						Log.v("DEBUG", "Speaker ON");
					}
				} else {
					upsidedownCurrentState = false;
					if (upsidedownCurrentState != upsidedownLastState) {
						Toast.makeText(this.getApplicationContext(),
								"Speaker OFF", Toast.LENGTH_SHORT).show();
						upsidedownLastState = upsidedownCurrentState;
						myaudio.setSpeakerphoneOn(false);
						Log.v("DEBUG", "Speaker OFF");
					}
				}
			} else if (currentState
					.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				// Log.v("DEBUG", "Phone ringing");
				if (!seenPhone) {
					if (prox > 4
							&& !((pitch < -160 || pitch > 160) && (roll < 20 && roll > -20))) {
						seenPhone = true;
						Log.v("DEBUG", "SeenPhone" + prox + ", Pitch " + pitch
								+ ", Roll " + roll);
					} else {
						// Loud ring here
						// Log.v("DEBUG", "Loud Ring");
					}
				} else {
					if ((pitch < -160 || pitch > 160)
							&& (roll < 20 && roll > -20)) {
						// Silence call
						if (!silenced) {
							Log.v("DEBUG", "Silencing Call");
							myaudio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
							silenced = true;
						}
					} else {
						if (prox < 4) {
							// Answer phone
							// Log.v("DEBUG", "Answer Call");
						}
					}
				}
			}
		}
	}
}