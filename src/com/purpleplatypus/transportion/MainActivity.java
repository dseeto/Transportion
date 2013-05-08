
package com.purpleplatypus.transportion;

import java.sql.Timestamp;
import java.util.ArrayList;

import java.util.Hashtable;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import org.json.JSONArray;

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

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseAnalytics;

public class MainActivity extends TransportionActivity implements OnItemSelectedListener{

	Spinner dropDownMenu;

	ApplicationState appState;
	
	//For the Graph
	LinearLayout chartLayout;
	GraphicalView pieChartView;
	List<ChartSection> chartValues;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFrameView(R.layout.activity_main);

		// SET UP MODEL
		appState = (ApplicationState) this.getApplication();
		System.out.println("CREATE DATABASE:");
		appState.data.createDatabase(this);	
		appState.data.mDbHelper.cleanTable();
		
//		appState.data.mDbHelper.rawDataRemoveAll();
//		Date date = new java.util.Date();
//		int year = date.getYear();
//		int month = date.getMonth();
//		int day = date.getDate();
//		Timestamp today = new Timestamp(new java.util.Date(year, month, day).getTime());
//		appState.data.mDbHelper.updateEntry(today, "walk", 153, 52);
//		appState.data.mDbHelper.updateEntry(today, "walk", 7, 8);
//		
//		Timestamp oldDay = new Timestamp(new java.util.Date(year-1, month, day+1).getTime());
//		appState.data.mDbHelper.updateEntry(oldDay, "walk", 100, 10);
//		appState.data.mDbHelper.rawDataGetAll();
//		
//		appState.data.mDbHelper.cleanTable();
//		System.out.println("Cleaned the table!");
//		appState.data.mDbHelper.rawDataGetAll();
		
		//Transportion2 Account
		Parse.initialize(this, "EL8WO95o0oQa9wKN1AMRfaQBmWpZNlLMXZlWFnXX", "JE5I5thI0ptOZaLNH6HdOHQdUuLRuwykIIRG9f0u"); 

		// track statistics around application opens
		ParseAnalytics.trackAppOpened(getIntent());	
		
		// Start Tracking Location:
		Intent intent = new Intent(this, LocationService.class);
		startService(intent);
		
		// Set drop down menu and set frame with the right graph.
		dropDownMenu = (Spinner) findViewById(R.id.spinner1);
		dropDownMenu.setOnItemSelectedListener(this);
		
		TextView title = (TextView) findViewById(R.id.title);		
		title.setText("Overall Usage");
		
		LinearLayout pieChartLayout = (LinearLayout) findViewById(R.id.mainChart);
		
		// HARD CODE:
		//order: car, bus, bike, walk
		chartValues = new ArrayList<ChartSection>();
		chartValues.add(new ChartSection("Car", Color.parseColor("#315489"), 100));
		chartValues.add(new ChartSection("Bike", Color.parseColor("#343a41"), 50));
		chartValues.add(new ChartSection("Bus", Color.parseColor("#6a94d4"), 500));
		chartValues.add(new ChartSection("Walk", Color.parseColor("#00ab6f"), 500));	    

		View pieChartView = this.makePieChart(chartValues);
		pieChartLayout.addView(pieChartView);
		
	}
		
	//TODO make sections of pie chart clickable

	public View makePieChart(List<ChartSection> chartValues) {
		CategorySeries series = new CategorySeries("Pie graph");
		DefaultRenderer renderer = new DefaultRenderer();

		renderer.setPanEnabled(false);
		renderer.setShowLegend(false);

		renderer.setLabelsColor(Color.BLACK);
		renderer.setLabelsTextSize(32);
		renderer.setLegendTextSize(32);

		renderer.setClickEnabled(true);
	    renderer.setSelectableBuffer(10);
	    
		for(ChartSection section : chartValues) {
			series.add(section.transportationMode, section.amount);
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(section.color);
			renderer.addSeriesRenderer(r);
		}
	    
		pieChartView = ChartFactory.getPieChartView(this, series, renderer);
	    pieChartView.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	          SeriesSelection seriesSelection = pieChartView.getCurrentSeriesAndPoint();
	          if (seriesSelection == null) {
	            Toast.makeText(MainActivity.this, "No chart element was clicked", Toast.LENGTH_SHORT).show();
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
	
	/*
	 * For Clicking on legend.
	 */
	public void onClick(View view) {
		int v = view.getId();
		Class<?> detailsActivity = null;
		switch(v) {
		case R.id.car:
			detailsActivity = CarDetails.class;
			break;
		case R.id.bus:
			detailsActivity = BusDetails.class;
			break;
		case R.id.bike:
			detailsActivity = BikeDetails.class;
			break;
		case R.id.walk:
			detailsActivity = WalkDetails.class;
			break;
		default:
			System.out.println("DID NOT REGISTER THE RIGHT THING!!");
			break;
		}
		Intent intent = new Intent(MainActivity.this, detailsActivity);
        startActivity(intent);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long itemSelected) {
		chartLayout = (LinearLayout) findViewById(R.id.mainChart);
	    chartLayout.removeAllViews();
		View pieChartView;
		View legend = (LinearLayout) findViewById(R.id.legend);
		switch((int)itemSelected) {
		case 0:	
			//hardcode values for pie chart:
			//order: car, bus, bike, walk
			chartValues = new ArrayList<ChartSection>();
		    chartValues.add(new ChartSection("Car", Color.parseColor("#315489"), 100));
		    chartValues.add(new ChartSection("Bike", Color.parseColor("#343a41"), 50));
		    chartValues.add(new ChartSection("Bus", Color.parseColor("#6a94d4"), 500));
		    chartValues.add(new ChartSection("Walk", Color.parseColor("#00ab6f"), 20));	    
		    
		    pieChartView = this.makePieChart(chartValues);
		    chartLayout.addView(pieChartView);
			legend.setVisibility(View.VISIBLE);
		    break;
		case 1:
			chartValues = new ArrayList<ChartSection>();
		    chartValues.add(new ChartSection("Car", Color.parseColor("#315489"), 50));
		    chartValues.add(new ChartSection("Bike", Color.parseColor("#343a41"), 80));
		    chartValues.add(new ChartSection("Bus", Color.parseColor("#6a94d4"), 200));
		    chartValues.add(new ChartSection("Walk", Color.parseColor("#00ab6f"), 20));	    
		    
		    pieChartView = this.makePieChart(chartValues);
		    chartLayout.addView(pieChartView);
			legend.setVisibility(View.VISIBLE);
		    break;
		case 2:
			LayoutInflater factory = LayoutInflater.from(this);
			View myView = factory.inflate(R.layout.activity_friends, null);
			chartLayout.addView(myView);
			
			legend.setVisibility(View.INVISIBLE);
			break;
		default:
			System.out.println("Drop down menu selected out of bounds item.");
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		Toast.makeText(this, "Nothing Selected.", Toast.LENGTH_LONG).show();
		
	}
	
	
}
