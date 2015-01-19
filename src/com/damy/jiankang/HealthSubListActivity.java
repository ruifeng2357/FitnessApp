/* 健康*/
package com.damy.jiankang;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.damy.Utils.PullToRefreshBase;
import com.damy.Utils.PullToRefreshListView;
import com.damy.adapters.HealthVideoSubAdapter;
import com.damy.adapters.NewsAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.datatypes.STHealthVideoSubInfo;


public class HealthSubListActivity extends BaseActivity implements MyActivityMethods {

	public static final String 					MAIN_ID = "MAINID";
	public static final String 					MAIN_TITLE = "MAINTITLE";
    private ImageButton 						m_imgbtnBack;
	private PullToRefreshListView				m_lvBaseListView;

	
	private ArrayList<STHealthVideoSubInfo> 	m_BaseHealthList;	
	private HealthVideoSubAdapter				m_BaseHealthAdapter = null;
	private ListView							mRealListView;

	private int									m_nCurPageNumber = 1;
	private int                         		main_id = 0;
	private String                     			main_title = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_healthsublist);
		main_id = getIntent().getIntExtra(MAIN_ID, 0);
		main_title = getIntent().getStringExtra(MAIN_TITLE);
        initActivity(R.id.rl_healthsublist);
        initControl();
        initHandler();        
        setBaseAdapter();
        
        new LoadResponseThread(HealthSubListActivity.this).start();        
	}

    @Override
    public void initControl() {
        m_imgbtnBack = (ImageButton) findViewById(R.id.img_healthsublist_back);        
        m_lvBaseListView = (PullToRefreshListView)findViewById(R.id.anHealthSubListContentView);
        TextView txt_title = (TextView)findViewById(R.id.lbl_healthsub_title);
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
		finish();
	}
    
    
	private void setBaseAdapter() {
		
		m_BaseHealthList = new ArrayList<STHealthVideoSubInfo>();
		m_lvBaseListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        // Set a listener to be invoked when the list should be refreshed.
		m_lvBaseListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                m_nCurPageNumber = m_nCurPageNumber + 1;              
                new LoadResponseThread(HealthSubListActivity.this).start();
            }
        });

        mRealListView = m_lvBaseListView.getRefreshableView();
        registerForContextMenu(mRealListView);

        //mRealListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        mRealListView.setCacheColorHint(Color.TRANSPARENT);
        mRealListView.setDividerHeight(0);
        mRealListView.setDrawSelectorOnTop(false);

       	m_BaseHealthAdapter = new HealthVideoSubAdapter(HealthSubListActivity.this, m_BaseHealthList);
        mRealListView.setAdapter(m_BaseHealthAdapter);
    
        
		mRealListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickItem(position);
        	}
		});
        
	}	

	private void onClickItem(int pos)
	{
		STHealthVideoSubInfo clickedItem = getItem(pos - 1);
			
		Intent sub_activity = new Intent(this, HealthSubActivity.class);
		sub_activity.putExtra(HealthSubActivity.MAIN_ID, main_id);
		sub_activity.putExtra(HealthSubActivity.MAIN_TITLE, main_title);
		sub_activity.putExtra(HealthSubActivity.SUB_ID, clickedItem.id);
		sub_activity.putExtra(HealthSubActivity.SUB_URL, clickedItem.url);
		sub_activity.putExtra(HealthSubActivity.SUB_TITLE, clickedItem.title);
		startActivity(sub_activity);	
		finish();
	}
	
	public STHealthVideoSubInfo getItem(int position)
	{
		if (position < 0 || position >= m_BaseHealthList.size())
			return null;
		
		return m_BaseHealthList.get(position);
	}
    
    public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			m_BaseHealthAdapter.notifyDataSetChanged();
			m_lvBaseListView.onRefreshComplete();
		}
	}
	
	public void getResponseJSON() {
		try {				
				m_nResponse = ResponseRet.RET_SUCCESS;				
				
				String strRequest = HttpConnUsingJSON.REQ_GETHALTHVIDEOSUBLIST;
				strRequest += "?main_id=" + main_id;
				strRequest += "&page=" + m_nCurPageNumber;				
				
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
			            STHealthVideoSubInfo itemInfo = new STHealthVideoSubInfo();
	
						itemInfo.id = tmpObj.getInt("id");
						itemInfo.title = tmpObj.getString("title");
						itemInfo.image = tmpObj.getString("image");	
						itemInfo.url = tmpObj.getString("url");
						
						m_BaseHealthList.add(itemInfo);
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
