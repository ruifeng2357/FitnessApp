/* 健康*/
package com.damy.jiankang;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class RegisterActivity extends BaseActivity {

	EditText txt_userid;
	EditText txt_password;
	EditText txt_retype_password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		initActivity(R.id.rl_register);
		
		txt_userid = (EditText)findViewById(R.id.edit_register_userid);
		txt_password = (EditText)findViewById(R.id.edit_register_password);
		txt_retype_password = (EditText)findViewById(R.id.edit_register_rpassword);
		
		
		initControls();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_register_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		
		Button btn_register = (Button)findViewById(R.id.btn_register_register);

		btn_register.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickRegister();
        		//onSuccessRegister();
        	}
        });
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	onClickBack();
        	return true;
        }
        return false;
    }
	
	private void onClickBack()
	{
		Intent login_activity = new Intent(this, LoginActivity.class);
		startActivity(login_activity);	
		finish();
	}

	private void onClickRegister()
	{
		if ( txt_userid.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_userid));
			return;
		}
		
		if ( txt_userid.getText().toString().length() != 10 )
		{
			showToastMessage(getResources().getString(R.string.error_userid_length));
			return;
		}
		
		if ( txt_password.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_password));
			return;
		}
		
		if (txt_password.getText().toString().length() < 5 || txt_password.getText().toString().length() > 16)
		{
			showToastMessage(getResources().getString(R.string.error_password_charactercount));
			return;
		}
		
		if (!txt_password.getText().toString().equals(txt_retype_password.getText().toString())) 
		{
			showToastMessage(getResources().getString(R.string.error_notmatch_password));
			return;
		}
		
		new LoadResponseThread(RegisterActivity.this).start();
	}
	
	private void onSuccessRegister()
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
        
		SharedPreferences pref = getSharedPreferences(Global.STR_SETTING, 0);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean(Global.STR_LOGIN, true);        
        edit.putString(Global.STR_USERID, Global.Cur_UserLoginId);
        edit.putString(Global.STR_PASSWORD, Global.Cur_UserPassword);
        edit.commit();
		
		Intent sex_activity = new Intent(this, SexActivity.class);
		startActivity(sex_activity);	
		finish();
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {			
							
			onSuccessRegister();		
		 }
		else if (m_nResponse == ResponseRet.RET_DUPLICATEUSER) {
			showToastMessage(getResources().getString(R.string.error_duplicate_userid));
		}
	}
	
	public void getResponseJSON() {
		try {
			m_nResponse = ResponseRet.RET_SUCCESS;
			
			String strRequest = HttpConnUsingJSON.REQ_REGISTER;
						
			JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);
			
			if (response == null) {
				m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
				return;
			}
			
			m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
            if (m_nResponse == ResponseRet.RET_SUCCESS) {
            	
            	JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
            	
            	Global.Cur_UserId = dataObject.getInt("uid");
            	Global.Cur_UserPassword = txt_password.getText().toString();
            	Global.Cur_UserLoginId = txt_userid.getText().toString();
			}
            else if (m_nResponse == ResponseRet.RET_DUPLICATEUSER) {
            	
            }
            	
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
	
	
	public JSONObject makeRequestJSON() throws JSONException {
		
		JSONObject requestObj = new JSONObject();		
		    	
		requestObj.put("userid", txt_userid.getText().toString());
		requestObj.put("password", txt_password.getText().toString());
		
		return requestObj;
	}
	
}
