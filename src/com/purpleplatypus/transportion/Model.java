package com.purpleplatypus.transportion;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;


public class Model {
	
	
	Context context;
	DbHelper mDbHelper; 
	String userID; 
	String userName="Joe";
	ArrayList<Info_FriendsList> friendsList;
	ArrayList<Info_Leaderboard> leaderboardList;
	
	int year;
	int month;
	int day;
	int hour;
	int min;

	public Model() {
		SharedPreferences savedSession = ApplicationState.getContext().getSharedPreferences("facebook-session",Context.MODE_PRIVATE);
        userID = savedSession.getString("id", null);
	}
		
	public void createDatabase(Context c) {
		context = c;		
		mDbHelper = new DbHelper(context);
		
		Calendar rightnow = Calendar.getInstance();
		// FOR TESTING PURPOSES
		year = rightnow.get(Calendar.YEAR);
		month = rightnow.get(Calendar.MONTH);
		day = rightnow.get(Calendar.DAY_OF_MONTH);
		hour = rightnow.get(Calendar.HOUR);
		min = rightnow.get(Calendar.MINUTE);
		
		System.out.println("DATE:");
		System.out.println(year);
		System.out.println(month);
		System.out.println(day);
		System.out.println(hour);
		System.out.println(min);
	}
	
	/*
	 * Sends all the raw data in UserData table to the server.
	 * After sent, clears the local db to save room.
	 */
	public void sendDataToServer(Hashtable<String, JSONArray> data) throws JSONException { // should send in one object		
		
		ParseObject user = new ParseObject("Users");
		
		user.put("id", userID);
		user.put("miles", data.get("miles"));
		user.put("modes", data.get("modes"));
		user.put("timespans", data.get("timespans"));
		user.put("carbon", data.get("carbons"));
		
		user.saveEventually();
		
		/*
		ArrayList<Segment> data = mDbHelper.rawDataGetAll();
		System.out.println("GOT TO SEND DATA!!!");
		ParseObject user = new ParseObject("Segments");	
		JSONArray timestamps = new JSONArray();
		JSONArray modes = new JSONArray();
		JSONArray distances = new JSONArray();
		JSONArray intervals = new JSONArray();
		
		int count = 0;
		for (Segment s : data) {	
			System.out.println(count+"");
			
			timestamps.put(s.timestamp.toString());
			modes.put( s.mode);
			distances.put(s.distance);
			intervals.put(s.interval);
			
			count++;						
		}		
		user.put("timestamp", timestamps);
		user.put("mode", modes);
		user.put("distance", distances);
		user.put("interval", intervals);
	
		user.put("id", userID);
		
		//user.saveEventually();
		user.saveInBackground();
		
		// IMPLEMENT: check if cloud got it!!! to do this might have to keep track of the count of the number of objects to check...
		*/
		// delete table
		// mDbHelper.rawDataRemoveAll();		
	}
	
	/*
	 * Same as above method. NOT BEEN TESTED!!!! NEEDS TO BE FIXED TO BE LIKE retrieveLeaderboardDataFromServer!!
	 */
	
