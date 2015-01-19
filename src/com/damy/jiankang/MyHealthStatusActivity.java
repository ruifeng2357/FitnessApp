/* 健康*/
package com.damy.jiankang;


import java.util.Date;

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
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyHealthStatusActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myhealthstatus);
		
		initControls();
		
		initActivity(R.id.rl_myhealthstatus);
	}
	
	private void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_myhealthstatus_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		Button btn_next = (Button)findViewById(R.id.btn_myhealthstatus_next);

		btn_next.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		new LoadResponseThread(MyHealthStatusActivity.this).start();
        	}
        });
		
		displayContents();
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
		if (Global.registering_flag)
		{
			Intent dailysportintensity_activity = new Intent(this, DailySportIntensityActivity.class);
			startActivity(dailysportintensity_activity);	
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
		Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}
	
	private void displayContents()
	{
		TextView txt_bmi = (TextView)findViewById(R.id.lbl_myhealthstatus_bmi);
		TextView txt_bminote = (TextView)findViewById(R.id.lbl_myhealthstatus_bmi_note);
		TextView txt_age = (TextView)findViewById(R.id.lbl_myhealthstatus_age);
		TextView txt_weight = (TextView)findViewById(R.id.lbl_myhealthstatus_weight);
		TextView txt_height = (TextView)findViewById(R.id.lbl_myhealthstatus_height);
		TextView txt_targetweight = (TextView)findViewById(R.id.lbl_myhealthstatus_targetweight);
		TextView txt_curweight = (TextView)findViewById(R.id.lbl_myhealthstatus_currentweight);
		TextView txt_targetwaistline = (TextView)findViewById(R.id.lbl_myhealthstatus_targetwaistline);
		TextView txt_curwaistline = (TextView)findViewById(R.id.lbl_myhealthstatus_currentwaistline);
		RelativeLayout rl_line1 = (RelativeLayout)findViewById(R.id.rl_myhealthstatus_line1);
		RelativeLayout rl_line2 = (RelativeLayout)findViewById(R.id.rl_myhealthstatus_line2);
		RelativeLayout rl_line3 = (RelativeLayout)findViewById(R.id.rl_myhealthstatus_line3);
		RelativeLayout rl_targetweight = (RelativeLayout)findViewById(R.id.rl_myhealthstatus_targetweight);
		RelativeLayout rl_currentweight = (RelativeLayout)findViewById(R.id.rl_myhealthstatus_currentweight);
		
		float fBMI = Global.Cur_UserWeight / (((float)Global.Cur_UserHeight / 100) * ((float)Global.Cur_UserHeight / 100));

		txt_bmi.setText(String.format("%s : %.01f", getResources().getString(R.string.activity_myhealthstatus_bmi), fBMI));
		String strTmp = "";
		if ( fBMI < 18.5 )
			strTmp = getResources().getString(R.string.activity_myhealthstatus_bminote1);
		else if ( fBMI >= 18.5 && fBMI < 24 )
			strTmp = getResources().getString(R.string.activity_myhealthstatus_bminote2);
		else if ( fBMI >= 24 )
			strTmp = getResources().getString(R.string.activity_myhealthstatus_bminote3);
		txt_bminote.setText(strTmp);
		txt_age.setText(String.format("%s : %d%s", getResources().getString(R.string.activity_myhealthstatus_age), Global.Cur_UserAge, getResources().getString(R.string.common_old)));
		txt_weight.setText(String.format("%s : %.01f%s", getResources().getString(R.string.activity_myhealthstatus_weight), Global.Cur_UserWeight, getResources().getString(R.string.common_unit_gongjin)));
		txt_height.setText(String.format("%s : %.01f%s", getResources().getString(R.string.activity_myhealthstatus_height), Global.Cur_UserHeight, getResources().getString(R.string.common_unit_centimeter)));
		txt_targetweight.setText(String.format("%s %.01f%s", getResources().getString(R.string.activity_myhealthstatus_targetweight), Global.Cur_UserWeightTarget, getResources().getString(R.string.common_unit_gongjin)));
		txt_curweight.setText(String.format("%s %.01f%s", getResources().getString(R.string.activity_myhealthstatus_currentweight), Global.Cur_UserWeight, getResources().getString(R.string.common_unit_gongjin)));
		txt_targetwaistline.setText(String.format("%s : %.01f%s", getResources().getString(R.string.activity_myhealthstatus_targetwaistline), Global.Cur_UserWaistlineTarget, getResources().getString(R.string.common_unit_centimeter)));
		txt_curwaistline.setText(String.format("%s : %.01f%s", getResources().getString(R.string.activity_myhealthstatus_currentwaistline), Global.Cur_UserWaistline, getResources().getString(R.string.common_unit_centimeter)));
		
		int PARENT_WIDTH = 663;
		int LINEBAR_WIDTH = 626;
		int NOTE_WIDTH1 = 180;
		int NOTE_WIDTH2 = 220;
		
		float sample_weight = (float)22 * ((float)Global.Cur_UserHeight / 100) * ((float)Global.Cur_UserHeight / 100);
		
		float sample_min = sample_weight * (float)0.9;
		float sample_max = sample_weight * (float)1.1;
		
		float line_width = ((float)sample_max + (float)40) - ((float)sample_min - (float)10);
		float line_rate = (float)LINEBAR_WIDTH / (float)line_width;
		
		RelativeLayout.LayoutParams layoutParams;
		layoutParams = (RelativeLayout.LayoutParams) rl_line1.getLayoutParams();
        layoutParams.width = (int)((float)10 * (float)line_rate);
        rl_line1.setLayoutParams(layoutParams);
        layoutParams = (RelativeLayout.LayoutParams) rl_line2.getLayoutParams();
        layoutParams.width = (int)((float)(sample_max - sample_min) * (float)line_rate);
        rl_line2.setLayoutParams(layoutParams);
        layoutParams = (RelativeLayout.LayoutParams) rl_line3.getLayoutParams();
        layoutParams.width = (int)((float)40 * (float)line_rate);
        rl_line3.setLayoutParams(layoutParams);
        
        layoutParams = (RelativeLayout.LayoutParams) rl_targetweight.getLayoutParams();
		float target_pos = ((float)(PARENT_WIDTH - LINEBAR_WIDTH) / 2) + (float)(Global.Cur_UserWeightTarget - sample_min + 10) * (float)line_rate - ((float)NOTE_WIDTH1 / 2);
		layoutParams.setMargins((int)target_pos, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
		rl_targetweight.setLayoutParams(layoutParams);
        
        layoutParams = (RelativeLayout.LayoutParams) rl_currentweight.getLayoutParams();
		float cur_pos = ((float)(PARENT_WIDTH - LINEBAR_WIDTH) / 2) + (float)(Global.Cur_UserWeight - sample_min + 10) * (float)line_rate - ((float)NOTE_WIDTH2 / 2);
		layoutParams.setMargins((int)cur_pos, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
		rl_currentweight.setLayoutParams(layoutParams);
	}	
	
	public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			onClickNext();
		 }
	}
	
	public void getResponseJSON() {
		try {
			m_nResponse = ResponseRet.RET_SUCCESS;
			
			String strRequest = HttpConnUsingJSON.REQ_EDITUSER;
						
			JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);
			
			if (response == null) {
				m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
				return;
			}
			
			m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
            if (m_nResponse == ResponseRet.RET_SUCCESS) {
            	
            	JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);           	
            	
			}            
            	
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
	
	
	public JSONObject makeRequestJSON() throws JSONException {
		
		JSONObject requestObj = new JSONObject();		
		    	
		requestObj.put("uid", Global.Cur_UserId);
		requestObj.put("sex", Global.Cur_UserSex);
		requestObj.put("birthday", Global.Cur_UserBirthday);
		requestObj.put("high", Global.Cur_UserHeight);	
		requestObj.put("weight_now", Global.Cur_UserWeight);
		requestObj.put("weight_target", Global.Cur_UserWeightTarget);
		requestObj.put("waist_now", Global.Cur_UserWaistline);
		requestObj.put("waist_target", Global.Cur_UserWaistlineTarget);
		requestObj.put("level", Global.Cur_UserLevel);
		
		return requestObj;
	}
}
