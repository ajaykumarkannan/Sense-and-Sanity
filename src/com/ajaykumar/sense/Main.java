package com.ajaykumar.sense;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity implements SensorEventListener {
	GlobalState myapp;
	private SensorManager mSensorManager;
	private Sensor mOrientation;
	private boolean enableFlipForSpeaker;
	private CheckBox flipForSpeakerBox;

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		myapp = (GlobalState) getApplicationContext();
		enableFlipForSpeaker = myapp.getStateFlipForSpeaker();
		flipForSpeakerBox = (CheckBox) findViewById(R.id.flipBox);

		// flipForSpeakerBox.setOnCheckedChangeListener(null);

		if (enableFlipForSpeaker) {
			flipForSpeakerBox.setChecked(true);
		} else {
			flipForSpeakerBox.setChecked(false);
		}

		flipForSpeakerBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						myapp.setStateFlipForSpeaker(isChecked);
						Toast.makeText(myapp,
								"state: " + myapp.getStateFlipForSpeaker(),
								Toast.LENGTH_SHORT).show();
					}
				});

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mOrientation,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		TextView tvX = (TextView) findViewById(R.id.x_axis);
		TextView tvY = (TextView) findViewById(R.id.y_axis);
		TextView tvZ = (TextView) findViewById(R.id.z_axis);
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		tvX.setText(Float.toString(x));
		tvY.setText(Float.toString(y));
		tvZ.setText(Float.toString(z));
	}
}