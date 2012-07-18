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
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class SenseBckgnd extends Service implements SensorEventListener {
	private SensorManager mSensorManager;
	private float[] mGravs = new float[3];
	private float[] mGeoMags = new float[3];
	private float[] mOrientation = new float[3];
	private float[] mRotationM = new float[9];

	private boolean upsidedownCurrentState = false;
	private boolean upsidedownLastState = false;
	private AudioManager myaudio;
	private NotificationManager mNotificationManager;
	SharedPreferences flags;
	private boolean flipForSpeaker;
	private boolean silenceFlip;
	private boolean seenPhone = false;
	private String currentState = TelephonyManager.EXTRA_STATE_RINGING;
	private boolean silenced = false;
	private int ringerState;
	float pitch = 0;
	float roll = 0;

	Time initTime, currTime;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		initTime = new Time();
		currTime = new Time();
		
		initTime.setToNow();
		flags = getSharedPreferences("myprefs", MODE_PRIVATE);
		flipForSpeaker = flags.getBoolean("flipforspeaker", true);
		silenceFlip = flags.getBoolean("silenceflip", true);

		if (flipForSpeaker || silenceFlip) {
			/************** Sensor Code *******************/
			mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			mSensorManager
					.registerListener(this, mSensorManager
							.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
							SensorManager.SENSOR_DELAY_UI);
			mSensorManager.registerListener(this,
					mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_UI);
			
			myaudio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			ringerState = myaudio.getRingerMode();
		}

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification not = new Notification(R.drawable.ic_launcher,
				"Sense Service Started", System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, SenseBckgnd.class),
				Notification.FLAG_ONGOING_EVENT);
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
		currTime.setToNow();
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			System.arraycopy(event.values, 0, mGravs, 0, 3);
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			for (int i = 0; i < 3; i++)
				System.arraycopy(event.values, 0, mGeoMags, 0, 3);
			break;
		default:
			return;
		}
		if (currTime.toMillis(true) - initTime.toMillis(true) > 1000) {
			if (SensorManager.getRotationMatrix(mRotationM, null, mGravs,
					mGeoMags)) {
				SensorManager.getOrientation(mRotationM, mOrientation);
				pitch = Math.round(Math.toDegrees(mOrientation[1]));
				roll = Math.round(Math.toDegrees(mOrientation[2]));
				// Log.v("DEBUG", "Pitch " + pitch+ ", Roll " + roll);
				if (currentState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
					// Log.v("DEBUG", "Phone off hook in service");
					if ((myabs(pitch) < 20) && (myabs(roll) > 160)) {
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
						if (!((myabs(pitch) < 40) && (myabs(roll) > 150))) {
							seenPhone = true;
							Log.v("DEBUG", "SeenPhone: " + " Pitch " + pitch
									+ ", Roll " + roll);
						} else {
							// Loud ring here
							// Log.v("DEBUG", "Loud Ring");
						}
					} else {
						if ((myabs(pitch) < 20) && (myabs(roll) > 160)) {
							// Silence call
							if (!silenced) {
								if (silenceFlip) {
									Log.v("DEBUG", "Silencing Call");
									myaudio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
									silenced = true;
								}
							}
						} else {
							// if (prox < 4) {
							// Answer phone
							// Log.v("DEBUG", "Answer Call");
						}
					}
				}
			}
		}
	}

	float myabs(float in) {
		return in > 0 ? in : -in;
	}
}