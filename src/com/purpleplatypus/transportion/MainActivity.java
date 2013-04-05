package com.purpleplatypus.transportion;

import ws.munday.slidingmenu.SlidingMenuActivity;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends SlidingMenuActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setLayoutIds(R.layout.menu, R.layout.activity_main);
		setAnimationDuration(300);
		setAnimationType(MENU_TYPE_SLIDEOVER);
		super.onCreate(savedInstanceState);
		/*
		TextView tv = (TextView) findViewById(R.id.content_content);
		
		tv.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleMenu();
			}
		});
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
