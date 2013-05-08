package com.purpleplatypus.transportion;

import java.util.ArrayList;

import com.parse.ParseObject;

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
		
				
		// CAR
		int myCarMiles = (new Double(m.getStat("car", "month", "distance"))).intValue();
		//me
		ProgressBar selfProg = (ProgressBar)findViewById(R.id.carSelfProg);
		selfProg.setProgress(myCarMiles);
		TextView selfVal = (TextView)findViewById(R.id.carSelfValue);
		selfVal.setText(myCarMiles + " miles");
		//friend
		/*
		// HARD CODE
		ProgressBar friendProg = (ProgressBar)findViewById(R.id.carFriendProg);
		friendProg.setProgress(22);
		TextView friendVal = (TextView)findViewById(R.id.carFriendValue);
		friendVal.setText("22 miles");
		TextView friendNameCar = (TextView)findViewById(R.id.carFriendName);
		friendNameCar.setText(name.split(" ")[0]);
		*/
		// Connection:
		ProgressBar friendProg = (ProgressBar)findViewById(R.id.carFriendProg);
		ArrayList<ParseObject> fl = (ArrayList<ParseObject>) ApplicationState.getModel().friendsList;
		
		
		//BUS
		//me
		int myBusMiles = (new Double(m.getStat("bus", "month", "distance"))).intValue();
		selfProg = (ProgressBar)findViewById(R.id.busSelfProg);
		selfProg.setProgress(myBusMiles);
		selfVal = (TextView)findViewById(R.id.busSelfValue);
		selfVal.setText(myBusMiles + " miles");
		//friend
		TextView friendNameBus = (TextView)findViewById(R.id.busFriendName);
		friendNameBus.setText(name.split(" ")[0]);
		
		
		//BIKE
		//me
		int myBikeMiles = (new Double(m.getStat("bike", "month", "distance"))).intValue();
		selfProg = (ProgressBar)findViewById(R.id.bikeSelfProg);
		selfProg.setProgress(myBikeMiles);
		selfVal = (TextView)findViewById(R.id.bikeSelfValue);
		selfVal.setText(myBikeMiles + " miles");
		//friend
		TextView friendNameBike = (TextView)findViewById(R.id.bikeFriendName);
		friendNameBike.setText(name.split(" ")[0]);
		
		//WALK
		//me
		int myWalkMiles = (new Double(m.getStat("walk", "month", "distance"))).intValue();
		selfProg = (ProgressBar)findViewById(R.id.walkSelfProg);
		selfProg.setProgress(myWalkMiles);
		selfVal = (TextView)findViewById(R.id.walkSelfValue);
		selfVal.setText(myWalkMiles + " miles");
		//friend
		TextView friendNameWalk = (TextView)findViewById(R.id.walkFriendName);
		friendNameWalk.setText(name.split(" ")[0]);
		
		//TOTAL
		//me
		int myTotalMiles = (new Double(m.getStat("all", "month", "distance"))).intValue();
		selfProg = (ProgressBar)findViewById(R.id.totalSelfProg);
		selfProg.setProgress(myTotalMiles);
		selfVal = (TextView)findViewById(R.id.totalSelfValue);
		selfVal.setText(myTotalMiles + " miles");
		//friend		
		TextView friendNameTotal = (TextView)findViewById(R.id.totalFriendName);
		friendNameTotal.setText(name.split(" ")[0]);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
}
