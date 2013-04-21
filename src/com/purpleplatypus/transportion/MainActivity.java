
package com.purpleplatypus.transportion;



import java.util.ArrayList;
import java.util.List;


import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.json.JSONException;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends TransportionActivity {


	RelativeLayout pieChartLayout;
	DrawPieChart pieChart;
	ChartSection[]chartValues = new ChartSection[4];
	ApplicationState appState;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFrameView(R.layout.activity_main);

		// SET UP MODEL
		//appState = (ApplicationState) this.getApplication();
		//appState.data.createDatabase(this);		

		Parse.initialize(this, "i4mqhdigRXwjs66dfZdCdMsF7fuwcIsEGoJUV0Te", "IYX3qei450z9etih7tz7dsobEaenaQmt5oJWu7QT");
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
		
		appState.data.mDbHelper.rawDataAddEntry(new Timestamp(new java.util.Date().getTime()), (float)123.090, (float)134.234);
		//appState.data.mDbHelper.rawDataAddEntry(new Timestamp(new java.util.Date().getTime()), (float)100.111, (float)100.111);
		//appState.data.mDbHelper.rawDataGetAll(); // will print
		//appState.data.mDbHelper.rawDataRemoveAll();
		//appState.data.mDbHelper.rawDataGetAll(); // will print
		try {
			appState.data.sendDataToServer();
		} catch (JSONException e) {	
			System.out.println("DIDN'T SEND!!!");
			e.printStackTrace();
		}
		
		
		appState.data.mDbHelper.rawDataAddEntry(new Timestamp(new java.util.Date().getTime()), (float)111.111, (float)111.111);
		appState.data.mDbHelper.rawDataAddEntry(new Timestamp(new java.util.Date().getTime()), (float)121.111, (float)111.111);
		appState.data.mDbHelper.rawDataAddEntry(new Timestamp(new java.util.Date().getTime()), (float)131.111, (float)111.111);
		appState.data.mDbHelper.rawDataAddEntry(new Timestamp(new java.util.Date().getTime()), (float)141.111, (float)111.111);
		try {
			appState.data.sendDataToServer();
		} catch (JSONException e) {	
			System.out.println("DIDN'T SEND!!!");
			e.printStackTrace();
		}
		
		// appState.data.retrieveUserDataFromServer();
		*/
		// MODEL TESTING - REMOVE
		
		
		// SET UP REST OF PAGE
		TextView title = (TextView) findViewById(R.id.title);		
		title.setText("Overall Usage");
		
		pieChartLayout = (RelativeLayout) findViewById(R.id.mainPieChart);
		
		//hardcode values for pie chart:
		//order: car, bus, bike, walk
		List<ChartSection> chartValues = new ArrayList<ChartSection>();
	    chartValues.add(new ChartSection("Car", Color.RED, 100));
	    chartValues.add(new ChartSection("Bus", Color.YELLOW, 500));
	    chartValues.add(new ChartSection("Bike", Color.BLUE, 50));
	    chartValues.add(new ChartSection("Walk", Color.parseColor("#008000"), 500));	    
	    
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
//		renderer.setLegendHeight(5);
		
//		renderer.setFitLegend(true);


		View pieChartView = ChartFactory.getPieChartView(this, series, renderer);
		return pieChartView;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
