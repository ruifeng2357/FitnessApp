/* 健康*/
package com.damy.jiankang;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.damy.Utils.ResolutionSet;
import com.damy.Utils.WheelPicker.NumericWheelAdapter;
import com.damy.Utils.WheelPicker.WheelAdapter;
import com.damy.Utils.WheelPicker.WheelView;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;

public class WeightinfoActivity extends BaseActivity implements MyActivityMethods {

	private ImageButton 			m_imgbtnBack;
    private Button      			m_savebtn;
    private TextView    			lbl_greeting;    
    private TextView    			txt_weight;
    private TextView    			txt_waist;
    private TextView    			txt_dialogtitle;
    
    private double  				weight = -1;
    private double  				waist = -1;
    
    private RelativeLayout			rl_maskLayer;
    private RelativeLayout			rl_dialog;
    
    private RelativeLayout			rl_weight_picker;
    private RelativeLayout			rl_waistline_picker;
    
    private WheelView				wheel_weight_int;
    private WheelView				wheel_weight_float;
    
    private WheelView				wheel_waistline_int;
    private WheelView				wheel_waistline_float;
    
    private final int 				START_INT = 5;
    private final int 				END_INT = 300;
    
    private float					default_weight = 60;
    private float					default_waistline = 70;
    
    private int						m_nCurDialog = 0;

    
    private enum REQ_TYPE{REQ_GETWEIGHTINFO, REQ_SETWEIGHTINFO};
	
