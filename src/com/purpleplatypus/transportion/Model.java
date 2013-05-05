package com.purpleplatypus.transportion;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class Model {
	
	// TODO: keep track of friends
	
	Context context;
	DbHelper mDbHelper; 
	String userID; 
	String userName="Joe";
	ArrayList<Info_User> userList;
	ArrayList<Info_User> friendList;
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
		
		// FOR TESTING PURPOSES
		year = 2013;
		month = 1;
		day = 1;
		hour = 4;
		min = 0;
		
	}
	
	/*
	 * Sends all the raw data in UserData table to the server.
	 * After sent, clears the local db to save room.
	 */
	public void sendDataToServer() throws JSONException { // should send in one object		
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
		
		// delete table
		mDbHelper.rawDataRemoveAll();		
	}
	
	/*
	 * Should have data saved in cache. IMPLEMENT: need to test if need a backup table (already implemented - Retrieved Data) if cache fails!!
	 * ASSUMPTION: Object for user data received from server: mode (string), timespan (string), 
	 * 	miles (double), carbon (double), timespent (int - in minutes), percent (double), gas (double)
	 * RETURN: ArrayList of Info objects (Info class described near bottom)
	 * 
	 * HAS NOT BEEN TESTED!!!
	 * 
	 * !! NEEDS TO BE FIXED TO BE LIKE retrieveLeaderboardDataFromServer!!
	 */	
	public ArrayList<Info_User> retrieveUserDataFromServer() {		
		userList = new ArrayList<Info_User>();
		ParseQuery query = new ParseQuery("UserRetrievedData");
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE); // or use CACHE_THEN_NETWORK if network too slow
		query.whereEqualTo("UserID", userID);
		query.findInBackground(new FindCallback() {
		    public void done(List<ParseObject> infoList, ParseException e) {
		        if (e == null) { // no exception
		        	Info_User i;
		        	for (ParseObject p : infoList) {		        		
		        		i = new Info_User(p.getString("mode"), p.getString("timespan"), (float)p.getDouble("miles"),
		        				(float)p.getDouble("carbon"), p.getInt("timespent"), (float)p.getDouble("percent"), (float)p.getDouble("gas"));
		        		userList.add(i);
		        	}
		        } else {
		            // IMPLEMENT: error
		        	System.out.println("retriveUserDataFromServer ERROR!!!!");
		        }
		    }
		});
		return userList;		
	}
	
	/*
	 * Same as above method. NOT BEEN TESTED!!!! NEEDS TO BE FIXED TO BE LIKE retrieveLeaderboardDataFromServer!!
	 */
	
	public ArrayList<Info_User> retrieveFriendDataFromServer(String friendID) {		
		friendList = new ArrayList<Info_User>();
		
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
		        		friendList.add(i);
		        	}
		        } else {
		            // IMPLEMENT: error
		        	System.out.println("retriveFriendDataFromServer ERROR!!!!");
		        }
		    }
		});
		
		return friendList;
	}
	
	/*
	 * Get data for leaderboard.
	 */
	public void retrieveLeaderboardDataFromServer(final LeaderboardActivity l) {		
		leaderboardList = new ArrayList<Info_Leaderboard>();
		
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
	}
	
	// IMPLEMENT: once know more about functions, implement
	public void removeFromServer() { // raw data point?!?!
			
			
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
	        db.execSQL(RAW_DATA_SQL_CREATE_ENTRIES);	        
	    }
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // This database is only a cache for online data, so its upgrade policy is
	        // to simply to discard the data and start over
	        db.execSQL(RAW_DATA_SQL_DELETE_ENTRIES);
	        //db.execSQL(RETRIEVED_DATA_SQL_DELETE_ENTRIES);
	        onCreate(db);
	    }
	    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        onUpgrade(db, oldVersion, newVersion);
	    }
	    
	    
		// =============== RAW DATA TABLE =============== // 
	   
	    public static final String RAW_DATA_TABLE_NAME = "UserData";
	    public static final String COLUMN_NAME_DISTANCE = "distance"; // miles
	    public static final String COLUMN_NAME_INTERVAL = "interval"; // minutes
	    public static final String COLUMN_NAME_MODE = "mode";	    	 
	    public static final String COLUMN_NAME_TIMESTAMP = "time";

	    public static final String RAW_DATA_SQL_CREATE_ENTRIES = 
	    		"CREATE TABLE IF NOT EXISTS " + RAW_DATA_TABLE_NAME + " (" + COLUMN_NAME_TIMESTAMP + " TIMESTAMP PRIMARY KEY, " + COLUMN_NAME_MODE + " TEXT, " + COLUMN_NAME_DISTANCE + " FLOAT, " + COLUMN_NAME_INTERVAL + " FLOAT" + ")";
	    
	    public static final String RAW_DATA_SQL_DELETE_ENTRIES =
	    		"DROP TABLE IF EXIST " + RAW_DATA_TABLE_NAME;
	    
	   
		
	    public void rawDataAddEntry(Timestamp timestamp, String mode, float distance, float interval) {
			SQLiteDatabase db;		
			db = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put(COLUMN_NAME_TIMESTAMP, timestamp.toString());
			values.put(COLUMN_NAME_MODE, mode);
			values.put(COLUMN_NAME_DISTANCE, distance);		
			values.put(COLUMN_NAME_INTERVAL, interval);
			
			db.insert(RAW_DATA_TABLE_NAME, null, values);
			db.close();
		}
	    
	    /*
	     *  Should return the points in order by timestamp.
	     */
	    public ArrayList<Segment> rawDataGetAll() {
	    	ArrayList<Segment> list = new ArrayList<Segment>();
	    	String query = "SELECT * FROM " + RAW_DATA_TABLE_NAME + " ORDER BY " + COLUMN_NAME_TIMESTAMP + " ASC";
	    	
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	Cursor cursor = db.rawQuery(query, null);
	    	
	    	if (cursor.moveToFirst()) {
	            do {
	            	// TESTING PURPOSES	    	
	    	    	System.out.println(cursor.getString(0) + " ," + cursor.getString(1) + " ," + cursor.getString(2) + " ," + cursor.getString(3));
	    	        // TESTING PURPOSES
	            	
	            	
	                Segment p = new Segment(Timestamp.valueOf(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3));	                	                
	                list.add(p);
	            } while (cursor.moveToNext());
	        }
	    	cursor.close();
	    	db.close();
	        return list;
	    }
		
	    public void rawDataRemoveAll() { // after data is in remote server
			SQLiteDatabase db = this.getWritableDatabase(); 
		    db.delete(RAW_DATA_TABLE_NAME, null, null);	    
		    db.close();
		}
		
	    // ================= RETRIEVED DATA TABLE ================= // 
	    /*
		public static final String RETRIEVED_DATA_TABLE_NAME = "RetrievedData";
		public static final String COLUMN_NAME_MODE = "mode";
		public static final String COLUMN_NAME_TIMESPAN = "timespan";
		public static final String COLUMN_NAME_MILES = "miles";
		public static final String COLUMN_NAME_CARBON = "carbon";
		public static final String COLUMN_NAME_TIMESPENT = "timespent";
		public static final String COLUMN_NAME_PERCENT = "percent";
		public static final String COLUMN_NAME_GAS = "gas";

		public static final String RETRIEVED_DATA_SQL_CREATE_ENTRIES = 
			 "CREATE TABLE " + RETRIEVED_DATA_TABLE_NAME + " (" + 
					 COLUMN_NAME_MODE + " VARCHAR(5), " + // CAR, WALK ETC.
					 COLUMN_NAME_TIMESPAN + " VARCHAR(10), " + // DAY, WEEK ETC.
					 COLUMN_NAME_MILES + " FLOAT, " +
					 COLUMN_NAME_CARBON + " FLOAT, " + // GRAMS?
					 COLUMN_NAME_TIMESPENT + " INT, " + // MINUTES?
					 COLUMN_NAME_PERCENT + " FLOAT, " + // PERCENTAGE POINTS
					 COLUMN_NAME_GAS + " FLOAT, " + // GALLONS - INT OF FLOAT?!?
					 "PRIMARY KEY(" + COLUMN_NAME_MODE + ", " + COLUMN_NAME_TIMESPAN + "))";
		   
		public static final String RETRIEVED_DATA_SQL_DELETE_ENTRIES =
		    		"DROP TABLE IF EXIST " + RETRIEVED_DATA_TABLE_NAME;
	  
		public Info retrievedDataGetEntry(String mode, String timespan) {			
			// IMPLEMENT: make sure that this query only returns one entry!!!!
			
			mode = "'" + mode + "'";
			timespan = "'" + timespan + "'";
			String query = "SELECT * FROM " + RETRIEVED_DATA_TABLE_NAME + " WHERE " + COLUMN_NAME_MODE + " = " + mode + " AND " + COLUMN_NAME_TIMESPAN + " = " + timespan;
			
			SQLiteDatabase db = this.getWritableDatabase();
	    	Cursor cursor = db.rawQuery(query, null);
	    	Info i;
	    	if (cursor.moveToFirst()) {
	            do {
	            	// TESTING PURPOSES	    	
	    	    	System.out.println(cursor.getString(0) + " ," + cursor.getString(1) + " ," + cursor.getString(2)
	    	    			+ ", " + cursor.getString(3) + ", " + cursor.getString(4) + ", " + cursor.getString(3));
	    	        // TESTING PURPOSES
	            	
	            	
	                i = new Info(cursor.getString(0), cursor.getString(1), 
	                	Float.parseFloat(cursor.getString(2)), Float.parseFloat(cursor.getString(3)), Integer.parseInt(cursor.getString(4)),
	                	Float.parseFloat(cursor.getString(5)), Float.parseFloat(cursor.getString(6)));	                	                
	                break;
	            } while (cursor.moveToNext());
	            db.close();
	            cursor.close();
	            return i;
	        } else {
	        	db.close();
	        	return null;
	        }
	    	
		}
		
		// for testing purposes:
		public void retrievedDataAddEntry(String mode, String timespan, float miles, float carbon,
				int timespent, float percent, float gas) {
			SQLiteDatabase db;		
			db = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put(COLUMN_NAME_MODE, mode);
			values.put(COLUMN_NAME_TIMESPAN, timespan);
			values.put(COLUMN_NAME_MILES, miles);
			values.put(COLUMN_NAME_CARBON, carbon);
			values.put(COLUMN_NAME_TIMESPENT, timespent);
			values.put(COLUMN_NAME_PERCENT, percent);
			values.put(COLUMN_NAME_GAS, gas);
			
			db.insert(RETRIEVED_DATA_TABLE_NAME, null, values);
			db.close();
		}
		
		public void retrievedDataRemoveAll() {
			SQLiteDatabase db = this.getWritableDatabase(); 
		    db.delete(RETRIEVED_DATA_TABLE_NAME, null, null);
		    db.close();
		}
		*/
	}
	
	/*
	 * Class for Retrieved Data from server
	 */
	public class Info_User {
		String mode;
		String timespan;
		float miles;
		float carbon;
		int timespent;
		float percent;
		float gas;
		
		public Info_User(String m, String t, float mi, float c, int tspent, float p, float g) {
			mode = m;
			timespan = t;
			miles = mi;
			carbon = c;
			timespent = tspent;
			percent = p;
			gas = g;
		}
	}
	
	/*
	 * Class for Raw Data Table in local db
	 */
	public class Segment {
		Timestamp timestamp;
		String mode;
		String distance; // decimal = meteres
		String interval; // minutes = int
		
		public Segment(Timestamp t, String m, String d, String i) {
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
		// PROFILE PIC????
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
	
	/**
	 * FOR TESTING PURPOSES: POPULATE DATA
	 * @throws JSONException 
	 */
	public void populateSegments() throws JSONException {
		
		ParseObject user = new ParseObject("Segments");
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
		hour = hour+1;
		if (hour > 23) {
			day++;
			hour = 0;
		}
		if (day > 30) {
			month++;
			day = 0;
		}
		c.set(year,month,day,hour,min);
		d = c.getTimeInMillis();
		
		timestamps.put(new Timestamp(d));
		modes.put("bike");
		distances.put(generator.nextDouble()*20);
		intervals.put(generator.nextInt(60));	
		hour = hour+1;
		if (hour > 23) {
			day++;
			hour = 0;
		}
		if (day > 30) {
			month++;
			day = 0;
		}
		c.set(year,month,day,hour,min);
		d = c.getTimeInMillis();
		
		timestamps.put(new Timestamp(d));
		modes.put("walk");
		distances.put(generator.nextDouble()*10);
		intervals.put(generator.nextInt(60));	
		hour = hour+1;
		if (hour > 23) {
			day++;
			hour = 0;
		}
		if (day > 30) {
			month++;
			day = 0;
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
	
}
