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

public class Details extends Activity {

	// pass in which mode!!!
	// floats to 2 decimal places!!
	// need a different pic and color scheme!!!	
	
	String mode;
	String[] modes = {"Car", "Bus", "Car", "Walk"};	
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
	TextView gas;
	TextView percent;
	TextView carbon;
		
	TextView title;
	LinearLayout pic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		day = (TextView) findViewById(R.id.day);
		week = (TextView) findViewById(R.id.week);
		month = (TextView) findViewById(R.id.month);
		year = (TextView) findViewById(R.id.year);
		
		miles = (TextView) findViewById(R.id.miles);
		gas = (TextView) findViewById(R.id.gas);
		percent = (TextView) findViewById(R.id.percent);
		carbon = (TextView) findViewById(R.id.carbon);
		
		title = (TextView) findViewById(R.id.title);
		
		// IMPELEMENT: get sent data: which mode and then set the mode!!!!!!
		Bundle extras = getIntent().getExtras();
		mode = extras.getString("Mode");
		
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);	
		
		// default is month
		if (mode.equals("Car")) {			
			miles.setText("Miles Travelled: 100 Miles");
			gas.setText("Gas Used: " + df.format(100.00/37.00) + "Gallons");
			percent.setText("% of Travelled: " + df.format((float)100.00/(100+71+87)*100) + "%");
			carbon.setText("Carbon Emitted: " + df.format(109.00*(100/1.6)) + " Grams"); // 109 per km; 1.6 km for each mile
			
			// also set background color
			miles.setBackgroundColor(Color.parseColor("#ff4e50"));
			gas.setBackgroundColor(Color.parseColor("#ff4e50"));
			percent.setBackgroundColor(Color.parseColor("#ff4e50"));
			carbon.setBackgroundColor(Color.parseColor("#ff4e50"));	
			
			// set pic
			LinearLayout layout = (LinearLayout)findViewById(R.id.pic);			
			ImageView imageView1 = new ImageView(this);			
			// not sure what this is
			//imageView1.setId(j+1);			
			imageView1.setImageResource(R.drawable.car);			
			layout.addView(imageView1);
		} else if (mode.equals("Bus")) {
			// set text
			title.setText("Bus Details");
			
			miles.setText("Miles Travelled: 71 Miles");
			gas.setText("Gas Used: " + df.format(71/6) + " Gallons"); // 6 miles per gallon
			percent.setText("% of Travelled: " + df.format(71.00/(100+71+87)*100) + "%");
			carbon.setText("Carbon Emitted: " + df.format(140*71) + " Grams");
			
			// also set background color
			miles.setBackgroundColor(Color.parseColor("#ffff73"));
			gas.setBackgroundColor(Color.parseColor("#ffff73"));
			percent.setBackgroundColor(Color.parseColor("#ffff73"));
			carbon.setBackgroundColor(Color.parseColor("#ffff73"));
			
			// set pic
			LinearLayout layout = (LinearLayout)findViewById(R.id.pic);			
			ImageView imageView1 = new ImageView(this);			
			// not sure what this is
			//imageView1.setId(j+1);			
			imageView1.setImageResource(R.drawable.bus);			
			layout.addView(imageView1);

		} else if (mode.equals("Bike")) {
			// set text
			title.setText("Bike Details");
			miles.setText("Miles Travelled: 0 Miles");
			gas.setText("Gas Used: 0 Gallons");
			percent.setText("% of Travelled: 0%");
			carbon.setText("Carbon Emitted: 0 Grams");
			
			// also set background color
			miles.setBackgroundColor(Color.parseColor("#4869d6"));
			gas.setBackgroundColor(Color.parseColor("#4869d6"));
			percent.setBackgroundColor(Color.parseColor("#4869d6"));
			carbon.setBackgroundColor(Color.parseColor("#4869d6"));
			
			// set pic
			LinearLayout layout = (LinearLayout)findViewById(R.id.pic);			
			ImageView imageView1 = new ImageView(this);			
			// not sure what this is
			//imageView1.setId(j+1);			
			imageView1.setImageResource(R.drawable.bike);			
			layout.addView(imageView1);
		} else {
			// set text
			title.setText("Walk Details");
			miles.setText("Miles Travelled: 87 Miles");
			gas.setText("Gas Used: 0 Gallons");
			percent.setText("% of Travelled: " + df.format(87.00/(100+71+87)*100) + "%");
			carbon.setText("Carbon Emitted: 0 Grams");
			
			// also set background color
			miles.setBackgroundColor(Color.parseColor("#66e275"));
			gas.setBackgroundColor(Color.parseColor("#66e275"));
			percent.setBackgroundColor(Color.parseColor("#66e275"));
			carbon.setBackgroundColor(Color.parseColor("#66e275"));
			
			// set pic
			LinearLayout layout = (LinearLayout)findViewById(R.id.pic);			
			ImageView imageView1 = new ImageView(this);			
			// not sure what this is
			//imageView1.setId(j+1);			
			imageView1.setImageResource(R.drawable.walk);			
			layout.addView(imageView1);
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_details, menu);
		return true;
	}
	
	public void onClick(View v) {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);		
		
		switch (v.getId()) {
		case R.id.left:
			place = (place - 1) % 4;			
			break;
		case R.id.right:
			place = (place + 1) % 4;
			break;
		}
		switch (place) {
		case 0: // day
			day.setTypeface(null, Typeface.BOLD);
			day.setBackgroundColor(Color.WHITE);
			
			// update info
			if (mode.equals("Car")) {
				miles.setText("Miles Travelled: 0 Miles");
				gas.setText("Gas Used: 0 Gallons");
				percent.setText("% of Travelled: 0%");
				carbon.setText("Carbon Emitted: 0 Grams");
			} else if (mode.equals("Bus")) {
				miles.setText("Miles Travelled: 2 Miles");
				gas.setText("Gas Used: 0.33 Gallons");
				percent.setText("% of Travelled: 34%"); // in terms of miles
				carbon.setText("Carbon Emitted: 280 Grams");
			} else if (mode.equals("Bike")) {
				miles.setText("Miles Travelled: 0 Miles");
				gas.setText("Gas Used: 0 Gallons");
				percent.setText("% of Travelled: 0%");
				carbon.setText("Carbon Emitted: 0 Grams");
			} else {
				miles.setText("Miles Travelled: 4 Miles");
				gas.setText("Gas Used: 0 Gallons");
				percent.setText("% of Travelled: 66%");
				carbon.setText("Carbon Emitted: 0 Grams");
			}						
			
			// make the old place one normal
			if (spans[oldPlace].equals("Year")) {
				year.setTypeface(null, Typeface.NORMAL);
				year.setBackgroundColor(Color.TRANSPARENT);
			} else { // week
				week.setTypeface(null, Typeface.NORMAL);
				week.setBackgroundColor(Color.TRANSPARENT);
			}
			// update old place
			oldPlace = place;
			break;
		case 1: // week
			week.setTypeface(null, Typeface.BOLD);
			week.setBackgroundColor(Color.WHITE);	
			
			// update info
			if (mode.equals("Car")) {
				miles.setText("Miles Travelled: 0 Miles");
				gas.setText("Gas Used: 0 Gallons");
				percent.setText("% of Travelled: 0%");
				carbon.setText("Carbon Emitted: 0 Grams");
			} else if (mode.equals("Bus")) {
				miles.setText("Miles Travelled: 20 Miles");
				gas.setText("Gas Used: 3.33 Gallons");
				percent.setText("% of Travelled: " + df.format(20.00/(45)*100) + "%");
				carbon.setText("Carbon Emitted: " + 140*20 + " Grams"); // 140 grams per mile
			} else if (mode.equals("Bike")) {
				miles.setText("Miles Travelled: 0 Miles");
				gas.setText("Gas Used: 0 Gallons");
				percent.setText("% of Travelled: 0%");
				carbon.setText("Carbon Emitted: 0 Grams");
			} else {
				miles.setText("Miles Travelled: 25 Miles");
				gas.setText("Gas Used: 0 Gallons");
				percent.setText("% of Travelled: " + df.format(25.00/45*100) + "%");
				carbon.setText("Carbon Emitted: 0 Grams");
			}
			
			// make the old place one normal
			if (spans[oldPlace].equals("Day")) {
				day.setTypeface(null, Typeface.NORMAL);
				day.setBackgroundColor(Color.TRANSPARENT);				
			} else { // month
				month.setTypeface(null, Typeface.NORMAL);
				month.setBackgroundColor(Color.TRANSPARENT);
			}
			// update old place
			oldPlace = place;
			break;
		case 2: // month
			month.setTypeface(null, Typeface.BOLD);
			month.setBackgroundColor(Color.WHITE);	
			
			// update info
			if (mode.equals("Car")) {
				miles.setText("Miles Travelled: 100 Miles");
				gas.setText("Gas Used: " + df.format(100/37) + " Gallons");
				percent.setText("% of Travelled: " + df.format(100.00/(100+71+87)*100) + "%");
				carbon.setText("Carbon Emitted: " + df.format(109.00*(100/1.6)) + " Grams"); // 109 per km; 1.6 km for each mile
			} else if (mode.equals("Bus")) {
				miles.setText("Miles Travelled: 71 Miles");
				gas.setText("Gas Used: " + df.format(71.00/6) + " Gallons"); // 6 miles per gallon
				percent.setText("% of Travelled: " + df.format(71.00/(100+71+87)*100) + "%");
				carbon.setText("Carbon Emitted: " + 140*71 + " Grams");
			} else if (mode.equals("Bike")) {
				miles.setText("Miles Travelled: 0 Miles");
				gas.setText("Gas Used: 0 Gallons");
				percent.setText("% of Travelled: 0%");
				carbon.setText("Carbon Emitted: 0 Grams");
			} else {
				miles.setText("Miles Travelled: 87 Miles");
				gas.setText("Gas Used: 0 Gallons");
				percent.setText("% of Travelled: " + df.format(87.00/(100+71+87)*100) + "%");
				carbon.setText("Carbon Emitted: 0 Grams");
			}		
			
			// make the old place one normal
			if (spans[oldPlace].equals("Week")) {
				week.setTypeface(null, Typeface.NORMAL);
				week.setBackgroundColor(Color.TRANSPARENT);
			} else { // year
				year.setTypeface(null, Typeface.NORMAL);
				year.setBackgroundColor(Color.TRANSPARENT);
			}
			// update old place
			oldPlace = place;
			break;
		case 3: // year
			year.setTypeface(null, Typeface.BOLD);
			year.setBackgroundColor(Color.WHITE);	
			
			// update info
			if (mode.equals("Car")) {
				miles.setText("Miles Travelled: 1455 Miles");
				gas.setText("Gas Used: " + df.format(1455/37) + " Gallons");
				percent.setText("% of Travelled: " + df.format(783.00/(1455+723+783)*100) + "%");
				carbon.setText("Carbon Emitted: " + df.format(109.00*(1455/1.6)) + " Grams");
			} else if (mode.equals("Bus")) {
				miles.setText("Miles Travelled: 0 Miles");
				gas.setText("Gas Used: 0 Gallons");
				percent.setText("% of Travelled: 0%");
				carbon.setText("Carbon Emitted: 0 Grams");
			} else if (mode.equals("Bike")) {
				miles.setText("Miles Travelled: 723 Miles");
				gas.setText("Gas Used: " + df.format(723.00/6) + " Gallons");
				percent.setText("% of Travelled: " + df.format(723.00/(1455+723+783)*100) + "%");
				carbon.setText("Carbon Emitted: " + df.format(140*723) + " Grams");
			} else {
				miles.setText("Miles Travelled: 783 Miles");
				gas.setText("Gas Used: 0 Gallons");
				percent.setText("% of Travelled: " + df.format(783.00/(1455+723+783)) + "%");
				carbon.setText("Carbon Emitted: 0 Grams");
			}
			
			// make the old place one normal
			if (spans[oldPlace].equals("Month")) {
				month.setTypeface(null, Typeface.NORMAL);
				month.setBackgroundColor(Color.TRANSPARENT);
			} else { // day
				day.setTypeface(null, Typeface.NORMAL);
				day.setBackgroundColor(Color.TRANSPARENT);
			}
			// update old place
			oldPlace = place;
			break;		
		}
	}

}
