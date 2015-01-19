/* 健康*/
package com.damy.jiankang;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.damy.Utils.PullToRefreshBase;
import com.damy.Utils.PullToRefreshListView;
import com.damy.Utils.ResolutionSet;
import com.damy.Utils.WheelPicker.NumericWheelAdapter;
import com.damy.Utils.WheelPicker.OnWheelChangedListener;
import com.damy.Utils.WheelPicker.WheelView;
import com.damy.adapters.WeightListAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STWeighListInfo;;


public class WeightInfoListActivity extends BaseActivity{

    private ImageButton 				m_imgbtnBack;
	private PullToRefreshListView		m_lvBaseWeightinfoListView;
	private TextView  					edit_startdate;
	private TextView  					edit_enddate;
	
	private Date						date_start = new Date();
	private Date						date_end = new Date();
	
	private ArrayList<STWeighListInfo> 	m_BaseWeightinfoList;
	private WeightListAdapter			m_BaseWeightinfoAdapter = null;
	private ListView					mRealListView;

	private int							m_nCurPageNumber = 1;
	
	private WheelView 					years;
    private WheelView 					months;
    private WheelView 					days;
    
    private RelativeLayout				rl_maskLayer;
    private	RelativeLayout				rl_datedialog;
    
    private final int 					START_YEAR = 1950;
    private final int 					END_YEAR = 2050;
    
    private Date						curDate = new Date();
    
