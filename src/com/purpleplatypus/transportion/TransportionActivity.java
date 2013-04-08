package com.purpleplatypus.transportion;


import java.util.ArrayList;
import java.util.Arrays;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout;

public class TransportionActivity extends SlidingMenuActivity {
	
	static final String[] SLIDING_MENU_ITEMS = new String[] {
		"Main", "Friends", "Leaderboard", "Car Details", 
		"Walk Details", "Bike Details", "Bus Details" };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setLayoutIds(R.layout.menu, R.layout.frame);
		setAnimationDuration(300);
		setAnimationType(MENU_TYPE_SLIDEOVER);
		super.onCreate(savedInstanceState);

		ImageButton menuButton = (ImageButton) findViewById(R.id.menu_button);
		
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
		HashMap<String, ArrayList<String[]>> activityPutExtras;
		
		public OnMenuItemClickListener() {
			super();
			activityClasses = new HashMap<String, Class>();
			activityClasses.put("Main", MainActivity.class);
			activityClasses.put("Friends", FriendsActivity.class);
			activityClasses.put("Car Details", Details.class);
			activityClasses.put("Walk Details", Details.class);
			activityClasses.put("Bike Details", Details.class);
			activityClasses.put("Bus Details", Details.class);
			
			activityPutExtras = new HashMap<String, ArrayList<String[]>>();
			ArrayList<String[]> putExtra = new ArrayList<String[]>();
				putExtra.add(new String[]{"Mode", "Car"});
			activityPutExtras.put("Car Details", putExtra);
			
			putExtra = new ArrayList<String[]>();
				putExtra.add(new String[]{"Mode", "Walk"});
			activityPutExtras.put("Walk Details", putExtra);
			
			putExtra = new ArrayList<String[]>();
				putExtra.add(new String[]{"Mode", "Bike"});
			activityPutExtras.put("Bike Details", putExtra);
			
			putExtra = new ArrayList<String[]>();
				putExtra.add(new String[]{"Mode", "Bus"});
			activityPutExtras.put("Bus Details", putExtra);
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String textClicked = ((TextView) view).getText().toString();		
			toggleMenu();
			
			beginNewActivity(textClicked);
		}
		
		public void beginNewActivity(String activityClass){
			// Launching new Activity on selecting single List Item
			Intent i = new Intent(getApplicationContext(), getActivityClass(activityClass));
			// sending data to new activity
			ArrayList<String[]> puts = activityPutExtras.get(activityClass);
			if (puts != null) {
				for (int j = 0; j < puts.size(); j++) {
					String[] data = puts.get(j);
					i.putExtra(data[0], data[1]);
				}
			}
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
