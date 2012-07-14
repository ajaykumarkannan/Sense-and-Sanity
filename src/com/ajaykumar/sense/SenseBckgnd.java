package com.ajaykumar.sense;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class SenseBckgnd extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this.getApplicationContext(), "Service started.",
				Toast.LENGTH_LONG).show();

		mynotify("Service started", "The serivce has been started.", "Sense");
		for (int i = 0; i < 10; i++) {
			Timer temp = new Timer();
			temp.schedule(new TimerTask() {

				@Override
				public void run() {
					mynotify("Task", "Task repeating", "Sense");

				}
			}, 10000);
		}
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this.getApplicationContext(), "Stopping service.",
				Toast.LENGTH_LONG).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		return START_STICKY;
	}

	public void mynotify(String title, String text, String ticker) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = ticker;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		Context context = getApplicationContext();
		CharSequence contentTitle = title;
		CharSequence contentText = text;
		Intent notificationIntent = new Intent(this, SenseBckgnd.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		final int HELLO_ID = 1;

		mNotificationManager.notify(HELLO_ID, notification);
	}
}
