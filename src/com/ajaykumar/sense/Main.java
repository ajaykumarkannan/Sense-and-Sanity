package com.ajaykumar.sense;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Main extends Activity  {
	private boolean enableFlipForSpeaker;
	private CheckBox flipForSpeakerBox;
	SharedPreferences flags;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		flipForSpeakerBox = (CheckBox) findViewById(R.id.flipBox);

		flags = this.getSharedPreferences("myprefs", MODE_WORLD_READABLE);
		final SharedPreferences.Editor prefsEditor = flags.edit();
		enableFlipForSpeaker = flags.getBoolean("flipforspeaker", true);

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

						if (isChecked) {
							prefsEditor.putBoolean("flipforspeaker", true);
							prefsEditor.commit();
						} else {
							prefsEditor.putBoolean("flipforspeaker", false);
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
}