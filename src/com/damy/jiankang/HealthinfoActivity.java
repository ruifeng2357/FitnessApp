/* 健康*/
package com.damy.jiankang;

import org.json.JSONArray;
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
import android.widget.TextView;

public class HealthinfoActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETBMIBMRDESCRIPTION};
	
	private REQ_TYPE			m_reqType;
	
	private String				m_bmiMsg;
	private String				m_bmrMsg;
	
	private RelativeLayout		rl_bmiDescription;
	private RelativeLayout		rl_bmrDescription;
	
	private RelativeLayout 		rl_mask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_healthinfo);
		
		initControls();
		
		initActivity(R.id.rl_healthinfo);
		readContents();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_healthinfo_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		Button btn_next = (Button)findViewById(R.id.btn_healthinfo_next);

		btn_next.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickNext();
        	}
        });
		
		ImageView img_bmihelp = (ImageView)findViewById(R.id.img_healthinfo_bmihelp);
		ImageView img_bmrhelp = (ImageView)findViewById(R.id.img_healthinfo_bmrhelp);
		
		img_bmihelp.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		showBMIHelp();
        	}
        });
		
		img_bmrhelp.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		showBMRHelp();
        	}
        });
		
		rl_bmiDescription = (RelativeLayout)findViewById(R.id.rl_healthinfo_bmidescription);
		rl_bmrDescription = (RelativeLayout)findViewById(R.id.rl_healthinfo_bmrdescription);
		
		rl_mask = (RelativeLayout)findViewById(R.id.rl_healthinfo_mask);
		rl_mask.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		hideBMIHelp();
        		hideBMRHelp();
        	}
        });
		rl_mask.setVisibility(View.INVISIBLE);
		
		rl_bmiDescription.setVisibility(View.INVISIBLE);
		rl_bmrDescription.setVisibility(View.INVISIBLE);
		
		if ( !Global.registering_flag )
		{
			btn_next.setVisibility(View.INVISIBLE);
		}
		
		calculateBMI();
		calculateBMR();
		calculateLineBar();
	}
	
	private void readContents()
	{
		m_reqType = REQ_TYPE.REQ_GETBMIBMRDESCRIPTION;
		new LoadResponseThread(HealthinfoActivity.this).start();
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
			Intent waistline_activity = new Intent(this, WaistlineActivity.class);
			startActivity(waistline_activity);
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
		Intent targetweight_activity = new Intent(this, TargetWeightActivity.class);
		startActivity(targetweight_activity);	
		finish();
	}
	
	private void calculateBMI()
	{
		float fBMI = 0;
		fBMI = Global.Cur_UserWeight / (((float)Global.Cur_UserHeight / 100) * ((float)Global.Cur_UserHeight / 100));		
		TextView txt_bmi = (TextView)findViewById(R.id.lbl_healthinfo_bmi);
		TextView txt_suggestmsg = (TextView)findViewById(R.id.lbl_healthinfo_suggestmsg);
		
		String strTmp = "", strTmp1 = "";
		
		if ( fBMI < 18.5 )
		{
			strTmp = getResources().getString(R.string.activity_healthinfo_statustext1);
			strTmp1 = getResources().getString(R.string.activity_healthinfo_suggestmsg1);
		}
		else if ( fBMI >= 18.5 && fBMI < 24 )
		{
			strTmp = getResources().getString(R.string.activity_healthinfo_statustext2);
			strTmp1 = getResources().getString(R.string.activity_healthinfo_suggestmsg2);
		}
		else if ( fBMI >= 24 && fBMI < 27 )
		{
			strTmp = getResources().getString(R.string.activity_healthinfo_statustext3);
			strTmp1 = getResources().getString(R.string.activity_healthinfo_suggestmsg3);
		}
		else if ( fBMI >= 27 && fBMI < 30 )
		{
			strTmp = getResources().getString(R.string.activity_healthinfo_statustext4);
			strTmp1 = getResources().getString(R.string.activity_healthinfo_suggestmsg4);
		}
		else if ( fBMI >= 30 && fBMI < 35 )
		{
			strTmp = getResources().getString(R.string.activity_healthinfo_statustext5);
			strTmp1 = getResources().getString(R.string.activity_healthinfo_suggestmsg5);
		}
		else if ( fBMI >= 35 )
		{
			strTmp = getResources().getString(R.string.activity_healthinfo_statustext6);
			strTmp1 = getResources().getString(R.string.activity_healthinfo_suggestmsg6);
		}
		
		txt_bmi.setText(String.format("BMI = %.01f %s", fBMI, strTmp));
		txt_suggestmsg.setText(strTmp1);
	}
	
	private void calculateBMR()
	{
		double fBMR = 0;
		if ( Global.Cur_UserSex == 0 )
			fBMR = (13.7 * Global.Cur_UserWeight) + (5 * (float)Global.Cur_UserHeight) - (6.8 * Global.Cur_UserAge) + 66;
		else
			fBMR = (9.6 * Global.Cur_UserWeight) + (1.8 * (float)Global.Cur_UserHeight) - (4.7 * Global.Cur_UserAge) + 655;	
		
		TextView txt_bmr = (TextView)findViewById(R.id.lbl_healthinfo_bmr);
		txt_bmr.setText(String.format("BMR = %.02f", fBMR));
	}
	
	private void calculateLineBar()
	{
		int PARENT_WIDTH = 720;
		int LINEBAR_WIDTH = 626;
		int NOTE_WIDTH = 96;
		float sample_weight = 0.f;
		sample_weight = (float)22 * ((float)Global.Cur_UserHeight / 100) * ((float)Global.Cur_UserHeight / 100);
		
		float sample_min = sample_weight * (float)0.9;
		float sample_max = sample_weight * (float)1.1;
		
		TextView txt_note = (TextView)findViewById(R.id.lbl_healthinfo_note);
		txt_note.setText(String.format("%s ( %.01f~%.01f%s )", getResources().getString(R.string.activity_healthinfo_noteprefix), sample_min, sample_max, getResources().getString(R.string.common_unit_gongjin)));
		
		float line_width = ((float)sample_max + (float)40) - ((float)sample_min - (float)10);
		float line_rate = (float)LINEBAR_WIDTH / (float)line_width;
		
		RelativeLayout rl_line1 = (RelativeLayout)findViewById(R.id.rl_healthinfo_line1);
		RelativeLayout rl_line2 = (RelativeLayout)findViewById(R.id.rl_healthinfo_line2);
		RelativeLayout rl_line3 = (RelativeLayout)findViewById(R.id.rl_healthinfo_line3);
		
		RelativeLayout.LayoutParams layoutParams;
		
		layoutParams = (RelativeLayout.LayoutParams) rl_line1.getLayoutParams();
        layoutParams.width = (int)((float)10 * (float)line_rate ); 
        rl_line1.setLayoutParams(layoutParams);
        
        layoutParams = (RelativeLayout.LayoutParams) rl_line2.getLayoutParams();
        layoutParams.width = (int)((float)(sample_max - sample_min) * (float)line_rate );
        rl_line2.setLayoutParams(layoutParams);
        
        layoutParams = (RelativeLayout.LayoutParams) rl_line3.getLayoutParams();
        layoutParams.width = (int)((float)40 * (float)line_rate );
        rl_line3.setLayoutParams(layoutParams);
        
        RelativeLayout rl_sample = (RelativeLayout)findViewById(R.id.rl_healthinfo_targetnote);
		RelativeLayout rl_cur = (RelativeLayout)findViewById(R.id.rl_healthinfo_curnote);
		TextView txt_sample = (TextView)findViewById(R.id.lbl_healthinfo_targetvalue);
		TextView txt_cur = (TextView)findViewById(R.id.lbl_healthinfo_curvalue);
		
		txt_sample.setText(String.format("%.01fkg", sample_weight));
		txt_cur.setText(String.format("%.01fkg", Global.Cur_UserWeight));
		
		layoutParams = (RelativeLayout.LayoutParams) rl_sample.getLayoutParams();
		float sample_pos = ((float)(PARENT_WIDTH - LINEBAR_WIDTH) / 2) + (float)(sample_weight - sample_min + 10) * (float)line_rate - ((float)NOTE_WIDTH / 2);
		layoutParams.setMargins((int)sample_pos, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
        rl_sample.setLayoutParams(layoutParams);
        
        layoutParams = (RelativeLayout.LayoutParams) rl_cur.getLayoutParams();
        float cur_pos = 0.f;
        cur_pos = ((float)(PARENT_WIDTH - LINEBAR_WIDTH) / 2) + (float)(Global.Cur_UserWeight - sample_min + 10) * (float)line_rate - ((float)NOTE_WIDTH / 2);
        
		layoutParams.setMargins((int)cur_pos, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
		rl_cur.setLayoutParams(layoutParams);
	}
	
	private void showBMIHelp()
	{
		rl_bmiDescription.setVisibility(View.VISIBLE);
		rl_mask.setVisibility(View.VISIBLE);
	}
	
	private void hideBMIHelp()
	{
		rl_bmiDescription.setVisibility(View.INVISIBLE);
		rl_mask.setVisibility(View.INVISIBLE);
	}
	
	private void showBMRHelp()
	{
		rl_bmrDescription.setVisibility(View.VISIBLE);
		rl_mask.setVisibility(View.VISIBLE);
	}
	
	private void hideBMRHelp()
	{
		rl_bmrDescription.setVisibility(View.INVISIBLE);
		rl_mask.setVisibility(View.INVISIBLE);
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETBMIBMRDESCRIPTION )
		{
			TextView txt_bmiDescription = (TextView)findViewById(R.id.lbl_healthinfo_bmidescription);
			TextView txt_bmrDescription = (TextView)findViewById(R.id.lbl_healthinfo_bmrdescription);
			
			txt_bmiDescription.setText(m_bmiMsg);
			txt_bmrDescription.setText(m_bmrMsg);
		}
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETBMIBMRDESCRIPTION )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETBMIBMRDESCRIPTION;
				
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
				
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
					
					m_bmiMsg = dataObject.getString("bmi");
	            	m_bmrMsg = dataObject.getString("bmr");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
}
