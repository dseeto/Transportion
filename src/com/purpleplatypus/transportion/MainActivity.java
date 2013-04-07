package com.purpleplatypus.transportion;

import ws.munday.slidingmenu.SlidingMenuActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends SlidingMenuActivity {

	LinearLayout pieChartLayout;
	DrawPieChart pieChart;
	float[]chartValues = new float[4];
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setLayoutIds(R.layout.menu, R.layout.activity_main);
		setAnimationDuration(300);
		setAnimationType(MENU_TYPE_SLIDEOVER);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		pieChartLayout = (LinearLayout) findViewById(R.id.mainPieChart);
		
		//hardcode values for pie chart:
		//order: car, bus, bike, walk
	    chartValues[0] = 100;
	    chartValues[1] = 500;
	    chartValues[2] = 0;
	    chartValues[3] = 500;
	    
	    pieChart = new DrawPieChart(this, chartValues);
	    pieChartLayout.addView(pieChart);


		/*
		TextView tv = (TextView) findViewById(R.id.content_content);
		
		tv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleMenu();
			}
		});
		*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onClick(View v) {
		Intent i = new Intent(getApplicationContext(), Details.class);
		switch (v.getId()) {
		case R.id.car:						
			i.putExtra("Mode", "Car");			
			startActivity(i);
			this.finish(); // finish or keep running?!?!
			break;
		case R.id.bus:					
			i.putExtra("Mode", "Bus");			
			startActivity(i);
			this.finish();
			break;
		case R.id.bike:					
			i.putExtra("Mode", "Bike");			
			startActivity(i);
			this.finish();
			break;
		case R.id.walk:					
			i.putExtra("Mode", "Walk");			
			startActivity(i);
			this.finish();
			break;
		}
	}
	
	

}
