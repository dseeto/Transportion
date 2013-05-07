package com.purpleplatypus.transportion;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FriendsCompareActivity extends TransportionActivity {

	String name;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFrameView(R.layout.activity_friend_compare);
		
		// set title
		TextView title = (TextView) findViewById(R.id.title);		
		title.setText("Compare");

		// get intent value
		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		TextView compareWith = (TextView) findViewById(R.id.friend_compare);
		compareWith.setText("You VS. " + name);
		
		
		// HARD CODE:
		ProgressBar selfProg = (ProgressBar)findViewById(R.id.mode1SelfProg);
		selfProg.setProgress(45);
		ProgressBar friendProg = (ProgressBar)findViewById(R.id.mode1FriendProg);
		friendProg.setProgress(22);
		
		TextView selfVal = (TextView)findViewById(R.id.mode1SelfValue);
		selfVal.setText("45 miles");
		TextView friendVal = (TextView)findViewById(R.id.mode1FriendValue);
		friendVal.setText("22 miles");
		
		TextView friendNameCar = (TextView)findViewById(R.id.mode1FriendName);
		friendNameCar.setText(name.split(" ")[0]);
		
		TextView friendNameBus = (TextView)findViewById(R.id.textView2);
		friendNameBus.setText(name.split(" ")[0]);
		
		TextView friendNameBike = (TextView)findViewById(R.id.textView3);
		friendNameBike.setText(name.split(" ")[0]);
		
		TextView friendNameWalk = (TextView)findViewById(R.id.textView4);
		friendNameWalk.setText(name.split(" ")[0]);
		
		TextView friendNameTotal = (TextView)findViewById(R.id.textView5);
		friendNameTotal.setText(name.split(" ")[0]);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
}
