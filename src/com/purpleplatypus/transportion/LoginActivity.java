package com.purpleplatypus.transportion;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

public class LoginActivity extends Activity {
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    public void call(Session session, SessionState state, Exception exception) {
			System.out.println("session.statuscallback called");
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	protected boolean isResumed = false;
	private UiLifecycleHelper uiHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    System.out.println("login screen: session state changed to " + state.toString());
	    if (isResumed) {
	        if (state.isOpened()) {
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(i);
	        }
	    }
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		isResumed = true;
		
        SharedPreferences savedSession = getApplicationContext().getSharedPreferences("facebook-session",Context.MODE_PRIVATE);
        System.out.println("login screen: saved facebook id was " + savedSession.getString("id", null));
        
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

}

