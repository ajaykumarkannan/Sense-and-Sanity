package com.ajaykumar.sense;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {
	Intent service;
	SharedPreferences flags;
	boolean flipForSpeaker;
	SharedPreferences.Editor prefsEditor;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();

		flags = context.getSharedPreferences("myprefs", Context.MODE_PRIVATE);
		flipForSpeaker = flags.getBoolean("flipforspeaker", true);

		prefsEditor = flags.edit();

		if (extras != null && flipForSpeaker) {
			String state = extras.getString(TelephonyManager.EXTRA_STATE);
			prefsEditor.putString("currentState", state);
			prefsEditor.commit();
			if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)
					|| state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				Log.v("DEBUG", state);
				service = new Intent(context, SenseBckgnd.class);
				service.putExtras(extras);
				context.startService(service);
			} else {
				Log.v("DEBUG", state);
				service = new Intent(context, SenseBckgnd.class);
				context.stopService(service);
			}
		}
	}
}
