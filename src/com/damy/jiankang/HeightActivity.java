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

public class HeightActivity extends BaseActivity {
	
	public float				cur_height = 170;
	
	private final int 			START_INT = 50;
    private final int 			END_INT = 250;
    
    private WheelView 			wheel_int;
    private WheelView 			wheel_float;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_height);
		
		initActivity(R.id.rl_height);
		
		initControls();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_height_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		
		Button btn_next = (Button)findViewById(R.id.btn_height_next);

		btn_next.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickNext();
        	}
        });
		
		Button btn_save = (Button)findViewById(R.id.btn_height_save);
		
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
		
		cur_height = Global.Cur_UserHeight;
		
		int fntSize = (int)(getResources().getDimension(R.dimen.wheelview_fnt_size) * ResolutionSet.fYpro + 0.50001);
		
		wheel_int = (WheelView) findViewById(R.id.wheel_height_int);
		wheel_int.setDefTextSize(fntSize);
		wheel_int.setAdapter(new NumericWheelAdapter(START_INT, END_INT));
		wheel_int.setLabel(" .");
		wheel_int.setCurrentItem((int)cur_height - START_INT);
		
		wheel_float = (WheelView) findViewById(R.id.wheel_height_float);
		wheel_float.setDefTextSize(fntSize);
		wheel_float.setAdapter(new NumericWheelAdapter(0, 9));
		wheel_float.setLabel("   " + getResources().getString(R.string.common_unit_centimeter));
		float tmp = (float)cur_height - (int)cur_height;
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
		if ( Global.registering_flag)
		{
			Intent birthday_activity = new Intent(this, BirthdayActivity.class);
			startActivity(birthday_activity);
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
		getSelectedHeight();
		
		Global.Cur_UserHeight = cur_height;
		
		Intent weight_activity = new Intent(this, WeightActivity.class);
		startActivity(weight_activity);	
		finish();
	}
	
	private void onClickSave()
	{
		getSelectedHeight();
		
		new LoadResponseThread(HeightActivity.this).start();
	}
	
	private void getSelectedHeight()
	{
		cur_height = (wheel_int.getCurrentItem() + START_INT) + ((float)wheel_float.getCurrentItem() / (float)10);
	}
	
	private void onSuccessHeight()
	{
		Intent myinfo_activity = new Intent(this, MyInfoActivity.class);
		startActivity(myinfo_activity);	
		finish();
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			Global.Cur_UserHeight = cur_height;
			onSuccessHeight();
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
		requestObj.put("type", "high");
		requestObj.put("data", cur_height);
		
		return requestObj;
	}
}
