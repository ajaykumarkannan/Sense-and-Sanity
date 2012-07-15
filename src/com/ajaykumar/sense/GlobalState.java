package com.ajaykumar.sense;

import android.app.Application;
import android.content.res.Configuration;

public class GlobalState extends Application {
	private boolean enableFlipforSpeaker = true;

	public boolean getStateFlipForSpeaker() {
		return enableFlipforSpeaker;
	}

	public void setStateFlipForSpeaker(boolean in) {
		enableFlipforSpeaker = in;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
