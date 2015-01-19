/* �亙熒*/
package com.damy.jiankang;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.ResolutionSet;
import com.damy.adapters.AlarmAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STSportRecordInfo;
import com.damy.datatypes.STAlarmInfo;
import com.damy.datatypes.STSportTypeInfo;


import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AlarmActivity extends BaseActivity {
	
	public static final String PREFERENCES = "AlarmClock";
	
	private ListView						lv_Alarm;
	private ArrayList<STAlarmInfo>			m_AlarmList;
	private AlarmAdapter					m_AlarmAdapter = null;
	
	private int								m_curClickedItem = 0;
	
	private RelativeLayout 					m_deleteLayer;
	private RelativeLayout					m_maskLayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		
		initActivity(R.id.rl_alarm);
		
		initControls();
		setAlarmAdapter();
		
		new LoadResponseThread(AlarmActivity.this).start();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_alarm_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		Button btn_add = (Button)findViewById(R.id.btn_alarm_add);
		btn_add.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickAdd();
        	}
        });
		
		lv_Alarm = (ListView)findViewById(R.id.list_alarm_content);
		
		m_maskLayer = (RelativeLayout)findViewById(R.id.rl_alarm_mask);
		m_maskLayer.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        	}
        });
		m_maskLayer.setVisibility(View.INVISIBLE);
		
		m_deleteLayer = (RelativeLayout)findViewById(R.id.rl_dialog_delconfirm_dialog);
		m_deleteLayer.setVisibility(View.INVISIBLE);
		
		Button fl_delconfirm_ok = (Button)findViewById(R.id.btn_dialog_delconfirm_ok);
		Button fl_delconfirm_cancel = (Button)findViewById(R.id.btn_dialog_delconfirm_cancel);
		
		fl_delconfirm_ok.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDelConfirmOk();
        	}
        });
		fl_delconfirm_cancel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDelConfirmCancel();
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
		Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}
	
	private void onClickAdd()
	{
		Intent alarminfo_activity = new Intent(this, AlarmInfoActivity.class);
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_INFOID, -1);
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_REPEATTYPE, 0);
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_REPEATSTRING, "");
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_SOUND, (Parcelable)null);
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_VIBRATION, 0);
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_TITLE, "");
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_STATUS, 1);
		
		Date curDate = new Date();
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_HOUR, curDate.getHours());
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_MINUTE, curDate.getMinutes());
		
		startActivity(alarminfo_activity);
		finish();
	}
	
	public void onSwitchChanged(int pos)
	{
		STAlarmInfo item = m_AlarmList.get(pos);
		if ( item.status == 1 )
			item.status = 0;
		else
			item.status = 1;
		m_AlarmList.set(pos, item);
		
		Alarms.enableAlarm(this, item.id, item.status == 1);
		
		m_AlarmAdapter.notifyDataSetChanged();
	}
	
	private void readContents()
	{
		Cursor all_data = Alarms.getAlarmsCursor(getContentResolver());
		
		while ( all_data.moveToNext() )
		{
			Alarm anAlarm = new Alarm(all_data);
		
			STAlarmInfo itemInfo = new STAlarmInfo();
			
	        itemInfo.id = anAlarm.id;
	        itemInfo.hour = anAlarm.hour;
	        itemInfo.minute = anAlarm.minutes;
	        
	        Alarm.DaysOfWeek day_week = anAlarm.daysOfWeek;
	        itemInfo.repeat_week = "";
	        if ( !day_week.isRepeatSet() )
	        	itemInfo.repeat_type = 0;
	        else if ( day_week.isEverydaySet() )
	        	itemInfo.repeat_type = 1;
	        else if ( day_week.is5daysSet() )
	        	itemInfo.repeat_type = 2;
	        else
	        {
	        	for ( int j = 0; j <= 6; j++ )
	        	{
	        		if ( day_week.isSet(j) )
	        		{
	        			int tmp = j + 2;
	        			if ( tmp == 8 ) tmp = 1;
	        			itemInfo.repeat_week += String.valueOf(tmp) + ",";
	        		}
	        	}
	        	itemInfo.repeat_type = 3;
	        	itemInfo.repeat_week = itemInfo.repeat_week.substring(0, itemInfo.repeat_week.length() - 1);
	        }
	        
	        itemInfo.title = anAlarm.label;
	        itemInfo.status = anAlarm.enabled ? 1 : 0;
	        itemInfo.sound = anAlarm.alert;
	        itemInfo.vibration = anAlarm.vibrate ? 1 : 0; 

	        m_AlarmList.add(itemInfo);
		}
		
		m_AlarmAdapter.notifyDataSetChanged();
	}
	
	private void setAlarmAdapter() {
		m_AlarmList = new ArrayList<STAlarmInfo>();
		
		lv_Alarm.setCacheColorHint(Color.TRANSPARENT);
		lv_Alarm.setDividerHeight(0);
		lv_Alarm.setDrawSelectorOnTop(false);

        m_AlarmAdapter = new AlarmAdapter(AlarmActivity.this, m_AlarmList);
        lv_Alarm.setAdapter(m_AlarmAdapter);
        
        lv_Alarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickItem(position);
	    	}
		});
        
        lv_Alarm.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				onLongClickItem(parent, position);
				return true;
	    	}
		});
	}
	
	private void onClickItem(int pos)
	{
		STAlarmInfo item = m_AlarmList.get(pos);
		
		Intent alarminfo_activity = new Intent(this, AlarmInfoActivity.class);
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_INFOID, item.id);
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_REPEATTYPE, item.repeat_type);
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_REPEATSTRING, item.repeat_week);
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_SOUND, item.sound);
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_VIBRATION, item.vibration);
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_TITLE, item.title);
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_HOUR, item.hour);
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_MINUTE, item.minute);
		alarminfo_activity.putExtra(AlarmInfoActivity.EXTRADATA_STATUS, item.status);
		startActivity(alarminfo_activity);
		finish();
	}
	
	public void onLongClickItem(View parent, int pos)
	{
		m_curClickedItem = pos;
		
		m_maskLayer.setVisibility(View.VISIBLE);
		m_deleteLayer.setVisibility(View.VISIBLE);
	}

	private void onClickDelConfirmOk()
	{
		m_maskLayer.setVisibility(View.INVISIBLE);
		m_deleteLayer.setVisibility(View.INVISIBLE);
		
		Alarms.deleteAlarm(this, m_AlarmList.get(m_curClickedItem).id);
		
		m_AlarmList.remove(m_curClickedItem);
		m_AlarmAdapter.notifyDataSetChanged();
	}
	
	private void onClickDelConfirmCancel()
	{
		m_maskLayer.setVisibility(View.INVISIBLE);
		m_deleteLayer.setVisibility(View.INVISIBLE);
	}
	
	public void refreshUI() {
		super.refreshUI();
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			readContents();
		 }
	}
	
	public void getResponseJSON() {
		try {
			m_nResponse = ResponseRet.RET_SUCCESS;
			
			String strRequest = HttpConnUsingJSON.REQ_GETALARMLIST;
			strRequest += "?uid=" + String.valueOf(Global.Cur_UserId);
			
			JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
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
}
