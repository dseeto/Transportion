package com.purpleplatypus.transportion;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LeaderboardActivity extends TransportionActivity {

	TextView title;
	ApplicationState appState;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFrameView(R.layout.activity_leaderboard);
		
		title = (TextView) findViewById(R.id.title);
		title.setText("Leaderboard");
		
		appState = (ApplicationState) this.getApplication();
		
		appState.data.retrieveLeaderboardDataFromServer(this);
			
	}

	public void fillListView(ArrayList<Model.Info_Leaderboard> list) {
		System.out.println(list.size());
		ListView lv = (ListView) findViewById(R.id.leaderboardList);
		System.out.println("UGHHH");
		lv.setAdapter(new LeaderboardAdapter(this, list));
		System.out.println("UGHHH!!!!!!!");	
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
		
		public LeaderboardAdapter(Context context, ArrayList<Model.Info_Leaderboard> list) {
			super(context, 0, list);
			
			// REMOVE
			System.out.println("LEADERBOARDADAPTER CONSTRUCTOR!!");
			// REMOVE
			
			this.list = list;
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
				carbon.setText(list.get(position).carbon_amount);
				System.out.println(list.get(position).carbon_amount);
				// CHANGE
				ImageView image=(ImageView) v.findViewById(R.id.profilePic_leaderboard);
	            image.setImageResource(R.drawable.social_person);
				// CHANGE	            
			}
			
            return v;			
		}
		
	}
	

}
