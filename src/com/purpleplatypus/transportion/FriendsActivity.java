package com.purpleplatypus.transportion;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.parse.ParseObject;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchManager.OnDismissListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FriendsActivity extends TransportionActivity {
	
	protected static JSONArray friendsJsons = null;
	protected static ArrayList<String[]> transportionFriends = null; // String[name, carbon] - only information needed for list
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// special TransportionActivity onCreate procedure
		super.onCreate(savedInstanceState);
		// set the layout to whichever layout this activity is attached to
		setFrameView(R.layout.activity_friends);
		
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("Friends");
		// do normal initialization stuff
		ImageView searchButton = (ImageView) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				onSearchRequested();
			}
		});
		
		friendsJsons = ApplicationState.getModel().fbFriendsJsons;
		
		// fix the bug where soft keyboard doesn't disappear after search is dismissed
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchManager.setOnDismissListener(new OnDismissListener() {
		    @Override
		    public void onDismiss() {
		        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.toggleSoftInput(0, 0);
		    }
		});
		
		showFriends(ApplicationState.getModel().transportionFriendsList);
		
	}
	
	public void onResume() {
		super.onResume();
		System.out.println("friends page: onResume called");
		//handle search query
		if (Session.getActiveSession().isOpened()) {
			System.out.println("friends page: handling intent");
			handleIntent(getIntent());
		} else {
			System.out.println("friends page: handling not logged in");
			handleNotLoggedIn();
		}
	}

	protected void onNewIntent(Intent intent) {
		if (Session.getActiveSession().isOpened()) {
			setIntent(intent);
			handleIntent(intent);
		} else {
			handleNotLoggedIn();
		}
	}
	
	protected void handleIntent(Intent intent) { // WHAT HAPPENS WHEN WE DON'T HAVE TRANSPORTIONFRIENDS YET??? WHEN IT IS NULL?!?!?
		
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
	    	// must be the case that a friend had been clicked on while on suggestion
	    	System.out.println("friends page: clicked on suggestion");
	    	
			Intent i = new Intent(getApplicationContext(), FriendsCompareActivity.class);
			
			String name = intent.getData().getLastPathSegment().toLowerCase();
			i.putExtra("name", name);
	    	System.out.println("friends page: name from search was " + name);
			
			startActivity(i);
	    }
		
		
		ListView friendsList = (ListView) findViewById(R.id.friendsList);
		
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	// clicked on search tab, 
	    	// get the data
			System.out.println("friends page: clicked on search button");
	    	String query = intent.getStringExtra(SearchManager.QUERY);
	    	ArrayList<String[]> itemsToShow = new ArrayList<String[]>();
	    	
	      	//for (int i = 0; i < FriendsActivity.friendsJsons.length(); i++) {
	      	for (int i = 0; i < transportionFriends.size(); i++) {
	      			
	    		String name = transportionFriends.get(i)[0];
	    		
	      		if (name.toLowerCase().contains(query.toLowerCase())) {
					//Integer id = Integer.valueOf(i);
	      			String carbon = transportionFriends.get(i)[1];
	      			String[] temp = {name, carbon};
					itemsToShow.add(temp);
				}
			}
	      	
	      	// put it in the ListView
	      	friendsList = (ListView) findViewById(R.id.friendsList);
	      	friendsList.setAdapter(new resultsAdapter(this, itemsToShow));
	    } else {
	    	// just initialized, so just show all friends
	    	System.out.println("friends page: just initialized");
	    	
	    	//getFriends();	    	
	    }
		
		friendsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String name = ((TextView) view.findViewById(R.id.name)).getText().toString();
				
				Intent i = new Intent(getApplicationContext(), FriendsCompareActivity.class);
				i.putExtra("name", name);
				startActivity(i);
			}
		});
	}
	
	protected void getFriends(){
		System.out.println("get friends called");
	    if(Session.getActiveSession().getState().isOpened()){
	    	System.out.println("getFriends: session is open, making the request");
	    	// show loading
	    	showLoading();
	    	// make the request
	        Request friendRequest = Request.newMyFriendsRequest(Session.getActiveSession(), 
	        	new GraphUserListCallback(){
	                @Override
	                public void onCompleted(List<GraphUser> users, Response response) {
	                	// onCompleted handler
	                	System.out.println(response.toString());
	                	// respond to possible error
	                	if (response.getError() != null) {
	                		System.out.println("getFriends: response error: " + response.getError().toString());
	                		return;
	                	}
	                	// take valid response and put it into static data structure
	                	JSONObject returnObj = response.getGraphObject().getInnerJSONObject();
	                    try {
	                    	System.out.println("before data");
	                    	JSONArray data = (JSONArray) returnObj.get("data");
	                    	FriendsActivity.friendsJsons = data;	                    	
	                    	System.out.println("after data");
	                    	// give friendsJsons to model
	                    	
	                    	System.out.println("successfully set friendsJson to gotten data");
	                    	System.out.println(data.toString());
	                    	
	                    	// show friends in friendsJson
	                    	//showFriends(data);
	                    	// remove loading icon
	                    	//removeLoading();
	                    } catch (Exception e) {
	                    	System.out.println("error while getting data of getFriends response: " + e.toString());
	                    }
	                    try {
							getTransportionFriends(FriendsActivity.friendsJsons);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                }
	        });
	        
	        Bundle params = new Bundle();
	        params.putString("fields", "id, name, picture");
	        friendRequest.setParameters(params);
	        
	        friendRequest.executeAsync();
	    }
	}
	
	protected void getTransportionFriends(JSONArray friendsJsons) throws JSONException {
		ApplicationState.getModel().retrieveFriendDataFromServer(friendsJsons, this);
	}
	
	protected void showLoading() {
		LinearLayout baseView = (LinearLayout) findViewById(R.id.loadingHolderLayout);
		
		if (baseView.getChildCount() != 0) return;
		
		ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyle);
		progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
		baseView.addView(progressBar);
	}
	
	protected void removeLoading() {
		LinearLayout baseView = (LinearLayout) findViewById(R.id.loadingHolderLayout);
		baseView.removeAllViews();
		
	}
	/*
	protected void showFriends(){
		System.out.println("friends page: show friends called");
		if (FriendsActivity.friendsJsons == null) {
			System.out.println("friends page: friendsJson is null, aborting showFriends");
			return;
		}
		
    	ArrayList<Integer> itemsToShow = new ArrayList<Integer>();
    	for (int i = 0; i < FriendsActivity.friendsJsons.length(); i++) {
    		itemsToShow.add(Integer.valueOf(i));
    	}
    	
      	// put it in the ListView
      	ListView friendsList = (ListView) findViewById(R.id.friendsList);
      	friendsList.setAdapter(new resultsAdapter(this, itemsToShow));
	}
	*/
	
	protected void showFriends(List<ParseObject> friendsData) { // need to fix resultsAdapter
		System.out.println("friends page: show friends called");
		ArrayList<String[]> flist = new ArrayList<String[]>();
		for (ParseObject p : friendsData) {
			// get name
			String name = (String) p.get("name");
			String carbon = (String) p.get("total_carbon");
			
			String[] temp = {name, carbon};
			
			flist.add(temp);
		}
		
		transportionFriends = flist;
		// put it in the ListView
		ListView friendsList = (ListView) findViewById(R.id.friendsList);
      	friendsList.setAdapter(new resultsAdapter(this, flist)); 
		
		removeLoading();
	}
	
	protected void handleNotLoggedIn() {
		System.out.println("friends page: handleNotLoggedIn called");
		if (Session.getActiveSession().isOpened()) {
			System.out.println("friends page: handleNotLoggedIn already logged in");
			handleIntent(getIntent());
			return;
		}
		
		System.out.println("friends page: handleNotLoggedIn is starting relogin activity");
		Intent i = new Intent(getApplicationContext(), ReloginActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(i);
	}
	
	public class resultsAdapter extends BaseAdapter {
        
        private Activity activity;
        private ArrayList<String[]> data;
        private LayoutInflater inflater=null;
        private int screenHeight, screenWidth;
        
        public resultsAdapter(Activity a, ArrayList<String[]> d) {
            activity = a;
            data=d;
            inflater = (LayoutInflater)activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return data.size();
        }
        
        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
        	
            View vi=convertView;
            if(convertView==null) {
                vi = inflater.inflate(R.layout.list_adapter_friend, null);
            }
           
            // set name
            TextView name = (TextView) vi.findViewById(R.id.name);
            try {
            	name.setText(data.get(position)[0]);
            	//name.setText(FriendsActivity.friendsJsons.getJSONObject(data.get(position)).getString("name"));
            } catch (Exception e) {
            	name.setText(e.toString());
            }
            
            // set carbon
            TextView carbon = (TextView) vi.findViewById(R.id.carbonAmount);
            try {
            	carbon.setText(Double.valueOf(data.get(position)[1]).intValue()+"");
            	//carbon.setText(FriendsActivity.friendsJsons.getJSONObject(data.get(position)).getString("id") + " GRAMS");
            } catch (Exception e) {
            	carbon.setText(e.toString());
            }
            
            return vi;
        }
    }

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	            return null;
	        }
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) {
	    	if (result != null) {
	    		bmImage.setImageBitmap(result);
	    	}
	    }
	}
	
	/**
	 * FOR TESTING PURPOSES - CLICK TO POPULATE DATA IN SERVER
	 * @throws JSONException 
	 */
	/*
	public void onClick(View v) throws JSONException {
		int button = v.getId();
		switch(button) {
		case R.id.dayDataSend:
			
			//ApplicationState.getModel().populateSegmentsDay();						

			break;
		case R.id.hourDataSend:
			System.out.println("GOT TO BUTTON!!!!!!");
			ApplicationState appState = (ApplicationState) this.getApplication();
//			appState.data.populateSegmentsHour();
			
			// For testing whats in the local db:
			appState.data.mDbHelper.rawDataGetAll();
			break;
		}
		
	}
	*/
}
