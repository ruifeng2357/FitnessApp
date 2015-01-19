/* 健康*/
package com.damy.jiankang;

import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.ResolutionSet1;
import com.damy.Utils.WheelPicker.WheelView;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

public class SplashActivity extends BaseActivity {
	
	String  str_userid = "";
	String  str_password= "";
	Boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		try
		{
	        DisplayMetrics metrics = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(metrics);
	        
	        ResolutionSet1._instance.setResolution(metrics.widthPixels, metrics.heightPixels); 
	        
	        WheelView.setAdditionalItemHeightwithResolution(ResolutionSet1.fPro);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

        Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message message)
            {
                onClickNext();
            }
        };

        handler.sendEmptyMessageDelayed(0, 2000);
	}
	
	private void onClickNext()
	{
		SharedPreferences pref = getSharedPreferences(Global.STR_SETTING, 0);
		 
		 flag = pref.getBoolean(Global.STR_LOGIN, false);
		 str_userid = pref.getString(Global.STR_USERID, "");
		 str_password = pref.getString(Global.STR_PASSWORD, "");
		
		if ( !flag )
			ToLogin();
		else
			new LoadResponseThread(SplashActivity.this).start();
	}
	
	private void ToLogin()
	{
		Intent login_activity = new Intent(this, LoginActivity.class);
		startActivity(login_activity);
		finish();
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			if ( Global.Cur_UserHeight != 0 )
			{
				Intent main_activity = new Intent(this, MainActivity.class);
				startActivity(main_activity);
			}
			else
			{
				Global.registering_flag = true;
				
				Global.Cur_UserName = "";
		        Global.Cur_UserSex = 1;
		        Global.Cur_UserBirthday = "1960-1-1";
		        Global.Cur_UserHeight = 170;
		        Global.Cur_UserWeight = 60;
		        Global.Cur_UserWaistline = 70;
		        Global.Cur_UserWeightTarget = 60;
		        Global.Cur_UserWaistlineTarget = 70;
		        Global.Cur_UserLevel = 0;
		        Global.Cur_UserImage = "";
		        
				Intent sex_activity = new Intent(this, SexActivity.class);
				startActivity(sex_activity);
			}
			finish();
		}
		else
			ToLogin();
	}
	
	public void getResponseJSON() {
		try {
			m_nResponse = ResponseRet.RET_SUCCESS;
			
			String strRequest = HttpConnUsingJSON.REQ_LOGIN;
			strRequest += "?userid=" + EncodeToUTF8(str_userid);
			strRequest += "&password=" + str_password;

			JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
			
			if (response == null) {
				m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
				return;
			}
			
			m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
            if (m_nResponse == ResponseRet.RET_SUCCESS) {
            	
            	JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
            	
            	Global.Cur_UserId = dataObject.getInt("uid");
            	Global.Cur_UserName = dataObject.getString("name");
            	Global.Cur_UserSex = dataObject.getInt("sex");
            	Global.Cur_UserAge = dataObject.getInt("age");
            	Global.Cur_UserBirthday = dataObject.getString("birthday");
            	Global.Cur_UserHeight = (float)dataObject.getDouble("high");
            	Global.Cur_UserLevel = dataObject.getInt("level");
            	Global.Cur_UserWaistline = (float)dataObject.getDouble("waist_now");
            	Global.Cur_UserWaistlineTarget = (float)dataObject.getDouble("waist_target");
            	Global.Cur_UserWeight = (float)dataObject.getDouble("weight_now");
            	Global.Cur_UserWeightTarget = (float)dataObject.getDouble("weight_target");
            	Global.Cur_UserImage = dataObject.getString("image");
            	if ( Global.Cur_UserImage.equals("null") )
            		Global.Cur_UserImage = "";
            	Global.Cur_UserPassword = str_password;
            	Global.Cur_UserLoginId = str_userid;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
}
