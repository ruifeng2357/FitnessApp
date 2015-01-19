/* �亙熒*/
package com.damy.jiankang;

import java.io.File;
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


import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AlarmInfoActivity extends BaseActivity {
	
	private int 					REQUEST_SOUND = 0;
	
	public static String			EXTRADATA_INFOID = "ALARMINFO_INFOID";
	public static String			EXTRADATA_REPEATTYPE = "ALARMINFO_REPEATTYPE";
	public static String			EXTRADATA_REPEATSTRING = "ALARMINFO_REPEATSTRING";
	public static String			EXTRADATA_SOUND = "ALARMINFO_SOUND";
	public static String			EXTRADATA_VIBRATION = "ALARMINFO_VIBRATION";
	public static String			EXTRADATA_TITLE = "ALARMINFO_TITLE";
	public static String			EXTRADATA_HOUR = "ALARMINFO_HOUR";
	public static String			EXTRADATA_MINUTE = "ALARMINFO_MINUTE";
	public static String			EXTRADATA_STATUS = "ALARMINFO_STATUS";
	
	private int						m_nInfoId;
	private int						m_nRepeatType;
	private String					m_strRepeatString;
	private Uri						m_strSound;
	private int						m_nVibration;
	private String					m_strTitle;
	private int						m_nHour;
	private int						m_nMinute;
	private int						m_nStatus;
	
	private RelativeLayout			rl_repeat_setting1;
	private RelativeLayout			rl_repeat_setting2;
	private RelativeLayout			rl_title_setting;
	private RelativeLayout			rl_maskLayer;
	private CheckBox				chk_week1;
	private CheckBox				chk_week2;
	private CheckBox				chk_week3;
	private CheckBox				chk_week4;
	private CheckBox				chk_week5;
	private CheckBox				chk_week6;
	private CheckBox				chk_week7;
	private TextView				lbl_repeat;
	private TextView				lbl_sound;
	private EditText				edit_title;
	private ImageView				img_vibration;
    
    private WheelView 				wheel_hour;
    private WheelView 				wheel_minute;
    
    private int						m_dlgtype = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarminfo);
		
		initActivity(R.id.rl_alarminfo);
		
		initControls();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_alarminfo_back);
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		Button btn_save = (Button)findViewById(R.id.btn_alarminfo_save);
		btn_save.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSave();
        	}
        });
		
		lbl_repeat = (TextView)findViewById(R.id.lbl_alarminfo_repeattype);
		lbl_sound = (TextView)findViewById(R.id.lbl_alarminfo_sound);
		edit_title = (EditText)findViewById(R.id.edit_alarminfo_alarmname_setting_value);
		img_vibration = (ImageView)findViewById(R.id.img_alarminfo_vibration);
		
		rl_maskLayer = (RelativeLayout)findViewById(R.id.rl_alarminfo_mask);
		rl_maskLayer.setVisibility(View.INVISIBLE);
		rl_maskLayer.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if ( m_dlgtype == 1 )
        		{
        			rl_maskLayer.setVisibility(View.INVISIBLE);
        			rl_repeat_setting1.setVisibility(View.INVISIBLE);
        			
        			m_dlgtype = 0;
        		}
        	}
        });
		
		m_nInfoId = getIntent().getIntExtra(EXTRADATA_INFOID, 0);
		m_nRepeatType = getIntent().getIntExtra(EXTRADATA_REPEATTYPE, 0);
		m_strRepeatString = getIntent().getStringExtra(EXTRADATA_REPEATSTRING);
		m_strSound = getIntent().getParcelableExtra(EXTRADATA_SOUND);
		m_nVibration = getIntent().getIntExtra(EXTRADATA_VIBRATION, 0);
		m_strTitle = getIntent().getStringExtra(EXTRADATA_TITLE);
		m_nStatus = getIntent().getIntExtra(EXTRADATA_STATUS, 0);
		m_nHour = getIntent().getIntExtra(EXTRADATA_HOUR, 0);
		m_nMinute = getIntent().getIntExtra(EXTRADATA_MINUTE, 0);
		
		RelativeLayout rl_repeat = (RelativeLayout)findViewById(R.id.rl_alarminfo_repeat);
		RelativeLayout rl_sound = (RelativeLayout)findViewById(R.id.rl_alarminfo_sound);
		//RelativeLayout rl_vibration = (RelativeLayout)findViewById(R.id.rl_alarminfo_vibration);
		RelativeLayout rl_title = (RelativeLayout)findViewById(R.id.rl_alarminfo_alarmname);
		
		rl_repeat.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickRepeat();
        	}
        });
		
		rl_sound.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSound();
        	}
        });
		
		rl_title.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickTitle();
        	}
        });
		
		rl_repeat_setting1 = (RelativeLayout)findViewById(R.id.rl_alarminfo_repeat_setting1);
		rl_repeat_setting1.setVisibility(View.INVISIBLE);
		RelativeLayout rl_type1 = (RelativeLayout)findViewById(R.id.rl_alarminfo_repeat_setting1_node2);
		RelativeLayout rl_type2 = (RelativeLayout)findViewById(R.id.rl_alarminfo_repeat_setting1_node3);
		RelativeLayout rl_type3 = (RelativeLayout)findViewById(R.id.rl_alarminfo_repeat_setting1_node4);
		RelativeLayout rl_type4 = (RelativeLayout)findViewById(R.id.rl_alarminfo_repeat_setting1_node5);
		rl_type1.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		m_nRepeatType = 0;
        		m_strRepeatString = "";
        		rl_repeat_setting1.setVisibility(View.INVISIBLE);
        		rl_maskLayer.setVisibility(View.INVISIBLE);
        		
        		lbl_repeat.setText(getResources().getString(R.string.alarm_onetime));
        		
        		m_dlgtype = 0;
        	}
        });
		rl_type2.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		m_nRepeatType = 1;
        		m_strRepeatString = "";
        		rl_repeat_setting1.setVisibility(View.INVISIBLE);
        		rl_maskLayer.setVisibility(View.INVISIBLE);
        		
        		lbl_repeat.setText(getResources().getString(R.string.alarm_daily));
        		
        		m_dlgtype = 0;
        	}
        });
		rl_type3.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		m_nRepeatType = 2;
        		m_strRepeatString = "";
        		rl_repeat_setting1.setVisibility(View.INVISIBLE);
        		rl_maskLayer.setVisibility(View.INVISIBLE);
        		
        		lbl_repeat.setText(getResources().getString(R.string.alarm_5days));
        		
        		m_dlgtype = 0;
        	}
        });
		rl_type4.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		m_nRepeatType = 3;
        		changeCheckBoxState();
        		rl_repeat_setting2.setVisibility(View.VISIBLE);
        		
        		m_dlgtype = 0;
        	}
        });
		
		rl_repeat_setting2 = (RelativeLayout)findViewById(R.id.rl_alarminfo_repeat_setting2);
		rl_repeat_setting2.setVisibility(View.INVISIBLE);
		Button btn_repeat_setting2_ok = (Button)findViewById(R.id.btn_alarminfo_repeat_setting2_ok);
		Button btn_repeat_setting2_cancel = (Button)findViewById(R.id.btn_alarminfo_repeat_setting2_cancel);
		chk_week1 = (CheckBox)findViewById(R.id.chk_alarminfo_week1);
		chk_week2 = (CheckBox)findViewById(R.id.chk_alarminfo_week2);
		chk_week3 = (CheckBox)findViewById(R.id.chk_alarminfo_week3);
		chk_week4 = (CheckBox)findViewById(R.id.chk_alarminfo_week4);
		chk_week5 = (CheckBox)findViewById(R.id.chk_alarminfo_week5);
		chk_week6 = (CheckBox)findViewById(R.id.chk_alarminfo_week6);
		chk_week7 = (CheckBox)findViewById(R.id.chk_alarminfo_week7);
		
		btn_repeat_setting2_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updateRepeatString();
				
				if ( m_strRepeatString.length() == 0 )
					return;
				
				rl_repeat_setting2.setVisibility(View.INVISIBLE);
				rl_repeat_setting1.setVisibility(View.INVISIBLE);
				rl_maskLayer.setVisibility(View.INVISIBLE);
				
				String tmp = makeDisplayRepeatString();
				lbl_repeat.setText(tmp);
        	}
		});
		
		btn_repeat_setting2_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				rl_repeat_setting2.setVisibility(View.INVISIBLE);
        	}
		});
		
		chk_week1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked )
					buttonView.setTextColor(getResources().getColor(R.color.common_normal_text));
				else
					buttonView.setTextColor(getResources().getColor(R.color.common_whitegray));
				
			}
		});
		chk_week2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked )
					buttonView.setTextColor(getResources().getColor(R.color.common_normal_text));
				else
					buttonView.setTextColor(getResources().getColor(R.color.common_whitegray));
				
			}
		});
		chk_week3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked )
					buttonView.setTextColor(getResources().getColor(R.color.common_normal_text));
				else
					buttonView.setTextColor(getResources().getColor(R.color.common_whitegray));
				
			}
		});
		chk_week4.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked )
					buttonView.setTextColor(getResources().getColor(R.color.common_normal_text));
				else
					buttonView.setTextColor(getResources().getColor(R.color.common_whitegray));
				
			}
		});
		chk_week5.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked )
					buttonView.setTextColor(getResources().getColor(R.color.common_normal_text));
				else
					buttonView.setTextColor(getResources().getColor(R.color.common_whitegray));
				
			}
		});
		chk_week6.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked )
					buttonView.setTextColor(getResources().getColor(R.color.common_normal_text));
				else
					buttonView.setTextColor(getResources().getColor(R.color.common_whitegray));
				
			}
		});
		chk_week7.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked )
					buttonView.setTextColor(getResources().getColor(R.color.common_normal_text));
				else
					buttonView.setTextColor(getResources().getColor(R.color.common_whitegray));
				
			}
		});
		
		rl_title_setting = (RelativeLayout)findViewById(R.id.rl_alarminfo_alarmname_setting);
		rl_title_setting.setVisibility(View.INVISIBLE);
		Button btn_title_setting_ok = (Button)findViewById(R.id.btn_alarminfo_alarmname_setting_ok);
		Button btn_title_setting_cancel = (Button)findViewById(R.id.btn_alarminfo_alarmname_setting_cancel);
		btn_title_setting_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				m_strTitle = edit_title.getText().toString();
				rl_title_setting.setVisibility(View.INVISIBLE);
				rl_maskLayer.setVisibility(View.INVISIBLE);
        	}
		});
		btn_title_setting_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				edit_title.setText(m_strTitle);
				rl_title_setting.setVisibility(View.INVISIBLE);
				rl_maskLayer.setVisibility(View.INVISIBLE);
        	}
		});
		
		img_vibration.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if ( m_nVibration == 0 )
				{
					m_nVibration = 1;
					img_vibration.setImageResource(R.drawable.activity_alarm_switchon);
				}
				else
				{
					m_nVibration = 0;
					img_vibration.setImageResource(R.drawable.activity_alarm_switchoff);
				}
        	}
		});
		
		if ( m_nInfoId > 0 )
		{
			if ( m_nRepeatType == 0 )
				lbl_repeat.setText(getResources().getString(R.string.alarm_onetime));
			else if ( m_nRepeatType == 1 )
				lbl_repeat.setText(getResources().getString(R.string.alarm_daily));
			else if ( m_nRepeatType == 2 )
				lbl_repeat.setText(getResources().getString(R.string.alarm_5days));
			else
			{
				changeCheckBoxState();
				lbl_repeat.setText(makeDisplayRepeatString());
			}
			
			if ( m_strSound != null )
				lbl_sound.setText(String.format("%s", m_strSound.getPath()));
			else
				lbl_sound.setText(getResources().getString(R.string.activity_alarminfo_nosound));
			
			edit_title.setText(m_strTitle);
			
			if ( m_nVibration == 1 )
				img_vibration.setImageResource(R.drawable.activity_alarm_switchon);
			else
				img_vibration.setImageResource(R.drawable.activity_alarm_switchoff);
		}
		else
		{
			lbl_repeat.setText(getResources().getString(R.string.alarm_onetime));
			lbl_sound.setText(getResources().getString(R.string.activity_alarminfo_nosound));
			edit_title.setText("");
			img_vibration.setImageResource(R.drawable.activity_alarm_switchoff);
			
			m_nRepeatType = 0;
			m_strSound = null;
			m_strRepeatString = "";
			m_nVibration = 0;
			m_strTitle = "";
		}
		
		int fntSize = (int)(getResources().getDimension(R.dimen.wheelview_fnt_size) * ResolutionSet.fYpro + 0.50001);
		
		wheel_hour = (WheelView) findViewById(R.id.wheel_alarminfo_hour);
		wheel_hour.setDefTextSize(fntSize);
		wheel_hour.setAdapter(new NumericWheelAdapter(0, 23));
		wheel_hour.setLabel(" " + getResources().getString(R.string.common_hours));
		wheel_hour.setCurrentItem(m_nHour);
		
		wheel_minute = (WheelView) findViewById(R.id.wheel_alarminfo_minute);
		wheel_minute.setDefTextSize(fntSize);
		wheel_minute.setAdapter(new NumericWheelAdapter(0, 59));
		wheel_minute.setLabel(" " + getResources().getString(R.string.common_minute));
		wheel_minute.setCurrentItem(m_nMinute);
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
		Intent alarm_activity = new Intent(this, AlarmActivity.class);
		startActivity(alarm_activity);	
		finish();
	}
	
	private void onClickRepeat()
	{
		rl_maskLayer.setVisibility(View.VISIBLE);
		rl_repeat_setting1.setVisibility(View.VISIBLE);
		
		m_dlgtype = 1;
	}
	
	private void onClickSound()
	{
		Global.AlarmSound_CurSelSound = -1;
		Intent fileexploerer_activity = new Intent(this, FileExplorer.class);
		if ( m_strSound != null )
			fileexploerer_activity.putExtra("CURFILEPATH", m_strSound.getPath());
		else
			fileexploerer_activity.putExtra("CURFILEPATH", "/");
		
		startActivityForResult(fileexploerer_activity, REQUEST_SOUND);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_SOUND && resultCode == RESULT_OK)
		{
			String tmp = data.getStringExtra(AlarmSoundActivity.szRetCode);
			
			if ( tmp.length() > 0 )
			{
				m_strSound = Uri.fromFile(new File(tmp));
				lbl_sound.setText(String.format("%s", m_strSound.getPath()));
			}
			else
				lbl_sound.setText(getResources().getString(R.string.activity_alarminfo_nosound));
		}
	}
	
	private void onClickTitle()
	{
		rl_title_setting.setVisibility(View.VISIBLE);
		rl_maskLayer.setVisibility(View.VISIBLE);
	}
	
	private void onClickSave()
	{
		Alarm anAlarm;
		
		if ( m_nInfoId >= 0 )
			anAlarm = Alarms.getAlarm(getContentResolver(), m_nInfoId);
		else
			anAlarm = new Alarm();
		
		anAlarm.hour = wheel_hour.getCurrentItem();
		anAlarm.minutes = wheel_minute.getCurrentItem();
		anAlarm.vibrate = (m_nVibration == 1);
		anAlarm.label = m_strTitle;
		anAlarm.enabled = (m_nStatus == 1);
		
		anAlarm.daysOfWeek.setNoRepeat();
		if ( m_nRepeatType == 0 )
			anAlarm.daysOfWeek.setNoRepeat();
		else if ( m_nRepeatType == 1 )
			anAlarm.daysOfWeek.setEveryday();
		else if ( m_nRepeatType == 2 )
			anAlarm.daysOfWeek.set5days();
		else
		{
			if ( chk_week1.isChecked() ) anAlarm.daysOfWeek.set(6, true);
			if ( chk_week2.isChecked() ) anAlarm.daysOfWeek.set(0, true);
			if ( chk_week3.isChecked() ) anAlarm.daysOfWeek.set(1, true);
			if ( chk_week4.isChecked() ) anAlarm.daysOfWeek.set(2, true);
			if ( chk_week5.isChecked() ) anAlarm.daysOfWeek.set(3, true);
			if ( chk_week6.isChecked() ) anAlarm.daysOfWeek.set(4, true);
			if ( chk_week7.isChecked() ) anAlarm.daysOfWeek.set(5, true);
		}
		
		anAlarm.alert = m_strSound;
		
		if ( m_nInfoId >= 0 )
			Alarms.setAlarm(this, anAlarm);
		else
			Alarms.addAlarm(this, anAlarm);
		
		onClickBack();
	}
	
	private void updateRepeatString()
	{
		String str_week = "";
		if ( chk_week1.isChecked() )
			str_week += "1,";
		if ( chk_week2.isChecked() )
			str_week += "2,";
		if ( chk_week3.isChecked() )
			str_week += "3,";
		if ( chk_week4.isChecked() )
			str_week += "4,";
		if ( chk_week5.isChecked() )
			str_week += "5,";
		if ( chk_week6.isChecked() )
			str_week += "6,";
		if ( chk_week7.isChecked() )
			str_week += "7,";
		
		if ( str_week.length() > 0 )
			m_strRepeatString = str_week.substring(0, str_week.length() - 1);
	}
	
	private String makeDisplayRepeatString()
	{
		String str_week = "";
		String tmp = "";
		if ( chk_week1.isChecked() )
			str_week += getResources().getString(R.string.alarm_sun) + getResources().getString(R.string.common_comma);
		if ( chk_week2.isChecked() )
			str_week += getResources().getString(R.string.alarm_mon) + getResources().getString(R.string.common_comma);
		if ( chk_week3.isChecked() )
			str_week += getResources().getString(R.string.alarm_tue) + getResources().getString(R.string.common_comma);
		if ( chk_week4.isChecked() )
			str_week += getResources().getString(R.string.alarm_wed) + getResources().getString(R.string.common_comma);
		if ( chk_week5.isChecked() )
			str_week += getResources().getString(R.string.alarm_thr) + getResources().getString(R.string.common_comma);
		if ( chk_week6.isChecked() )
			str_week += getResources().getString(R.string.alarm_fri) + getResources().getString(R.string.common_comma);
		if ( chk_week7.isChecked() )
			str_week += getResources().getString(R.string.alarm_sat) + getResources().getString(R.string.common_comma);
		
		if ( str_week.length() > 0 )
			tmp = str_week.substring(0, str_week.length() - 1);
		
		return tmp;
	}
	
	private void changeCheckBoxState()
	{
		String rep_str[] = m_strRepeatString.split(",");
		
		int cnt = rep_str.length;
		
		chk_week1.setChecked(false);
		chk_week1.setTextColor(getResources().getColor(R.color.common_whitegray));
		chk_week2.setChecked(false);
		chk_week2.setTextColor(getResources().getColor(R.color.common_whitegray));
		chk_week3.setChecked(false);
		chk_week3.setTextColor(getResources().getColor(R.color.common_whitegray));
		chk_week4.setChecked(false);
		chk_week4.setTextColor(getResources().getColor(R.color.common_whitegray));
		chk_week5.setChecked(false);
		chk_week5.setTextColor(getResources().getColor(R.color.common_whitegray));
		chk_week6.setChecked(false);
		chk_week6.setTextColor(getResources().getColor(R.color.common_whitegray));
		chk_week7.setChecked(false);
		chk_week7.setTextColor(getResources().getColor(R.color.common_whitegray));
		
		if ( m_strRepeatString.length() > 0 )
		{
			for ( int i = 0; i < cnt; i++ )
			{
				int tmp = Integer.valueOf(rep_str[i]);
				if ( tmp == 1 )
				{
					chk_week1.setChecked(true);
					chk_week1.setTextColor(getResources().getColor(R.color.common_normal_text));
				}
				if ( tmp == 2 )
				{
					chk_week2.setChecked(true);
					chk_week2.setTextColor(getResources().getColor(R.color.common_normal_text));
				}
				if ( tmp == 3 )
				{
					chk_week3.setChecked(true);
					chk_week3.setTextColor(getResources().getColor(R.color.common_normal_text));
				}
				if ( tmp == 4 )
				{
					chk_week4.setChecked(true);
					chk_week4.setTextColor(getResources().getColor(R.color.common_normal_text));
				}
				if ( tmp == 5 )
				{
					chk_week5.setChecked(true);
					chk_week5.setTextColor(getResources().getColor(R.color.common_normal_text));
				}
				if ( tmp == 6 )
				{
					chk_week6.setChecked(true);
					chk_week6.setTextColor(getResources().getColor(R.color.common_normal_text));
				}
				if ( tmp == 7 )
				{
					chk_week7.setChecked(true);
					chk_week7.setTextColor(getResources().getColor(R.color.common_normal_text));
				}
			}
		}
	}
}
