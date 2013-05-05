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

public class WalkDetails extends TransportionActivity {

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
	
	DecimalFormat df; 
	
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
		// set text
		title.setText("Walk Details");
		
		miles.setText("87 Miles");
		time.setText("37 Hrs 23 Min");
		gas.setText("0 Gallons");
		percent.setText("" + df.format(87.00/(100+71+87)*100) + "%");
		carbon.setText("0 Grams");
		/*
		// also set background color
		miles.setBackgroundResource(Color.parseColor("#24913c"));
		time.setBackgroundResource(Color.parseColor("#24913c"));
		gas.setBackgroundResource(Color.parseColor("#24913c"));
		percent.setBackgroundResource(Color.parseColor("#24913c"));
		carbon.setBackgroundResource(Color.parseColor("#24913c"));
		*/
		// set pic
		LinearLayout layout = (LinearLayout)findViewById(R.id.pic);			
		ImageView imageView1 = new ImageView(this);			
		// not sure what this is
		//imageView1.setId(j+1);			
		imageView1.setImageResource(R.drawable.walk);			
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
			
			miles.setText("4 Miles");
			time.setText("37 Min");
			gas.setText("0 Gallons");
			percent.setText("66%");
			carbon.setText("0 Grams");

			break;
		case R.id.week:
			place = 1;
			week.setTypeface(null, Typeface.BOLD);
			week.setBackgroundResource(R.drawable.blue_button);
			
			miles.setText("25 Miles");
			time.setText("2 Hrs 18 Min");
			gas.setText("0 Gallons");
			percent.setText("" + df.format(25.00/45*100) + "%");
			carbon.setText("0 Grams");
		
			break;
		case R.id.month:
			place = 2;
			month.setTypeface(null, Typeface.BOLD);
			month.setBackgroundResource(R.drawable.blue_button);
			
			miles.setText("87 Miles");
			time.setText("37 Hr 23 Min");
			gas.setText("0 Gallons");
			percent.setText("" + df.format(87.00/(100+71+87)*100) + "%");
			carbon.setText("0 Grams");
		
			break;
		case R.id.year:
			place = 3;
			year.setTypeface(null, Typeface.BOLD);
			year.setBackgroundResource(R.drawable.blue_button);
			
			miles.setText("783 Miles");
			time.setText("302 Hr 27 Min");
			gas.setText("0 Gallons");
			percent.setText("" + df.format(783.00/(1455+723+783)*100) + "%");
			carbon.setText("0 Grams");
		
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

