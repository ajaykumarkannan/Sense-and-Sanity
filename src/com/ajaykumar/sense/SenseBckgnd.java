/***************Copyright 2012 Ajaykumar Kannan******************************

This file is part of Sense and Sanity.

Sense and Sanity is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Sense and Sanity is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Sense and Sanity.  If not, see <http://www.gnu.org/licenses/>.
 ****************************************************************************/

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
import android.view.KeyEvent;
import android.widget.Toast;

public class SenseBckgnd extends Service implements SensorEventListener {
	private int MAX = 100;

	private boolean answered = false;

	private SensorManager mSensorManager;
	private float[] mGravs = new float[3];
	private float[] mGeoMags = new float[3];
	private float[] mOrientation = new float[3];
	private float[] mRotationM = new float[9];
	private int speakerVolume, headsetVolume;
	private int volMax = 0;

	private boolean upsidedownCurrentState = false;
	private boolean upsidedownLastState = false;
	private AudioManager myaudio;
	private NotificationManager mNotificationManager;
	SharedPreferences flags;
	private boolean flipForSpeaker, silenceFlip, pickupAnswer;
	private boolean seenPhone = false;
	private String currentState = TelephonyManager.EXTRA_STATE_RINGING;
	private boolean silenced = false;
	private int ringerState;
	float pitch = 0;
	float roll = 0;
	float prox = 0;
	private TelephonyManager tm;
	boolean orientationInit = false;
	boolean proxInit = false;

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

		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		flags = getSharedPreferences("myprefs", MODE_PRIVATE);
		flipForSpeaker = flags.getBoolean("flipforspeaker", true);
		silenceFlip = flags.getBoolean("silenceflip", true);
		pickupAnswer = flags.getBoolean("answerPickup", true);
		speakerVolume = flags.getInt("speakervolume", MAX);
		headsetVolume = flags.getInt("headsetvolume", MAX);

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
			mSensorManager.registerListener(this,
					mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
					SensorManager.SENSOR_DELAY_UI);
			myaudio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			volMax = myaudio.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
			speakerVolume *= volMax;
			speakerVolume /= 100;
			headsetVolume *= volMax;
			headsetVolume /= 100;
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
			System.arraycopy(event.values, 0, mGeoMags, 0, 3);
			break;
		case Sensor.TYPE_PROXIMITY:
			prox = event.values[0];
			proxInit = true;
			Log.v("DEBUG", "Prox: " + prox);
			break;
		default:
			return;
		}
		if (currTime.toMillis(true) - initTime.toMillis(true) > 500) {
			if (SensorManager.getRotationMatrix(mRotationM, null, mGravs,
					mGeoMags)) {
				orientationInit = true;
			} else {
				orientationInit = false;
			}
			
			if (orientationInit && proxInit) {
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
							myaudio.setStreamVolume(
									AudioManager.STREAM_VOICE_CALL,
									speakerVolume, AudioManager.FLAG_VIBRATE);
							Log.v("DEBUG", "Speaker ON");
						}
					} else {
						upsidedownCurrentState = false;
						if (upsidedownCurrentState != upsidedownLastState) {
							Toast.makeText(this.getApplicationContext(),
									"Speaker OFF", Toast.LENGTH_SHORT).show();
							upsidedownLastState = upsidedownCurrentState;
							myaudio.setSpeakerphoneOn(false);
							myaudio.setStreamVolume(
									AudioManager.STREAM_VOICE_CALL,
									headsetVolume, AudioManager.FLAG_VIBRATE);
							Log.v("DEBUG", "Speaker OFF");
						}
					}
				} else if (currentState
						.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
					// Log.v("DEBUG", "Phone ringing");
					if (!seenPhone) {
						if (prox >= 1
								&& !((myabs(pitch) < 40) && (myabs(roll) > 150))) {
							seenPhone = true;
							Log.v("DEBUG", "SeenPhone: Prox " + prox
									+ ", Pitch " + pitch + ", Roll " + roll);
						} else {
							// Loud ring here
							// Log.v("DEBUG", "Loud Ring");
						}
					} else {
						if ((myabs(pitch) < 20) && (myabs(roll) > 160)) {
							// Silence call
							if (!silenced && silenceFlip) {
								Log.v("DEBUG", "Silencing Call");
								myaudio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
								silenced = true;
							}
						} else if (prox < 1) {
							// Answer phone
							if (pickupAnswer) {
								if (tm.getCallState() != TelephonyManager.CALL_STATE_RINGING) {
									return;
								}
								if (!answered) {
									Log.v("DEBUG", "Answer Call");
									// Answer the phone
									answerPhoneHeadsethook(this
											.getBaseContext());
									answered = true;
								}
							}
						}
					}
				}
			}
		} else {
			// Do nothing
		}
	}

	float myabs(float in) {
		return in > 0 ? in : -in;
	}

	private void answerPhoneHeadsethook(Context context) {
		// Simulate a press of the headset button to pick up the call
		Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
		buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
				KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
		context.sendOrderedBroadcast(buttonDown,
				"android.permission.CALL_PRIVILEGED");

		// froyo and beyond trigger on buttonUp instead of buttonDown
		Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
		buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
				KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
		context.sendOrderedBroadcast(buttonUp,
				"android.permission.CALL_PRIVILEGED");

		if (myaudio.isMicrophoneMute()) {
			Log.v("DEBUG", "Microphone mute");
			myaudio.setMicrophoneMute(false);
		}
	}
}
