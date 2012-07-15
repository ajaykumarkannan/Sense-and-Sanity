package com.ajaykumar.sense;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity implements SensorEventListener {
	Button start, stop;
	Intent service;
	private SensorManager mSensorManager;
	private Sensor mOrientation;

	OnClickListener startButtonHandler = new OnClickListener() {

		@Override
		public void onClick(View v) {
			startBackgroundService();
		}
	};

	OnClickListener stopButtonHandler = new OnClickListener() {

		@Override
		public void onClick(View v) {
			stopBackgroundService();
		}
	};

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);

		start.setOnClickListener(startButtonHandler);
		stop.setOnClickListener(stopButtonHandler);

		// Sensor Code
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

	void startBackgroundService() {
		service = new Intent(this, SenseBckgnd.class);
		startService(service);
	}

	void stopBackgroundService() {
		stopService(service);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		TextView tvX = (TextView) findViewById(R.id.x_axis);
		TextView tvY = (TextView) findViewById(R.id.y_axis);
		TextView tvZ = (TextView) findViewById(R.id.z_axis);
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		tvX.setText(Float.toString(x) + " | ");
		tvY.setText(Float.toString(y) + " | ");
		tvZ.setText(Float.toString(z));

	}
}