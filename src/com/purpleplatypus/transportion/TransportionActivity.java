package com.purpleplatypus.transportion;


import java.util.HashMap;

import ws.munday.slidingmenu.SlidingMenuActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout;

public class TransportionActivity extends SlidingMenuActivity {
	
	static final String[] SLIDING_MENU_ITEMS = new String[] {
		"Main", "Friends", "Leaderboard", "Car Details", 
		"Walk Details", "Bike Details", "Public Transit Details" };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setLayoutIds(R.layout.menu, R.layout.frame);
		setAnimationDuration(300);
		setAnimationType(MENU_TYPE_SLIDEOVER);
		super.onCreate(savedInstanceState);

		Button menuButton = (Button) findViewById(R.id.menu_button);
		
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleMenu();
			}
		});
		
		ListView menuListView = (ListView) findViewById(R.id.menuListView);
		menuListView.setAdapter(new ArrayAdapter<String>(this, R.layout.menu_text, SLIDING_MENU_ITEMS));
		
		menuListView.setOnItemClickListener(new OnMenuItemClickListener());
	}
	
	public void setFrameView(int viewID) {
		LayoutInflater factory = LayoutInflater.from(this);
		View myView = factory.inflate(viewID, null);
		
		RelativeLayout frameView = (RelativeLayout) findViewById(R.id.frameView);
		frameView.addView(myView);
	}
	
	public class OnMenuItemClickListener implements OnItemClickListener {
		
		HashMap<String, Class> activityClasses;
		
		public OnMenuItemClickListener() {
			super();
			activityClasses = new HashMap<String, Class>();
			activityClasses.put("Main", MainActivity.class);
			activityClasses.put("Friends", FriendsActivity.class);
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String textClicked = ((TextView) view).getText().toString();
			
			TextView testView = (TextView) findViewById(R.id.testTextView);
			testView.setText(textClicked);
			
			toggleMenu();

			// Launching new Activity on selecting single List Item
			Intent i = new Intent(getApplicationContext(), getActivityClass(textClicked));
			// sending data to new activity
			// i.putExtra("product", product);
			startActivity(i);
		}
		
		public Class getActivityClass(String name) {
			if (activityClasses.containsKey(name)) {
				return activityClasses.get(name);
			}
			return FriendsActivity.class;
		}
	}
	
}
