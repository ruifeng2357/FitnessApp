/* 健康*/
package com.damy.jiankang;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.damy.adapters.FoodSubAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.datatypes.STFoodSubInfo;


public class FoodSubListActivity extends BaseActivity implements MyActivityMethods {

	public static final String          MAIN_ID = "MAINID";
	public static final String          MAIN_TITLE = "MAINTITLE";
    private ImageButton                 m_imgbtnBack;

	private ArrayList<STFoodSubInfo> 	m_BaseFoodList;	
	private FoodSubAdapter		        m_BaseFoodAdapter = null;

	private int                         main_id = 0;
	private String                      main_title = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_foodsublist);
		
		main_id = getIntent().getIntExtra(MAIN_ID, 0);
		main_title = getIntent().getStringExtra(MAIN_TITLE);
		
        initActivity(R.id.rl_foodsublist);
        initControl();
        initHandler();    
        m_BaseFoodList = new ArrayList<STFoodSubInfo>();      
        
        new LoadResponseThread(FoodSubListActivity.this).start();
	}

    @Override
    public void initControl() {
        m_imgbtnBack = (ImageButton) findViewById(R.id.img_foodsublist_back);        
       
        TextView txt_title = (TextView)findViewById(R.id.lbl_foodsub_title);
        txt_title.setText(main_title);
    }

    @Override
    public void initHandler() {
        m_imgbtnBack.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	onClickBack();        		                
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
    
    private void onClickBack() {
    	Intent foodmain_activity = new Intent(this, FoodMainListActivity.class);
		startActivity(foodmain_activity);	
		finish();
	}
    
	private void setBaseAdapter() {		
		
		m_BaseFoodAdapter = new FoodSubAdapter(this, m_BaseFoodList);
		
		ListView list = (ListView)findViewById(R.id.lv_food_sub_data);
		list.setAdapter(m_BaseFoodAdapter);
		list.setDrawSelectorOnTop(false);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.common_lightgray)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickFoodItem(position);
        	}
		});        
	}	

	private void onClickFoodItem(int pos)
	{
		STFoodSubInfo item = m_BaseFoodList.get(pos);
		
		Intent foodrecordinfo_activity = new Intent(this, FoodRecordInfoActivity.class);
		foodrecordinfo_activity.putExtra(FoodRecordInfoActivity.EXTRADATA_RECORDID, (long)0);
		foodrecordinfo_activity.putExtra(FoodRecordInfoActivity.EXTRADATA_FOODID, item.id);
		startActivity(foodrecordinfo_activity);
		
		finish();
	}
	
    public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			setBaseAdapter();
		}
	}
	
	public void getResponseJSON() {
		try {				
				m_nResponse = ResponseRet.RET_SUCCESS;				
				
				String strRequest = HttpConnUsingJSON.REQ_GETFOODSUBLIST;				
				strRequest += "?main_id=" + main_id;
				
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
		            	STFoodSubInfo itemInfo = new STFoodSubInfo();
	
						itemInfo.id = tmpObj.getLong("id");
						itemInfo.title = tmpObj.getString("name");
						itemInfo.image = tmpObj.getString("image");
						itemInfo.mass_calory = String.valueOf(tmpObj.getInt("mass_calory"));
						itemInfo.mass = String.valueOf(tmpObj.getInt("mass"));
						itemInfo.count = String.valueOf(tmpObj.getInt("count"));
						
						m_BaseFoodList.add(itemInfo);
		            }
				}
			} catch (JSONException e) {
				e.printStackTrace();
				m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
			}
	}
	
	public JSONObject makeRequestJSON() throws JSONException {
		return null;
	}
	
}
