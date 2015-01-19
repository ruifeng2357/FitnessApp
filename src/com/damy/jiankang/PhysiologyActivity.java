/* 健康*/
package com.damy.jiankang;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;



public class PhysiologyActivity extends BaseActivity implements MyActivityMethods {

    ImageButton m_imgbtnBack;
    Button      m_savebtn;
    TextView    lbl_greeting;    
    EditText    txt_high_blood;
    EditText    txt_low_blood;
    EditText    txt_blood_sugar;
    RadioButton  rd_empty_yes;
    RadioButton  rd_empty_no;
    EditText    txt_high_cholesterol;
    EditText    txt_low_cholesterol;
    EditText    txt_glyceride;    
    TextView    lbl_description;
    int  high_blood = 0;
    int  low_blood = 0;
    int  blood_sugar = 0;
    int  empty = 0;
    int  high_cholesterol = 0;
    int  low_cholesterol = 0;
    int  glyceride = 0;
    
    private enum REQ_TYPE{REQ_GETPHYSIOLOGY, REQ_SETPHYSIOLOGY};
	
	private REQ_TYPE					m_reqType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_physiology);

        initActivity(R.id.rlMain);
        initControl();
        initHandler();    
        
        
	}

    @Override
    public void initControl() {
        m_imgbtnBack = (ImageButton) findViewById(R.id.imgbtnBack);
        m_savebtn = (Button) findViewById(R.id.btn_save_physiology);
        lbl_greeting = (TextView)findViewById(R.id.lblGreeting);        
        txt_high_blood = (EditText)findViewById(R.id.txt_high_blood);
        txt_low_blood = (EditText)findViewById(R.id.txt_low_blood);
        txt_blood_sugar = (EditText)findViewById(R.id.txt_blood_sugar);
        rd_empty_yes = (RadioButton)findViewById(R.id.rd_empty_yes);
        rd_empty_no = (RadioButton)findViewById(R.id.rd_empty_no);
        txt_high_cholesterol = (EditText)findViewById(R.id.txt_high_cholesterol);
        txt_low_cholesterol = (EditText)findViewById(R.id.txt_low_cholesterol);
        txt_glyceride = (EditText)findViewById(R.id.txt_glyceride);
        lbl_description = (TextView)findViewById(R.id.lbl_physiology_description);
        Date curDate = new Date();
		String strDate = String.valueOf(curDate.getYear() + 1900) + getResources().getString(R.string.common_year) + String.valueOf(curDate.getMonth() + 1) + getResources().getString(R.string.common_month) + String.valueOf(curDate.getDate()) + getResources().getString(R.string.common_day);
		
		lbl_greeting.setText(Global.Cur_UserName + getResources().getString(R.string.common_greeting) + "!" + getResources().getString(R.string.common_jintianshi) + strDate);	
              
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
            	onClickSave();
            }
        });
        
        
        
        /*
        txt_high_blood.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetDescription();
			}
		});
        
        txt_low_blood.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetDescription();
			}
		});
        
        txt_blood_sugar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetDescription();
			}
		});
        
        txt_high_cholesterol.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetDescription();
			}
		});
        
        txt_low_cholesterol.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetDescription();
			}
		});
        
        txt_glyceride.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetDescription();
			}
		});
        
        rd_empty_yes.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetDescription();
			}
		});
        
        rd_empty_no.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetDescription();
			}
		});
        */
        
        
        m_reqType = REQ_TYPE.REQ_GETPHYSIOLOGY;
        new LoadResponseThread(PhysiologyActivity.this).start();
       
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
		Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}
	
	private void onClickSave()
	{
		if ( txt_high_blood.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.activity_physilogy_required_highblood));
			return;
		}
		
		if ( txt_low_blood.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.activity_physilogy_required_lowblood));
			return;
		}
		
		if ( txt_blood_sugar.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.activity_physilogy_required_sugar));
			return;
		}
		
		if ( txt_high_cholesterol.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.activity_physilogy_required_highcholesterol));
			return;
		}
		
		if ( txt_low_cholesterol.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.activity_physilogy_required_lowcholesterol));
			return;
		}
		
		if ( txt_glyceride.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.activity_physilogy_required_glyceride));
			return;
		}
		
    	m_reqType = REQ_TYPE.REQ_SETPHYSIOLOGY;
    	new LoadResponseThread(PhysiologyActivity.this).start();
	}
	
	private void SetData()
	{
		txt_high_blood.setText(String.valueOf(high_blood));
		txt_low_blood.setText(String.valueOf(low_blood));
		txt_blood_sugar.setText(String.valueOf(blood_sugar));
		if (empty == 0)
		{
			rd_empty_yes.setChecked(true);
		}
		else
		{
			rd_empty_no.setChecked(true);
		}
			
		txt_high_cholesterol.setText(String.valueOf(high_cholesterol));
		txt_low_cholesterol.setText(String.valueOf(low_cholesterol));
		txt_glyceride.setText(String.valueOf(glyceride));
	}
	
	private void SetDescription()
	{
        String message = "";
        Boolean flag = false;
        
        try {
	        if ( txt_high_blood.getText().toString().length() > 0 )
	        	high_blood = Integer.valueOf(txt_high_blood.getText().toString());
	        else
	        	high_blood = 0;
	        
	        if ( txt_low_blood.getText().toString().length() > 0 )
	        	low_blood = Integer.parseInt(txt_low_blood.getText().toString());
	        else
	        	low_blood = 0;
	        
	        if ( txt_blood_sugar.getText().toString().length() > 0 )
	        	blood_sugar = Integer.parseInt(txt_blood_sugar.getText().toString());
	        else
	        	blood_sugar = 0;
	        
	        if ( txt_high_cholesterol.getText().toString().length() > 0 )
	        	high_cholesterol = Integer.parseInt(txt_high_cholesterol.getText().toString());
	        else
	        	high_cholesterol = 0;
	        
	        if ( txt_low_cholesterol.getText().toString().length() > 0 )
	        	low_cholesterol = Integer.parseInt(txt_low_cholesterol.getText().toString());
	        else
	        	low_cholesterol = 0;
	        
	        if ( txt_glyceride.getText().toString().length() > 0 )
	        	glyceride = Integer.parseInt(txt_glyceride.getText().toString());
	        else
	        	glyceride = 0;
        }
        catch(Exception e)
        {
        	
        }
        
        empty = rd_empty_yes.isChecked() ? 0 : 1;
        
        
        if ( high_blood > 0 && low_blood > 0)
        {
			if (high_blood < 120 && low_blood < 80)
                message = getResources().getString(R.string.common_blood_normal);
            else if (high_blood > 140 && low_blood > 90)
	                message = getResources().getString(R.string.common_blood_error2);
	        else 
	            	message = getResources().getString(R.string.common_blood_error1);
	     }
	        
        
        if (empty == 1)
        {        
        	if (message.length() > 0) message += "\n";
        	
            if (blood_sugar < 100)
                message += getResources().getString(R.string.common_blood_sugar_normal);
            else if (blood_sugar < 126)
                message += getResources().getString(R.string.common_blood_sugar_error1);
            else 
                message += getResources().getString(R.string.common_blood_sugar_error2);
        }
        else
        {
        	if (message.length() > 0) message += "\n";
        	
            if (blood_sugar < 140)
                message += getResources().getString(R.string.common_blood_sugar_normal);
            else if (blood_sugar < 200)
            	message += getResources().getString(R.string.common_blood_sugar_error1);
            else 
            	message += getResources().getString(R.string.common_blood_sugar_error2);
        }
      
        
        if ( high_cholesterol > 0 )
        {
        	if (message.length() > 0) message += "\n";
        	
	        if ((high_cholesterol >= 40 && Global.Cur_UserSex == 0) || (high_cholesterol >= 50 && Global.Cur_UserSex == 1))
	        	message += getResources().getString(R.string.common_high_cholesterol_normal);
        }
        
        
        if ( low_cholesterol > 0 )
        {
        	if (message.length() > 0) message += "\n";
        	
	        if (low_cholesterol < 130)
	        	message += getResources().getString(R.string.common_low_cholesterol_normal);
	        else if (low_cholesterol < 160)
	        	message += getResources().getString(R.string.common_low_cholesterol_error1);
	        else
	        	message += getResources().getString(R.string.common_low_cholesterol_error2);
        }
        

        int sum_cholestrol = low_cholesterol + high_cholesterol;

        if ( sum_cholestrol > 0 )
        {
        	if (message.length() > 0) message += "\n";
        	
	        if (sum_cholestrol < 200)
	        	message += getResources().getString(R.string.common_sum_cholesterol_normal);
	        else if (sum_cholestrol < 240)
	        	message += getResources().getString(R.string.common_sum_cholesterol_error1);
	        else
	        	message += getResources().getString(R.string.common_sum_cholesterol_error2);
        }

        if ( glyceride > 0 )
        {
        	if (message.length() > 0) message += "\n";
        	
	        if (glyceride < 150)
	        	message += getResources().getString(R.string.common_glyceride_normal);
	        else if (glyceride < 200)
	        	message += getResources().getString(R.string.common_glyceride_error1);
	        else
	        	message += getResources().getString(R.string.common_glyceride_error2);
        }
        

        
        if ( message.length() > 0 )
        	lbl_description.setText(message);
        else
        	lbl_description.setVisibility(View.INVISIBLE);
	}
    
    public void refreshUI() {
		super.refreshUI();
		if (m_reqType == REQ_TYPE.REQ_GETPHYSIOLOGY) {
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				SetData();
				SetDescription();
			}
			else if ( m_nResponse == ResponseRet.RET_NOINFO )
			{
				lbl_description.setVisibility(View.GONE);
			}
				
		}
		else if (m_reqType == REQ_TYPE.REQ_SETPHYSIOLOGY) {
			onClickBack();
		}
			
		
	}
	
	public void getResponseJSON() {
		try {	
				if (m_reqType == REQ_TYPE.REQ_GETPHYSIOLOGY) {
					m_nResponse = ResponseRet.RET_SUCCESS;
					
					Date curDate = new Date();
					String strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-" + String.valueOf(curDate.getDate());
					
					String strRequest = HttpConnUsingJSON.REQ_GETPHYSIOLOGYINFO;
					strRequest += "?uid=" + String.valueOf(Global.Cur_UserId);					
					strRequest += "&Date=" + strDate;
					
					JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
					
					if (response == null) {
						m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
						return;
					}
					
					m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
		            if (m_nResponse == ResponseRet.RET_SUCCESS) {	            		            	
		            	JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
						
						high_blood = dataObject.getInt("high_blood");
						low_blood = dataObject.getInt("low_blood");
						blood_sugar = dataObject.getInt("blood_sugar");
						empty = dataObject.getInt("empty");
						high_cholesterol = dataObject.getInt("high_cholesterol");
						low_cholesterol = dataObject.getInt("low_cholesterol");
						glyceride = dataObject.getInt("glyceride");						
			            
					}
				}
				else if (m_reqType == REQ_TYPE.REQ_SETPHYSIOLOGY) {
					m_nResponse = ResponseRet.RET_SUCCESS;
					
					String strRequest = HttpConnUsingJSON.REQ_SETPHYSIOLOGYINFO;
					
					JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);
					
					if (response == null) {
						m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
						return;
					}
					
					m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
		            if (m_nResponse == ResponseRet.RET_SUCCESS) {           		            	
	            	
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
			}
	}
	
	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();		
    	
		requestObj.put("uid", Global.Cur_UserId);
		requestObj.put("high_blood", txt_high_blood.getText().toString());
		requestObj.put("low_blood", txt_low_blood.getText().toString());
		requestObj.put("blood_sugar", txt_blood_sugar.getText().toString());		
		requestObj.put("high_cholesterol", txt_high_cholesterol.getText().toString());
		requestObj.put("low_cholesterol", txt_low_cholesterol.getText().toString());
		requestObj.put("glyceride", txt_glyceride.getText().toString());
		if (rd_empty_yes.isChecked())
			requestObj.put("empty", "0");
		else
			requestObj.put("empty", "1");
		//requestObj.put("data", txt_name.getText().toString());
		
		return requestObj;
	}
	
}
