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

import com.damy.adapters.FoodMainAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.datatypes.STFoodMainInfo;



public class FoodMainListActivity extends BaseActivity implements MyActivityMethods {

    private ImageButton                 m_imgbtnBack;
	
	private ArrayList<STFoodMainInfo> 	m_BaseFoodList;	
	private FoodMainAdapter		        m_BaseFoodAdapter = null;
	
	private int                         main_id = 0;
	private String                      main_title = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_foodmainlist);
		
        initActivity(R.id.rl_foodmainlist);
        initControl();
        initHandler();    
        m_BaseFoodList = new ArrayList<STFoodMainInfo>();      
        
        new LoadResponseThread(FoodMainListActivity.this).start();    
       
	}

    @Override
    public void initControl() {
        m_imgbtnBack = (ImageButton) findViewById(R.id.img_foodmainlist_back);        

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
    	Intent foodrecord_activity = new Intent(this, FoodRecordActivity.class);
		startActivity(foodrecord_activity);	
		finish();
	}
    
    
	private void setBaseAdapter() {		
		
		m_BaseFoodAdapter = new FoodMainAdapter(this, m_BaseFoodList);
		
		ListView list = (ListView)findViewById(R.id.lv_food_main_data);
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
		STFoodMainInfo clickedItem = m_BaseFoodList.get(pos);
		
		Intent sub_activity = new Intent(this, FoodSubListActivity.class);
		sub_activity.putExtra(FoodSubListActivity.MAIN_ID, clickedItem.id);
		sub_activity.putExtra(FoodSubListActivity.MAIN_TITLE, clickedItem.title);
		startActivity(sub_activity);
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
				
				String strRequest = HttpConnUsingJSON.REQ_GETFOODMAINLIST;			
				
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
			            STFoodMainInfo itemInfo = new STFoodMainInfo();
	
						itemInfo.id = tmpObj.getInt("id");
						itemInfo.title = tmpObj.getString("name");
						
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
