package com.purpleplatypus.transportion;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseObject;

public class FriendsCompareActivity extends TransportionActivity implements OnItemSelectedListener {

	String name;
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
	int myCarTime = (new Double(m.getStat("car", "month", "timespan"))).intValue();
	int myBusTime = (new Double(m.getStat("bus", "month", "timespan"))).intValue();
	int myBikeTime = (new Double(m.getStat("bike", "month", "timespan"))).intValue();
	int myWalkTime = (new Double(m.getStat("walk", "month", "timespan"))).intValue();
	int myTotalTime = (new Double(m.getStat("all", "month", "timespan"))).intValue();
	Double friendCarTime;
	Double friendBikeTime;
	Double friendWalkTime;
	Double friendBusTime;
	Double friendTotalTime;
	Double milesTotal;
	Double timeTotal;
	Spinner dropDownMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFrameView(R.layout.activity_friend_compare);
		
		// set title
		TextView title = (TextView) findViewById(R.id.title);		
		title.setText("Compare");
		
		// Set drop down menu and set frame with the right graph.
		dropDownMenu = (Spinner) findViewById(R.id.spinner1);
		dropDownMenu.setOnItemSelectedListener(this);

		// get intent value
		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		TextView compareWith = (TextView) findViewById(R.id.friend_compare);
		compareWith.setText("You VS. " + name);
		
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
		
		ArrayList arr = (ArrayList) data.get("timespans");
		String timeCarString = (String) arr.get(0);
		friendCarTime = Double.valueOf(timeCarString);
		
		String timeBikeString = (String) arr.get(1);
		friendBikeTime = Double.valueOf(timeBikeString);
		
		
		String timeWalkString = (String) arr.get(2);
		friendWalkTime = Double.valueOf(timeWalkString);
		
		
		String timeBusString = (String) arr.get(3);
		friendBusTime = Double.valueOf(timeBusString);
				
		
		String timeTotalString = (String) arr.get(4).toString();
		friendTotalTime = Double.valueOf(timeTotalString);
		
		if (myTotalTime > friendTotalTime) {
			timeTotal = (double) myTotalTime;
		} else {
			timeTotal = friendTotalTime;
		}
		
		arr = (ArrayList) data.get("miles");
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
		
