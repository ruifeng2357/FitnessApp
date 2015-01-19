/* 健康*/
package com.damy.jiankang;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.ResponseRet;

import com.damy.Utils.ResolutionSet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class BaseActivity extends Activity {
	
	HttpConnUsingJSON	m_HttpConnUsingJSON;
	int					m_nResponse = ResponseRet.RET_SUCCESS;
	
	boolean m_bInitialized = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		m_HttpConnUsingJSON = new HttpConnUsingJSON(this);
	}
	
	public JSONObject makeRequestJSON() throws JSONException{return null;}
	public void getResponseJSON() {}
	public void refreshUI() {
		if (isNetworkConnected() || isWiFiConnected()) {
			switch (m_nResponse) {
			case ResponseRet.RET_SUCCESS:
				//showToastMessage(getResources().getString(R.string.common_success));
				break;
			case ResponseRet.RET_FAILURE:
				showToastMessage(getResources().getString(R.string.common_fail));
				break;
			case ResponseRet.RET_NOUSER:
				showToastMessage(getResources().getString(R.string.error_no_user));
				break;
			case ResponseRet.RET_DUPLICATEUSER:
				showToastMessage(getResources().getString(R.string.error_duplicate_userid));
				break;
			case ResponseRet.RET_NOIMAGE:
				showToastMessage(getResources().getString(R.string.error_no_image));
				break;
			case ResponseRet.RET_OLDPASSNOTMATCH:
				showToastMessage(getResources().getString(R.string.error_oldpassword));
				break;
            case ResponseRet.RET_SUCCFBLOGIN:
                break;
			/*case ResponseRet.RET_NOINFO:
				showToastMessage(getResources().getString(R.string.error_no_info));
				break;*/
			default:
				break;
			}
		} else {
			showToastMessage(getResources().getString(R.string.common_fail));
		}
	}
	
	public void showToastMessage(String strMsg) {
		Toast.makeText(this, strMsg, Toast.LENGTH_LONG).show();
	}
	
	public void hideSoftKeyboard(/*View view*/) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (getCurrentFocus() != null)
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken()/*view.getWindowToken()*/, 0);
	}

	public boolean isNetworkConnected()
	{
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null)
		{
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if (ni != null && ni.isConnected())
				return true;
		}
		
		return false;
	}
	
	public boolean isWiFiConnected()
	{
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null)
		{
			NetworkInfo networkinfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if ((networkinfo != null) && (networkinfo.isAvailable() == true) && (networkinfo.getState() == NetworkInfo.State.CONNECTED))
				return true;
		}
		
		return false;
	}
	
	public String EncodeToUTF8(String str)
	{
		String tmp;
		
		try {
			tmp = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			tmp = str;
		}
		
		return tmp;
	}

    public void initActivity (final int i_nRootLayoutID) {
        final View rlayout = findViewById(i_nRootLayoutID);
        rlayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (m_bInitialized == false)
                        {
                            Rect r = new Rect();
                            rlayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height());
                            ResolutionSet._instance.iterateChild(findViewById(i_nRootLayoutID));
                            m_bInitialized = true;
                        }
                    }
                }
        );
    }
    
}
