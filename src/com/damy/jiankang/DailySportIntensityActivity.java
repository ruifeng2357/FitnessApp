/* �亙熒*/
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class DailySportIntensityActivity extends BaseActivity {

	private enum REQ_TYPE{REQ_EDITUSERINFO, REQ_GETLEVELDESCRIPTION};
	
	private REQ_TYPE		m_reqType;

	private TextView 		note;
	private String 			note_data = "";
	
	private SeekBar			seek_level;
	private ImageView		img_level;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dailysportintensity);
		
		initActivity(R.id.rl_dailysportintensity);
		
		initControls();
		readContents();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_dailysportintensity_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		
		Button btn_next = (Button)findViewById(R.id.btn_dailysportintensity_next);

		btn_next.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickNext();
        	}
        });
		
		Button btn_save = (Button)findViewById(R.id.btn_dailysportintensity_save);
		
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
		
		seek_level = (SeekBar)findViewById(R.id.seekbar_dailysportintensity_level);
		
		img_level = (ImageView)findViewById(R.id.img_dailysportintensity_seek);
		
		seek_level.setProgress(Global.Cur_UserLevel);
		
		seek_level.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            	int nVal = seekBar.getProgress();
                
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) img_level.getLayoutParams();
        		float sample_pos =  (586 / 3) * nVal * ResolutionSet.fXpro;
        		layoutParams.setMargins((int)sample_pos, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
        		img_level.setLayoutParams(layoutParams);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                
            }
        });
		
		
		int nVal = seek_level.getProgress();
        
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) img_level.getLayoutParams();
		float sample_pos =  (586 / 3) * nVal;
		layoutParams.setMargins((int)sample_pos, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
		img_level.setLayoutParams(layoutParams);
		
		
		note = (TextView)findViewById(R.id.lbl_dailysportintensity_note);
	}
	
	private void readContents()
	{
		m_reqType = REQ_TYPE.REQ_GETLEVELDESCRIPTION;
		new LoadResponseThread(DailySportIntensityActivity.this).start();
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
			Intent targetwaistline_activity = new Intent(this, TargetWaistlineActivity.class);
			startActivity(targetwaistline_activity);	
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
		Global.Cur_UserLevel = seek_level.getProgress();
		
		Intent myhealthstatus_activity = new Intent(this, MyHealthStatusActivity.class);
		startActivity(myhealthstatus_activity);	
		finish();
	}
	
	private void onClickSave()
	{
		m_reqType = REQ_TYPE.REQ_EDITUSERINFO;
		new LoadResponseThread(DailySportIntensityActivity.this).start();
	}
	
	private void onSuccessLevel()
	{
		Intent myinfo_activity = new Intent(this, MyInfoActivity.class);
		startActivity(myinfo_activity);
		finish();
	}
	
	public void refreshUI() {
		super.refreshUI();
		if ( m_reqType == REQ_TYPE.REQ_GETLEVELDESCRIPTION )
		{
			if (m_nResponse == ResponseRet.RET_SUCCESS) {	
				note.setText(note_data);			
			}
		}
		else if ( m_reqType == REQ_TYPE.REQ_EDITUSERINFO)
		{
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				Global.Cur_UserLevel = seek_level.getProgress();
				onSuccessLevel();
			}
		}
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETLEVELDESCRIPTION )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETLEVELDESCRIPTION;
							
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
				
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
	            if (m_nResponse == ResponseRet.RET_SUCCESS) {
	            	
	            	JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
	            	
	            	note_data = dataObject.getString("level");
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_EDITUSERINFO )
			{
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
			}
            
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
	
	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();		
    	
		requestObj.put("uid", Global.Cur_UserId);
		requestObj.put("type", "level");
		requestObj.put("data", seek_level.getProgress());
		
		return requestObj;
	}
}
