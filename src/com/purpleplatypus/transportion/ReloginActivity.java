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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReloginActivity extends LoginActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView loginTitle = (TextView) findViewById(R.id.loginTitle);
		loginTitle.setTextSize((float) 25.0);
		loginTitle.setText("Facebook session expired. Please reauthenticate to access facebook features");
		
		Button cancelButton = new Button(this);
		cancelButton.setText("cancel");
		cancelButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		LinearLayout baseView = (LinearLayout) findViewById(R.id.baseLinearLayout);
		baseView.addView(cancelButton);
	}
	
	@Override
	protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    System.out.println("login screen: session state changed to " + state.toString());
	    if (isResumed) {
	        if (state.isOpened()) {
				finish();
	        }
	    }
	}

}

