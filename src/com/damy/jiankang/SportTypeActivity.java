/* �亙熒*/
package com.damy.jiankang;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.ResolutionSet;
import com.damy.adapters.SportTypeAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STSportRecordInfo;
import com.damy.datatypes.STSportTypeInfo;


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

public class SportTypeActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETSPORTLIST};
	
	private ListView						lv_SportType;
	private ArrayList<STSportTypeInfo>		m_SportTypeList;
	private SportTypeAdapter				m_SportTypeAdapter = null;
	
	private REQ_TYPE						m_reqType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sporttype);
		
		initActivity(R.id.rl_sporttype);
		
		initControls();
		setSportTypeAdapter();
		readContents();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_sporttype_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		lv_SportType = (ListView)findViewById(R.id.list_sporttype_content);
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
	
	private void readContents()
	{
		m_reqType = REQ_TYPE.REQ_GETSPORTLIST;
		new LoadResponseThread(SportTypeActivity.this).start();
	}
	
	private void setSportTypeAdapter() {
		m_SportTypeList = new ArrayList<STSportTypeInfo>();

		lv_SportType.setCacheColorHint(Color.TRANSPARENT);
		lv_SportType.setDividerHeight(0);
		lv_SportType.setDrawSelectorOnTop(false);

        m_SportTypeAdapter = new SportTypeAdapter(SportTypeActivity.this, m_SportTypeList);
        lv_SportType.setAdapter(m_SportTypeAdapter);
        
        lv_SportType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickItem(position);
	    	}
		});
        
	}
	
	private void onClickItem(int pos)
	{
		STSportTypeInfo item = m_SportTypeList.get(pos);
		
		Intent sportrecordinfo_activity = new Intent(this, SportRecordInfoActivity.class);
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_RECORDID, (long)0);
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_SPORTID, item.sport_id);
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_NAME, item.name);
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_IMAGE, item.image);
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_YEAR, Global.SportRecord_CurDate.getYear() + 1900);
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_MONTH, Global.SportRecord_CurDate.getMonth() + 1);
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_DATE, Global.SportRecord_CurDate.getDate());
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_STARTTIME, "");
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_ENDTIME, "");	
		startActivity(sportrecordinfo_activity);
		finish();
	}
	
	public void refreshUI() {
		super.refreshUI();
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			m_SportTypeAdapter.notifyDataSetChanged();
		 }
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETSPORTLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSPORTLIST;
				
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
					
					int count = dataObject.getInt("count");

		            JSONArray dataList = dataObject.getJSONArray("data");
		            
		            for (int i = 0; i < count; i++) {
		            	JSONObject tmpObj = (JSONObject) dataList.get(i);
			            STSportTypeInfo itemInfo = new STSportTypeInfo();

			            itemInfo.sport_id = tmpObj.getLong("id");
						itemInfo.name = tmpObj.getString("name");
						itemInfo.time = tmpObj.getInt("time");
						itemInfo.calory = tmpObj.getInt("calory");
						itemInfo.image = tmpObj.getString("image");
						
						m_SportTypeList.add(itemInfo);
		            }
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
}
