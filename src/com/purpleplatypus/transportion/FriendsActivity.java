package com.purpleplatypus.transportion;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchManager.OnDismissListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FriendsActivity extends TransportionActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// special TransportionActivity onCreate procedure
		super.onCreate(savedInstanceState);
		// set the layout to whichever layout this activity is attached to
		setFrameView(R.layout.activity_friends);
		
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("Friends");
		// do normal initialization stuff
		EditText searchBar = (EditText) findViewById(R.id.searchBar);
		searchBar.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				onSearchRequested();
			}
		});
		
		// fix the bug where soft keyboard doesn't disappear after search is dismissed
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchManager.setOnDismissListener(new OnDismissListener() {
		    @Override
		    public void onDismiss() {
		        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.toggleSoftInput(0, 0);
		    }
		});
		
		//handle search query
		Intent intent = getIntent();
		handleIntent(intent);
	}
	
	protected void onNewIntent(Intent intent) {
	    setIntent(intent);
	    handleIntent(intent);
	}
	
	protected void handleIntent(Intent intent) {
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
	    	// must be the case that a friend had been clicked on while on suggestion
	    	System.out.println("clicked on suggestion");
	    	
			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			
			String name = intent.getData().getLastPathSegment().toLowerCase();
			i.putExtra("name", name);
	    	System.out.println("name from search was " + name);
			
			startActivity(i);
	    }
		
		ListView friendsList;
		
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	// clicked on search tab, 
	    	// get the data
	    	String query = intent.getStringExtra(SearchManager.QUERY);
	    	ArrayList<Integer> itemsToShow = new ArrayList<Integer>();
	      	for (int i = 0; i < SearchFriendsSuggestionProvider.friends.length; i++) {
				if (SearchFriendsSuggestionProvider.friends[i].toLowerCase().contains(query.toLowerCase())) {
					String emission = new DecimalFormat("#.##").format(SearchFriendsSuggestionProvider.emissions[i]) + " pounds of carbon emitted";
					String name = SearchFriendsSuggestionProvider.friends[i];
					Integer id = Integer.valueOf(i);
					itemsToShow.add(id);
				}
			}
	      	
	      	// put it in the ListView
	      	friendsList = (ListView) findViewById(R.id.friendsList);
	      	friendsList.setAdapter(new resultsAdapter(this, itemsToShow));
	    }
	    else {
	    	// just initialized, so just show all friends
	    	ArrayList<Integer> itemsToShow = new ArrayList<Integer>();
	    	for (int i = 0; i < SearchFriendsSuggestionProvider.friends.length; i++) {
	    		itemsToShow.add(Integer.valueOf(i));
	    	}
	    	
	      	// put it in the ListView
	      	friendsList = (ListView) findViewById(R.id.friendsList);
	      	friendsList.setAdapter(new resultsAdapter(this, itemsToShow));
	    }
		
		friendsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String name = ((TextView) view.findViewById(R.id.name)).getText().toString();
				
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				i.putExtra("name", name);
				startActivity(i);
			}
		});
	}
	
	public class resultsAdapter extends BaseAdapter {
        
        private Activity activity;
        private ArrayList<Integer> data;
        private LayoutInflater inflater=null;
        private int screenHeight, screenWidth;
        
        public resultsAdapter(Activity a, ArrayList<Integer> d) {
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
            ImageView image=(ImageView) vi.findViewById(R.id.profilePic);
            image.setImageResource(R.drawable.social_person);
            
            TextView name = (TextView) vi.findViewById(R.id.name);
            name.setText(SearchFriendsSuggestionProvider.friends[data.get(position)]);
            
            TextView carbon = (TextView) vi.findViewById(R.id.carbonAmount);
            carbon.setText(new DecimalFormat("#.##").format(SearchFriendsSuggestionProvider.emissions[data.get(position)]) + " pounds of carbon emitted");
            
            return vi;
        }
    }
}