	private REQ_TYPE					m_reqType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weightinfo);

        initActivity(R.id.rlMain);
        initControl();
        initHandler();        
        
	}

    @Override
    public void initControl() {
        m_imgbtnBack = (ImageButton) findViewById(R.id.imgbtnBack);
        m_savebtn = (Button) findViewById(R.id.btn_save_weightinfo);
        lbl_greeting = (TextView)findViewById(R.id.lblGreeting);        
        txt_weight = (TextView)findViewById(R.id.txt_weightinfo_weight);
        txt_waist = (TextView)findViewById(R.id.txt_weightinfo_waist);
        txt_dialogtitle = (TextView)findViewById(R.id.lbl_weightinfo_pickerdialog_title);
                
        Date curDate = new Date();
		String strDate = String.valueOf(curDate.getYear() + 1900) + getResources().getString(R.string.common_year) + String.valueOf(curDate.getMonth() + 1) + getResources().getString(R.string.common_month) + String.valueOf(curDate.getDate()) + getResources().getString(R.string.common_day);
		
		lbl_greeting.setText(Global.Cur_UserName + getResources().getString(R.string.common_greeting) + "!" + getResources().getString(R.string.common_jintianshi) + strDate);	
        
		rl_maskLayer = (RelativeLayout)findViewById(R.id.rl_weightinfo_mask);
		rl_dialog = (RelativeLayout)findViewById(R.id.rl_weightinfo_pickerdialog);
		
		
		
		rl_maskLayer.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
		
		
		int fntSize = (int)(getResources().getDimension(R.dimen.wheelview_fnt_size) * ResolutionSet.fYpro + 0.50001);
		
		wheel_weight_int = (WheelView)findViewById(R.id.wheel_weightinfo_weight_int);
		wheel_weight_int.setDefTextSize(fntSize);
		wheel_weight_int.setAdapter(new NumericWheelAdapter(START_INT, END_INT));
		wheel_weight_int.setLabel(" .");
		wheel_weight_int.setCurrentItem((int)default_weight - START_INT);
		
		wheel_weight_float = (WheelView)findViewById(R.id.wheel_weightinfo_weight_float);
		wheel_weight_float.setDefTextSize(fntSize);
		wheel_weight_float.setAdapter(new NumericWheelAdapter(0, 9));
		wheel_weight_float.setLabel("   " + getResources().getString(R.string.common_unit_gongjin));
		wheel_weight_float.setCurrentItem(0);
		
		
		wheel_waistline_int = (WheelView)findViewById(R.id.wheel_weightinfo_waistline_int);
		wheel_waistline_int.setDefTextSize(fntSize);
		wheel_waistline_int.setAdapter(new NumericWheelAdapter(START_INT, END_INT));
		wheel_waistline_int.setLabel(" .");
		wheel_waistline_int.setCurrentItem((int)default_waistline - START_INT);
		
		wheel_waistline_float = (WheelView)findViewById(R.id.wheel_weightinfo_waistline_float);
		wheel_waistline_float.setDefTextSize(fntSize);
		wheel_waistline_float.setAdapter(new NumericWheelAdapter(0, 9));
		wheel_waistline_float.setLabel("   " + getResources().getString(R.string.common_unit_centimeter));
		wheel_waistline_float.setCurrentItem(0);
		
		txt_weight.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
            	m_nCurDialog = 0;
                onClickShowDialog();
            }
        });
		
		txt_waist.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
            	m_nCurDialog = 1;
                onClickShowDialog();
            }
        });
		
		Button btn_dialog_ok = (Button)findViewById(R.id.btn_weightinfo_pickerdialog_ok);
        Button btn_dialog_cancel = (Button)findViewById(R.id.btn_weightinfo_pickerdialog_cancel);
        
        btn_dialog_ok.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	onClickDialogOk();
            }
        });
        
        btn_dialog_cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	onClickDialogCancel();
            }
        });
        
        rl_maskLayer.setVisibility(View.INVISIBLE);
		rl_dialog.setVisibility(View.INVISIBLE);
		
		rl_weight_picker = (RelativeLayout)findViewById(R.id.rl_weightinfo_pickerdialog_weight);
		rl_waistline_picker= (RelativeLayout)findViewById(R.id.rl_weightinfo_pickerdialog_waistline);
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
        
        m_reqType = REQ_TYPE.REQ_GETWEIGHTINFO;
        new LoadResponseThread(WeightinfoActivity.this).start();
       
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
    	if ( txt_weight.getText().toString().length() == 0 )
    	{
    		showToastMessage(getResources().getString(R.string.activity_weightinfo_required_weight));
    		return;
    	}
    	
    	if ( txt_waist.getText().toString().length() == 0 )
    	{
    		showToastMessage(getResources().getString(R.string.activity_weightinfo_required_waistline));
    		return;
    	}
    	
    	m_reqType = REQ_TYPE.REQ_SETWEIGHTINFO;
    	new LoadResponseThread(WeightinfoActivity.this).start();
    }
    
    private void onClickShowDialog()
    {
    	float cur_val = 0;
    	String strTitle = "";
    	
    	if ( m_nCurDialog == 0 )
    	{
    		if ( weight > 0 )
    			cur_val = (float)weight;
    		else
    			cur_val = default_weight;
    		
    		strTitle = getResources().getString(R.string.common_weight);
    		
    		wheel_weight_int.setCurrentItem((int)cur_val - START_INT);
    		float tmp = (float)cur_val - (int)cur_val;
    		wheel_weight_float.setCurrentItem((int)(tmp * 10));
    		
    		rl_weight_picker.setVisibility(View.VISIBLE);
    		rl_waistline_picker.setVisibility(View.INVISIBLE);
    	}
    	else
    	{
    		if ( waist > 0 )
    			cur_val = (float)waist;
    		else
    			cur_val = default_waistline;
    		
    		strTitle = getResources().getString(R.string.common_waist);
    		
    		wheel_waistline_int.setCurrentItem((int)cur_val - START_INT);
    		float tmp = (float)cur_val - (int)cur_val;
    		wheel_waistline_float.setCurrentItem((int)(tmp * 10));
    		
    		rl_weight_picker.setVisibility(View.INVISIBLE);
    		rl_waistline_picker.setVisibility(View.VISIBLE);
    	}
    	
    	
    	txt_dialogtitle.setText(strTitle);
		
		rl_maskLayer.setVisibility(View.VISIBLE);
		rl_dialog.setVisibility(View.VISIBLE);
    }
    
    private void onClickDialogOk()
    {
    	if ( m_nCurDialog == 0 )
    		weight = (wheel_weight_int.getCurrentItem() + START_INT) + ((float)wheel_weight_float.getCurrentItem() / (float)10);
    	else
    		waist = (wheel_waistline_int.getCurrentItem() + START_INT) + ((float)wheel_waistline_float.getCurrentItem() / (float)10);
    	
    	SetData();
    	
    	onClickDialogCancel();
    }
    
    private void onClickDialogCancel()
    {
    	rl_maskLayer.setVisibility(View.INVISIBLE);
    	rl_dialog.setVisibility(View.INVISIBLE);
    }
    
	private void onSuccessWeightInfo()
	{		
		Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}
	
	private void SetData()
	{
		if ( weight > 0 )
			txt_weight.setText(String.format("%.01f", weight));
		
		if ( waist > 0 )
			txt_waist.setText(String.format("%.01f", waist));
	}
    
    public void refreshUI() {
		super.refreshUI();
		if (m_reqType == REQ_TYPE.REQ_GETWEIGHTINFO) {
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				SetData();
			}
		}
		else if (m_reqType == REQ_TYPE.REQ_SETWEIGHTINFO) {
			onSuccessWeightInfo();
		}
	}
	
	public void getResponseJSON() {
		try {	
				if (m_reqType == REQ_TYPE.REQ_GETWEIGHTINFO) {
					m_nResponse = ResponseRet.RET_SUCCESS;
					
					Date curDate = new Date();
					String strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-" + String.valueOf(curDate.getDate());
					
					String strRequest = HttpConnUsingJSON.REQ_GETWEIGHTINFO;
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
						
						weight = dataObject.getDouble("weight");
						waist = dataObject.getDouble("waist");
					}
				}
				else if (m_reqType == REQ_TYPE.REQ_SETWEIGHTINFO) {
					m_nResponse = ResponseRet.RET_SUCCESS;
					
					String strRequest = HttpConnUsingJSON.REQ_SETWEIGHTINFO;
					
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
		requestObj.put("weight", txt_weight.getText().toString());
		requestObj.put("waist", txt_waist.getText().toString());		
		
		
		return requestObj;
	}
	
}
