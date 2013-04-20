package com.purpleplatypus.transportion;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import ws.munday.slidingmenu.SlidingMenuActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout;

public class TransportionActivity extends SlidingMenuActivity {
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    public void call(Session session, SessionState state, Exception exception) {
			System.out.println("session.statuscallback called");
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	private boolean isResumed = false;
	private UiLifecycleHelper uiHelper;
	
	private String userIdentifier = "";
	
	private static int facebookConnectionAttempts = 5;
	
	static final String[] SLIDING_MENU_ITEMS = new String[] {
		"Home", "Friends", "Leaderboard", "Car Details", 
		"Walk Details", "Bike Details", "Bus Details", "Logout" };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setLayoutIds(R.layout.menu, R.layout.frame);
		setAnimationDuration(300);
		setAnimationType(MENU_TYPE_SLIDEOVER);
		super.onCreate(savedInstanceState);

		ImageButton menuButton = (ImageButton) findViewById(R.id.menu_button);
		
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toggleMenu();
			}
		});
		
		ListView menuListView = (ListView) findViewById(R.id.menuListView);
		menuListView.setAdapter(new ArrayAdapter<String>(this, R.layout.menu_text, SLIDING_MENU_ITEMS));
		
		menuListView.setOnItemClickListener(new OnMenuItemClickListener());
		
		//facebook stuff
		
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	public void setFrameView(int viewID) {
		LayoutInflater factory = LayoutInflater.from(this);
		View myView = factory.inflate(viewID, null);
		
		RelativeLayout frameView = (RelativeLayout) findViewById(R.id.frameView);
		frameView.addView(myView);
	}
	
	public class OnMenuItemClickListener implements OnItemClickListener {
		
		HashMap<String, Class> activityClasses;
		HashMap<String, ArrayList<String[]>> activityPutExtras;
		
		public OnMenuItemClickListener() {
			super();
			activityClasses = new HashMap<String, Class>();
			activityClasses.put("Home", MainActivity.class);
			activityClasses.put("Friends", FriendsActivity.class);
			activityClasses.put("Car Details", Details.class);
			activityClasses.put("Walk Details", Details.class);
			activityClasses.put("Bike Details", Details.class);
			activityClasses.put("Bus Details", Details.class);
			
			activityPutExtras = new HashMap<String, ArrayList<String[]>>();
			ArrayList<String[]> putExtra = new ArrayList<String[]>();
				putExtra.add(new String[]{"Mode", "Car"});
			activityPutExtras.put("Car Details", putExtra);
			
			putExtra = new ArrayList<String[]>();
				putExtra.add(new String[]{"Mode", "Walk"});
			activityPutExtras.put("Walk Details", putExtra);
			
			putExtra = new ArrayList<String[]>();
				putExtra.add(new String[]{"Mode", "Bike"});
			activityPutExtras.put("Bike Details", putExtra);
			
			putExtra = new ArrayList<String[]>();
				putExtra.add(new String[]{"Mode", "Bus"});
			activityPutExtras.put("Bus Details", putExtra);
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String textClicked = ((TextView) view).getText().toString();		
			toggleMenu();
			if (textClicked == "Logout") {
				onClickLogout();
			} else {
				beginNewActivity(textClicked);		
			}
		}
		
		public void beginNewActivity(String activityClass){
			// Launching new Activity on selecting single List Item
			Intent i = new Intent(getApplicationContext(), getActivityClass(activityClass));
			// sending data to new activity
			ArrayList<String[]> puts = activityPutExtras.get(activityClass);
			if (puts != null) {
				for (int j = 0; j < puts.size(); j++) {
					String[] data = puts.get(j);
					i.putExtra(data[0], data[1]);
				}
			}
			// bring activity to front if one already exists
			i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i);
		}
		
		public Class getActivityClass(String name) {
			if (activityClasses.containsKey(name)) {
				return activityClasses.get(name);
			}
			return FriendsActivity.class;
		}
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    System.out.println("transportion screen: session state changed to " + state.toString());
	    if (isResumed) {
	        if (state.isOpened()) {
	        	
	            SharedPreferences savedSession = getApplicationContext().getSharedPreferences("facebook-session",Context.MODE_PRIVATE);
	            String id = savedSession.getString("id", null);
	            
	            System.out.println("transportion screen: saved facebook id is " + id);
	            
	            if (id == null) {
	            	System.out.println("transportion screen: making me request for facebook id");
	            	makeMeRequest(Session.getActiveSession());
	            }
	        }
	    }
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		isResumed = true;
		
        SharedPreferences savedSession = getApplicationContext().getSharedPreferences("facebook-session",Context.MODE_PRIVATE);
        System.out.println("transportion screen: saved facebook id was " + savedSession.getString("id", null));
        
        onSessionStateChange(Session.getActiveSession(), Session.getActiveSession().getState(), null);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		isResumed = false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private void onClickLogout() {
		Session session = Session.getActiveSession();
		System.out.println("onClickLogout activated with state " + session.getState().toString());
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
		}
		
    	Editor editor = getApplicationContext().getSharedPreferences("facebook-session", Context.MODE_PRIVATE).edit();
    	editor.remove("id");
		editor.commit();
    	
		Intent i = new Intent(getApplicationContext(), LoginActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(i);
	}
	
	private void makeUIDRequest(final Session session) {
		System.out.println("making facebook graphPathRequest with access token as " + session.getAccessToken());
		new AsyncTask<Session, Void, String>() {
			public String doInBackground(Session... arg) {
				Session session = arg[0];
				HttpResponse response = null;
				try {        
				        HttpClient client = new DefaultHttpClient();
				        HttpGet request = new HttpGet();
				        request.setURI(new URI("https://graph.facebook.com/me?fields=third_party_id&access_token="+session.getAccessToken()));
				        response = client.execute(request);
				} catch (Exception e) {
				        e.printStackTrace();
				        return null;
				}
				String jsonString = null;
				try {
					jsonString = convertStreamToString(response.getEntity().getContent());
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				
				JSONObject json = null;
				String result = null;
				try {
					json = new JSONObject(jsonString);
					result = json.getString("third_party_id");
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}

				return result;
			} 
			public void onPostExecute(String thirdPartyId) {
				if (thirdPartyId == null) {
					System.out.println("third_party_id is null!");
					return;
				}
				System.out.println("third_party_id is " + thirdPartyId);
				userIdentifier = thirdPartyId;
			}
			
			public String convertStreamToString(InputStream inputStream) throws IOException {
		        if (inputStream != null) {
		            Writer writer = new StringWriter();

		            char[] buffer = new char[1024];
		            try {
		                Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),1024);
		                int n;
		                while ((n = reader.read(buffer)) != -1) {
		                    writer.write(buffer, 0, n);
		                }
		            } finally {
		                inputStream.close();
		            }
		            return writer.toString();
		        } else {
		            return "";
		        }
		    }
			
		}.execute(session);
	} 

	private void getFriends(){
		System.out.println("get friends called");
	    Session activeSession = Session.getActiveSession();
	    if(activeSession.getState().isOpened()){
	    	System.out.println("getFriends: session is open, making the request");
	        Request friendRequest = Request.newMyFriendsRequest(activeSession, 
	        	new GraphUserListCallback(){
	                @Override
	                public void onCompleted(List<GraphUser> users, Response response) {
	                    System.out.println("getfriends response is " + response.toString());
	                }
	        });
	        Bundle params = new Bundle();
	        params.putString("fields", "id, name, picture");
	        friendRequest.setParameters(params);
	        friendRequest.executeAsync();
	    }
	}

	private void makeMeRequest(final Session session) {
		if (facebookConnectionAttempts <= 0) {
			System.out.println("exhausted all attempts, logging out");
			onClickLogout();
		}
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
	    Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	        	SharedPreferences savedSession = getApplicationContext().getSharedPreferences("facebook-session", Context.MODE_PRIVATE);
	        	
	            if (session == Session.getActiveSession()) {
	                if (user != null) {
	                	System.out.println("me request returned with user as: ");
	                	System.out.println(user.getId() + " : " + user.getName());
	                	
	                    Editor editor = savedSession.edit();
	                    editor.putString("id", user.getId());
	                    editor.commit();
	                    
	                    facebookConnectionAttempts = 5;
	                }
	                else {
	                	System.out.println("me request returned empty user");
		                facebookConnectionAttempts = facebookConnectionAttempts - 1;
		                System.out.println("retrying me request. attempts left is " + facebookConnectionAttempts);
		                makeMeRequest(Session.getActiveSession());
	                }
	            }
	            if (response.getError() != null) {
	                System.out.println("me request failed with error " + response.getError());
	                facebookConnectionAttempts = facebookConnectionAttempts - 1;
	                System.out.println("retrying me request. attempts left is " + facebookConnectionAttempts);
	                makeMeRequest(Session.getActiveSession());
	            }
	        }
	    });
	    request.executeAsync();
	} 

}
