package com.purpleplatypus.transportion;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FriendStatsActivity extends TransportionActivity {

	RelativeLayout barChartLayout;
	List<ChartSection> friendChartValues;
	List<ChartSection> userChartValues;
	String name;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFrameView(R.layout.activity_friend_stats);
		friendChartValues = new ArrayList<ChartSection>();
		userChartValues = new ArrayList<ChartSection>();
		
		// set title
		TextView title = (TextView) findViewById(R.id.title);		
		title.setText("Compare");

		// get intent value
		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		TextView compareWith = (TextView) findViewById(R.id.friend_compare);
		compareWith.setText("with " + name);
		
		// make chart values
		barChartLayout = (RelativeLayout) findViewById(R.id.comp_bar_chart);
		
	    userChartValues.add(new ChartSection("Car", Color.RED, 100));
	    userChartValues.add(new ChartSection("Bus", Color.YELLOW, 500));
	    userChartValues.add(new ChartSection("Bike", Color.BLUE, 50));
	    userChartValues.add(new ChartSection("Walk", Color.parseColor("#008000"), 500));	    
	    
	    friendChartValues.add(new ChartSection("Car", Color.RED, 622));
	    friendChartValues.add(new ChartSection("Bus", Color.YELLOW, 180));
	    friendChartValues.add(new ChartSection("Bike", Color.BLUE, 470));
	    friendChartValues.add(new ChartSection("Walk", Color.parseColor("#008000"), 100));	    
	   
	    View barChartView = this.makeCompBarChart(userChartValues, friendChartValues);
	    barChartLayout.addView(barChartView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	public View makeCompBarChart(List<ChartSection> userVals, List<ChartSection> friendVals) {
		CategorySeries series = new CategorySeries("You");


		for(ChartSection section : userVals) {
			series.add(section.transportationMode, section.amount);
		}
		
		CategorySeries series2 = new CategorySeries(name);
		for(ChartSection section : friendVals) {
			series2.add(section.transportationMode, section.amount);
		}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series.toXYSeries());
		dataset.addSeries(series2.toXYSeries());
		
		//customize bar 1
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setDisplayChartValues(true);
		renderer.setChartValuesSpacing((float) 0.5);
		renderer.setColor(Color.BLUE);
		
		//customize bar 2
		XYSeriesRenderer renderer2 = new XYSeriesRenderer();
		renderer2.setDisplayChartValues(true);
		renderer2.setChartValuesSpacing((float) 0.5);
		renderer2.setColor(Color.GREEN);
		
		//customization for graph itself
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.addSeriesRenderer(renderer2);

		mRenderer.setChartTitle("Demo Graph");
		mRenderer.setXTitle("Transportation Modes");
		
//		looping through and add 
		for(int i = 0; i < userVals.size(); i++) {
			ChartSection section = userVals.get(i);
			mRenderer.addXTextLabel(i+1, section.transportationMode);
		}
		mRenderer.setXLabels(0);
		mRenderer.setYLabels(0);


//		mRenderer.addXTextLabel(3, "helicopter");

		mRenderer.setYTitle("Usage");
		mRenderer.setAxisTitleTextSize((float)24.0);
		mRenderer.setLabelsTextSize((float)36.0);

		double[] range = {0.0,5.0,0,1000};
		mRenderer.setRange(range);
		mRenderer.setBarSpacing(0.5);
		
		View barChartView = ChartFactory.getBarChartView(this, dataset, mRenderer, Type.DEFAULT);
		return barChartView;
	}
	
}
