/* 健康*/
package com.damy.jiankang;

import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.ResolutionSet;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class ChangePasswordActivity extends BaseActivity {

	EditText txt_old;
	EditText txt_new;
	EditText txt_retype;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changepassword);
		
		initActivity(R.id.rl_changepassword);
		
		txt_old = (EditText)findViewById(R.id.edit_changepassword_old);
		txt_new = (EditText)findViewById(R.id.edit_changepassword_new);
		txt_retype = (EditText)findViewById(R.id.edit_changepassword_retype);		
		
		initControls();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_changepassword_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		
		Button btn_save = (Button)findViewById(R.id.btn_changepassword_save);

		btn_save.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSave();
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

		Intent myinfo_activity = new Intent(this, MyInfoActivity.class);
		startActivity(myinfo_activity);	
		finish();		

	}

	private void onClickSave()
	{
		if ( txt_old.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_password));
			return;
		}
		
		if ( txt_new.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_password));
			return;
		}
		
		if (txt_new.getText().toString().length() < 5 || txt_new.getText().toString().length() > 16)
		{
			showToastMessage(getResources().getString(R.string.error_password_charactercount));
			return;
		}
		
		if (!txt_new.getText().toString().equals(txt_retype.getText().toString())) 
		{
			showToastMessage(getResources().getString(R.string.error_notmatch_password));
			return;
		}
		
		new LoadResponseThread(ChangePasswordActivity.this).start();
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			onClickBack();		
		}
	}
	
	public void getResponseJSON() {
		try {
			m_nResponse = ResponseRet.RET_SUCCESS;
			
			String strRequest = HttpConnUsingJSON.REQ_CHANGEPASSWORD;
						
			JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);
			
			if (response == null) {
				m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
				return;
			}
			
			m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
            if (m_nResponse == ResponseRet.RET_SUCCESS) {
            	
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
		    	
		requestObj.put("uid", Global.Cur_UserId);
		requestObj.put("oldpass", txt_old.getText().toString());
		requestObj.put("newpass", txt_new.getText().toString());
		
		return requestObj;
	}
	
}
