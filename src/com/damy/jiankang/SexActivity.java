/* 健康*/
package com.damy.jiankang;

import org.json.JSONException;
import org.json.JSONObject;

import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;

import android.os.Bundle;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SexActivity extends BaseActivity {

	private int				m_curSex = 1;
	
	private ImageView 		img_man;
	private ImageView 		img_woman;
	private TextView 		txt_man_u;
	private TextView 		txt_man_d;
	private TextView 		txt_woman_u;
	private TextView 		txt_woman_d;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sex);
		
		initActivity(R.id.rl_sex);
		
		initControls();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_sex_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		
		Button btn_next = (Button)findViewById(R.id.btn_sex_next);

		btn_next.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickNext();
        	}
        });
		
		Button btn_sex_save = (Button)findViewById(R.id.btn_sex_save);
		
		btn_sex_save.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSave();
        	}
        });
		
		img_man = (ImageView)findViewById(R.id.img_sex_man);
		img_woman = (ImageView)findViewById(R.id.img_sex_woman);
		txt_man_u = (TextView)findViewById(R.id.lbl_sex_man_u);
		txt_man_d = (TextView)findViewById(R.id.lbl_sex_man_d);
		txt_woman_u = (TextView)findViewById(R.id.lbl_sex_woman_u);
		txt_woman_d = (TextView)findViewById(R.id.lbl_sex_woman_d);
		
		txt_man_d.setVisibility(View.INVISIBLE);
		txt_woman_u.setVisibility(View.INVISIBLE);

		RelativeLayout rl_man_btn = (RelativeLayout)findViewById(R.id.rl_sex_man);
		RelativeLayout rl_woman_btn = (RelativeLayout)findViewById(R.id.rl_sex_woman);
		
		rl_man_btn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickMan();
        	}
        });
		
		rl_woman_btn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickWoman();
        	}
        });
		
		if (Global.registering_flag)
		{
			btn_next.setVisibility(android.view.View.VISIBLE);
			btn_sex_save.setVisibility(android.view.View.INVISIBLE);
			btn_back.setVisibility(android.view.View.INVISIBLE);
		}
		else
		{
			btn_next.setVisibility(android.view.View.INVISIBLE);
			btn_sex_save.setVisibility(android.view.View.VISIBLE);
			btn_back.setVisibility(android.view.View.VISIBLE);
		}
		
		if (Global.Cur_UserSex == 0)
			onClickMan();
		else
			onClickWoman();
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
		if (Global.registering_flag)
		{
			finish();
		}
		else
		{
			Intent myinfo_activity = new Intent(this, MyInfoActivity.class);
			startActivity(myinfo_activity);	
			finish();		
		}
	}

	private void onClickNext()
	{
		Global.Cur_UserSex = m_curSex;
		
		Intent birthday_activity = new Intent(this, BirthdayActivity.class);
		startActivity(birthday_activity);	
		finish();
	}
	
	private void onClickSave()
	{
		new LoadResponseThread(SexActivity.this).start();
	}
	
	private void onSuccessSex()
	{
		Intent myinfo_activity = new Intent(this, MyInfoActivity.class);
		startActivity(myinfo_activity);	
		finish();
	}
	
	private void onClickMan()
	{
		img_man.setImageDrawable(getResources().getDrawable(R.drawable.activity_sex_man_d));
		img_woman.setImageDrawable(getResources().getDrawable(R.drawable.activity_sex_woman_u));

		txt_man_u.setVisibility(View.INVISIBLE);
		txt_man_d.setVisibility(View.VISIBLE);
		txt_woman_u.setVisibility(View.VISIBLE);
		txt_woman_d.setVisibility(View.INVISIBLE);
		
		m_curSex = 0;
	}
	
	private void onClickWoman()
	{
		img_man.setImageDrawable(getResources().getDrawable(R.drawable.activity_sex_man_u));
		img_woman.setImageDrawable(getResources().getDrawable(R.drawable.activity_sex_woman_d));
		
		txt_man_u.setVisibility(View.VISIBLE);
		txt_man_d.setVisibility(View.INVISIBLE);
		txt_woman_u.setVisibility(View.INVISIBLE);
		txt_woman_d.setVisibility(View.VISIBLE);
		
		m_curSex = 1;
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			Global.Cur_UserSex = m_curSex;
			onSuccessSex();
		 }
	}
	
	public void getResponseJSON() {
		try {
			m_nResponse = ResponseRet.RET_SUCCESS;
			
			String strRequest = HttpConnUsingJSON.REQ_EDITUSERINFO;

			JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);
			
			if (response == null) {
				m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
				return;
			}
			
			m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
            if (m_nResponse == ResponseRet.RET_SUCCESS) {            	
            	
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
	
		
	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();		
    	
		requestObj.put("uid", Global.Cur_UserId);
		requestObj.put("type", "sex");
		requestObj.put("data", Global.Cur_UserSex);
		
		return requestObj;
	}
	
}
