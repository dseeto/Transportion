package com.purpleplatypus.transportion;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class SearchFriendsActivity extends TransportionActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFrameView(R.layout.activity_search_friends);
		
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	
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
	      	ListView searchResults = (ListView) findViewById(R.id.searchResults);
	      	searchResults.setAdapter(new resultsAdapter(this, itemsToShow));
	    }
	    if (Intent.ACTION_VIEW.equals(intent.getAction())) {
	    	System.out.println("clicked on suggestion");
	    	
			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(i);
	    }
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
        	/*
            View vi=convertView;
            if(convertView==null) {
                vi = inflater.inflate(R.layout.image_item, null);
            }
            ImageView image=(ImageView)vi.findViewById(R.id.image);
            
            BitmapFactory.Options dimensions = new BitmapFactory.Options(); 
            dimensions.inJustDecodeBounds = true;
            Bitmap mBitmap = BitmapFactory.decodeResource(MapActivity.mainActivity.getResources(), data[position], dimensions);
            int imageHeight = dimensions.outHeight;
            int imageWidth = dimensions.outWidth;
            
            screenHeight = MapActivity.mainActivity.findViewById(R.id.fragment_container).getMeasuredHeight();
            screenWidth = MapActivity.mainActivity.findViewById(R.id.fragment_container).getMeasuredWidth();
            
            double heightRatio = 1.0*screenHeight/imageHeight;
            double widthRatio = 1.0*screenWidth/imageWidth;
            
            image.setLayoutParams(new LayoutParams((int) Math.floor(heightRatio*imageWidth), (int) Math.floor(heightRatio*imageHeight)));
            
            image.setImageResource(data[position]);
            
            return vi;
            */
            return null;
        }
    }
}
