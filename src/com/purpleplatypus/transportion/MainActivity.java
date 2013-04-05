package com.purpleplatypus.transportion;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	
	LinearLayout pieChartLayout;
	DrawPieChart pieChart;
	float[]chartValues = new float[4];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		pieChartLayout = (LinearLayout) findViewById(R.id.mainPieChart);
		
		//hardcode values for pie chart:
		//order: car, bus, bike, walk
	    chartValues[0] = 100;
	    chartValues[1] = 500;
	    chartValues[2] = 0;
	    chartValues[3] = 1000;
	    
	    pieChart = new DrawPieChart(this, chartValues);
	    pieChartLayout.addView(pieChart);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.car:
			
			break;
		case R.id.bus:
			
			break;
		case R.id.bike:
			
			break;
		case R.id.walk:
			
			break;
		}
	}
	
	

}