		if (myTotalMiles > friendTotalMiles) {
			milesTotal = (double) myTotalMiles;
		} else {
			milesTotal = friendTotalMiles;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long itemSelected) {
		ProgressBar selfProg;
		TextView selfVal;
		ProgressBar friendProg;
		TextView friendVal;
		TextView friendName;
		switch((int)itemSelected) {
		case 0:	
			// CAR
			//me
			selfProg = (ProgressBar)findViewById(R.id.carSelfProg);
			selfProg.setProgress(new Double(myCarMiles/milesTotal*100).intValue());
			selfVal = (TextView)findViewById(R.id.carSelfValue);
			selfVal.setText(myCarMiles + " miles");
			//friend
			friendProg = (ProgressBar)findViewById(R.id.carFriendProg);
			friendProg.setProgress(new Double(friendCarMiles/milesTotal*100).intValue());
			friendVal = (TextView)findViewById(R.id.carFriendValue);
			friendVal.setText(new Double(friendCarMiles).intValue() +" miles");
			TextView friendNameCar = (TextView)findViewById(R.id.carFriendName);
			friendNameCar.setText(name.split(" ")[0]);
			
			//BUS
			//me		
			selfProg = (ProgressBar)findViewById(R.id.busSelfProg);
			selfProg.setProgress(new Double(myBusMiles/milesTotal*100).intValue());
			selfVal = (TextView)findViewById(R.id.busSelfValue);
			selfVal.setText(myBusMiles + " miles");
			//friend
			TextView friendNameBus = (TextView)findViewById(R.id.busFriendName);
			friendNameBus.setText(name.split(" ")[0]);
			TextView friendBusVal = (TextView)findViewById(R.id.busFriendValue);
			friendBusVal.setText(new Double(friendBusMiles).intValue() +" miles");
			ProgressBar friendBusProg = (ProgressBar)findViewById(R.id.busFriendProg);
			friendBusProg.setProgress(new Double(friendBusMiles/milesTotal*100).intValue());
			
			//BIKE
			//me	
			selfProg = (ProgressBar)findViewById(R.id.bikeSelfProg);
			selfProg.setProgress(new Double(myBikeMiles/milesTotal*100).intValue());
			selfVal = (TextView)findViewById(R.id.bikeSelfValue);
			selfVal.setText(myBikeMiles + " miles");
			//friend
			TextView friendNameBike = (TextView)findViewById(R.id.bikeFriendName);
			friendNameBike.setText(name.split(" ")[0]);
			TextView friendBikeVal = (TextView)findViewById(R.id.bikeFriendValue);
			friendBikeVal.setText(new Double(friendBikeMiles).intValue()+" miles");
			ProgressBar friendBikeProg = (ProgressBar)findViewById(R.id.bikeFriendProg);
			friendBikeProg.setProgress(new Double(friendBikeMiles/milesTotal*100).intValue());
			
			//WALK
			//me
			selfProg = (ProgressBar)findViewById(R.id.walkSelfProg);
			selfProg.setProgress(new Double(myWalkMiles/milesTotal*100).intValue());
			selfVal = (TextView)findViewById(R.id.walkSelfValue);
			selfVal.setText(myWalkMiles + " miles");
			//friend
			TextView friendNameWalk = (TextView)findViewById(R.id.walkFriendName);
			friendNameWalk.setText(name.split(" ")[0]);
			TextView friendWalkVal = (TextView)findViewById(R.id.walkFriendValue);
			friendWalkVal.setText(new Double(friendWalkMiles).intValue()+" miles");
			ProgressBar friendWalkProg = (ProgressBar)findViewById(R.id.walkFriendProg);
			friendWalkProg.setProgress(new Double(friendWalkMiles/milesTotal*100).intValue());
			
			//TOTAL
			//me
			selfProg = (ProgressBar)findViewById(R.id.totalSelfProg);
			selfProg.setProgress((int) (myTotalMiles/milesTotal*100));
			selfVal = (TextView)findViewById(R.id.totalSelfValue);
			selfVal.setText(myTotalMiles + " miles");
			//friend		
			TextView friendNameTotal = (TextView)findViewById(R.id.totalFriendName);
			friendNameTotal.setText(name.split(" ")[0]);
			TextView friendTotalVal = (TextView)findViewById(R.id.totalFriendValue);
			friendTotalVal.setText(new Double(friendTotalMiles).intValue()+" miles");
			ProgressBar friendTotalProg = (ProgressBar)findViewById(R.id.totalFriendProg);
			friendTotalProg.setProgress(new Double(friendTotalMiles/milesTotal*100).intValue());
		    break;
		case 1:
			// CAR
			//me
			selfProg = (ProgressBar)findViewById(R.id.carSelfProg);
			selfProg.setProgress(new Double(myCarTime/timeTotal*100).intValue());
			selfVal = (TextView)findViewById(R.id.carSelfValue);
			selfVal.setText(myCarTime/60 + " Hr " + myCarTime%60 + " Min");
			//friend
			friendProg = (ProgressBar)findViewById(R.id.carFriendProg);
			friendProg.setProgress(new Double(friendCarTime/timeTotal*100).intValue());
			friendVal = (TextView)findViewById(R.id.carFriendValue);
			friendVal.setText(new Double(friendCarTime).intValue()/60 + " Hr " + new Double(friendCarTime).intValue()%60 + " Min");
			friendNameCar = (TextView)findViewById(R.id.carFriendName);
			friendNameCar.setText(name.split(" ")[0]);
			
			//BUS
			//me		
			selfProg = (ProgressBar)findViewById(R.id.busSelfProg);
			selfProg.setProgress(new Double(myBusTime/timeTotal*100).intValue());
			selfVal = (TextView)findViewById(R.id.busSelfValue);
			selfVal.setText(myBusTime/60 + " Hr " + myBusTime%60 + " Min");
			//friend
			friendNameBus = (TextView)findViewById(R.id.busFriendName);
			friendNameBus.setText(name.split(" ")[0]);
			friendVal = (TextView)findViewById(R.id.busFriendValue);
			friendVal.setText(new Double(friendBusTime).intValue()/60 + " Hr " + new Double(friendBusTime).intValue()%60 + " Min");
			friendProg = (ProgressBar)findViewById(R.id.busFriendProg);
			friendProg.setProgress(new Double(friendBusTime/timeTotal*100).intValue());
			
			//BIKE
			//me	
			selfProg = (ProgressBar)findViewById(R.id.bikeSelfProg);
			selfProg.setProgress(new Double(myBikeTime/timeTotal*100).intValue());
			selfVal = (TextView)findViewById(R.id.bikeSelfValue);
			selfVal.setText(myBikeTime/60 + " Hr " + myBikeTime%60 + " Min");
			//friend
			friendName = (TextView)findViewById(R.id.bikeFriendName);
			friendName.setText(name.split(" ")[0]);
			friendVal = (TextView)findViewById(R.id.bikeFriendValue);
			friendVal.setText(new Double(friendBikeTime).intValue()/60 + " Hr " + new Double(friendBikeTime).intValue()%60 + " Min");
			friendProg = (ProgressBar)findViewById(R.id.bikeFriendProg);
			friendProg.setProgress(new Double(friendBikeTime/timeTotal*100).intValue());
			
			//WALK
			//me
			selfProg = (ProgressBar)findViewById(R.id.walkSelfProg);
			selfProg.setProgress(new Double(myWalkTime/timeTotal*100).intValue());
			selfVal = (TextView)findViewById(R.id.walkSelfValue);
			selfVal.setText(myWalkTime/60 + " Hr " + myWalkTime%60 + " Min");
			//friend
			friendName = (TextView)findViewById(R.id.walkFriendName);
			friendName.setText(name.split(" ")[0]);
			friendVal = (TextView)findViewById(R.id.walkFriendValue);
			friendVal.setText(new Double(friendWalkTime).intValue()/60 + " Hr " + new Double(friendWalkTime).intValue()%60 + " Min");
			friendProg = (ProgressBar)findViewById(R.id.walkFriendProg);
			friendProg.setProgress(new Double(friendWalkTime/timeTotal*100).intValue());
			
			//TOTAL
			//me
			selfProg = (ProgressBar)findViewById(R.id.totalSelfProg);
			selfProg.setProgress((int) (myTotalTime/timeTotal*100));
			selfVal = (TextView)findViewById(R.id.totalSelfValue);
			selfVal.setText(myTotalTime/60 + " Hr " + myTotalTime%60 + " Min");
			//friend		
			friendName = (TextView)findViewById(R.id.totalFriendName);
			friendName.setText(name.split(" ")[0]);
			friendVal = (TextView)findViewById(R.id.totalFriendValue);
			friendVal.setText(new Double(friendTotalTime).intValue()/60 + " Hr " + new Double(friendTotalTime).intValue()%60 + " Min");
			friendProg = (ProgressBar)findViewById(R.id.totalFriendProg);
			friendProg.setProgress(new Double(friendTotalTime/timeTotal*100).intValue());
		    break;	
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
	
}
