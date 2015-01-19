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

public class WeightActivity extends BaseActivity {

	public float				cur_weight = (float)65.0;
	
	private final int 			START_INT = 5;
    private final int 			END_INT = 300;
    
    private WheelView 			wheel_int;
    private WheelView 			wheel_float;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weight);
		
		initActivity(R.id.rl_weight);
		
		initControls();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_weight_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		
		Button btn_next = (Button)findViewById(R.id.btn_weight_next);

		btn_next.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickNext();
        	}
        });
		
		Button btn_save = (Button)findViewById(R.id.btn_weight_save);
		
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
		
		cur_weight = Global.Cur_UserWeight;
		
		int fntSize = (int)(getResources().getDimension(R.dimen.wheelview_fnt_size) * ResolutionSet.fYpro + 0.50001);
		
		wheel_int = (WheelView) findViewById(R.id.wheel_weight_int);
		wheel_int.setDefTextSize(fntSize);
		wheel_int.setAdapter(new NumericWheelAdapter(START_INT, END_INT));
		wheel_int.setLabel(" .");
		wheel_int.setCurrentItem((int)cur_weight - START_INT);
		
		wheel_float = (WheelView) findViewById(R.id.wheel_weight_float);
		wheel_float.setDefTextSize(fntSize);
		wheel_float.setAdapter(new NumericWheelAdapter(0, 9));
		wheel_float.setLabel("   " + getResources().getString(R.string.common_unit_gongjin));
		float tmp = (float)cur_weight - (int)cur_weight;
		wheel_float.setCurrentItem((int)(tmp * 10));
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
			Intent height_activity = new Intent(this, HeightActivity.class);
			startActivity(height_activity);	
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
		
		Global.Cur_UserWeight = cur_weight;
		
		Intent waistline_activity = new Intent(this, WaistlineActivity.class);
		startActivity(waistline_activity);	
		finish();
	}
	
	private void onClickSave()
	{
		getSelectedWeight();
		
		new LoadResponseThread(WeightActivity.this).start();
	}
	
	private void getSelectedWeight()
	{
		cur_weight = (wheel_int.getCurrentItem() + START_INT) + ((float)wheel_float.getCurrentItem() / (float)10);
	}
	
	private void onSuccessWeight()
	{
		Intent myinfo_activity = new Intent(this, MyInfoActivity.class);
		startActivity(myinfo_activity);	
		finish();
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			Global.Cur_UserWeight = cur_weight;
			onSuccessWeight();
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
		requestObj.put("type", "weight_now");
		requestObj.put("data", cur_weight);
		
		return requestObj;
	}
}
