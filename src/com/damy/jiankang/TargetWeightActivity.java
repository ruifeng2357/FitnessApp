/* 健康*/
package com.damy.jiankang;

import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.ResolutionSet;
import com.damy.Utils.WheelPicker.NumericWheelAdapter;
import com.damy.Utils.WheelPicker.WheelView;
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
import android.widget.ImageButton;
import android.widget.TextView;

public class TargetWeightActivity extends BaseActivity {
	
	public float					cur_targetweight = 70;
	
	private final int 				START_INT = 5;
    private final int 				END_INT = 300;
    
    private WheelView 				wheel_int;
    private WheelView 				wheel_float;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_targetweight);
		
		initActivity(R.id.rl_targetweight);
		
		initControls();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_targetweight_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		
		Button btn_next = (Button)findViewById(R.id.btn_targetweight_next);

		btn_next.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickNext();
        	}
        });
		
		Button btn_save = (Button)findViewById(R.id.btn_targetweight_save);
		
		btn_save.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSave();
        	}
        });
		
		if ( Global.registering_flag )
		{
			btn_next.setVisibility(View.VISIBLE);
			btn_save.setVisibility(View.INVISIBLE);
		}
		else
		{
			btn_next.setVisibility(View.INVISIBLE);
			btn_save.setVisibility(View.VISIBLE);
		}
		
		cur_targetweight= Global.Cur_UserWeightTarget;
		
		int fntSize = (int)(getResources().getDimension(R.dimen.wheelview_fnt_size) * ResolutionSet.fYpro + 0.50001);
		
		wheel_int = (WheelView) findViewById(R.id.wheel_targetweight_int);
		wheel_int.setDefTextSize(fntSize);
		wheel_int.setAdapter(new NumericWheelAdapter(START_INT, END_INT));
		wheel_int.setLabel(" .");
		wheel_int.setCurrentItem((int)cur_targetweight - START_INT);
		
		wheel_float = (WheelView) findViewById(R.id.wheel_targetweight_float);
		wheel_float.setDefTextSize(fntSize);
		wheel_float.setAdapter(new NumericWheelAdapter(0, 9));
		wheel_float.setLabel("   " + getResources().getString(R.string.common_unit_gongjin));
		float tmp = (float)cur_targetweight - (int)cur_targetweight;
		wheel_float.setCurrentItem((int)(tmp * 10));
		
		float sample_weight = 0.f;
		sample_weight = (float)22 * ((float)Global.Cur_UserHeight / 100) * ((float)Global.Cur_UserHeight / 100);
		
		float sample_min = sample_weight * (float)0.9;
		float sample_max = sample_weight * (float)1.1;
		
		TextView lbl_note2 = (TextView)findViewById(R.id.lbl_targetweight_note2);
		lbl_note2.setText(String.format("%s( %.01f~%.01f%s )", getResources().getString(R.string.activity_targetweight_note2), sample_min, sample_max, getResources().getString(R.string.common_unit_gongjin)) );
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
		if ( Global.registering_flag )
		{
			Intent healthinfo_activity = new Intent(this, HealthinfoActivity.class);
			startActivity(healthinfo_activity);	
			finish();
		}
		else
		{
			Intent myinfo_activity = new Intent(this, MyInfoActivity.class);
			startActivity(myinfo_activity);	
			finish();
		}
	}

	private void onClickNext()
	{
		getSelectedWeight();
		
		Global.Cur_UserWeightTarget = cur_targetweight;
		
		Intent targetwaistline_activity = new Intent(this, TargetWaistlineActivity.class);
		startActivity(targetwaistline_activity);	
		finish();
	}
	
	private void onClickSave()
	{
		getSelectedWeight();
		
		new LoadResponseThread(TargetWeightActivity.this).start();
	}
	
	private void getSelectedWeight()
	{
		cur_targetweight = (wheel_int.getCurrentItem() + START_INT) + ((float)wheel_float.getCurrentItem() / (float)10);
	}
	
	private void onSuccessTargetweight()
	{
		Intent myinfo_activity = new Intent(this, MyInfoActivity.class);
		startActivity(myinfo_activity);	
		finish();
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			Global.Cur_UserWeightTarget = cur_targetweight;
			onSuccessTargetweight();
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
            	
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
	
	
	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();		
    	
		requestObj.put("uid", Global.Cur_UserId);
		requestObj.put("type", "weight_target");
		requestObj.put("data", cur_targetweight);
		
		return requestObj;
	}
}
