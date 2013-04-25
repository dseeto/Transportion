package com.purpleplatypus.transportion;

import java.text.DecimalFormat;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Details extends TransportionActivity {

	// pass in which mode!!!
	// floats to 2 decimal places!!
	// need a different pic and color scheme!!!	
	
	String mode;
	String[] modes = {"Car", "Bus", "Bike", "Walk"};	
	String span = "Month";
	String[] spans = {"Day", "Week", "Month", "Year"};
	int place = 2;
	int oldPlace = 2;
	
	// widgets
	TextView day;
	TextView week;
	TextView month;
	TextView year;
	
	TextView miles;
	TextView time;
	TextView gas;
	TextView percent;
	TextView carbon;
	
	TextView title;
	LinearLayout pic;
	
	// Sub Details Class
	CarDetails carDetails;
	BusDetails busDetails;
	BikeDetails bikeDetails;
	WalkDetails walkDetails;
	
	DecimalFormat df;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		df = new DecimalFormat();
		df.setMaximumFractionDigits(2);	
		
		// special TransportionActivity onCreate procedure
		super.onCreate(savedInstanceState);
		// set the layout to whichever layout this activity is attached to
		setFrameView(R.layout.activity_details);
				
		day = (TextView) findViewById(R.id.day);
		week = (TextView) findViewById(R.id.week);
		month = (TextView) findViewById(R.id.month);
		year = (TextView) findViewById(R.id.year);
		
		miles = (TextView) findViewById(R.id.miles);
		time = (TextView) findViewById(R.id.time);
		gas = (TextView) findViewById(R.id.gas);
		percent = (TextView) findViewById(R.id.percent);
		carbon = (TextView) findViewById(R.id.carbon);
		
		title = (TextView) findViewById(R.id.title);
				
		Bundle extras = getIntent().getExtras();
		mode = extras.getString("Mode");		
		
		// default is month
		if (mode.equals("Car")) {
			carDetails = new CarDetails();
		} else if (mode.equals("Bus")) {					
			busDetails = new BusDetails();
		} else if (mode.equals("Bike")) {
			bikeDetails = new BikeDetails();
		} else {
			walkDetails= new WalkDetails();
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_details, menu);
		return true;
	}
	
	public void onClick(View v) {	
		if (mode.equals("Car")) {
			carDetails.onClick(v); // not sure if this works...
		} else if (mode.equals("Bus")) {
			busDetails.onClick(v);
		} else if (mode.equals("Bike")) {
			bikeDetails.onClick(v);
		} else {
			walkDetails.onClick(v);
		}
				
	}

}
