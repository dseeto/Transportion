package com.purpleplatypus.transportion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View;

public class DrawPieChart extends View{

	Paint p = new Paint(Paint.ANTI_ALIAS_FLAG); // for rect?!?!?
	Paint p0 = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint p1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint p2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint p3 = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	float[] valueDegrees;
	
	int c[] = { Color.RED, Color.YELLOW, Color.BLUE, Color.parseColor("#008000")};

	
	public DrawPieChart(Context context, float[] data) {
		super(context);
		// hardcode the piechart:
		valueDegrees = data;
	}	
	
	public void draw(Canvas canvas) {
	    int x = getWidth();
	    int y = getHeight();	    
	    // CHANGE THESE SETTINGS
	    p.setColor(Color.parseColor("#78777D"));
	    p.setStyle(Style.STROKE);
	    p.setStrokeWidth(2);
	    canvas.drawRect(0, 0, x - 1, y - 1, p);	    	    	    
	    p.setStyle(Style.FILL);
	    
	    float curPos = -90;
	    RectF rect = new RectF(30, 30, x - 30, y - 30);
	    // CHANGE THESE SETTINGS	    	    
	    
	    float temp;
	    // car
	    p0.setColor(c[0]);
	    curPos = curPos + valueDegrees[0];
    	temp = 360 * valueDegrees[0]/4;
    	canvas.drawArc(rect, curPos, temp, true, p0);
    	curPos = curPos + valueDegrees[0];
    	// bus
    	p1.setColor(c[1]);
	    curPos = curPos + valueDegrees[1];
    	temp = 360 * valueDegrees[1]/4;
    	canvas.drawArc(rect, curPos, temp, true, p1);
    	curPos = curPos + valueDegrees[1];
    	// bike
    	p2.setColor(c[2]);
	    curPos = curPos + valueDegrees[2];
    	temp = 360 * valueDegrees[2]/4;
    	canvas.drawArc(rect, curPos, temp, true, p2);
    	curPos = curPos + valueDegrees[2];
    	// walk
    	p3.setColor(c[3]);
	    curPos = curPos + valueDegrees[3];
    	temp = 360 * valueDegrees[3]/4;
    	canvas.drawArc(rect, curPos, temp, true, p3);
    	curPos = curPos + valueDegrees[3];    	
	    
	    /* FOR REFERENCE:	   
	    for (int i = 0; i < 4; i++) {
	        p.setColor(c[i]);
	        float thita = (t == 0) ? 0 : 360 * aList.get(i) / t;
	        canvas.drawArc(rect, curPos, thita, true, p);
	        curPos = curPos + thita;
	    }
	    */
	}

	
}
