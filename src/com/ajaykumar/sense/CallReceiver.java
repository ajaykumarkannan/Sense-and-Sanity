/***************Copyright 2012 Ajaykumar Kannan******************************

This file is part of Sense and Sanity.

Sense and Sanity is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Sense and Sanity is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Sense and Sanity.  If not, see <http://www.gnu.org/licenses/>.
 ****************************************************************************/

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
