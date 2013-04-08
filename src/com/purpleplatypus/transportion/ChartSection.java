package com.purpleplatypus.transportion;

import android.graphics.Color;

public class ChartSection {
	String transportationMode;
	int color;
	float amount;
	
	public ChartSection(String transportationMode, int c, float amount) {
		super();
		this.transportationMode = transportationMode;
		this.color = c;
		this.amount = amount;
	}	
	
}
