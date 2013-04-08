package com.purpleplatypus.transportion;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FriendsActivity extends TransportionActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// special TransportionActivity onCreate procedure
		super.onCreate(savedInstanceState);
		// set the layout to whichever layout this activity is attached to
		setFrameView(R.layout.activity_friends);
		
		// do normal initialization stuff
		// Button testButton = (Button) findViewById(R.id.testButton); // TOOK OUT
		/*
		testButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				TextView title = (TextView) findViewById(R.id.title);
				title.setText("blah blah blah");
			}
		});
		*/
	}
}
