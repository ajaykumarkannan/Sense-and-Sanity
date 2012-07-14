package com.ajaykumar.sense;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			String state = extras.getString(TelephonyManager.EXTRA_STATE);
			Log.w("DEBUG", state);
			if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				String phoneNumber = extras
						.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
				Log.v("DEBUG", "Log " + phoneNumber);
			}
		}
	}

	/*
	 * public void onReceive(Context context, Intent intent) {
	 * 
	 * final String originalNumber = intent
	 * .getStringExtra(Intent.EXTRA_PHONE_NUMBER);
	 * 
	 * Toast.makeText(context, "I know what you did last summer " +
	 * originalNumber, Toast.LENGTH_LONG);
	 * 
	 * // START YOUR SERVICE HERE FOR COUNTING MINS Intent myintent = new
	 * Intent(context, SenseBckgnd.class); myintent.putExtra("number",
	 * originalNumber); context.startService(myintent);
	 * 
	 * }
	 */
}
