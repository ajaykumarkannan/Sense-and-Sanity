package com.ajaykumar.sense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity {

	Button start, stop;
Intent service;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);

		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startBackgroundService();

			}
		});

		stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stopBackgroundService();

			}
		});
	}

	void startBackgroundService() {
		service = new Intent(this, SenseBckgnd.class);
		startService(service);
	}

	void stopBackgroundService() {
		stopService(service);
	}
}