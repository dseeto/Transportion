package com.purpleplatypus.transportion;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class Model {
	
	// TODO: ADD FRIEND STUFF: FRIEND LIST, EDIT FRIEND LIST...
		// Idea: keep the retrieved info in a Dictionary in the model class so if go back to that page, we don't have to retrieve it again?? 
	
	Context context;
	DbHelper mDbHelper; 
	String userID = "JOE"; 
	
	public Model() {
		
	}
		
	public void createDatabase(Context c) {
		context = c;		
		mDbHelper = new DbHelper(context);		
		
	}
	
	/*
	 * Sends all the raw data in UserData table to the server.
	 * Sent in the form of time_point_*count* (ex. time_point_0, time_point_1 etc.) as the key and
	 * 	a JSONArray that includes the timestamp, latitude, and longitude in that order as the value.
	 * The ParseObject sent also has a count key which gives the number of time_points in the object.
	 * Should be in the order by timestamp, earlier the time, the lower the count.
	 * After sent, clears the local db to save room.
	 */
	public void sendDataToServer() throws JSONException { // should send in one object
		userID = "JOE"; // CHANGE THIS!!
		ArrayList<Point> data = mDbHelper.rawDataGetAll();
		System.out.println("GOT TO SEND DATA!!!");
		ParseObject user = new ParseObject(userID);
		
		int count = 0;
		for (Point p : data) {	
			System.out.println(count+"");
			JSONArray time_point = new JSONArray();
			time_point.put(p.timestamp.toString());
			time_point.put(p.latitude);
			time_point.put(p.longitude);			
			user.put("time_point_"+count, time_point);
			count++;						
		}
		user.put("count", count); // number of time_points!!
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
	 */	
	public ArrayList<Info> retrieveUserDataFromServer() {		
		
		final ArrayList<Info> list = new ArrayList<Info>();
		
		ParseQuery query = new ParseQuery("UserRetrievedData");
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE); // or use CACHE_THEN_NETWORK if network too slow
		query.whereEqualTo("UserID", userID);
		query.findInBackground(new FindCallback() {
		    public void done(List<ParseObject> infoList, ParseException e) {
		        if (e == null) { // no exception
		        	Info i;
		        	for (ParseObject p : infoList) {		        		
		        		i = new Info(p.getString("mode"), p.getString("timespan"), (float)p.getDouble("miles"),
		        				(float)p.getDouble("carbon"), p.getInt("timespent"), (float)p.getDouble("percent"), (float)p.getDouble("gas"));
		        		list.add(i);
		        	}
		        } else {
		            // IMPLEMENT: error
		        	System.out.println("retriveUserDataFromServer ERROR!!!!");
		        }
		    }
		});
		return list;
		
	}
	/*
	 * Same as above method. NOT BEEN TESTED!!!!
	 */
	
	public ArrayList<Info> retrieveFriendDataFromServer(String friendID) {		
		
		final ArrayList<Info> list = new ArrayList<Info>();
		
		ParseQuery query = new ParseQuery(friendID + "RetrievedData");
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE); // or use CACHE_THEN_NETWORK if network too slow
		query.whereEqualTo("UserID", friendID);
		query.findInBackground(new FindCallback() {
		    public void done(List<ParseObject> infoList, ParseException e) {
		        if (e == null) { // no exception
		        	Info i;
		        	for (ParseObject p : infoList) {		        		
		        		i = new Info(p.getString("mode"), p.getString("timespan"), (float)p.getDouble("miles"),
		        				(float)p.getDouble("carbon"), p.getInt("timespent"), (float)p.getDouble("percent"), (float)p.getDouble("gas"));
		        		list.add(i);
		        	}
		        } else {
		            // IMPLEMENT: error
		        	System.out.println("retriveFriendDataFromServer ERROR!!!!");
		        }
		    }
		});
		return list;
		
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
	    public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
	    public static final String COLUMN_NAME_LAT = "latitude";
	    public static final String COLUMN_NAME_LON = "longitude";	    	   

	    public static final String RAW_DATA_SQL_CREATE_ENTRIES = 
	    		"CREATE TABLE " + RAW_DATA_TABLE_NAME + "(" + COLUMN_NAME_TIMESTAMP + " TIMESTAMP PRIMARY KEY, " + COLUMN_NAME_LAT + " FLOAT, " + COLUMN_NAME_LON + " FLOAT" + ")";
	    
	    public static final String RAW_DATA_SQL_DELETE_ENTRIES =
	    		"DROP TABLE IF EXIST " + RAW_DATA_TABLE_NAME;
	    
	   
		
	    public void rawDataAddEntry(Timestamp timestamp, float lat, float lon) {
			SQLiteDatabase db;		
			db = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put(COLUMN_NAME_TIMESTAMP, timestamp.toString());
			values.put(COLUMN_NAME_LAT, lat);
			values.put(COLUMN_NAME_LON, lon);			
			
			db.insert(RAW_DATA_TABLE_NAME, null, values);
			db.close();
		}
	    
	    /*
	     *  Should return the points in order by timestamp.
	     */
	    public ArrayList<Point> rawDataGetAll() {
	    	ArrayList<Point> list = new ArrayList<Point>();
	    	String query = "SELECT * FROM " + RAW_DATA_TABLE_NAME + " ORDER BY " + COLUMN_NAME_TIMESTAMP + " ASC";
	    	
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	Cursor cursor = db.rawQuery(query, null);
	    	
	    	if (cursor.moveToFirst()) {
	            do {
	            	// TESTING PURPOSES	    	
	    	    	System.out.println(cursor.getString(0) + " ," + cursor.getString(1) + " ," + cursor.getString(2));
	    	        // TESTING PURPOSES
	            	
	            	
	                Point p = new Point(Timestamp.valueOf(cursor.getString(0)), Float.parseFloat(cursor.getString(1)), Float.parseFloat(cursor.getString(2)));	                	                
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
	public class Info {
		String mode;
		String timespan;
		float miles;
		float carbon;
		int timespent;
		float percent;
		float gas;
		
		public Info(String m, String t, float mi, float c, int tspent, float p, float g) {
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
	public class Point {
		Timestamp timestamp;
		float latitude;
		float longitude;
		
		public Point(Timestamp t, float lat, float lon) {
			timestamp = t;
			latitude = lat;
			longitude = lon;
		}
	}
	
	// remote
}
