package com.purpleplatypus.transportion;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.parse.ParseGeoPoint;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class LocationService extends Service {

	// SHOULD THIS BE RUN IN ANOTHER THREAD?!?
	
	LocationManager lm;
	MyLocationListener locationListener;
	
	// location variables
	int minTimeMillisPoll = 1000*60*5; 		// 5 minutes
	int minDistanceMetersPoll = 200;	// 500 meters?!?! 
	int minAccuracyMeters = 35;	
	int minDistanceMetersCheck = 20;
	
	//boolean traveling = false;
	Location lastLocation;
	List<Location> tripLocations = new ArrayList<Location>();
	
	Model data;
	Context context;
	
	int lastStatus = 0;
	
	Timestamp lastTimestamp;
	ParseGeoPoint lastPoint;
	
	public LocationService() {	
		super();		
		data = ApplicationState.getModel();
		context = ApplicationState.getContext();
	}
	
	/** Called when the activity is first created. */
    private void startLoggerService() {
    	
    	// REMOVE
    	System.out.println("!!!!!!!!!!!!!!!!!!!!GOT TO startLoggerService!!!!!!!!!!!!!!!!!!!!");
    	// REMOVE
    	
        // ---use the LocationManager class to obtain GPS locations---
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new MyLocationListener();

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 	// figure out which is best! - NETWORK_PROVIDER
                        minTimeMillisPoll, 
                        minDistanceMetersPoll,
                        (LocationListener) locationListener);

        //initDatabase();        
        //data.createDatabase(context);
    }
    
    private void shutDownLoggerService() {    	
    	// not sure if need:
    	((LocationManager) lm).requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener); //NETWORK_PROVIDER
    	
    	lm.removeUpdates(locationListener);
    }
	 
    public class MyLocationListener implements LocationListener {

    	/**
    	 * Possibilities: turn off app, turn off device => how does this affect service?!?!?
    	 * what happens when service first starts?
    	 * what happens when service ends?
    	 * IMPLEMENT: When turn off device, make sure to save the trip to the database!! - do this in shutDownLoggerService()
    	 */
    	
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			System.out.println("GOT TO onLocationChanged");
			System.out.println("TIME:");
			System.out.println(location.getTime());
			System.out.println("LATITUDE");
			System.out.println(location.getLatitude());
			if (location != null) {
				if (location.hasAccuracy() && location.getAccuracy() <= minAccuracyMeters) {														
					if (lastLocation != null) {
						float distance = (float) (location.distanceTo(lastLocation)*0.000621371); // in miles
						System.out.println("MillisecondInterval: " + (location.getTime() - lastLocation.getTime()));
						int interval = (int) ((location.getTime() - lastLocation.getTime())*.001/60); // in minutes
						float speed = distance/interval;						
						String mode = "";
						if (speed >= 0.33) {
							mode = "car";
						} else if (speed < 0.33 && speed > 0.09) {
							mode = "bike";
						} else {
							mode = "walk";	// max = 250 cm/s => .09 miles / minute
						}
						Toast.makeText(getBaseContext(), "GEOPOINT ADDED!", Toast.LENGTH_SHORT).show();
						
						System.out.println("GEOPOINT ADDED!!");
						System.out.println(distance + ", " + interval + ", " + mode);
						data.mDbHelper.rawDataAddEntry(new Timestamp(new java.util.Date().getTime()), mode, distance, interval);
					}
						/*
						if (traveling && distance <= minDistanceMetersCheck) { // end of trip b/c haven't moved in 5 minutes
							traveling = false;
							// calculate the speed => guess the mode
							Location beginning = tripLocations.get(0);
							Location end = tripLocations.get(tripLocations.size()-1);
							float tripDistanceKm = beginning.distanceTo(end)/1000;
							float tripTimeHours = ((end.getTime() - beginning.getTime())/60000)/60;
							
							float speed = tripDistanceKm / tripTimeHours;
							// average car speed: 
							// average bike speed: 10km/hr
							// average walk speed:
							
							// put it into the local database
							
							data.mDbHelper.rawDataAddEntry(
			                		   new Timestamp(new java.util.Date().getTime()), (float)location.getLatitude(), (float)location.getLongitude(), 
			                		   (float) (location.hasSpeed() ? location.getSpeed() : -1.0));
							
							tripLocations.clear();
						} else if (traveling && distance > minDistanceMetersCheck) {
							// update trip							
							// add to tripLocations
							tripLocations.add(location);
						} else if (!traveling && distance > minDistanceMetersCheck) {
							// if there is still trip data from before, push it to the local db
							
							// start trip
							
							traveling = true;
						} else {
							// nothing has happened - do nothing
						}	
					} 
					*/
					lastLocation = location;
				}				
			}			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			String showStatus = null;
            if (status == LocationProvider.AVAILABLE) {
            	showStatus = "Available";
        		System.out.println("Status: Available");
            }
                    
            if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            	showStatus = "Temporarily Unavailable";
            	System.out.println("Status:TEMPORARILY_UNAVAILABLE");
            }
                    
           
            if (status == LocationProvider.OUT_OF_SERVICE) {
            	showStatus = "Out of Service";
                System.out.println("Status: OUT_OF_SERVICE");
            }
                    
            if (status != lastStatus) {
                Toast.makeText(getBaseContext(),
                                "new status: " + showStatus,
                                Toast.LENGTH_SHORT).show();
            }
            lastStatus = status;
            
		}
		
    }
    
    /*
     * Service Methods:
     */
    
    public void onCreate() {
    	super.onCreate();
    	startLoggerService();
    	Toast.makeText(getBaseContext(), "Tracking Service Started.", Toast.LENGTH_SHORT).show();
    }
    
    public int onStartCommand(Intent i, int flag, int startID) {
    	return START_STICKY;
    }
    
    public void onDestroy() {
    	super.onDestroy();    	
    	shutDownLoggerService();
    	Toast.makeText(getBaseContext(), "Tracking Service Stopped.", Toast.LENGTH_SHORT).show();
    	System.out.println("=======================EEK DESTORYED=========================");
    }
   
    @Override
   	public IBinder onBind(Intent arg0) {   	
   		return null;
   	}
    
}
