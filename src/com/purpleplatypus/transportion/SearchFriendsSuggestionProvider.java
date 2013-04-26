package com.purpleplatypus.transportion;

import java.text.DecimalFormat;
import java.util.List;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

public class SearchFriendsSuggestionProvider extends SearchRecentSuggestionsProvider {
	static final String TAG = SearchFriendsSuggestionProvider.class.getSimpleName();
	public static final String AUTHORITY = SearchFriendsSuggestionProvider.class.getName();
	public static final int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;
	private static final String[] COLUMNS = {
		"_id", // must include this column
		SearchManager.SUGGEST_COLUMN_TEXT_1,
		SearchManager.SUGGEST_COLUMN_TEXT_2,
		SearchManager.SUGGEST_COLUMN_ICON_1,
		SearchManager.SUGGEST_COLUMN_INTENT_DATA,
		SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
		SearchManager.SUGGEST_COLUMN_SHORTCUT_ID };

	public SearchFriendsSuggestionProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		String query = uri.getLastPathSegment().toLowerCase();
		if (query == null || query.length() == 0) {
			query = "";
		}

		MatrixCursor cursor = new MatrixCursor(COLUMNS);
		for (int i = 0; i < FriendsActivity.friendsJsons.length(); i++) {
      		String profileName = null, profileId = null;
      		try {
      			profileName = FriendsActivity.friendsJsons.getJSONObject(i).getString("name");
      			profileId = FriendsActivity.friendsJsons.getJSONObject(i).getString("id");
      			
      		} catch (Exception e) {
      			System.out.println("error while suggestions is trying to sort through friends list for names that match query: " + e.toString());
      			return cursor;
      		}
			if (profileName.toLowerCase().contains(query.toLowerCase())) {
				String emission = profileId + " pounds of carbon emitted";
				String name = profileName;
				Integer id = new Integer(i);
				cursor.addRow(createRow(id, name, emission, ""));
			}
		}
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	private Object[] createRow(Integer id, String text1, String text2,
			String name) {
		return new Object[] { id, // _id
				text1, // text1
				text2, // text2
				R.drawable.social_person,
				text1, "android.intent.action.VIEW", // action
				SearchManager.SUGGEST_NEVER_MAKE_SHORTCUT };
	}
}