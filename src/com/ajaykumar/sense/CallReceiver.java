package com.ajaykumar.sense;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {
	Intent service;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			String state = extras.getString(TelephonyManager.EXTRA_STATE);
			if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
				String phoneNumber = extras
						.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
				Log.v("DEBUG", state + " " + phoneNumber);
				service = new Intent(context, SenseBckgnd.class);
				context.startService(service);
				Toast toast = Toast.makeText(context, "Text Toast", Toast.LENGTH_LONG);
				toast.show();

			} else {
				Log.v("DEBUG", state);
				service = new Intent(context, SenseBckgnd.class);
				context.stopService(service);
			}
		}
	}
}
