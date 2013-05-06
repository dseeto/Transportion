
package com.purpleplatypus.transportion;

import java.util.ArrayList;
import java.util.List;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.parse.Parse;
import com.parse.ParseAnalytics;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;

import ws.munday.slidingmenu.SlidingMenuActivity;
import android.os.Bundle;

import android.app.Activity;
import android.graphics.Color;

import android.content.Intent;

import android.util.Log;

import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends TransportionActivity {

	//Spinner dropDownMenu;

	LinearLayout pieChartLayout;
	DrawPieChart pieChart;
	ApplicationState appState;
	GraphicalView pieChartView;
	
	List<ChartSection> chartValues;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFrameView(R.layout.activity_main);

		// SET UP MODEL
		appState = (ApplicationState) this.getApplication();
		appState.data.createDatabase(this);		

		// Transportion Account
		//Parse.initialize(this, "i4mqhdigRXwjs66dfZdCdMsF7fuwcIsEGoJUV0Te", "IYX3qei450z9etih7tz7dsobEaenaQmt5oJWu7QT");
		
		//Transportion2 Account
		Parse.initialize(this, "EL8WO95o0oQa9wKN1AMRfaQBmWpZNlLMXZlWFnXX", "JE5I5thI0ptOZaLNH6HdOHQdUuLRuwykIIRG9f0u"); 
		/*
		ParseObject testObject = new ParseObject("TestObject");
		testObject.put("foo", "bar");
		testObject.saveInBackground();
		*/
		// track statistics around application opens
		ParseAnalytics.trackAppOpened(getIntent());
		
		// MODEL TESTING - REMOVE
		/*
		appState.data.mDbHelper.retrievedDataAddEntry("Car", "Week", (float)123.3, (float)123.3, 123, (float)123.3, (float)123.3);
		appState.data.mDbHelper.retrievedDataGetEntry("Car", "Week"); // will print
		appState.data.mDbHelper.retrievedDataRemoveAll();
		//appState.data.mDbHelper.retrievedDataGetEntry("Car", "Week"); // this causes a problem w/ the cursor b/c its empty => FIX!!!!
		
		
		//ParseObject testObject = new ParseObject("TestObject");
		//testObject.put("foo", "bar");
		//testObject.saveInBackground();
		
		System.out.println("================= RAW DATA =================");
		
		appState.data.mDbHelper.rawDataRemoveAll();
		/*
		appState.data.mDbHelper.rawDataAddEntry(new Timestamp(new java.util.Date().getTime()), (float)11, (float)11, (float)22);
		//appState.data.mDbHelper.rawDataAddEntry(new Timestamp(new java.util.Date().getTime()), (float)100.111, (float)100.111, (float)22);
		//appState.data.mDbHelper.rawDataGetAll(); // will print
		//appState.data.mDbHelper.rawDataRemoveAll();
		//appState.data.mDbHelper.rawDataGetAll(); // will print
		try {
			appState.data.sendDataToServer();
		} catch (JSONException e) {	
			System.out.println("DIDN'T SEND!!!");
			e.printStackTrace();
		}
		
		
		appState.data.mDbHelper.rawDataAddEntry(new Timestamp(new java.util.Date().getTime()), (float)12, (float)11, (float)-1.0);
		appState.data.mDbHelper.rawDataAddEntry(new Timestamp(new java.util.Date().getTime()), (float)13, (float)11, (float)-1.0);
		appState.data.mDbHelper.rawDataAddEntry(new Timestamp(new java.util.Date().getTime()), (float)14, (float)11, (float)22);
		appState.data.mDbHelper.rawDataAddEntry(new Timestamp(new java.util.Date().getTime()), (float)15, (float)11, (float)22);
		
		try {
			appState.data.sendDataToServer();
		} catch (JSONException e) {	
			System.out.println("DIDN'T SEND!!!");
			e.printStackTrace();
		}
		*/
		// appState.data.retrieveUserDataFromServer();
		
		// MODEL TESTING - REMOVE
		/*
		// Start Tracking Location:
		Intent intent = new Intent(this, LocationService.class);
		startService(intent);
		
		// stopService(intent);
		*/
		// SET UP REST OF PAGE
		
		// NOT USED YET
		//dropDownMenu = (Spinner) findViewById(R.id.spinner1);
		// NOT USED YET
		
		TextView title = (TextView) findViewById(R.id.title);		
		title.setText("Overall Usage");
		
		pieChartLayout = (LinearLayout) findViewById(R.id.mainPieChart);
		
		//hardcode values for pie chart:
		//order: car, bus, bike, walk
		chartValues = new ArrayList<ChartSection>();
	    chartValues.add(new ChartSection("Car", Color.parseColor("#315489"), 100));
	    chartValues.add(new ChartSection("Bike", Color.parseColor("#343a41"), 50));
	    chartValues.add(new ChartSection("Bus", Color.parseColor("#6a94d4"), 500));
	    chartValues.add(new ChartSection("Walk", Color.parseColor("#00ab6f"), 500));	    
	    
//	    pieChart = new DrawPieChart(this, chartValues);
	    View pieChartView = this.makePieChart(chartValues);
	    pieChartLayout.addView(pieChartView);
	    
	}
	

	
	//TODO make sections of pie chart clickable
	public View makePieChart(List<ChartSection> chartValues) {
		CategorySeries series = new CategorySeries("Pie graph");
		DefaultRenderer renderer = new DefaultRenderer();

		for(ChartSection section : chartValues) {
			series.add(section.transportationMode, section.amount);
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(section.color);
			renderer.addSeriesRenderer(r);
		}
//		renderer.setChartTitle("Overall Usage");

//		renderer.setChartTitleTextSize(86);
		renderer.setPanEnabled(false);

		renderer.setLabelsColor(Color.BLACK);
		renderer.setLabelsTextSize(32);
		renderer.setLegendTextSize(32);
		renderer.setShowLegend(false);
//		renderer.setLegendHeight(5);
		
//		renderer.setFitLegend(true);

		renderer.setClickEnabled(true);
	    renderer.setSelectableBuffer(10);
	    
		pieChartView = ChartFactory.getPieChartView(this, series, renderer);
	    pieChartView.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	          SeriesSelection seriesSelection = pieChartView.getCurrentSeriesAndPoint();
	          if (seriesSelection == null) {
//	            Toast
//	                .makeText(MainActivity.this, "No chart element was clicked", Toast.LENGTH_SHORT)
//	                .show();
	          } else {	            
	            int chartSectIndex = seriesSelection.getPointIndex();
	            Class<?> detailsActivity = null;
	            switch (chartSectIndex) {
					case 0:
						detailsActivity = CarDetails.class;
						break;
					case 1:
						detailsActivity = BikeDetails.class;
						break;
					case 2:
						detailsActivity = BusDetails.class;
						break;
					case 3:
						detailsActivity = WalkDetails.class;
						break;
	
					default:
						break;
				}
	            
	            Intent intent = new Intent(MainActivity.this, detailsActivity);
	            startActivity(intent);
	          }
	        }
	    });
	    
		return pieChartView;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
}
