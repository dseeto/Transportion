package com.purpleplatypus.transportion;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class Model {
	
	
	Context context;
	DbHelper mDbHelper; 
	String userID; 

	String userName;
	List<ParseObject> friendsList = null;
	ArrayList<Info_Leaderboard> leaderboardList = null;
	
	public static int carbonPerGallon = 13;
	public static double treesPerCarbon = 0.0000165;
	public static double milesPerGallon = 25.0;
	
	public static double busMilesPerGallon = 50.0;
	
	JSONObject carStats, bikeStats, walkStats, busStats, allStats;
	
	int year;
	int month;
	int day;
	int hour;
	int min;

	public Model() {
		SharedPreferences savedSession = ApplicationState.getContext().getSharedPreferences("facebook-session",Context.MODE_PRIVATE);
        userID = savedSession.getString("id", null);
        userName = savedSession.getString("name", null); // not sure if this is saved
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
	public void sendDataToServer(final Hashtable<String, JSONArray> data) throws JSONException { // data for last month		
		
		// IMPLEMENT: need to do update
		
		ParseQuery query = new ParseQuery("Users");
		query.whereEqualTo("user_id", userID);
		query.findInBackground(new FindCallback() {
		    public void done(List<ParseObject> l, ParseException e) {
		        if (e == null) { // no exception
		        	if (l.size() == 0) {
		        		
		        		ParseObject user = new ParseObject("Users");
		        		
		        		user.put("user_id", userID);
		        		user.put("miles", data.get("miles"));
		        		user.put("modes", data.get("modes"));
		        		user.put("timespans", data.get("timespans"));
		        		user.put("carbons", data.get("carbons"));
		        		try {
							user.put("total_carbon", data.get("carbons").get(data.get("carbons").length()-1));
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		        		user.put("name", userName);
		        		user.saveEventually();
		        	} else {
		        		if (l.size() > 1) {
		        			// PROBLEM!! 
		        			System.out.println("MORE THAN ONE ENTRY OF SAME USER IN TABLE!!!");
		        		} else { // update
		        			ParseObject temp = l.get(0);
		        			temp.put("user_id", userID);
		        			temp.put("miles", data.get("miles"));
		        			temp.put("modes", data.get("modes"));
		        			temp.put("timespans", data.get("timespans"));
		        			temp.put("carbons", data.get("carbons"));
		        		}
		        	}
		        	
		        } else {
		            // IMPLEMENT: error
		        	System.out.println("retriveFriendDataFromServer ERROR!!!!");
		        }
		    }
		});
		
		
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
	
	public void retrieveFriendDataFromServer(JSONArray fbFriends, final FriendsActivity a) throws JSONException {	// called by FriendsActivity.java	
		//friendsList = new ArrayList<Info_FriendsList>();
		
		// get list of ids from JSON
		List<String> ids = new ArrayList<String>(); // not sure if this has to be an array
		for (int i = 0; i < fbFriends.length(); i++) {
			ids.add(((JSONObject) fbFriends.get(i)).getString("id"));			
		}
			
		ParseQuery query = new ParseQuery("Users");
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE); // or use CACHE_THEN_NETWORK if network too slow
		
		query.whereContainedIn("user_id", ids);
		query.orderByAscending("name"); // not sure if you can do that for string
		query.findInBackground(new FindCallback() {
		    public void done(List<ParseObject> fl, ParseException e) {
		        if (e == null) { // no exception
		        	// save this info in model (not in local db)
		        	System.out.println(fl.size());
		        	friendsList = fl;
		        	a.showFriends(fl);
		        	
		        } else {
		            // IMPLEMENT: error
		        	System.out.println("retriveFriendDataFromServer ERROR!!!!");
		        	System.out.println(e.toString());
		        }
		    }
		});
		
		/*
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
		*/
		//return friendsList;
	}
	
	/*
	 * Get data for leaderboard.
	 */
	public void retrieveLeaderboardDataFromServer(final LeaderboardActivity l) {		
		leaderboardList = new ArrayList<Info_Leaderboard>();
		
		ParseQuery query = new ParseQuery("Users");
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE); // or use CACHE_THEN_NETWORK if network too slow
		query.setLimit(10);
		query.orderByAscending("total_carbon");
		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					Info_Leaderboard i;
					int j = 0;
					for (ParseObject p : list) {
						// REMOVE
						System.out.println("Leaderboard!!!");
						System.out.println(p.getString("name"));
						System.out.println(p.getString("total_carbon"));
						// REMOVE
						j++;
						i = new Info_Leaderboard(j+"", p.getString("name"), p.getString("total_carbon"));
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
		
		/*
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
			*/
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
	    
	    public void cleanTable() {
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	Date date = new java.util.Date();
			int year = date.getYear();
			int month = date.getMonth();
			int day = date.getDate();
			Timestamp lastYearToday = new Timestamp(new java.util.Date(year-1, month, day+1).getTime());
			
	    	String query = "SELECT * FROM " + DATA_TABLE_NAME + " ORDER BY " + COLUMN_NAME_TIMESTAMP + " ASC";
	    	Cursor cursor = db.rawQuery(query, null);
	    	if (cursor.moveToFirst()) {
	            do {
	            	if (Timestamp.valueOf(cursor.getString(0)).before(lastYearToday)) {
	            		db.delete(DATA_TABLE_NAME, "time=?", new String[]{String.valueOf(cursor.getString(0))});
	            	} else {
	            		break;
	            	}
	            } while (cursor.moveToNext());
	        }
	    	cursor.close();
	    	db.close();	
	    }
	    
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
	    
	    public Hashtable<String, Float[]> queryDatabase() {
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	Hashtable<String, Float[]> result = new Hashtable<String, Float[]>();
	    	
	    	Date today = new Date();
			int year = today.getYear();
			int month = today.getMonth();
			int date = today.getDate();
			long utc = new Date(year, month, date).getTime();
			Timestamp yesterday = new Timestamp(utc - 86400000);
			Timestamp lastWeek = new Timestamp(utc - 86400000 * 7);
			Timestamp lastMonth = new Timestamp(utc - 86400000 * 31);
			Timestamp lastYear = new Timestamp(utc - 86400000 * 365);

	    	
			ArrayList<Segment> allData = this.rawDataGetAll();
			for (int i = 0; i < allData.size(); i += 1) {
				if (allData.get(i).timestamp.after(lastYear)) {
					if (allData.get(i).timestamp.after(lastMonth)) {
						if (allData.get(i).timestamp.after(lastWeek)) {
							if (allData.get(i).timestamp.after(yesterday)) {
								String key = String.format("%s" + "," + "day" , allData.get(i).mode);
								Float[] val = {allData.get(i).distance, (float)allData.get(i).interval};
								if (result.containsKey(key)) {
									val[0] += result.get(key)[0];
									val[1] += result.get(key)[1];
								} 
								result.put(key, val);
							}
							String key = String.format("%s" + "," + "week" , allData.get(i).mode);
							Float[] val = {allData.get(i).distance, (float)allData.get(i).interval};
							if (result.containsKey(key)) {
								val[0] += result.get(key)[0];
								val[1] += result.get(key)[1];
							} 
							result.put(key, val);
						}
						String key = String.format("%s" + "," + "month" , allData.get(i).mode);
						Float[] val = {allData.get(i).distance, (float)allData.get(i).interval};
						if (result.containsKey(key)) {
							val[0] += result.get(key)[0];
							val[1] += result.get(key)[1];
						} 
						result.put(key, val);
					}
					String key = String.format("%s" + "," + "year" , allData.get(i).mode);
					Float[] val = {allData.get(i).distance, (float)allData.get(i).interval};
					if (result.containsKey(key)) {
						val[0] += result.get(key)[0];
						val[1] += result.get(key)[1];
					} 
					result.put(key, val);
				}
			}
	    	
	    	db.close();	
	    	return result;
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
		if (day < 1) {
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
		
		Random generator = new Random();
		
		Calendar c = Calendar.getInstance();		
		c.set(year, month, day, hour, min);
		long d = c.getTimeInMillis();
		
		// right now		
		double distanceCar = generator.nextDouble()*100;		
		int iCar = (int) ((distanceCar/45)*60);							
		mDbHelper.updateEntry(new Timestamp(d), "car", (float) distanceCar, iCar);
		
		double distanceBike = generator.nextDouble()*20; 		
		int iBike = (int) ((distanceBike/15)*60);
			
		mDbHelper.updateEntry(new Timestamp(d), "bike", (float) distanceBike, iBike);
		
		double distanceWalk = generator.nextDouble()*2;		
		int iWalk = (int) ((distanceWalk/5)*60);
		
		mDbHelper.updateEntry(new Timestamp(d), "walk", (float) distanceWalk, iWalk);
		
		day = day-1;		
		if (day < 0) {
			month--;
			day = 30;
		}
		if (month < 0) {
			year--;
			month = 12;
		}		
	}
	
	public double getTrees(String mode, String time) {
		JSONObject data = null;
		if (mode == "car") {
			data = carStats;
		} else if (mode == "bike") {
			data = bikeStats;
		} else if (mode == "walk") {
			data = walkStats;
		} else if (mode == "bus") {
			data = busStats;
		} else if (mode == "all") {
			data = allStats;
		}
		
		try {
			String miles = ((JSONArray)data.get(time)).getString(0);
			return (Double.parseDouble(miles)/Model.milesPerGallon)*Model.carbonPerGallon*Model.treesPerCarbon;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error on trying to get trees: " + e.getMessage());
			return -1.0;
		}
	}

	public double getGas(String mode, String time) {
		if (mode != "car" && mode != "bus") {
			return 0.0;
		}
		
		if (mode == "bus") {
			try {
				String miles = ((JSONArray)busStats.get(time)).getString(0);
				return Double.parseDouble(miles)/Model.busMilesPerGallon;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("error on trying to get gas: " + e.getMessage());
				return -1.0;
			}
		}
		
		try {
			String miles = ((JSONArray)carStats.get(time)).getString(0);
			return Double.parseDouble(miles)/Model.milesPerGallon;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error on trying to get gas: " + e.getMessage());
			return -1.0;
		}
	}
	
	public int getPercent(String mode, String time) {
		int miles = Integer.parseInt(getStat(mode, time, "distance"));
		int allMiles = (new Double(getStat("all", time, "distance"))).intValue();
		return (int) ((miles+0.0)/(allMiles+0.0)*100);
	}
	
	public String getStat(String mode, String time, String stat) {
		JSONObject data = null;
		if (mode == "car") {
			data = carStats;
		} else if (mode == "bike") {
			data = bikeStats;
		} else if (mode == "walk") {
			data = walkStats;
		} else if (mode == "bus") {
			data = busStats;
		} else if (mode == "all") {
			data = allStats;
		}
		
		int index = -1;
		if (stat == "distance") {
			index = 0;
		}
		if (stat == "timespan") {
			index = 1;
		}
		
		try {
			String result = ((JSONArray)data.get(time)).getString(index);
			return result;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error on trying to get trees: " + e.getMessage());
			return "";
		}
	}
	
	public void getAndSaveStats() {
    	SharedPreferences saved = context.getSharedPreferences("transportion-data", Context.MODE_PRIVATE);
		Editor edit = saved.edit();
    	String lastSave = saved.getString("last_query_db", "");
    	
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.DATE, -1);
    	SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
    	String yesterday = format.format(cal.getTime());
    	
    	System.out.println("getAndSaveStats: last_query_db was " + lastSave + ", and yesterday is " + yesterday);
    	//if (lastSave == "" || lastSave.compareTo(yesterday) < 0) {
    	if (true) {
    		System.out.println("getAnSaveStats: updating last_query_db");
    		// time to query db again, it's been a day.
    		// mark as edited
    		cal = Calendar.getInstance();
    		String now = format.format(cal.getTime());
    		edit.putString("last_query_db", now);
    		edit.commit();
    		
    		System.out.println("getAndSaveStats: last_query_db is now " + now);
    		
    		//put jsons into sharedpreferences
    		Hashtable<String, Hashtable<String, String[]>> queryResult = query_db();
    		System.out.println("getAndSaveStats: making json objects out of query_db() output...");
    		JSONObject carJson = getJSONFromHash(queryResult.get("car"));
    		System.out.println("getAndSaveStats: carJson: " + carJson.toString());
    		JSONObject bikeJson = getJSONFromHash(queryResult.get("bike"));
    		System.out.println("getAndSaveStats: bikeJson: " + bikeJson.toString());    		
    		JSONObject walkJson = getJSONFromHash(queryResult.get("walk"));
    		System.out.println("getAndSaveStats: walkJson: " + walkJson.toString());
    		JSONObject busJson = getJSONFromHash(queryResult.get("bus"));
    		System.out.println("getAndSaveStats: busJson: " + busJson.toString());
    		
    		edit.putString("car_stats", carJson.toString());
    		edit.putString("bike_stats", bikeJson.toString());
    		edit.putString("walk_stats", walkJson.toString());
    		edit.putString("bus_stats", walkJson.toString());
    		edit.commit();
    		
    		JSONObject allJson = new JSONObject();
    		String[] spans = {"month", "year", "day", "week"};
    		for (int i = 0; i < spans.length; i++) {
    			String span = spans[i];
    			int totalTime = 0;
    			double totalMiles = 0.0;
    			JSONObject[] fields = {carJson, bikeJson, walkJson, busJson};
    			try {
    			for (int j = 0; j < fields.length; j++) {
    				JSONArray info = fields[j].getJSONArray(span);
    				totalTime = totalTime + Integer.parseInt(info.getString(1));
    				totalMiles = totalMiles + Double.parseDouble(info.getString(0));
    			}
    			JSONArray milesAndTime = new JSONArray();
    			milesAndTime.put(totalMiles);
    			milesAndTime.put(totalTime);
    			allJson.put(span, milesAndTime);
    			} catch (JSONException e) {
    				e.printStackTrace();
    				System.out.println("error in trying to create allJson: " + e.getMessage());
    			}
    		}
    		
    		edit.putString("all_stats", allJson.toString());
    		System.out.println("getAndSaveStats: allJson: " + allJson.toString());
    		
    		// put json into instance variables
    		carStats = carJson;
    		bikeStats = bikeJson;
    		walkStats = walkJson;
    		busStats = busJson;
    		allStats = allJson;
    		
    		System.out.println("getAndSaveStats: making hash to send to sendDataToServer");
    		// pass to sendDataToServer:
    		Hashtable<String, JSONArray> hashForServer = new Hashtable<String, JSONArray>();
    		JSONArray modesArray = new JSONArray();
    		modesArray.put("car");
    		modesArray.put("bike");
    		modesArray.put("walk");
    		modesArray.put("bus");
    		modesArray.put("total");
    		System.out.println("getAndSaveStats: modesArray is " + modesArray.toString());
    		hashForServer.put("modes", modesArray);
    		try {
        		JSONArray milesArray = new JSONArray();
        		JSONArray carbonsArray = new JSONArray();
        		JSONArray timeArray = new JSONArray();
        		String milesThisMonth;
        		String timeThisMonth;
        		
        		String[] fields = {"car", "bike", "walk", "bus", "total"};
        		JSONObject[] objs = {carJson, bikeJson, walkJson, busJson};
        		
        		double totalMiles = 0.0;
        		int totalTime = 0;
        		for (int i = 0; i < fields.length-1; i++) {
            		milesThisMonth = ((JSONArray)objs[i].get("month")).getString(0);
        			milesArray.put(milesThisMonth);
        			carbonsArray.put((Double.parseDouble(milesThisMonth)/Model.milesPerGallon)*Model.carbonPerGallon);
        			totalMiles = totalMiles + Double.parseDouble(milesThisMonth);
        			
        			timeThisMonth = ((JSONArray)objs[i].get("month")).getString(1);
        			timeArray.put(timeThisMonth);
        			totalTime = totalTime + Integer.parseInt(timeThisMonth);
        		}
    			milesArray.put(totalMiles+"");
    			carbonsArray.put((totalMiles/Model.milesPerGallon)*Model.carbonPerGallon);
    			timeArray.put(totalTime);
    			
        		System.out.println("getAndSaveStats: milesArray is " + milesArray.toString());
    			hashForServer.put("miles", milesArray);
    			System.out.println("getAndSaveStats: carbonsArray is " + carbonsArray.toString());
    			hashForServer.put("carbons", carbonsArray);
    			System.out.println("getAndSaveStats: timeArray is " + timeArray.toString());
    			hashForServer.put("timespans", timeArray);
    		} catch (JSONException e) {
    			e.printStackTrace();
    			System.out.println("error in getting Json information: " + e.getMessage());
    		}

    		
//			try {
//				sendDataToServer(hashForServer);
//			} catch (JSONException e) {
//				e.printStackTrace();
//    			System.out.println("error in sending Json to server: " + e.getMessage());
//			}
			

    	}
    	else {
    		// just get the old data from the db
    		try {
    		if (carStats == null) {
    			System.out.println("attempting to get carStats from sharedPreferences");
    			carStats = new JSONObject(saved.getString("car_stats", null));
    			System.out.println("carStats is now " + carStats.toString());
    			System.out.println("carStats month distance is " + ((JSONArray)carStats.get("month")).getString(0));
    		}
    		if (bikeStats == null) {
    			System.out.println("attempting to get bikeStats from sharedPreferences");
    			bikeStats = new JSONObject(saved.getString("bike_stats", null));
    			System.out.println("bikeStats is now " + bikeStats.toString());
    		}
    		if (walkStats == null) {
    			System.out.println("attempting to get walkStats from sharedPreferences");
    			walkStats = new JSONObject(saved.getString("walk_stats", null));
    			System.out.println("walkStats is now " + walkStats.toString());
    		}
    		if (busStats == null) {
    			System.out.println("attempting to get busStats from sharedPreferences");
    			busStats = new JSONObject(saved.getString("bus_stats", null));
    			System.out.println("busStats is now " + busStats.toString());
    		}
    		} catch (JSONException e) {
    			e.printStackTrace();
    			System.out.println("getting JSON's from saved strings failed: " + e.getMessage());
    		}
    	}
	}
	
	public Hashtable<String, Hashtable<String, String[]>> query_db() {
		return dummy_query_db();
	}
	
	public JSONObject getJSONFromHash(Hashtable<String, String[]> ht) {
		JSONObject result = new JSONObject();
		Enumeration<String> keys = ht.keys();
		
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			try {
				String[] strArray = ht.get(key);
				JSONArray strJSONArray = new JSONArray();
				for (int i = 0; i < strArray.length; i++) {
					strJSONArray.put(strArray[i]);
				}
				
				result.put(key, strJSONArray);
			} catch (JSONException e1) {
				e1.printStackTrace();
				System.out.println("error in putting element in json: " + e1.getMessage());
			}
		}
		
		return result;
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
