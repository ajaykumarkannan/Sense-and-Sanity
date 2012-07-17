package com.ajaykumar.sense;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Main extends Activity {
	private boolean enableFlipForSpeaker;
	private boolean enableSilenceOnFlip;
	private CheckBox flipForSpeakerBox;
	private CheckBox silenceBox;
	SharedPreferences flags;

	// private SensorManager mSensorManager;
	// private Sensor mOrientation;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// mSensorManager = (SensorManager)
		// getSystemService(Context.SENSOR_SERVICE);
		// mOrientation =
		// mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		// mSensorManager.registerListener(this, mOrientation,
		// SensorManager.SENSOR_DELAY_NORMAL);

		flipForSpeakerBox = (CheckBox) findViewById(R.id.flipBox);
		silenceBox = (CheckBox) findViewById(R.id.silenceBox);

		flags = this.getSharedPreferences("myprefs", MODE_PRIVATE);
		enableFlipForSpeaker = flags.getBoolean("flipforspeaker", true);
		enableSilenceOnFlip = flags.getBoolean("silenceflip", true);
		
		// flipForSpeakerBox.setOnCheckedChangeListener(null);

		if (enableFlipForSpeaker) {
			flipForSpeakerBox.setChecked(true);
		} else {
			flipForSpeakerBox.setChecked(false);
		}

		if (enableSilenceOnFlip) {
			silenceBox.setChecked(true);
		} else {
			silenceBox.setChecked(false);
		}

		flipForSpeakerBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						SharedPreferences.Editor prefsEditor = flags.edit();
						if (isChecked) {
							prefsEditor.putBoolean("flipforspeaker", true);
							prefsEditor.commit();
						} else {
							prefsEditor.putBoolean("flipforspeaker", false);
							prefsEditor.commit();
						}
					}
				});
		silenceBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPreferences.Editor prefsEditor = flags.edit();
				if (isChecked) {
					prefsEditor.putBoolean("silenceflip", true);
					prefsEditor.commit();
				} else {
					prefsEditor.putBoolean("silenceflip", false);
					prefsEditor.commit();
				}
			}
		});

	}

	protected void onPause() {
		super.onPause();
	}

	protected void onResume() {
		super.onResume();
	}

	// @Override
	// public void onAccuracyChanged(Sensor arg0, int arg1) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onSensorChanged(SensorEvent event) {
	// // TODO Auto-generated method stub
	// Log.v("DEBUG", "(" + event.values[0] + ", " + event.values[1] + ","
	// + event.values[2] + ")");
	// }
}