	public ArrayList<Info_FriendsList> retrieveFriendDataFromServer(String friendID) {		
		friendsList = new ArrayList<Info_FriendsList>();
		
		/*
		// OLD CODE:
		ParseQuery query = new ParseQuery(friendID + "RetrievedData");
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE); // or use CACHE_THEN_NETWORK if network too slow
		query.whereEqualTo("UserID", friendID);
		query.findInBackground(new FindCallback() {
		    public void done(List<ParseObject> infoList, ParseException e) {
		        if (e == null) { // no exception
		        	Info_User i;
		        	for (ParseObject p : infoList) {		        		
		        		i = new Info_User(p.getString("mode"), p.getString("timespan"), (float)p.getDouble("miles"),
		        				(float)p.getDouble("carbon"), p.getInt("timespent"), (float)p.getDouble("percent"), (float)p.getDouble("gas"));
		        		friendsList.add(i);
		        	}
		        } else {
		            // IMPLEMENT: error
		        	System.out.println("retriveFriendDataFromServer ERROR!!!!");
		        }
		    }
		});
		*/
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("userID", userID);
		ParseCloud.callFunctionInBackground("getFriends", new HashMap<String, List<String>>(), new FunctionCallback<List<ParseObject>>() {
			   public void done(List<ParseObject> result, ParseException e) {
			       if (e == null) {
			    	   Info_FriendsList i;
			        	for (ParseObject p : result) {		        		
			        		i = new Info_FriendsList(p.getString("name"), p.getString("carbon"), p.getString("userID"));
			        		friendsList.add(i);
			        	}
			        	
			        	// IMPLEMENT: need to fill the list adapter for the friends list by calling a method here!!!!
			        	
			        } else {
			            // IMPLEMENT: error
			        	System.out.println("retriveFriendDataFromServer ERROR!!!!");
			        }
			   }
			});
		
		return friendsList;
	}
	
	/*
	 * Get data for leaderboard.
	 */
	public void retrieveLeaderboardDataFromServer(final LeaderboardActivity l) {		
		leaderboardList = new ArrayList<Info_Leaderboard>();
		/*
		// OLD CODE:
		ParseQuery query = new ParseQuery("Users");
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE); // or use CACHE_THEN_NETWORK if network too slow
		query.setLimit(10);
		query.orderByAscending("carbon");
		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					Info_Leaderboard i;
					int j = 0;
					for (ParseObject p : list) {
						// REMOVE
						System.out.println("Leaderboard!!!");
						System.out.println(p.getString("name"));
						System.out.println(p.getNumber("carbon"));
						// REMOVE
						j++;
						i = new Info_Leaderboard(j+"", p.getString("name"), p.getNumber("carbon").toString());
						System.out.println(i.toString());
						System.out.println(leaderboardList.add(i));
					}
					// NEED TO WAIT TILL THIS IS DONE IN ORDER FOR THE LIST TO BE POPULATED, SHOULD NOT RETURN LIST
					l.fillListView(leaderboardList);
				} else {
					// IMPLEMENT: error
					System.out.println("retrieveLeaderboardDataFromServer ERROR!!!!");
				}
			}
		});	
		*/
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("userID", userID);
		ParseCloud.callFunctionInBackground("getLeaderBoard", new HashMap<String, String>(), new FunctionCallback<List<ParseObject>>() {
			   public void done(List<ParseObject> result, ParseException e) {
			       if (e == null) {
			    	   Info_Leaderboard i;
						int j = 0;
						for (ParseObject p : result) {
							// REMOVE
							System.out.println("Leaderboard!!!");
							System.out.println(p.getString("name"));
							System.out.println(p.getNumber("carbon"));
							// REMOVE
							j++;
							i = new Info_Leaderboard(j+"", p.getString("name"), p.getNumber("carbon").toString());
							System.out.println(i.toString());
							System.out.println(leaderboardList.add(i));
						}
						// NEED TO WAIT TILL THIS IS DONE IN ORDER FOR THE LIST TO BE POPULATED, SHOULD NOT RETURN LIST
						l.fillListView(leaderboardList);
					} else {
						// IMPLEMENT: error
						System.out.println("retrieveLeaderboardDataFromServer ERROR!!!!");					
			       }
			   }
			});
	}
	
	/*
	 * Sets up the database for the Raw Data
	 * Each record in the table: timestamp, latitude, longitude
	 * The table for the Retrieved Data is currently not needed assuming the query cache provided by Parse works.
	 * If not we can save the retrieved data in the Retrieved Data Table in case the phone doesn't have internet.
	 */
	public class DbHelper extends SQLiteOpenHelper {
		// IMPLEMENT: create new table for friend data
		
