package com.purpleplatypus.transportion;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.model.GraphUser;
import com.purpleplatypus.transportion.FriendsActivity.resultsAdapter;
import com.purpleplatypus.transportion.Model.Info_Leaderboard;

import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LeaderboardActivity extends TransportionActivity {

	TextView title;
	ApplicationState appState;
	SharedPreferences savedSession;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFrameView(R.layout.activity_leaderboard);
		
		savedSession = getApplicationContext().getSharedPreferences("facebook-session", Context.MODE_PRIVATE);
		
		title = (TextView) findViewById(R.id.title);
		title.setText("Leaderboard");
		
		appState = (ApplicationState) this.getApplication();		
		// ACTUAL CODE:
		// ApplicationState.getModel().retrieveLeaderboardDataFromServer(this);
		 
	}

	public void onResume() {
		super.onResume();
		System.out.println("friends page: onResume called");
		//handle search query
		if (Session.getActiveSession().isOpened()) {
			System.out.println("friends page: handling intent");
			getFriends();
		} else {
			System.out.println("friends page: handling not logged in");
			handleNotLoggedIn();
		}
	}
	
	protected void showLoading() {
		LinearLayout baseView = (LinearLayout) findViewById(R.id.loading);
		
		if (baseView.getChildCount() != 0) return;
		
		ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyle);
		progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
		baseView.addView(progressBar);
	}
	
	protected void removeLoading() {
		LinearLayout baseView = (LinearLayout) findViewById(R.id.loading);
		baseView.removeAllViews();
		
	}
	
	protected void handleNotLoggedIn() {
		System.out.println("friends page: handleNotLoggedIn called");
		if (Session.getActiveSession().isOpened()) {
			System.out.println("friends page: handleNotLoggedIn already logged in");
			getFriends();
			return;
		}
		
		System.out.println("friends page: handleNotLoggedIn is starting relogin activity");
		Intent i = new Intent(getApplicationContext(), ReloginActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(i);
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
	                    	ApplicationState.getModel().fbFriendsList= data;	                    	
	                    	System.out.println("after data");
	                    	// give friendsJsons to model
	                    	
	                    	System.out.println("successfully set friendsJson to gotten data");
	                    	System.out.println(data.toString());
	                    	
	                    	// show friends in friendsJson
	                    	//showFriends(data);
	                    	// remove loading icon	                    	
	                    } catch (Exception e) {
	                    	System.out.println("error while getting data of getFriends response: " + e.toString());
	                    }
	                    try {
							getTransportionFriends(ApplicationState.getModel().fbFriendsList);	                    	
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
		ApplicationState.getModel().retrieveFriendDataFromServer(friendsJsons, this, "leader");
	}
	
	public void fillListView(List<Model.Info_Leaderboard> list) {
		System.out.println(list.size());
		ListView lv = (ListView) findViewById(R.id.leaderboardList);
		System.out.println("UGHHH");
		removeLoading();
		lv.setAdapter(new LeaderboardAdapter(this, list));
		System.out.println("UGHHH!!!!!!!");	
		
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String name = ((TextView) view.findViewById(R.id.name_leaderboard)).getText().toString();
				
				String savedName = savedSession.getString("name", "");
				
				if (name.equals(savedName)) {
					Intent i = new Intent(getApplicationContext(), MainActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(i);
				} else {
					Intent i = new Intent(getApplicationContext(), FriendsCompareActivity.class);
					i.putExtra("name", name);
					startActivity(i);
				}
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_leaderboard, menu);
		return true;
	}
	
	/*
	 * Get top 10, theoretically there will already be a list of them from the server
	 */
	
	public class LeaderboardAdapter extends ArrayAdapter<Model.Info_Leaderboard> {
		
		ArrayList<Model.Info_Leaderboard> list;
		LayoutInflater li;
		Context context;
		
		public LeaderboardAdapter(Context context, List<Model.Info_Leaderboard> list) {
			super(context, 0, list);
			
			// REMOVE
			System.out.println("LEADERBOARDADAPTER CONSTRUCTOR!!");
			// REMOVE
			
			this.list = (ArrayList<Info_Leaderboard>) list;
			this.context = context;
			li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			   
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			System.out.println("===========GET VIEW===========");
			View v = convertView;
			
			Model.Info_Leaderboard i = list.get(position);
			if (i != null) {
				v = li.inflate(R.layout.list_adapter_leaderboard, null);
				TextView ranking = (TextView) v.findViewById(R.id.ranking);
				TextView name = (TextView) v.findViewById(R.id.name_leaderboard);			
				TextView carbon = (TextView) v.findViewById(R.id.carbonAmount_leaderboard);
				
				ranking.setText(position+1+"");
				name.setText(list.get(position).name);
				System.out.println("INFO_LEADERBOARD STUFF:");
				System.out.println(list.get(position).name);
				carbon.setText(Double.valueOf(list.get(position).carbon_amount).intValue()+"");
				System.out.println(list.get(position).carbon_amount);				  
			}
			
            return v;			
		}
		
	}
	

}
