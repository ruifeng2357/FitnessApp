/* 健康*/
package com.damy.jiankang;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;



public class NewsDetailActivity extends BaseActivity implements MyActivityMethods {

	public static final String NEWS_ID = "NEWSID";
    ImageButton m_imgbtnBack;
    TextView    txt_title;    
    TextView    txt_date;
    WebView     wv_content;

    String      title;
    String      date;
    String      content;
    int m_nid = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsdetail);
		m_nid = getIntent().getIntExtra(NEWS_ID, 0);
        initActivity(R.id.rl_newsdetail);
        initControl();
        initHandler();
	}

    @Override
    public void initControl() {
        m_imgbtnBack = (ImageButton) findViewById(R.id.img_newsdetail_back);
      
        txt_title = (TextView)findViewById(R.id.txt_newsdetail_title);        
        txt_date = (TextView)findViewById(R.id.txt_newsdetail_date);
        wv_content = (WebView)findViewById(R.id.webView_newsdetail);  
	}

    @Override
    public void initHandler() {
        m_imgbtnBack.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	onClickBack();
                
            }
        });
        
       
        new LoadResponseThread(NewsDetailActivity.this).start();
       
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
		finish();
	}	

    
    public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			txt_title.setText(title);
			txt_date.setText(date);
			wv_content.loadData(content, "text/html", "UTF-8");
		}
	}
	
	public void getResponseJSON() {
		try {	
				
				m_nResponse = ResponseRet.RET_SUCCESS;				
				
				
				String strRequest = HttpConnUsingJSON.REQ_GetNEWSINFO;
				strRequest += "?id=" + m_nid;
				
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
				
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
	            if (m_nResponse == ResponseRet.RET_SUCCESS) {	            		            	
	            	JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
					
					title = dataObject.getString("title");
					date = dataObject.getString("date");
					content= dataObject.getString("content");								
		            
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