		// =============== SET UP DATABASE =============== //
		public static final int DATABASE_VERSION = 1;
	    public static final String DATABASE_NAME = "Transportion.db";
	    

		public DbHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	    public void onCreate(SQLiteDatabase db) {
	    	//db.execSQL(RETRIEVED_DATA_SQL_CREATE_ENTRIES);
	        db.execSQL(DATA_SQL_CREATE_ENTRIES);
	    }
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // This database is only a cache for online data, so its upgrade policy is
	        // to simply to discard the data and start over
	        db.execSQL(DATA_SQL_DELETE_ENTRIES);
	        //db.execSQL(RETRIEVED_DATA_SQL_DELETE_ENTRIES);
	        onCreate(db);
	    }
	    
	    @SuppressLint("Override")
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        onUpgrade(db, oldVersion, newVersion);
	    }
	    
	    
		// =============== RAW & DAY DATA TABLE =============== // 
	   
	    public static final String DATA_TABLE_NAME = "UserData";
	    public static final String COLUMN_NAME_DISTANCE = "distance"; // miles
	    public static final String COLUMN_NAME_INTERVAL = "interval"; // minutes
	    public static final String COLUMN_NAME_MODE = "mode";	    	 
	    public static final String COLUMN_NAME_TIMESTAMP = "time";

	    public static final String DATA_SQL_CREATE_ENTRIES = 
	    		"CREATE TABLE IF NOT EXISTS " + DATA_TABLE_NAME + " (" + COLUMN_NAME_TIMESTAMP + " TIMESTAMP PRIMARY KEY, " + COLUMN_NAME_MODE + " TEXT, " + COLUMN_NAME_DISTANCE + " FLOAT, " + COLUMN_NAME_INTERVAL + " INT" + ")";
	    
	    public static final String DATA_SQL_DELETE_ENTRIES =
	    		"DROP TABLE IF EXIST " + DATA_TABLE_NAME;
	    
	    
	    public void updateEntry(Timestamp timestamp, String mode, float distance, int interval) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(COLUMN_NAME_TIMESTAMP, timestamp.toString());
			values.put(COLUMN_NAME_MODE, mode);
			values.put(COLUMN_NAME_DISTANCE, distance);		
			values.put(COLUMN_NAME_INTERVAL, interval);
			
			String query = "SELECT * FROM " + DATA_TABLE_NAME + " WHERE (" + COLUMN_NAME_TIMESTAMP + " = " + String.format("\"%s\"", timestamp.toString()) + " AND " + COLUMN_NAME_MODE + " = " + String.format("\"%s\"", mode) + ")";
	    	Cursor cursor = db.rawQuery(query, null);
	    	
	    	if (cursor.moveToFirst()) {
	            distance = distance + cursor.getFloat(2);
	            interval = interval + cursor.getInt(3);	    
	            String update = "UPDATE " + DATA_TABLE_NAME + " SET " + COLUMN_NAME_DISTANCE + " = " + distance + ", " + COLUMN_NAME_INTERVAL + " = " + interval + " WHERE (" + COLUMN_NAME_TIMESTAMP + " = " + String.format("\"%s\"", timestamp.toString()) + " AND " + COLUMN_NAME_MODE + " = " + String.format("\"%s\"", mode) + ")";
	            db.execSQL(update);
	        } else {
	        	db.insert(DATA_TABLE_NAME, null, values);
	        }
	    	cursor.close();
			db.close();
		}
	    
	    /*
	     *  Should return the points in order by timestamp.
	     */
	    public ArrayList<Segment> rawDataGetAll() {
	    	ArrayList<Segment> list = new ArrayList<Segment>();
	    	String query = "SELECT * FROM " + DATA_TABLE_NAME + " ORDER BY " + COLUMN_NAME_TIMESTAMP + " ASC";
	    	
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	Cursor cursor = db.rawQuery(query, null);
	    	
	    	if (cursor.moveToFirst()) {
	            do {
	            	// TESTING PURPOSES	    	
	    	    	System.out.println(cursor.getString(0) + " ," + cursor.getString(1) + " ," + cursor.getString(2) + " ," + cursor.getString(3));
	    	        // TESTING PURPOSES
	            	
	            	
	                Segment p = new Segment(Timestamp.valueOf(cursor.getString(0)), cursor.getString(1), cursor.getFloat(2), cursor.getInt(3));	                	                
	                list.add(p);
	            } while (cursor.moveToNext());
	        }
	    	cursor.close();
	    	db.close();
	        return list;
	    }
		
	    public void rawDataRemoveAll() { // after data is in remote server
			SQLiteDatabase db = this.getWritableDatabase(); 
		    db.delete(DATA_TABLE_NAME, null, null);	    
		    db.close();
		}
		
	}
	
	/*
	 * Class for Raw Data Table in local db
	 */
	public class Segment {
		Timestamp timestamp;
		String mode;
		float distance; // decimal = miles
		int interval; // minutes = int
		
		public Segment(Timestamp t, String m, float d, int i) {
			timestamp = t;
			mode = m;
			distance= d;
			interval= i;
		}
	}
	
	/*
	 * Class for leaderbaord data from server; from server its gonna be: {1 {name, pic, 
	 * IMPLEMENT: profile pic!!
	 */	
	public class Info_Leaderboard {
		String name;
		String ranking;
		String carbon_amount;
		
		public Info_Leaderboard(String ranking, String name, String carbon_amount) {
			this.name = name;
			this.ranking = ranking;
			this.carbon_amount = carbon_amount;
		}
		
		public String toString() {
			return ranking + ", " + name + ", " + carbon_amount;
		}
	}
	
	public class Info_FriendsList {
		String name;
		String carbon;
		String userID;
		
		public Info_FriendsList(String n, String c, String u) {
			name = n;
			carbon = c;
			userID = u;
		}
	}
	
	/**
	 * FOR TESTING PURPOSES: POPULATE DATA 
	 * @throws JSONException 
	 * 
	 * IMPLEMENT: NEED TO BE CHANGED TO LOCALDB
	 */
	public void populateSegmentsHour() throws JSONException {
		
		ParseObject user = new ParseObject("FakeDataHour");
		Random generator = new Random();
		
		Calendar c = Calendar.getInstance();		
		c.set(year,month,day,hour,min);
		long d = c.getTimeInMillis();
		
		JSONArray timestamps = new JSONArray();
		JSONArray modes = new JSONArray();
		JSONArray distances = new JSONArray();
		JSONArray intervals = new JSONArray();
		
		timestamps.put(new Timestamp(d));
		modes.put("car");
		distances.put(generator.nextDouble()*50);
		intervals.put(generator.nextInt(60));		
		hour = hour-1;
		if (hour < 0) {
			day--;
			hour = 23;
		}
		if (day < 0) {
			month--;
			day = 30;
		}
		c.set(year,month,day,hour,min);
		d = c.getTimeInMillis();
		
		timestamps.put(new Timestamp(d));
		modes.put("bike");
		distances.put(generator.nextDouble()*20);
		intervals.put(generator.nextInt(60));	
		hour = hour-1;
		if (hour < 0) {
			day--;
			hour = 23;
		}
		if (day < 0) {
			month--;
			day = 30;
		}
		c.set(year,month,day,hour,min);
		d = c.getTimeInMillis();
		
		timestamps.put(new Timestamp(d));
		modes.put("walk");
		distances.put(generator.nextDouble()*10);
		intervals.put(generator.nextInt(60));	
		hour = hour-1;
		if (hour < 0) {
			day--;
			hour = 23;
		}
		if (day < 0) {
			month--;
			day = 30;
		}
		c.set(year,month,day,hour,min);
		d = c.getTimeInMillis();
		
		user.put("timestamp", timestamps);
		user.put("mode", modes);
		user.put("distance", distances);
		user.put("interval", intervals);
		user.put("userID", userID);
		
		user.saveInBackground();
	}
	
	/**
	 * FOR DEMO PURPOSES, WILL BE USED TO POPULATE FAKE DATA FOR A WHOLE MONTH.
	 * Each click on will populate the distance and interval for bike, car, walk for that day
	 * @throws JSONException
	 * 
	 * IMPLEMENT: NEED TO BE CHANGED TO LOCALDB
	 */
	public void populateSegmentsDay() throws JSONException {
		// average car: 45 miles per hour => 45/60
		// average bike: 15 miles per hour => 15/60
		// average walk: 5 miles per hour => 5/60
		
		ParseObject user = new ParseObject("FakeDataDay");
		Random generator = new Random();
		
		Calendar c = Calendar.getInstance();		
		c.set(year, month, day, hour, min);
		long d = c.getTimeInMillis();
		
		JSONArray timestamps = new JSONArray();
		JSONArray modes = new JSONArray();
		JSONArray distances = new JSONArray();
		JSONArray intervals = new JSONArray();
		
		// right now
		timestamps.put(new Timestamp(d));
		modes.put("car");
		double distanceCar = generator.nextDouble()*100;
		distances.put(distanceCar);
		int iCar = (int) ((distanceCar/45)*60);
		intervals.put(iCar);						
		
		timestamps.put(new Timestamp(d));
		modes.put("bike");
		double distanceBike = generator.nextDouble()*20; 
		distances.put(distanceBike);
		int iBike = (int) ((distanceBike/15)*60);
		intervals.put(iBike);	
		
		timestamps.put(new Timestamp(d));
		modes.put("walk");
		double distanceWalk = generator.nextDouble()*2;
		distances.put(distanceWalk);
		int iWalk = (int) ((distanceWalk/5)*60);
		intervals.put(iWalk);	
		
		day = day-1;		
		if (day < 0) {
			month--;
			day = 30;
		}
		if (month < 0) {
			year--;
			month = 12;
		}
		
		user.put("timestamp", timestamps);
		user.put("mode", modes);
		user.put("distance", distances);
		user.put("interval", intervals);
		user.put("userID", userID);
		
		user.saveInBackground();
	}
	
	public Hashtable<String, Hashtable<String, String[]>> query_db() {
		return dummy_query_db();
	}
	
	public Hashtable<String, Hashtable<String, String[]>> dummy_query_db() {
		Random rand = new Random();
		
		Hashtable carHash = new Hashtable<String, String[]>();
		carHash.put("day", generateRandom(rand));
		carHash.put("week", generateRandom(rand));
		carHash.put("month", generateRandom(rand));
		carHash.put("year", generateRandom(rand));
		
		Hashtable bikeHash = new Hashtable<String, String[]>();
		bikeHash.put("day", generateRandom(rand));
		bikeHash.put("week", generateRandom(rand));
		bikeHash.put("month", generateRandom(rand));
		bikeHash.put("year", generateRandom(rand));
		
		Hashtable busHash = new Hashtable<String, String[]>();
		busHash.put("day", generateRandom(rand));
		busHash.put("week", generateRandom(rand));
		busHash.put("month", generateRandom(rand));
		busHash.put("year", generateRandom(rand));
		
		Hashtable walkHash = new Hashtable<String, String[]>();
		walkHash.put("day", generateRandom(rand));
		walkHash.put("week", generateRandom(rand));
		walkHash.put("month", generateRandom(rand));
		walkHash.put("year", generateRandom(rand));

		Hashtable resultHash = new Hashtable<String, Hashtable<String, String[]>>();
		resultHash.put("car", carHash);
		resultHash.put("bike", bikeHash);
		resultHash.put("bus", busHash);
		resultHash.put("walk", walkHash);
		
		return resultHash;
	}
	
	public String[] generateRandom(Random rand) {
		String[] result = {rand.nextInt(50)+"", rand.nextInt(50)+""};
		return result;
	}
}
