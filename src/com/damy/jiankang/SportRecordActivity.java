/* �亙熒*/
package com.damy.jiankang;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.ResolutionSet1;
import com.damy.adapters.SportRecordAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STSportRecordInfo;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SportRecordActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETSPORTRECORDLIST, REQ_DELETESPORTRECORD};
	
	private ListView						lv_SportRecord;
	private ArrayList<STSportRecordInfo>	m_SportRecordList;
	private SportRecordAdapter				m_SportRecordAdapter = null;
	
	private String							m_strDate = "";
	private Date							m_curDate;
	private int								m_nCalory = 0;
	
	private RelativeLayout 					m_deleteLayer;
	private RelativeLayout					m_maskLayer;
	
	private int								m_curClickedItem = 0;
	
	
	private REQ_TYPE						m_reqType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sportrecord);
		
		initActivity(R.id.rl_sportrecord);
		
		m_curDate = new Date();
		
		if ( m_curDate.getHours() < 3 )
			m_curDate.setDate(m_curDate.getDate() - 1);
		
		initControls();
		setSportRecordAdapter();
		onClickDateChange(0);
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_sportrecord_back);
		ImageView img_datedown = (ImageView)findViewById(R.id.img_sportrecord_datedown);
		ImageView img_dateup = (ImageView)findViewById(R.id.img_sportrecord_dateup);
		ImageView img_plus = (ImageView)findViewById(R.id.img_sportrecord_plus);
		ImageButton btn_facebook = (ImageButton)findViewById(R.id.imgbtn_sportrecord_facebook);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		img_datedown.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDateChange(-1);
        	}
        });
		
		img_dateup.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDateChange(1);
        	}
        });
		
		img_plus.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPlus();
        	}
        });
		
		btn_facebook.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickFacebook();
        	}
        });
		
		
		lv_SportRecord = (ListView)findViewById(R.id.list_sportrecord_content);
		
		lv_SportRecord.setCacheColorHint(Color.TRANSPARENT);
		lv_SportRecord.setDividerHeight(0);
		lv_SportRecord.setDrawSelectorOnTop(false);
		
		lv_SportRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickItem(position);
        	}
		});
		
        lv_SportRecord.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				onLongClickItem(parent, position);
				return true;
	    	}
		});
		
		m_maskLayer = (RelativeLayout)findViewById(R.id.rl_sportrecord_mask);
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
	
	private void onClickFacebook()
	{
		Intent facebook_activity = new Intent(this, FacebookActivity.class);
		facebook_activity.putExtra(FacebookActivity.EXTRADATA_DATE, m_strDate);
		facebook_activity.putExtra(FacebookActivity.EXTRADATA_TYPE, 0);
		startActivity(facebook_activity);
	}
	
	private void onClickPlus()
	{
		Intent sporttype_activity = new Intent(this, SportTypeActivity.class);
		startActivity(sporttype_activity);
		finish();
	}
	
	private void onClickDateChange(int updown)
	{
		Global.SportRecord_CurDate.setDate(Global.SportRecord_CurDate.getDate() + updown);
		
		TextView txt_date = (TextView)findViewById(R.id.lbl_sportrecord_date);
		
		if ( Global.SportRecord_CurDate.getYear() == m_curDate.getYear() &&  Global.SportRecord_CurDate.getMonth() == m_curDate.getMonth() && Global.SportRecord_CurDate.getDate() == m_curDate.getDate())
			txt_date.setText(getResources().getString(R.string.common_today));
		else
			txt_date.setText(String.format("%d-%d-%d", 1900 + Global.SportRecord_CurDate.getYear(), Global.SportRecord_CurDate.getMonth() + 1, Global.SportRecord_CurDate.getDate()));
		
		m_strDate = String.format("%d-%d-%d", 1900 + Global.SportRecord_CurDate.getYear(), Global.SportRecord_CurDate.getMonth() + 1, Global.SportRecord_CurDate.getDate());
		
		m_SportRecordList.clear();
		readContents();
	}
	
	private void readContents()
	{
		m_reqType = REQ_TYPE.REQ_GETSPORTRECORDLIST;
		new LoadResponseThread(SportRecordActivity.this).start();
	}
	
	private void setSportRecordAdapter() {
		m_SportRecordList = new ArrayList<STSportRecordInfo>();

        m_SportRecordAdapter = new SportRecordAdapter(SportRecordActivity.this, m_SportRecordList);
        lv_SportRecord.setAdapter(m_SportRecordAdapter);
	}
	
	public void onClickItem(int pos)
	{
		STSportRecordInfo item = m_SportRecordList.get(pos);
		
		Intent sportrecordinfo_activity = new Intent(this, SportRecordInfoActivity.class);
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_RECORDID, item.record_id);
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_SPORTID, item.sport_id);
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_NAME, item.name);
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_IMAGE, item.image);
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_YEAR, Global.SportRecord_CurDate.getYear() + 1900);
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_MONTH, Global.SportRecord_CurDate.getMonth() + 1);
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_DATE, Global.SportRecord_CurDate.getDate());
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_STARTTIME, item.start_time);
		sportrecordinfo_activity.putExtra(SportRecordInfoActivity.EXTRADATA_ENDTIME, item.end_time);
		startActivity(sportrecordinfo_activity);
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
		
		m_reqType = REQ_TYPE.REQ_DELETESPORTRECORD;
		new LoadResponseThread(SportRecordActivity.this).start();
	}
	
	private void onClickDelConfirmCancel()
	{
		m_maskLayer.setVisibility(View.INVISIBLE);
		m_deleteLayer.setVisibility(View.INVISIBLE);
	}
	
	public void refreshUI() {
		super.refreshUI();
		if ( m_reqType == REQ_TYPE.REQ_GETSPORTRECORDLIST )
		{
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				TextView txt_calory = (TextView)findViewById(R.id.lbl_sportrecord_totalvalue);
				txt_calory.setText(String.format("%d", m_nCalory));
				
				m_SportRecordAdapter.notifyDataSetChanged();
			}
		}
		else
		{
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				m_SportRecordList.clear();
				readContents();
			}
		}
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETSPORTRECORDLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSPORTRECORDLIST;
				strRequest += "?uid=" + String.valueOf(Global.Cur_UserId);
				strRequest += "&date=" + m_strDate;
				
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
					
					int count = dataObject.getInt("count");
					m_nCalory = dataObject.getInt("calory");
					
		            JSONArray dataList = dataObject.getJSONArray("data");
		            
		            for (int i = 0; i < count; i++) {
		            	JSONObject tmpObj = (JSONObject) dataList.get(i);
			            STSportRecordInfo itemInfo = new STSportRecordInfo();
	
						itemInfo.record_id = tmpObj.getLong("id");
						itemInfo.sport_id = tmpObj.getLong("sport_id");
						itemInfo.name = tmpObj.getString("name");
						itemInfo.time = (int)tmpObj.getDouble("time");
						itemInfo.calory = (int)tmpObj.getDouble("calory");
						itemInfo.image = tmpObj.getString("image");
						itemInfo.start_time = tmpObj.getString("start");
						itemInfo.end_time = tmpObj.getString("end");
						
						m_SportRecordList.add(itemInfo);
		            }
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_DELETESPORTRECORD )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_DELETESPORTRECORD;
				
				JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
	
	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();
		
		requestObj.put("id", String.valueOf(m_SportRecordList.get(m_curClickedItem).record_id));
		requestObj.put("uid", String.valueOf(Global.Cur_UserId));

		return requestObj;
	}
}
