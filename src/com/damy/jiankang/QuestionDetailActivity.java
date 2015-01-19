/* �亙熒*/
package com.damy.jiankang;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.adapters.QuestionDetailAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STQuestionDetailInfo;


import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class QuestionDetailActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETQADETAIL, REQ_INSERTQUESTION};
	private REQ_TYPE							m_reqType;
	
	public static String						EXTRADATA_QUESTIONTYPEID = "SPORTRECORDINFO_RECORDID";
	
	private ListView							lv_QuestionDetail;
	private ArrayList<STQuestionDetailInfo>		m_QuestionDetailList;
	private QuestionDetailAdapter				m_QuestionDetailAdapter = null;
	
	private long								m_nCurQuestionTypeid;
	
	private EditText							edit_Note;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questiondetail);
		
		initActivity(R.id.rl_questiondetail);
		
		initControls();
		setQuestionDetailAdapter();
		readContents();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_questiondetail_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		edit_Note = (EditText)findViewById(R.id.edit_questiondetail_note);
		Button btn_submit = (Button)findViewById(R.id.btn_questiondetail_submit);
		btn_submit.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSubmit();
        	}
        });
		
		lv_QuestionDetail = (ListView)findViewById(R.id.list_questiondetail_content);
		
		m_nCurQuestionTypeid = getIntent().getLongExtra(EXTRADATA_QUESTIONTYPEID, 0);
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
		Intent questionlist_activity = new Intent(this, QuestionlistActivity.class);
		startActivity(questionlist_activity);	
		finish();
	}
	
	private void readContents()
	{
		m_reqType = REQ_TYPE.REQ_GETQADETAIL;
		new LoadResponseThread(QuestionDetailActivity.this).start();
	}
	
	private void onClickSubmit()
	{
		if ( edit_Note.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.activity_questiondetail_erroremptynote));
			return;
		}
		
		String strText = edit_Note.getText().toString();
		strText = strText.trim();
		
		if ( strText.length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.activity_questiondetail_erroremptynote));
			return;
		}
		
		m_reqType = REQ_TYPE.REQ_INSERTQUESTION;
		new LoadResponseThread(QuestionDetailActivity.this).start();
	}
	
	private void setQuestionDetailAdapter() {
		m_QuestionDetailList = new ArrayList<STQuestionDetailInfo>();

		lv_QuestionDetail.setCacheColorHint(Color.TRANSPARENT);
		lv_QuestionDetail.setDividerHeight(0);
		lv_QuestionDetail.setDrawSelectorOnTop(false);

        m_QuestionDetailAdapter = new QuestionDetailAdapter(QuestionDetailActivity.this, m_QuestionDetailList);
        lv_QuestionDetail.setAdapter(m_QuestionDetailAdapter);
        
	}
	
	public void refreshUI() {
		super.refreshUI();
		if ( m_reqType == REQ_TYPE.REQ_GETQADETAIL )
		{
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				m_QuestionDetailAdapter.notifyDataSetChanged();
			}
		}
		else
		{
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				edit_Note.setText("");
				readContents();
			}
		}
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETQADETAIL)
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETQADETAIL;
				strRequest += "?uid=" + String.valueOf(Global.Cur_UserId);
				strRequest += "&qid=" + String.valueOf(m_nCurQuestionTypeid);
				
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
					
					int count = dataObject.getInt("count");
					int a_count = 0;

		            JSONArray dataList = dataObject.getJSONArray("data");
		            
		            m_QuestionDetailList.clear();
		            
		            for (int i = 0; i < count; i++) {
		            	JSONObject tmpObj = (JSONObject) dataList.get(i);
		            	STQuestionDetailInfo itemInfo = new STQuestionDetailInfo();
			            itemInfo.type = 1; //question
			            itemInfo.regtime = tmpObj.getString("q_regtime");
			            itemInfo.content = tmpObj.getString("q_content");
						m_QuestionDetailList.add(itemInfo);
						
						a_count = tmpObj.getInt("a_count");
						JSONArray answerList = tmpObj.getJSONArray("a_data");
						for ( int j = 0; j < a_count; j++ )
						{
							JSONObject tmpObj1 = (JSONObject) answerList.get(j);
							STQuestionDetailInfo itemInfo1 = new STQuestionDetailInfo();
				            itemInfo1.type = 0; //answer
				            itemInfo1.regtime = tmpObj1.getString("regtime");
				            itemInfo1.content = tmpObj1.getString("content");
							m_QuestionDetailList.add(itemInfo1);
						}
		            }
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_INSERTQUESTION )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_INSERTQUESTION;
				
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
		
		requestObj.put("uid", String.valueOf(Global.Cur_UserId));
		requestObj.put("content", edit_Note.getText().toString());
		requestObj.put("q_tid", String.valueOf(m_nCurQuestionTypeid));

		return requestObj;
	}
}