    private int							m_CurSelDate = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weightinfolist);

        initActivity(R.id.rl_weightinfolist);
        initControl();
        setBaseWeightinfoAdapter();
        
        new LoadResponseThread(WeightInfoListActivity.this).start();
	}

    public void initControl() {
        m_imgbtnBack = (ImageButton) findViewById(R.id.img_weightinfolist_back);        
        m_lvBaseWeightinfoListView = (PullToRefreshListView)findViewById(R.id.anWeightinfoListContentView);
        edit_startdate = (TextView)findViewById(R.id.edit_weightinfolist_startdate);
        edit_enddate = (TextView)findViewById(R.id.edit_weightinfolist_enddate);
        
        m_imgbtnBack.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	onClickBack();        		                
            }
        });
        
        date_start.setYear(curDate.getYear());
        date_start.setMonth(curDate.getMonth());
        date_start.setDate(1);
        
        date_end.setYear(curDate.getYear());
        date_end.setMonth(curDate.getMonth());
        date_end.setDate(curDate.getDate());
        
        edit_startdate.setText(String.format("%d-%d-%d", date_start.getYear() + 1900, date_start.getMonth() + 1, date_start.getDate()));
        edit_enddate.setText(String.format("%d-%d-%d", date_end.getYear() + 1900, date_end.getMonth() + 1, date_end.getDate()));
        
        int fntSize = (int)(getResources().getDimension(R.dimen.wheelview_fnt_size) * ResolutionSet.fYpro + 0.50001);
		
		years = (WheelView) findViewById(R.id.wheel_weightinfolist_year);
		years.setDefTextSize(fntSize);
        years.setAdapter(new NumericWheelAdapter(START_YEAR, curDate.getYear() + 1900));
        years.setLabel(" " + getResources().getString(R.string.common_year));
        
        months = (WheelView) findViewById(R.id.wheel_weightinfolist_month);
        months.setDefTextSize(fntSize);
        months.setAdapter(new NumericWheelAdapter(1, 12));
        months.setLabel(" " + getResources().getString(R.string.common_month));

        days = (WheelView) findViewById(R.id.wheel_weightinfolist_day);
        days.setDefTextSize(fntSize);
        days.setAdapter(new NumericWheelAdapter(1, 31));
        days.setLabel(" " + getResources().getString(R.string.common_day));

        years.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            	Date curDate = new Date();
                int maxMonth = 12;
                if ( (curDate.getYear() + 1900) - (newValue + START_YEAR) == 0 )
                	maxMonth = curDate.getMonth() + 1;
                months.setAdapter(new NumericWheelAdapter(1, maxMonth));
                
                if ( months.getCurrentItem() + 1 > maxMonth )
                	months.setCurrentItem(maxMonth - 1); 
            }
        });
        
        months.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

                int maxDay = 30;
                Date curDate = new Date();
                
                if ( (curDate.getYear() + 1900) - (years.getCurrentItem() + START_YEAR) == 0 && (curDate.getMonth() == months.getCurrentItem()) )
                	maxDay = curDate.getDate();
                else {
	                switch (newValue)
	                {
	                    case 1:
	                    {
	                        int curYear = years.getCurrentItem() + START_YEAR;
	                        if (curYear % 4 == 0)
	                        {
	                            maxDay = 29;
	                        }
	                        else
	                        {
	                            maxDay = 28;
	                        }
	                        break;
	                    }
	                    case 0:
	                    case 2:
	                    case 4:
	                    case 6:
	                    case 7:
	                    case 9:
	                    case 11:
	                        maxDay = 31;
	                        break;
	                    default:
	                        maxDay = 30;
	                        break;
	                }
                }
                days.setAdapter(new NumericWheelAdapter(1, maxDay));
                
                if ( days.getCurrentItem() + 1 > maxDay )
                	days.setCurrentItem(maxDay - 1); 
            }
        });
        
        rl_maskLayer = (RelativeLayout)findViewById(R.id.rl_weightinfolist_mask);
        rl_datedialog = (RelativeLayout)findViewById(R.id.rl_weightinfolist_pickerdialog);
        
        rl_maskLayer.setVisibility(View.INVISIBLE);
        rl_datedialog.setVisibility(View.INVISIBLE);
        
        rl_maskLayer.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            }
        });
        
        edit_startdate.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	m_CurSelDate = 0;
            	onClickDate();
            }
        });
        
        edit_enddate.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	m_CurSelDate = 1;
            	onClickDate();
            }
        });
        
        Button btn_dialog_ok = (Button)findViewById(R.id.btn_weightinfolist_pickerdialog_ok);
        Button btn_dialog_cancel = (Button)findViewById(R.id.btn_weightinfolist_pickerdialog_cancel);
        
        btn_dialog_ok.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	onClickDialogOK();
            }
        });
        
        btn_dialog_cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	onClickDialogCancel();
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
    	Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}
    
	private void setBaseWeightinfoAdapter() {
		m_BaseWeightinfoList = new ArrayList<STWeighListInfo>();
		m_lvBaseWeightinfoListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        // Set a listener to be invoked when the list should be refreshed.
		m_lvBaseWeightinfoListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                m_nCurPageNumber = m_nCurPageNumber + 1;              
                new LoadResponseThread(WeightInfoListActivity.this).start();
            }
        });

        mRealListView = m_lvBaseWeightinfoListView.getRefreshableView();
        registerForContextMenu(mRealListView);

        //mRealListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        mRealListView.setCacheColorHint(Color.TRANSPARENT);
        mRealListView.setDividerHeight(0);
        mRealListView.setDrawSelectorOnTop(false);

        m_BaseWeightinfoAdapter = new WeightListAdapter(WeightInfoListActivity.this, m_BaseWeightinfoList);
        mRealListView.setAdapter(m_BaseWeightinfoAdapter);
	}	

	private void onClickDate()
	{
		String strTmp = "";
		if ( m_CurSelDate == 0 )
			strTmp = edit_startdate.getText().toString();
		else
			strTmp = edit_enddate.getText().toString();
		
		String[] arr = strTmp.split("-");
		
        // set current date
        try
        {
            years.setCurrentItem(Integer.valueOf(arr[0]) - START_YEAR);
            months.setCurrentItem(Integer.valueOf(arr[1]) - 1);
            days.setCurrentItem(Integer.valueOf(arr[2]) - 1);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        rl_maskLayer.setVisibility(View.VISIBLE);
        rl_datedialog.setVisibility(View.VISIBLE);
	}
	
	private void onClickDialogOK()
	{
		String strTmp = "";
		strTmp = String.format("%d-%d-%d", years.getCurrentItem() + START_YEAR, months.getCurrentItem() + 1, days.getCurrentItem() + 1);
		
		if ( m_CurSelDate == 0 )
		{
			String[] arr = strTmp.split("-");
			String[] arr1 = edit_startdate.getText().toString().split("-");
			
			int y1, y2, m1, m2, d1, d2;
			
			y1 = Integer.valueOf(arr[0]);
			y2 = Integer.valueOf(arr1[0]);
			m1 = Integer.valueOf(arr[1]);
			m2 = Integer.valueOf(arr1[1]);
			d1 = Integer.valueOf(arr[2]);
			d2 = Integer.valueOf(arr1[2]);
			
			if ( ( y1 > y2 ) || (y1 == y2 && m1 > m2) || (y1 == y2 && m1 == m2 && d1 > d2) )
				edit_startdate.setText(edit_enddate.getText().toString());
			else
				edit_startdate.setText(strTmp);
		}
		else
		{
			String[] arr = strTmp.split("-");
			String[] arr1 = edit_startdate.getText().toString().split("-");
			
			int y1, y2, m1, m2, d1, d2;
			
			y1 = Integer.valueOf(arr[0]);
			y2 = Integer.valueOf(arr1[0]);
			m1 = Integer.valueOf(arr[1]);
			m2 = Integer.valueOf(arr1[1]);
			d1 = Integer.valueOf(arr[2]);
			d2 = Integer.valueOf(arr1[2]);
			
			if ( ( y1 < y2 ) || (y1 == y2 && m1 < m2) || (y1 == y2 && m1 == m2 && d1 < d2) )
				edit_enddate.setText(edit_startdate.getText().toString());
			else
				edit_enddate.setText(strTmp);
		}
		
		onClickDialogCancel();
		
		new LoadResponseThread(WeightInfoListActivity.this).start();
	}
	
	private void onClickDialogCancel()
	{
		rl_maskLayer.setVisibility(View.INVISIBLE);
		rl_datedialog.setVisibility(View.INVISIBLE);
	}
    
    public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			m_BaseWeightinfoAdapter.notifyDataSetChanged();
			m_lvBaseWeightinfoListView.onRefreshComplete();
		}
	}
	
	public void getResponseJSON() {
		try {				
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETWEIGHTLIST;
				strRequest += "?uid=" + Global.Cur_UserId;
				strRequest += "&page=" + m_nCurPageNumber;
				strRequest += "&start_date=" + edit_startdate.getText().toString();
				strRequest += "&end_date=" + edit_enddate.getText().toString();
				
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
		            
		            m_BaseWeightinfoList.clear();
		            
		            for (int i = 0; i < count; i++) {
		            	JSONObject tmpObj = (JSONObject) dataList.get(i);
			            STWeighListInfo itemInfo = new STWeighListInfo();
	
						itemInfo.date = tmpObj.getString("date");
						itemInfo.weight = tmpObj.getString("weight");
						itemInfo.waist = tmpObj.getString("waist");
						itemInfo.calory = tmpObj.getString("calory");						
						
						m_BaseWeightinfoList.add(itemInfo);
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
