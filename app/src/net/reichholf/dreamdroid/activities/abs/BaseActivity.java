package net.reichholf.dreamdroid.activities.abs;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import net.reichholf.dreamdroid.DreamDroid;
import net.reichholf.dreamdroid.R;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import de.duenndns.ssl.MemorizingTrustManager;

/**
 * Created by Stephan on 06.11.13.
 */
public class BaseActivity extends ActionBarActivity {
	private MemorizingTrustManager mTrustManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			// set location of the keystore
			MemorizingTrustManager.setKeyStoreFile("private", "sslkeys.bks");
			// register MemorizingTrustManager for HTTPS
			SSLContext sc = SSLContext.getInstance("TLS");
			mTrustManager = new MemorizingTrustManager(this);
			sc.init(null, new X509TrustManager[] { mTrustManager },
					new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onCreate(savedInstanceState);
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				DreamDroid.PREFS_KEY_ENABLE_ANIMATIONS, true))
			overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
	}

	public void onPause(){
		mTrustManager.unbindDisplayActivity(this);
		super.onPause();
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				DreamDroid.PREFS_KEY_ENABLE_ANIMATIONS, true))
			overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
	}

	public void onResume(){
		mTrustManager.bindDisplayActivity(this);
		super.onResume();
	}

    public void setSupportProgressBarIndeterminateVisibility(boolean visible){
    }
}
