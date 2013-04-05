package com.purpleplatypus.transportion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View;

public class DrawPieChart extends View{

	Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);	
	float[] valueDegrees;
	int c[] = { Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN};

	
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
	    float curPos = -90;
	    p.setStyle(Style.FILL);
	    RectF rect = new RectF(20, 20, x - 20, y - 20);
	    // CHANGE THESE SETTINGS	    	    
	    
	    for (int i = 0; i < 4; i++) {
	    	p.setColor(c[i]);
	    	curPos = curPos + valueDegrees[i];
	    	float temp = 360 * valueDegrees[i]/4;
	    	canvas.drawArc(rect, curPos, temp, true, p);
	    }
	    
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
