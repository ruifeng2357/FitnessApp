/* �亙熒*/
package com.damy.jiankang;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.ResolutionSet;
import com.damy.Utils.WheelPicker.NumericWheelAdapter;
import com.damy.Utils.WheelPicker.WheelView;
import com.damy.adapters.SportTypeAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STSportTypeInfo;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SportRecordInfoActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_INSERTSPORTRECORD, REQ_UPDATESPORTRECORD};
	private REQ_TYPE				m_reqType;
	
	public static String			EXTRADATA_RECORDID = "SPORTRECORDINFO_RECORDID";
	public static String			EXTRADATA_SPORTID = "SPORTRECORDINFO_SPORTID";
	public static String			EXTRADATA_NAME = "SPORTRECORDINFO_NAME";
	public static String			EXTRADATA_IMAGE = "SPORTRECORDINFO_IMAGE";
	public static String			EXTRADATA_YEAR = "SPORTRECORDINFO_YEAR";
	public static String			EXTRADATA_MONTH = "SPORTRECORDINFO_MONTH";
	public static String			EXTRADATA_DATE = "SPORTRECORDINFO_DATE";
	public static String			EXTRADATA_STARTTIME = "SPORTRECORDINFO_STARTTIME";
	public static String			EXTRADATA_ENDTIME = "SPORTRECORDINFO_ENDTIME";
	
	private Long					m_nRecordId;
	private Long					m_nSportId;
	private String					m_strName;
	private String					m_strImage;
	private int						m_nYear;
	private int						m_nMonth;
	private int						m_nDate;
	private String					m_strStartTime;
	private String					m_strEndTime;
	
	private TextView				txt_time;
	private TextView				txt_dialogtitle;
	
	private WheelView 				wheel_hour;
    private WheelView 				wheel_minute;
    
    private int						m_CurSelTime = 0;
    
    private RelativeLayout 			rl_maskLayer;
    private RelativeLayout 			rl_timedialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sportrecordinfo);
		
		initActivity(R.id.rl_sportrecordinfo);
		
		initControls();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_sportrecordinfo_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		Button btn_starttime = (Button)findViewById(R.id.btn_sportrecordinfo_starttime);
		Button btn_endtime = (Button)findViewById(R.id.btn_sportrecordinfo_endtime);
		Button btn_save = (Button)findViewById(R.id.btn_sportrecordinfo_save);
		
		btn_starttime.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		m_CurSelTime = 0;
        		onClickTime();
        	}
        });
		
		btn_endtime.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		m_CurSelTime = 1;
        		onClickTime();
        	}
        });
		
		btn_save.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSave();
        	}
        });
		
		m_nRecordId = getIntent().getLongExtra(EXTRADATA_RECORDID, 0);
		m_nSportId = getIntent().getLongExtra(EXTRADATA_SPORTID, 0);
		m_strName = getIntent().getStringExtra(EXTRADATA_NAME);
		m_strImage = getIntent().getStringExtra(EXTRADATA_IMAGE);
		m_nYear = getIntent().getIntExtra(EXTRADATA_YEAR, 0);
		m_nMonth = getIntent().getIntExtra(EXTRADATA_MONTH, 0);
		m_nDate = getIntent().getIntExtra(EXTRADATA_DATE, 0);
		m_strStartTime = getIntent().getStringExtra(EXTRADATA_STARTTIME);
		m_strEndTime = getIntent().getStringExtra(EXTRADATA_ENDTIME);
		
		TextView txt_name = (TextView)findViewById(R.id.lbl_sportrecordinfo_type);
		ImageView img_icon = (ImageView)findViewById(R.id.img_sportrecordinfo_icon);
		TextView txt_date = (TextView)findViewById(R.id.lbl_sportrecordinfo_date);
		txt_time = (TextView)findViewById(R.id.lbl_sportrecordinfo_time);
		
		txt_name.setText(m_strName);
		txt_date.setText(String.format("%s : %d%s%d%s%d%s", getResources().getString(R.string.common_date), m_nYear, getResources().getString(R.string.common_year), m_nMonth, getResources().getString(R.string.common_month), m_nDate, getResources().getString(R.string.common_day)));
		try
        {
            Global.imageLoader.displayImage(HttpConnUsingJSON.BASE_IMAGEURL + m_strImage, img_icon, Global.sport_options);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
		
		if ( m_strStartTime.length() > 0 )
			txt_time.setText(String.format("%s ~ %s", m_strStartTime, m_strEndTime));
		else
			txt_time.setText("");
		
		int fntSize = (int)(getResources().getDimension(R.dimen.wheelview_fnt_size) * ResolutionSet.fYpro + 0.50001);
		
		wheel_hour = (WheelView) findViewById(R.id.wheel_sportrecordinfo_hour);
		wheel_hour.setDefTextSize(fntSize);
		wheel_hour.setAdapter(new NumericWheelAdapter(0, 23));
		wheel_hour.setLabel(" " + getResources().getString(R.string.common_hours));
		
		wheel_minute = (WheelView) findViewById(R.id.wheel_sportrecordinfo_minute);
		wheel_minute.setDefTextSize(fntSize);
		wheel_minute.setAdapter(new NumericWheelAdapter(0, 59));
		wheel_minute.setLabel(" " + getResources().getString(R.string.common_minute));
		
		rl_maskLayer = (RelativeLayout)findViewById(R.id.rl_sportrecordinfo_mask);
		rl_timedialog = (RelativeLayout)findViewById(R.id.rl_sportrecordinfo_pickerdialog);
		
		rl_maskLayer.setVisibility(View.INVISIBLE);
		rl_timedialog.setVisibility(View.INVISIBLE);
        
        rl_maskLayer.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            }
        });
        
        Button btn_dialog_ok = (Button)findViewById(R.id.btn_sportrecordinfo_pickerdialog_ok);
        Button btn_dialog_cancel = (Button)findViewById(R.id.btn_sportrecordinfo_pickerdialog_cancel);
        
        btn_dialog_ok.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	onClickDialogOK();
            }
        });
        
        btn_dialog_cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	onClickDialogCancel();
            }
        });
        
        txt_dialogtitle = (TextView)findViewById(R.id.lbl_sportrecordinfo_pickerdialog_title);
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
		Intent sportrecord_activity = new Intent(this, SportRecordActivity.class);
		startActivity(sportrecord_activity);	
		finish();
	}
	
	private void onClickTime()
	{
		String strTmp = "";
		if ( m_CurSelTime == 0 )
		{
			strTmp = m_strStartTime;
			txt_dialogtitle.setText(getResources().getString(R.string.activity_sportrecordinfo_selstarttime));
		}
		else
		{
			strTmp = m_strEndTime;
			txt_dialogtitle.setText(getResources().getString(R.string.activity_sportrecordinfo_selendtime));
		}
		
		if ( strTmp.length() == 0 )
			strTmp = "00:00";
		
		int nHour = Integer.valueOf(strTmp.substring(0, 2));
		int nMinute = Integer.valueOf(strTmp.substring(3, 5));
		
		wheel_hour.setCurrentItem(nHour);
		wheel_minute.setCurrentItem(nMinute);
		
		rl_maskLayer.setVisibility(View.VISIBLE);
		rl_timedialog.setVisibility(View.VISIBLE);
	}
	
	private void onClickDialogOK()
	{
		if ( m_CurSelTime == 0 )
		{
			
			int h1 = wheel_hour.getCurrentItem();
			int m1 = wheel_minute.getCurrentItem();
			
			if ( m_strEndTime.length() > 0 )
			{
				int h2 = Integer.valueOf(m_strEndTime.substring(0, 2));
				int m2 = Integer.valueOf(m_strEndTime.substring(3, 5));
	
				if ( (h1 > h2) || ((h1 == h2) && (m1 > m2)) )
				{
					showToastMessage(getResources().getString(R.string.error_timemismatch));
					return;
				}
				else
					m_strStartTime = String.format("%02d:%02d", h1, m1);
			}
			else
				m_strStartTime = String.format("%02d:%02d", h1, m1);
		}
		else
		{
			
			int h2 = wheel_hour.getCurrentItem();
			int m2 = wheel_minute.getCurrentItem();

			if ( m_strStartTime.length() > 0 )
			{
				int h1 = Integer.valueOf(m_strStartTime.substring(0, 2));
				int m1 = Integer.valueOf(m_strStartTime.substring(3, 5));								
				
				if ( (h1 > h2) || ((h1 == h2) && (m1 > m2)) )
				{
					showToastMessage(getResources().getString(R.string.error_timemismatch));
					return;
				}
				else
					m_strEndTime = String.format("%02d:%02d", h2, m2);
			}
			else
				m_strEndTime = String.format("%02d:%02d", h2, m2);
		}
		
		txt_time.setText(String.format("%s ~ %s", m_strStartTime, m_strEndTime));
		onClickDialogCancel();
	}
	
	private void onClickDialogCancel()
	{
		rl_maskLayer.setVisibility(View.INVISIBLE);
		rl_timedialog.setVisibility(View.INVISIBLE);
	}
	
	private void onClickSave()
	{
		if ( m_strStartTime.length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_notsetstarttime));
			return;
		}
		
		if ( m_strEndTime.length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_notsetendtime));
			return;
		}
		
		if ( m_nRecordId > 0 )
			m_reqType = REQ_TYPE.REQ_UPDATESPORTRECORD;
		else
			m_reqType = REQ_TYPE.REQ_INSERTSPORTRECORD;
		
		new LoadResponseThread(SportRecordInfoActivity.this).start();
	}
	public void refreshUI() {
		super.refreshUI();
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			onClickBack();
		 }
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_INSERTSPORTRECORD )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_INSERTSPORTRECORD;
				
				JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
	            	
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_UPDATESPORTRECORD )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_UPDATESPORTRECORD;
				
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
		
		if ( m_reqType == REQ_TYPE.REQ_UPDATESPORTRECORD )
			requestObj.put("id", String.valueOf(m_nRecordId));
		
		requestObj.put("sport_id", String.valueOf(m_nSportId));
		requestObj.put("start_time", String.format("%d-%d-%d %s", m_nYear, m_nMonth, m_nDate, m_strStartTime));
		requestObj.put("end_time", String.format("%d-%d-%d %s", m_nYear, m_nMonth, m_nDate, m_strEndTime));
		requestObj.put("date", String.format("%d-%d-%d", m_nYear, m_nMonth, m_nDate));
		requestObj.put("uid", String.valueOf(Global.Cur_UserId));
		
		return requestObj;
	}
}
