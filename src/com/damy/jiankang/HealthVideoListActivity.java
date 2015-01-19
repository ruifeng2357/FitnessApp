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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.damy.Utils.PullToRefreshBase;
import com.damy.Utils.PullToRefreshListView;
import com.damy.adapters.HealthVideoMainAdapter;
import com.damy.adapters.NewsAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STHealthVideoMainInfo;
import com.damy.datatypes.STNewsInfo;


public class HealthVideoListActivity extends BaseActivity implements MyActivityMethods {

	private enum REQ_TYPE{REQ_GETNEWSLIST, REQ_GETHEALTHMAINLIST};
		
	private REQ_TYPE					m_reqType;
	
    private ImageButton 				m_imgbtnBack;
	private PullToRefreshListView		m_lvBaseListView;
	private RelativeLayout 				rl_news;
	private RelativeLayout 				rl_health;
	private RelativeLayout 				rl_news_border;
	private RelativeLayout 				rl_health_border;
	private TextView       				txt_news;
	private TextView       				txt_health;
	
	private ArrayList<STHealthVideoMainInfo> 	m_BaseHealthList;
	private ArrayList<STNewsInfo> 	            m_BaseNewsList;
	private HealthVideoMainAdapter				m_BaseHealthAdapter = null;
	private NewsAdapter				            m_BaseNewsAdapter = null;
	private ListView					mRealListView;

