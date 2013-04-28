package com.purpleplatypus.transportion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View;

public class DrawPieChart extends View{

	Paint p = new Paint(Paint.ANTI_ALIAS_FLAG); // for rect?!?!? Yes

	
	ChartSection[] chartSections;
	
	public DrawPieChart(Context context, ChartSection[] data) {
		super(context);
		chartSections = data;
	}	
	
	public void draw(Canvas canvas) {
	    int x = getWidth();
	   
	    float total = chartSections[0].amount + chartSections[1].amount + chartSections[2].amount + chartSections[3].amount; 
	    p.setColor(Color.parseColor("#FFFFFF"));//#78777D"));
	    p.setStyle(Style.STROKE);
	    p.setStrokeWidth(2);
	    canvas.drawRect(0, 0, x - 1, x - 1, p);	
	    
	    p.setStyle(Style.FILL);
	    
	    float curPos = -90;
	    RectF rect = new RectF(30, 30, x - 30, x - 30);	    	    
	    for (int i = 0; i < 4; i++) {
	    	ChartSection section = chartSections[i];
		    float theta = (total == 0) ? 0 : 360 * section.amount / total;
	    	p.setColor(section.color);
	        canvas.drawArc(rect, curPos, theta, true, p);
	    	curPos = curPos + theta;
	    }
	}

	
	
}
