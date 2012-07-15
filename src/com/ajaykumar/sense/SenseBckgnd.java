package com.ajaykumar.sense;

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
	private AudioManager myaudio;
	GlobalState mystate;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		mystate = (GlobalState) getApplicationContext();

		Toast.makeText(this.getApplicationContext(),
				"state: " + mystate.getStateFlipForSpeaker(),
				Toast.LENGTH_SHORT).show();
		// Sensor Code
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mSensorManager.registerListener(this, mOrientation,
				SensorManager.SENSOR_DELAY_NORMAL);
		myaudio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	}

	@Override
	public void onDestroy() {
		mSensorManager.unregisterListener(this);
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
		if (mystate.getStateFlipForSpeaker()) {
			float pitch = event.values[1];
			if (pitch < -90 || pitch > 90) {
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
}
