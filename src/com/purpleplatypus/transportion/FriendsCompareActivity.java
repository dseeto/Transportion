package com.purpleplatypus.transportion;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
		TextView friendNameCar = (TextView)findViewById(R.id.mode1FriendName);
		friendNameCar.setText(name.split(" ")[0]);
		TextView friendVal = (TextView)findViewById(R.id.mode1FriendValue);
		friendVal.setText("22 miles");
		
		TextView friendNameBus = (TextView)findViewById(R.id.textView2);
		friendNameBus.setText(name.split(" ")[0]);
		
		TextView friendNameBike = (TextView)findViewById(R.id.textView3);
		friendNameBike.setText(name.split(" ")[0]);
		
		TextView friendNameWalk = (TextView)findViewById(R.id.textView4);
		friendNameWalk.setText(name.split(" ")[0]);
		
		TextView friendNameTotal = (TextView)findViewById(R.id.textView5);
		friendNameTotal.setText(name.split(" ")[0]);
		
//		layout = (LinearLayout)findViewById(R.id.busPic);			
//		imageView1 = new ImageView(this);						
//		imageView1.setImageResource(R.drawable.menu_bus);			
//		layout.addView(imageView1);
//		
//		selfProg = (ProgressBar)findViewById(R.id.mode2SelfProg);
//		selfProg.setProgress(100);
//		friendProg = (ProgressBar)findViewById(R.id.mode2FriendProg);
//		friendProg.setProgress(50);
//		
//		selfVal = (TextView)findViewById(R.id.mode2SelfValue);
//		selfVal.setText("You bused 26 miles.");
//		friendVal = (TextView)findViewById(R.id.mode2FriendValue);
//		friendVal.setText(name.split(" ")[0] + " bused 13 miles.");
//		
//		
//		layout = (LinearLayout)findViewById(R.id.bikePic);			
//		imageView1 = new ImageView(this);						
//		imageView1.setImageResource(R.drawable.menu_bike);			
//		layout.addView(imageView1);
//		
//		selfProg = (ProgressBar)findViewById(R.id.mode3SelfProg);
//		selfProg.setProgress(100);
//		friendProg = (ProgressBar)findViewById(R.id.mode3FriendProg);
//		friendProg.setProgress(50);
//		
//		selfVal = (TextView)findViewById(R.id.mode3SelfValue);
//		selfVal.setText("You biked 26 miles.");
//		friendVal = (TextView)findViewById(R.id.mode3FriendValue);
//		friendVal.setText(name.split(" ")[0] + " biked 13 miles.");
//		
//		
//		layout = (LinearLayout)findViewById(R.id.wPic);			
//		imageView1 = new ImageView(this);						
//		imageView1.setImageResource(R.drawable.menu_walk);			
//		layout.addView(imageView1);
//		
//		selfProg = (ProgressBar)findViewById(R.id.mode4SelfProg);
//		selfProg.setProgress(100);
//		friendProg = (ProgressBar)findViewById(R.id.mode4FriendProg);
//		friendProg.setProgress(50);
//		
//		selfVal = (TextView)findViewById(R.id.mode4SelfValue);
//		selfVal.setText("You walked 26 miles.");
//		friendVal = (TextView)findViewById(R.id.mode4FriendValue);
//		friendVal.setText(name.split(" ")[0] + " walked 13 miles.");
//		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
}
