/* 健康*/
package com.damy.jiankang;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;


public class NamesettingActivity extends BaseActivity implements MyActivityMethods {

    ImageButton m_imgbtnBack;
    Button      m_savebtn;
    EditText 	txt_name;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_namesetting);

        initActivity(R.id.rlMain);
        initControl();
        initHandler();        
        
	}

    @Override
    public void initControl() {
        m_imgbtnBack = (ImageButton) findViewById(R.id.imgbtnBack);
        m_savebtn = (Button) findViewById(R.id.btn_name_save);
        txt_name = (EditText)findViewById(R.id.txt_user_name);
        
        txt_name.setText(Global.Cur_UserName);
    }

    @Override
    public void initHandler() {
        m_imgbtnBack.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	onClickBack();
            }
        });
        
        m_savebtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	new LoadResponseThread(NamesettingActivity.this).start();
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
    
    public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			onClickBack();
		}
	}
	
	public void getResponseJSON() {
		try {	
				
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_EDITUSERINFO;
				
				JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);
				
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
				
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
	            if (m_nResponse == ResponseRet.RET_SUCCESS) {	            		            	
	            	Global.Cur_UserName = txt_name.getText().toString();

				}
	            
			} catch (JSONException e) {
				e.printStackTrace();
				m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
			}		
			
			
	}
	
	
	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();		
    	
		requestObj.put("uid", Global.Cur_UserId);
		requestObj.put("type", "username");
		requestObj.put("data", txt_name.getText().toString());
		
		return requestObj;
	}
	
}
