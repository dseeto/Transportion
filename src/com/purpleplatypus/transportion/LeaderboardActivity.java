package com.purpleplatypus.transportion;

import java.util.ArrayList;
import java.util.List;

import com.purpleplatypus.transportion.Model.Info_Leaderboard;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

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
		
		// ACTUAL CODE:
		ApplicationState.getModel().retrieveLeaderboardDataFromServer(this);
		
		/*
		// HARD CODE
		Info_Leaderboard one = appState.data.new Info_Leaderboard("1", "Oscar Koh", "10 lbs");
		Info_Leaderboard two = appState.data.new Info_Leaderboard("2", "Kristin Underwood", "19 lbs");
		Info_Leaderboard three = appState.data.new Info_Leaderboard("3", "Michael Lee", "20 lbs");
		Info_Leaderboard four = appState.data.new Info_Leaderboard("1", "Jessica Pew", "34 lbs");
		Info_Leaderboard five = appState.data.new Info_Leaderboard("5", "Brad Chang", "53 lbs");
		Info_Leaderboard six = appState.data.new Info_Leaderboard("6", "Joe Sarabia", "76 lbs");
		Info_Leaderboard seven = appState.data.new Info_Leaderboard("7", "Jane Magana", "190 lbs");
		
		List<Model.Info_Leaderboard> list = new ArrayList<Model.Info_Leaderboard>();
		list.add(one);
		list.add(two);
		list.add(three);
		list.add(four);
		list.add(five);
		list.add(six);
		list.add(seven);
		
		fillListView(list);
		// HARD CODE
		 * */
		 
	}

	public void fillListView(List<Model.Info_Leaderboard> list) {
		System.out.println(list.size());
		ListView lv = (ListView) findViewById(R.id.leaderboardList);
		System.out.println("UGHHH");
		lv.setAdapter(new LeaderboardAdapter(this, list));
		System.out.println("UGHHH!!!!!!!");	
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String name = ((TextView) view.findViewById(R.id.name_leaderboard)).getText().toString();
				
				Intent i = new Intent(getApplicationContext(), FriendsCompareActivity.class);
				i.putExtra("name", name);
				startActivity(i);
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
