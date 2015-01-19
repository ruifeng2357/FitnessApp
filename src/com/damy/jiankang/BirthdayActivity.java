/* 健康*/
package com.damy.jiankang;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.Utils.ResolutionSet;
import com.damy.Utils.WheelPicker.NumericWheelAdapter;
import com.damy.Utils.WheelPicker.OnWheelChangedListener;
import com.damy.Utils.WheelPicker.WheelView;

import android.os.Bundle;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class BirthdayActivity extends BaseActivity {
	
	public String					cur_Birthday = "1991-1-31";
	public int 						cur_Age = 23;
	
	private WheelView 				years;
    private WheelView 				months;
    private WheelView 				days;
    
    private final int 				START_YEAR = 1950;
    private final int 				END_YEAR = 2050;
    
    private Date					curDate = new Date();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_birthday);
		
		initControls();
		
		initActivity(R.id.rl_birthday);
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_birthday_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		
		Button btn_next = (Button)findViewById(R.id.btn_birthday_next);

		btn_next.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickNext();
        	}
        });
		
		Button btn_save = (Button)findViewById(R.id.btn_birthday_save);
		
		btn_save.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSave();
        	}
        });
		
		int fntSize = (int)(getResources().getDimension(R.dimen.wheelview_fnt_size) * ResolutionSet.fYpro + 0.50001);
		
		years = (WheelView) findViewById(R.id.wheel_birthday_year);
		years.setDefTextSize(fntSize);
        years.setAdapter(new NumericWheelAdapter(START_YEAR, curDate.getYear() + 1900));
        years.setLabel(" " + getResources().getString(R.string.common_year));
        
        months = (WheelView) findViewById(R.id.wheel_birthday_month);
        months.setDefTextSize(fntSize);
        months.setAdapter(new NumericWheelAdapter(1, 12));
        months.setLabel(" " + getResources().getString(R.string.common_month));

        days = (WheelView) findViewById(R.id.wheel_birthday_day);
        days.setDefTextSize(fntSize);
        days.setAdapter(new NumericWheelAdapter(1, 31));
        days.setLabel(" " + getResources().getString(R.string.common_day));

        years.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            	Date curDate = new Date();
                int maxMonth = 12;
                if ( (curDate.getYear() + 1900) - (newValue + START_YEAR) == 0 )
                	maxMonth = curDate.getMonth() + 1;
                months.setAdapter(new NumericWheelAdapter(1, maxMonth));
                
                if ( months.getCurrentItem() + 1 > maxMonth )
                	months.setCurrentItem(maxMonth - 1); 
                
            	cur_Age = curDate.getYear() + 1900 - (newValue + START_YEAR);
            	TextView lbl_age = (TextView)findViewById(R.id.lbl_birthday_age);
            	lbl_age.setText(String.format("%d %s", cur_Age, getResources().getString(R.string.activity_birthday_age)));
            }
        });
        
        months.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

                int maxDay = 30;
                Date curDate = new Date();
                
                if ( (curDate.getYear() + 1900) - (years.getCurrentItem() + START_YEAR) == 0 && (curDate.getMonth() == months.getCurrentItem()) )
                	maxDay = curDate.getDate();
                else {
	                switch (newValue)
	                {
	                    case 1:
	                    {
	                        int curYear = years.getCurrentItem() + START_YEAR;
	                        if (curYear % 4 == 0)
	                        {
	                            maxDay = 29;
	                        }
	                        else
	                        {
	                            maxDay = 28;
	                        }
	                        break;
	                    }
	                    case 0:
	                    case 2:
	                    case 4:
	                    case 6:
	                    case 7:
	                    case 9:
	                    case 11:
	                        maxDay = 31;
	                        break;
	                    default:
	                        maxDay = 30;
	                        break;
	                }
                }
                days.setAdapter(new NumericWheelAdapter(1, maxDay));
                
                if ( days.getCurrentItem() + 1 > maxDay )
                	days.setCurrentItem(maxDay - 1); 
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
        
        String[] arr = Global.Cur_UserBirthday.split("-");
		
        // set current date
        try
        {
            years.setCurrentItem(Integer.valueOf(arr[0]) - START_YEAR);
            months.setCurrentItem(Integer.valueOf(arr[1]) - 1);
            days.setCurrentItem(Integer.valueOf(arr[2]) - 1);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        years.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            	
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
		if (Global.registering_flag)
		{
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
		getSelectedDate();
		
		Global.Cur_UserBirthday = cur_Birthday;
		Global.Cur_UserAge = cur_Age;
		
		Intent height_activity = new Intent(this, HeightActivity.class);
		startActivity(height_activity);	
		finish();
	}
	
	private void onClickSave()
	{
		getSelectedDate();
		
		new LoadResponseThread(BirthdayActivity.this).start();
	}
	
	private void getSelectedDate()
	{
		int nYear = years.getCurrentItem() + START_YEAR;
        int nMonth = months.getCurrentItem() + 1;
        int nDay = days.getCurrentItem() + 1;
        
        cur_Birthday = String.format("%d-%d-%d", nYear, nMonth, nDay);
	}
	
	private void onSuccessBirthday()
	{
		Intent myinfo_activity = new Intent(this, MyInfoActivity.class);
		startActivity(myinfo_activity);	
		finish();
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			Global.Cur_UserBirthday = cur_Birthday;
			Global.Cur_UserAge = cur_Age;
			onSuccessBirthday();
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
		requestObj.put("type", "birthday");
		requestObj.put("data", cur_Birthday);
		
		return requestObj;
	}
}
