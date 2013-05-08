package com.purpleplatypus.transportion;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

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
		int myCarMiles = (new Double(m.getStat("car", "month", "distance"))).intValue();
		int myBusMiles = (new Double(m.getStat("bus", "month", "distance"))).intValue();
		int myBikeMiles = (new Double(m.getStat("bike", "month", "distance"))).intValue();
		int myWalkMiles = (new Double(m.getStat("walk", "month", "distance"))).intValue();
		int myTotalMiles = (new Double(m.getStat("all", "month", "distance"))).intValue();
		Double friendCarMiles;
		Double friendBikeMiles;
		Double friendWalkMiles;
		Double friendBusMiles;
		Double friendTotalMiles;
		
		
		List<ParseObject> fl = (List<ParseObject>) ApplicationState.getModel().friendsList;
		ParseObject data = null;
		
		for (int i = 0; i < fl.size(); i++) {
			if (((ParseObject) fl.get(i)).get("name").equals(name)) {
				data = (ParseObject) fl.get(i);
				System.out.println("FOUND ONE!!!");
				break;
			}
		}
		// car, bike, walk, bus, total
		
		if (data == null) {
			System.out.println("DATA IS NULL!!");
		}
		
	
		ArrayList arr = (ArrayList) data.get("miles");
		String milesCarString = (String) arr.get(0);
		friendCarMiles = Double.valueOf(milesCarString);
		
		String milesBikeString = (String) arr.get(1);
		friendBikeMiles = Double.valueOf(milesBikeString);
		
		
		String milesWalkString = (String) arr.get(2);
		friendWalkMiles = Double.valueOf(milesWalkString);
		
		
		String milesBusString = (String) arr.get(3);
		friendBusMiles = Double.valueOf(milesBusString);
				
		
		String milesTotalString = (String) arr.get(4);
		friendTotalMiles = Double.valueOf(milesTotalString);
		
		Double total;
		if (myTotalMiles > friendTotalMiles) {
			total = (double) myTotalMiles;
		} else {
			total = friendTotalMiles;
		}
		
		// CAR
		
		//me
		ProgressBar selfProg = (ProgressBar)findViewById(R.id.carSelfProg);
		selfProg.setProgress(new Double(myCarMiles/total*100).intValue());
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
		friendProg.setProgress(new Double(friendCarMiles/total*100).intValue());
		TextView friendVal = (TextView)findViewById(R.id.carFriendValue);
		friendVal.setText(friendCarMiles+" miles");
		TextView friendNameCar = (TextView)findViewById(R.id.carFriendName);
		friendNameCar.setText(name.split(" ")[0]);
		
		//BUS
		//me		
		selfProg = (ProgressBar)findViewById(R.id.busSelfProg);
		selfProg.setProgress(new Double(myBusMiles/total*100).intValue());
		selfVal = (TextView)findViewById(R.id.busSelfValue);
		selfVal.setText(myBusMiles + " miles");
		//friend
		TextView friendNameBus = (TextView)findViewById(R.id.busFriendName);
		friendNameBus.setText(name.split(" ")[0]);
		TextView friendBusVal = (TextView)findViewById(R.id.busFriendValue);
		friendBusVal.setText(friendBusMiles+" miles");
		ProgressBar friendBusProg = (ProgressBar)findViewById(R.id.busFriendProg);
		friendBusProg.setProgress(new Double(friendBusMiles/total*100).intValue());
		
		//BIKE
		//me
		
		selfProg = (ProgressBar)findViewById(R.id.bikeSelfProg);
		selfProg.setProgress(new Double(myBikeMiles/total*100).intValue());
		selfVal = (TextView)findViewById(R.id.bikeSelfValue);
		selfVal.setText(myBikeMiles + " miles");
		//friend
		TextView friendNameBike = (TextView)findViewById(R.id.bikeFriendName);
		friendNameBike.setText(name.split(" ")[0]);
		
		TextView friendBikeVal = (TextView)findViewById(R.id.bikeFriendValue);
		friendBikeVal.setText(friendBikeMiles+" miles");
		ProgressBar friendBikeProg = (ProgressBar)findViewById(R.id.bikeFriendProg);
		friendBikeProg.setProgress(new Double(friendBikeMiles/total*100).intValue());
		
		//WALK
		//me
		
		selfProg = (ProgressBar)findViewById(R.id.walkSelfProg);
		selfProg.setProgress(new Double(myWalkMiles/total*100).intValue());
		selfVal = (TextView)findViewById(R.id.walkSelfValue);
		selfVal.setText(myWalkMiles + " miles");
		//friend
		TextView friendNameWalk = (TextView)findViewById(R.id.walkFriendName);
		friendNameWalk.setText(name.split(" ")[0]);
		TextView friendWalkVal = (TextView)findViewById(R.id.walkFriendValue);
		friendWalkVal.setText(friendWalkMiles+" miles");
		ProgressBar friendWalkProg = (ProgressBar)findViewById(R.id.walkFriendProg);
		friendWalkProg.setProgress(new Double(friendWalkMiles/total*100).intValue());
		//TOTAL
		//me
		
		selfProg = (ProgressBar)findViewById(R.id.totalSelfProg);
		selfProg.setProgress((int) (myTotalMiles/total*100));
		selfVal = (TextView)findViewById(R.id.totalSelfValue);
		selfVal.setText(myTotalMiles + " miles");
		//friend		
		TextView friendNameTotal = (TextView)findViewById(R.id.totalFriendName);
		friendNameTotal.setText(name.split(" ")[0]);
		TextView friendTotalVal = (TextView)findViewById(R.id.totalFriendValue);
		friendTotalVal.setText(friendTotalMiles+" miles");
		ProgressBar friendTotalProg = (ProgressBar)findViewById(R.id.totalFriendProg);
		friendTotalProg.setProgress(new Double(friendTotalMiles/total*100).intValue());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
}
