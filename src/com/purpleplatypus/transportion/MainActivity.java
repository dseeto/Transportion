package com.purpleplatypus.transportion;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import ws.munday.slidingmenu.SlidingMenuActivity;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends SlidingMenuActivity {

	LinearLayout pieChartLayout;
	DrawPieChart pieChart;
	ChartSection[]chartValues = new ChartSection[4];
	
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
	    chartValues[0] = new ChartSection("Car", Color.RED, 100);
	    chartValues[1] = new ChartSection("Bus", Color.YELLOW, 500);
	    chartValues[2] = new ChartSection("Bike", Color.BLUE, 50);
	    chartValues[3] = new ChartSection("Walk", Color.parseColor("#008000"), 500);	    
	    
//	    pieChart = new DrawPieChart(this, chartValues);
	    View pieChartView = this.makePieChart(chartValues);
	    pieChartLayout.addView(pieChartView);


		/*
		TextView tv = (TextView) findViewById(R.id.content_content);
		
		tv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleMenu();
			}
		});
		*/

	}
	
	public View makePieChart(ChartSection[] chartValues) {
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
	
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.car:
//			
//			break;
//		case R.id.bus:
//			
//			break;
//		case R.id.bike:
//			
//			break;
//		case R.id.walk:
//			
//			break;
//		}
//	}
	
	

}
