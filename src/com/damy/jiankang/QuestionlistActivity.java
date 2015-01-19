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

import com.damy.Utils.PullToRefreshBase;
import com.damy.Utils.PullToRefreshListView;
import com.damy.adapters.QuestionAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STQuestionInfo;


public class QuestionlistActivity extends BaseActivity implements MyActivityMethods {

    private ImageButton m_imgbtnBack;
	private PullToRefreshListView		m_lvBaseQuestionListView;
	private ArrayList<STQuestionInfo> 	m_BaseQuestionList;
	private QuestionAdapter				m_BaseQuestionAdapter = null;
	private ListView					mRealListView;

	private int							m_nCurPageNumber = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questionlist);

        initActivity(R.id.rlMain);
        initControl();
        initHandler();        
        setBaseQuestionAdapter();
        
        new LoadResponseThread(QuestionlistActivity.this).start();        
	}

    @Override
    public void initControl() {
        m_imgbtnBack = (ImageButton) findViewById(R.id.imgbtnBack);
        m_lvBaseQuestionListView = (PullToRefreshListView)findViewById(R.id.anQuestionListContentView);
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
    
    private void onClickBack()
    {
    	Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);
		finish();
    }
    
	private void setBaseQuestionAdapter() {
		m_BaseQuestionList = new ArrayList<STQuestionInfo>();
		m_lvBaseQuestionListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        // Set a listener to be invoked when the list should be refreshed.
		m_lvBaseQuestionListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                m_nCurPageNumber = m_nCurPageNumber + 1;
                new LoadResponseThread(QuestionlistActivity.this).start();
            }
        });

        mRealListView = m_lvBaseQuestionListView.getRefreshableView();
        registerForContextMenu(mRealListView);

        mRealListView.setCacheColorHint(Color.TRANSPARENT);
        mRealListView.setDividerHeight(0);
        mRealListView.setDrawSelectorOnTop(false);

        m_BaseQuestionAdapter = new QuestionAdapter(QuestionlistActivity.this, m_BaseQuestionList);
        mRealListView.setAdapter(m_BaseQuestionAdapter);
        
        mRealListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {        	
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickItem(position);
        	}
		});

	}
	
	private void onClickItem(int position)
	{
		STQuestionInfo item = m_BaseQuestionList.get(position - 1);
		
		Intent questiondetail_activity = new Intent(this, QuestionDetailActivity.class);
		questiondetail_activity.putExtra(QuestionDetailActivity.EXTRADATA_QUESTIONTYPEID, item.id);
		startActivity(questiondetail_activity);
		finish();
	}
    
    public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			m_BaseQuestionAdapter.notifyDataSetChanged();
			m_lvBaseQuestionListView.onRefreshComplete();
		}
	}
	
	public void getResponseJSON() {
		try {				
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETQALIST;
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
			            STQuestionInfo itemInfo = new STQuestionInfo();
	
						itemInfo.id = tmpObj.getLong("id");
						itemInfo.content = tmpObj.getString("title");
						itemInfo.regtime = tmpObj.getString("regtime");
						itemInfo.count = tmpObj.getInt("count");
						
						m_BaseQuestionList.add(itemInfo);
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