	private int							m_nCurPageNumber = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_healthvideolist);

        initActivity(R.id.rl_healthvideolist);
        initControl();
        initHandler();        
        setBaseAdapter();
        
        new LoadResponseThread(HealthVideoListActivity.this).start();        
	}

    @Override
    public void initControl() {
        m_imgbtnBack = (ImageButton) findViewById(R.id.img_healthvideolist_back);        
        m_lvBaseListView = (PullToRefreshListView)findViewById(R.id.anHealthVideoListContentView);
        rl_news = (RelativeLayout)findViewById(R.id.rl_healthvideolist_title_news);
        txt_news = (TextView)findViewById(R.id.txt_healthvideolist_title_news);
        rl_news_border = (RelativeLayout)findViewById(R.id.rl_healthvideolist_title_news_border);
        rl_health = (RelativeLayout)findViewById(R.id.rl_healthvideolist_title_health);
        txt_health = (TextView)findViewById(R.id.txt_healthvideolist_title_health);
        rl_health_border = (RelativeLayout)findViewById(R.id.rl_healthvideolist_title_health_border);
    }

    @Override
    public void initHandler() {
        m_imgbtnBack.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	onClickBack();        		                
            }
        });
        
        rl_news.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	rl_news_border.setBackgroundColor(getResources().getColor(R.color.common_titlebar_back));
            	txt_news.setTextColor(getResources().getColor(R.color.common_titlebar_back));
            	rl_health_border.setBackgroundColor(getResources().getColor(R.color.color_white));
            	txt_health.setTextColor(getResources().getColor(R.color.common_note_text));
            	 m_reqType = REQ_TYPE.REQ_GETNEWSLIST;
            	 if (m_BaseNewsList.size() > 0) m_BaseNewsList.clear();
            	 setBaseAdapter(); 
            	 m_nCurPageNumber = 1;
            	 new LoadResponseThread(HealthVideoListActivity.this).start();      
            }
        });
        
        rl_health.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	rl_health_border.setBackgroundColor(getResources().getColor(R.color.common_titlebar_back));
            	txt_health.setTextColor(getResources().getColor(R.color.common_titlebar_back));
            	rl_news_border.setBackgroundColor(getResources().getColor(R.color.color_white));
            	txt_news.setTextColor(getResources().getColor(R.color.common_note_text));
            	 m_reqType = REQ_TYPE.REQ_GETHEALTHMAINLIST;
            	 if (m_BaseHealthList.size() > 0) m_BaseHealthList.clear();
            	 setBaseAdapter(); 
            	 m_nCurPageNumber = 1;
            	 new LoadResponseThread(HealthVideoListActivity.this).start();      
            }
        });
        
        m_reqType = REQ_TYPE.REQ_GETNEWSLIST;
        
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
    	Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}
    
    
	private void setBaseAdapter() {
		m_BaseNewsList = new ArrayList<STNewsInfo>();
		m_BaseHealthList = new ArrayList<STHealthVideoMainInfo>();
		m_lvBaseListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        // Set a listener to be invoked when the list should be refreshed.
		m_lvBaseListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                m_nCurPageNumber = m_nCurPageNumber + 1;              
                new LoadResponseThread(HealthVideoListActivity.this).start();
            }
        });

        mRealListView = m_lvBaseListView.getRefreshableView();
        registerForContextMenu(mRealListView);

        //mRealListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        mRealListView.setCacheColorHint(Color.TRANSPARENT);
        mRealListView.setDividerHeight(0);
        mRealListView.setDrawSelectorOnTop(false);

        if (m_reqType == REQ_TYPE.REQ_GETNEWSLIST) {
        	m_BaseNewsAdapter = new NewsAdapter(HealthVideoListActivity.this, m_BaseNewsList);
            mRealListView.setAdapter(m_BaseNewsAdapter);	
        }
        else if (m_reqType == REQ_TYPE.REQ_GETHEALTHMAINLIST) {
        	m_BaseHealthAdapter = new HealthVideoMainAdapter(HealthVideoListActivity.this, m_BaseHealthList);
            mRealListView.setAdapter(m_BaseHealthAdapter);
        }
        
		mRealListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickItem(position);
        	}
		});
        
	}	

	private void onClickItem(int pos)
	{
		if (m_reqType == REQ_TYPE.REQ_GETNEWSLIST) {
			STNewsInfo clickedItem = getItem(pos - 1);
			
			Intent newsdetail_activity = new Intent(this, NewsDetailActivity.class);
			newsdetail_activity.putExtra(NewsDetailActivity.NEWS_ID, clickedItem.id);
			startActivity(newsdetail_activity);	
			
		} else if (m_reqType == REQ_TYPE.REQ_GETHEALTHMAINLIST) {
			STHealthVideoMainInfo clickedItem = getHealthItem(pos - 1);
			
			Intent healthsub_activity = new Intent(this, HealthSubListActivity.class);
			healthsub_activity.putExtra(HealthSubListActivity.MAIN_ID, clickedItem.id);
			healthsub_activity.putExtra(HealthSubListActivity.MAIN_TITLE, clickedItem.title);
			startActivity(healthsub_activity);
		}
		
	}
	
	public STNewsInfo getItem(int position)
	{
		if (position < 0 || position >= m_BaseNewsList.size())
			return null;
		
		return m_BaseNewsList.get(position);
	}
	
	public STHealthVideoMainInfo getHealthItem(int position)
	{
		if (position < 0 || position >= m_BaseHealthList.size())
			return null;
		
		return m_BaseHealthList.get(position);
	}
    
    public void refreshUI() {
		super.refreshUI();
		if (m_reqType == REQ_TYPE.REQ_GETNEWSLIST) {
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				m_BaseNewsAdapter.notifyDataSetChanged();
				m_lvBaseListView.onRefreshComplete();
			}	
		}
		else if (m_reqType == REQ_TYPE.REQ_GETHEALTHMAINLIST) {
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				m_BaseHealthAdapter.notifyDataSetChanged();
				m_lvBaseListView.onRefreshComplete();
			}	
		} 
		
	}
	
	public void getResponseJSON() {
		try {				
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				if (m_reqType == REQ_TYPE.REQ_GETNEWSLIST) {
					String strRequest = HttpConnUsingJSON.REQ_GETNEWSLIST;
					strRequest += "?uid=" + Global.Cur_UserId;
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
				            STNewsInfo itemInfo = new STNewsInfo();
		
							itemInfo.id = tmpObj.getInt("id");
							itemInfo.title = tmpObj.getString("title");
							itemInfo.regtime = tmpObj.getString("date");
																		
							m_BaseNewsList.add(itemInfo);
			            }
					}
				}
				else if (m_reqType == REQ_TYPE.REQ_GETHEALTHMAINLIST) {
					String strRequest = HttpConnUsingJSON.REQ_GETHALTHVIDEOMAINLIST;
					strRequest += "?uid=" + Global.Cur_UserId;
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
				            STHealthVideoMainInfo itemInfo = new STHealthVideoMainInfo();
		
							itemInfo.id = tmpObj.getInt("id");
							itemInfo.title = tmpObj.getString("title");
							itemInfo.image = tmpObj.getString("image");											
							
							m_BaseHealthList.add(itemInfo);
			            }
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
