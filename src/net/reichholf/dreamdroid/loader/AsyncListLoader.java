/* © 2010 Stephan Reichholf <stephan at reichholf dot net>
 * 
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package net.reichholf.dreamdroid.loader;

import java.util.ArrayList;

import net.reichholf.dreamdroid.DreamDroid;
import net.reichholf.dreamdroid.helpers.ExtendedHashMap;
import net.reichholf.dreamdroid.helpers.SimpleHttpClient;
import net.reichholf.dreamdroid.helpers.enigma2.requestinterfaces.ListRequestInterface;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * @author sre
 * 
 */
public class AsyncListLoader extends AsyncTaskLoader<ArrayList<ExtendedHashMap>> {
	protected ArrayList<ExtendedHashMap> mList;
	protected ListRequestInterface mListRequestHandler;
	protected boolean mRequireLocsAndTags;
	protected ArrayList<String> mLocations;
	protected ArrayList<String> mTags;
	protected SimpleHttpClient mShc;
	protected ArrayList<NameValuePair> mParams;

	/**
	 * @param context
	 */
	public AsyncListLoader(Context context, ListRequestInterface listRequestHandler, boolean requireLocsAndTags,
			Bundle args) {
		super(context);
		mListRequestHandler = listRequestHandler;
		mRequireLocsAndTags = requireLocsAndTags;
		mShc = new SimpleHttpClient();

		if (args != null && args.containsKey("params"))
			mParams = (ArrayList<NameValuePair>) args.getSerializable("params");
		else
			mParams = new ArrayList<NameValuePair>();
	}

	@Override
	protected void onStartLoading() {
		forceLoad();
	}
	
    @Override protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

	@Override
	public ArrayList<ExtendedHashMap> loadInBackground() {
		if (mListRequestHandler == null) {
			throw new UnsupportedOperationException(
					"Method doInBackground not re-implemented while no ListRequestHandler has been given");
		}

		mList = new ArrayList<ExtendedHashMap>();

		String xml = mListRequestHandler.getList(mShc, mParams);
		if (xml != null) {

			mList.clear();

			if (mListRequestHandler.parseList(xml, mList)) {
				if (mRequireLocsAndTags) {
					if (DreamDroid.getLocations().size() == 0) {
						if (!DreamDroid.loadLocations(mShc)) {
							Log.e(DreamDroid.LOG_TAG, "ERROR loading locations");
						}
					}

					if (DreamDroid.getTags().size() == 0) {
						if (!DreamDroid.loadTags(mShc)) {
							Log.e(DreamDroid.LOG_TAG, "ERROR loading tags");
						}
					}
				}
				return mList;
			}
		}
		return null;
	}

}