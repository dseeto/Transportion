package com.purpleplatypus.transportion;

import java.text.DecimalFormat;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CarDetails extends TransportionActivity{
	DecimalFormat df;
	
		
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
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
	
		df = new DecimalFormat();
		df.setMaximumFractionDigits(0);	
		
		title.setText("Car Details");
		
		miles.setText("100 Miles");
		time.setText("1 Hr 3 Min");
		gas.setText("" + df.format(100.00/37.00) + " Gallons");
		percent.setText("" + df.format(100.00/(100+71+87)*100) + "%");
		carbon.setText("" + df.format(109.00*(100/1.6)) + " Grams"); // 109 per km; 1.6 km for each mile
		/*
		// also set background color
		miles.setBackgroundResource(Color.parseColor("#ff4e50"));
		time.setBackgroundResource(Color.parseColor("#ff4e50"));
		gas.setBackgroundResource(Color.parseColor("#ff4e50"));
		percent.setBackgroundResource(Color.parseColor("#ff4e50"));
		carbon.setBackgroundResource(Color.parseColor("#ff4e50"));	
		*/
		// set pic
		LinearLayout layout = (LinearLayout)findViewById(R.id.pic);			
		ImageView imageView1 = new ImageView(this);			
		// not sure what this is
		//imageView1.setId(j+1);			
		imageView1.setImageResource(R.drawable.car);			
		layout.addView(imageView1);
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_details, menu);
		return true;
	}
	
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.day:
			place = 0;
			day.setTypeface(null, Typeface.BOLD);
			day.setBackgroundResource(R.drawable.blue_button);
					
			miles.setText("0 Miles");
			time.setText("0 Min");
			gas.setText("0 Gallons");
			percent.setText("0%");
			carbon.setText("0 Grams");			
			
			break;
		case R.id.week:
			place = 1;
			week.setTypeface(null, Typeface.BOLD);
			week.setBackgroundResource(R.drawable.blue_button);
			
			miles.setText("0 Miles");
			time.setText("0 Min");
			gas.setText("0 Gallons");
			percent.setText("0%");
			carbon.setText("0 Grams");

			break;
		case R.id.month:
			place = 2;
			month.setTypeface(null, Typeface.BOLD);
			month.setBackgroundResource(R.drawable.blue_button);
			
			// update info
			miles.setText("100 Miles");
			time.setText("1 Hr 3 Min");
			gas.setText("" + df.format(100/37.00) + " Gallons");
			percent.setText("" + df.format(100.00/(100+71+87)*100) + "%");
			carbon.setText("" + df.format(109.00*(100/1.6)) + " Grams"); // 109 per km; 1.6 km for each mile
		
			break;
		case R.id.year:
			place = 3;
			year.setTypeface(null, Typeface.BOLD);
			year.setBackgroundResource(R.drawable.blue_button);
			
			// update info		
			miles.setText("1,455 Miles");
			time.setText("25 Hr 57 Min");
			gas.setText("" + df.format(1455/37) + " Gallons");
			percent.setText("" + df.format(783.00/(1455+723+783)*100) + "%");
			carbon.setText("" + df.format(109.00*(1455/1.6)) + " Grams");		
			break;
		}
		
		// make the old place one normal
		if (place == oldPlace) {
			
		} else if (spans[oldPlace].equals("Year")) {
			year.setTypeface(null, Typeface.NORMAL);
			year.setBackgroundResource(R.drawable.gray_button);			
		} else if (spans[oldPlace].equals("Week")) { // week
			week.setTypeface(null, Typeface.NORMAL);
			week.setBackgroundResource(R.drawable.gray_button);
		} else if (spans[oldPlace].equals("Month")) {
			month.setTypeface(null, Typeface.NORMAL);
			month.setBackgroundResource(R.drawable.gray_button);
		} else {
			day.setTypeface(null, Typeface.NORMAL);
			day.setBackgroundResource(R.drawable.gray_button);
		}
		// update old place
		oldPlace = place;
	}
}
