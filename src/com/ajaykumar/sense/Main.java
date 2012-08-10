package com.ajaykumar.sense;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Main extends Activity {
	private int MAX = 100;

	private boolean enableFlipForSpeaker, enableSilenceOnFlip,
			enableAnswerOnPickUp;
	private int speakerVol, headsetVol;
	private CheckBox flipForSpeakerBox, silenceBox, pickupBox;
	private SeekBar speakerBar, headsetBar;
	SharedPreferences flags;
	SharedPreferences.Editor prefsEditor;

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
		pickupBox = (CheckBox) findViewById(R.id.pickupBox);

		speakerBar = (SeekBar) findViewById(R.id.speakerBar);
		headsetBar = (SeekBar) findViewById(R.id.headsetBar);

		flags = this.getSharedPreferences("myprefs", MODE_PRIVATE);
		enableFlipForSpeaker = flags.getBoolean("flipforspeaker", true);
		enableSilenceOnFlip = flags.getBoolean("silenceflip", true);
		enableAnswerOnPickUp = flags.getBoolean("answerPickup", true);
		speakerVol = flags.getInt("speakervolume", MAX);
		headsetVol = flags.getInt("headsetvolume", MAX);

		prefsEditor = flags.edit();
		prefsEditor.putBoolean("flipforspeaker", enableFlipForSpeaker);
		prefsEditor.putBoolean("silenceflip", enableSilenceOnFlip);
		prefsEditor.putBoolean("answerPickup", enableAnswerOnPickUp);
		prefsEditor.putInt("speakervolume", speakerVol);
		prefsEditor.putInt("headsetvolume", headsetVol);
		prefsEditor.commit();

		flipForSpeakerBox.setChecked(enableFlipForSpeaker);
		silenceBox.setChecked(enableSilenceOnFlip);
		pickupBox.setChecked(enableAnswerOnPickUp);
		speakerBar.setMax(MAX);
		speakerBar.setProgress(speakerVol);
		headsetBar.setMax(MAX);
		headsetBar.setProgress(headsetVol);

		flipForSpeakerBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						prefsEditor = flags.edit();
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
				prefsEditor = flags.edit();
				if (isChecked) {
					prefsEditor.putBoolean("silenceflip", true);
					prefsEditor.commit();
				} else {
					prefsEditor.putBoolean("silenceflip", false);
					prefsEditor.commit();
				}
			}
		});
		pickupBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				prefsEditor = flags.edit();
				if (isChecked) {
					prefsEditor.putBoolean("answerPickup", true);
					prefsEditor.commit();
				} else {
					prefsEditor.putBoolean("answerPickup", false);
					prefsEditor.commit();
				}
			}
		});

		speakerBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					speakerVol = progress;
					prefsEditor = flags.edit();
					prefsEditor.putInt("speakervolume", speakerVol);
					prefsEditor.commit();
				}
			}
		});

		headsetBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					headsetVol = progress;
					prefsEditor = flags.edit();
					prefsEditor.putInt("headsetvolume", headsetVol);
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