/* �亙熒*/
package com.damy.jiankang;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.ResolutionSet;
import com.damy.adapters.AlarmSoundAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STSportRecordInfo;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AlarmSoundActivity extends BaseActivity {
	
	private ListView						lv_AlarmSound;
	private ArrayList<String>				m_AlarmSoundList;
	private AlarmSoundAdapter				m_AlarmSoundAdapter = null;
	
	public static String 					szRetCode = "RET";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarmsound);
		
		initActivity(R.id.rl_alarmsound);
		
		initControls();
		setAlarmSoundAdapter();
		readContents();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_alarmsound_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		lv_AlarmSound = (ListView)findViewById(R.id.list_alarmsound_content);
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
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		AlarmSoundActivity.this.finish();
	}
	
	private void readContents()
	{
		for (int i = 1; i <= 20; i++ )
			m_AlarmSoundList.add(String.format("Sound %02d", i));
		
		m_AlarmSoundAdapter.notifyDataSetChanged();
	}
	
	private void setAlarmSoundAdapter() {
		m_AlarmSoundList = new ArrayList<String>();

		lv_AlarmSound.setCacheColorHint(Color.TRANSPARENT);
		lv_AlarmSound.setDividerHeight(0);
		lv_AlarmSound.setDrawSelectorOnTop(false);

        m_AlarmSoundAdapter = new AlarmSoundAdapter(AlarmSoundActivity.this, m_AlarmSoundList);
        lv_AlarmSound.setAdapter(m_AlarmSoundAdapter);
        
        lv_AlarmSound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickItem(position);
	    	}
		});
	}
	
	private void onClickItem(int pos)
	{
		Intent returnIntent = new Intent();
		returnIntent.putExtra(szRetCode, pos);
		setResult(RESULT_OK, returnIntent);
		AlarmSoundActivity.this.finish();
	}
}
