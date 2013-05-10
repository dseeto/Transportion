package com.purpleplatypus.transportion;

import android.app.Application;
import android.content.Context;

public class ApplicationState extends Application {
	public static Model data;
	public static Context context;
	
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		data = new Model();		
		
	}
	
	public static Context getContext() {
		return context;
	}
	
	public static Model getModel() {
		return data;
	}
}
