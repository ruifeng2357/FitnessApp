/* �亙熒*/
package com.damy.jiankang;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.ResolutionSet;
import com.damy.Utils.ResolutionSet1;
import com.damy.adapters.FoodRecordAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STFoodMainInfo;
import com.damy.datatypes.STFoodRecordInfo;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

public class FoodRecordActivity extends BaseActivity {

	private enum REQ_TYPE {
		REQ_GETFOODRECORD, REQ_GETMEALRECORD, REQ_DELETEFOODRECORDINFO, REQ_SETCALORY 
	};

	private int PARENT_WIDTH = 720;
	private int SEEKBAR_WIDTH = 565;
	private int NOTE_WIDTH = 180;
	private int MAX_CALORY = 4500;
	private int LISTITEM_HEIGHT = 105;

	private ListView lv_FoodRecord1;
	private ListView lv_FoodRecord2;
	private ListView lv_FoodRecord3;
	private ListView lv_FoodRecord4;
	private FoodRecordAdapter m_FoodRecordAdapter1 = null;
	private FoodRecordAdapter m_FoodRecordAdapter2 = null;
	private FoodRecordAdapter m_FoodRecordAdapter3 = null;
	private FoodRecordAdapter m_FoodRecordAdapter4 = null;
	private ArrayList<STFoodRecordInfo> m_FoodRecordList;
	private int m_nCurClickedType = 0;

	private String m_strDate = "";
	private Date m_curDate;
	private int m_nCalory = 0;
	private int m_nSportCalory = 0;
	private int m_nFoodCalory = 0;
	private int m_nTotalCalory = 0;
	private int m_nBreakfastCalory = 0;
	private int m_nLunchCalory = 0;
	private int m_nOtherCalory = 0;
	private int m_nDinnerCalory = 0;

	private TextView txt_takecalory;
	private TextView txt_targetcalory;
	private TextView txt_foodcalory;
	private TextView txt_sportcalory;
	private TextView txt_totalcalory;
	private TextView txt_breakfastcalory;
	private TextView txt_lunchcalory;
	private TextView txt_dinnercalory;
	private TextView txt_othercalory;

	private RelativeLayout rl_takeCalory;
	private RelativeLayout rl_targetCalory;
	
	private SeekBar			seekbar_Calory;

	private RelativeLayout m_setLayer;
	private RelativeLayout m_deleteLayer;
	private RelativeLayout m_maskLayer;
	
	private EditText m_setDialogValue;

	private int m_curClickedItem = 0;

	private REQ_TYPE m_reqType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_foodrecord);

		initActivity(R.id.rl_foodrecord);

		m_curDate = new Date();
		
		if ( m_curDate.getHours() < 3 )
			m_curDate.setDate(m_curDate.getDate() - 1);

		initControls();
		setFoodRecordAdapter();
		onClickDateChange(0);
	}

	void initControls() {
		ImageButton btn_back = (ImageButton) findViewById(R.id.imgbtn_foodrecord_back);
		ImageView img_datedown = (ImageView) findViewById(R.id.img_foodrecord_datedown);
		ImageView img_dateup = (ImageView) findViewById(R.id.img_foodrecord_dateup);
		ImageView img_breakfastplus = (ImageView) findViewById(R.id.img_foodrecord_breakfastplus);
		ImageView img_lunchplus = (ImageView) findViewById(R.id.img_foodrecord_lunchplus);
		ImageView img_otherplus = (ImageView) findViewById(R.id.img_foodrecord_otherplus);
		ImageView img_dinnerplus = (ImageView) findViewById(R.id.img_foodrecord_dinnerplus);
		ImageButton btn_facebook = (ImageButton) findViewById(R.id.imgbtn_foodrecord_facebook);

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

		img_breakfastplus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickPlus(0);
			}
		});

		img_lunchplus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickPlus(1);
			}
		});

		img_dinnerplus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickPlus(2);
			}
		});

		img_otherplus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickPlus(3);
			}
		});
		
		btn_facebook.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickFacebook();
			}
		});
		
		seekbar_Calory = (SeekBar)findViewById(R.id.seekbar_foodrecord_calory);
		seekbar_Calory.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				m_reqType = REQ_TYPE.REQ_SETCALORY;
				new LoadResponseThread(FoodRecordActivity.this).start();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if ( fromUser == true )
				{
					MoveTargetCalory(progress);
					txt_targetcalory.setText(String.format("%s : %dkcal", getResources().getString(R.string.activity_foodrecord_targetcalory), m_nCalory));
				}
			}
		});
		
		txt_takecalory = (TextView) findViewById(R.id.lbl_foodrecord_takecalory);
		txt_targetcalory = (TextView) findViewById(R.id.lbl_foodrecord_targetcalory);
		txt_foodcalory = (TextView) findViewById(R.id.lbl_foodrecord_foodcalory);
		txt_sportcalory = (TextView) findViewById(R.id.lbl_foodrecord_sportcalory);
		txt_totalcalory = (TextView) findViewById(R.id.lbl_foodrecord_totalcalory);
		txt_breakfastcalory = (TextView) findViewById(R.id.lbl_foodrecord_breakfastvalue);
		txt_lunchcalory = (TextView) findViewById(R.id.lbl_foodrecord_lunchvalue);
		txt_dinnercalory = (TextView) findViewById(R.id.lbl_foodrecord_dinnervalue);
		txt_othercalory = (TextView) findViewById(R.id.lbl_foodrecord_othervalue);

		rl_takeCalory = (RelativeLayout) findViewById(R.id.rl_foodrecord_takecalory);
		rl_targetCalory = (RelativeLayout) findViewById(R.id.rl_foodrecord_targetcalory);
		
		txt_targetcalory.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showSetDialog();
			}
		});

		lv_FoodRecord1 = (ListView) findViewById(R.id.list_foodrecord_content1);
		lv_FoodRecord2 = (ListView) findViewById(R.id.list_foodrecord_content2);
		lv_FoodRecord3 = (ListView) findViewById(R.id.list_foodrecord_content4);
		lv_FoodRecord4 = (ListView) findViewById(R.id.list_foodrecord_content3);
		
		lv_FoodRecord1.setVisibility(View.GONE);
		lv_FoodRecord2.setVisibility(View.GONE);
		lv_FoodRecord3.setVisibility(View.GONE);
		lv_FoodRecord4.setVisibility(View.GONE);

		lv_FoodRecord1.setCacheColorHint(Color.TRANSPARENT);
		lv_FoodRecord1.setDividerHeight(0);
		lv_FoodRecord1.setDrawSelectorOnTop(false);
		lv_FoodRecord1
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						onClickItem(position, 0);
					}
				});
		lv_FoodRecord1
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						onLongClickItem(parent, position, 0);
						return true;
					}
				});

		lv_FoodRecord2.setCacheColorHint(Color.TRANSPARENT);
		lv_FoodRecord2.setDividerHeight(0);
		lv_FoodRecord2.setDrawSelectorOnTop(false);
		lv_FoodRecord2
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						onClickItem(position, 1);
					}
				});
		lv_FoodRecord2
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						onLongClickItem(parent, position, 1);
						return true;
					}
				});

		lv_FoodRecord3.setCacheColorHint(Color.TRANSPARENT);
		lv_FoodRecord3.setDividerHeight(0);
		lv_FoodRecord3.setDrawSelectorOnTop(false);
		lv_FoodRecord3
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						onClickItem(position, 2);
					}
				});
		lv_FoodRecord3
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						onLongClickItem(parent, position, 2);
						return true;
					}
				});

		lv_FoodRecord4.setCacheColorHint(Color.TRANSPARENT);
		lv_FoodRecord4.setDividerHeight(0);
		lv_FoodRecord4.setDrawSelectorOnTop(false);
		lv_FoodRecord4
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						onClickItem(position, 3);
					}
				});
		lv_FoodRecord4
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						onLongClickItem(parent, position, 3);
						return true;
					}
				});
		
		RelativeLayout rl_breakfast = (RelativeLayout)findViewById(R.id.rl_foodrecord_breakfast);
		RelativeLayout rl_lunch = (RelativeLayout)findViewById(R.id.rl_foodrecord_lunch);
		RelativeLayout rl_other = (RelativeLayout)findViewById(R.id.rl_foodrecord_other);
		RelativeLayout rl_dinner = (RelativeLayout)findViewById(R.id.rl_foodrecord_dinner);
		
		rl_breakfast.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickMeal(0);
			}
		});
		
		rl_lunch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickMeal(1);
			}
		});
		
		rl_dinner.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickMeal(2);
			}
		});
		
		rl_other.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickMeal(3);
			}
		});
		

		m_maskLayer = (RelativeLayout) findViewById(R.id.rl_foodrecord_mask);
		m_maskLayer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

			}
		});
		m_maskLayer.setVisibility(View.INVISIBLE);
		
		m_setLayer = (RelativeLayout)findViewById(R.id.rl_foodrecord_dialog_set);
		m_setLayer.setVisibility(View.INVISIBLE);
		
		Button fl_set_ok = (Button)findViewById(R.id.btn_foodrecord_dialog_set_ok);
		Button fl_set_cancel = (Button)findViewById(R.id.btn_foodrecord_dialog_set_cancel);
		
		fl_set_ok.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSetOk();
        	}
        });
		fl_set_cancel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSetCancel();
        	}
        });
		
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
		
		m_setDialogValue = (EditText)findViewById(R.id.edit_foodrecord_dialog_set_value);
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
	
	private void onClickFacebook()
	{
		Intent facebook_activity = new Intent(this, FacebookActivity.class);
		facebook_activity.putExtra(FacebookActivity.EXTRADATA_DATE, m_strDate);
		facebook_activity.putExtra(FacebookActivity.EXTRADATA_TYPE, 1);
		startActivity(facebook_activity);	
	}

	private void onClickPlus(int type) {
		Global.FoodRecord_CurType = type;
		
		Intent foodtype_activity = new Intent(this, FoodMainListActivity.class);
		startActivity(foodtype_activity);
		finish();
	}
	
	private void showSetDialog()
	{
		m_maskLayer.setVisibility(View.VISIBLE);
		m_setLayer.setVisibility(View.VISIBLE);
		
		if ( m_nCalory > 0 )
			m_setDialogValue.setText(String.valueOf(m_nCalory));
		else
			m_setDialogValue.setText("");
	}
	
	private void onClickSetOk()
	{
		if ( m_setDialogValue.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.activity_foodrecord_errorsetvalue));
			return;
		}
		
		onClickSetCancel();
		try {
			m_nCalory = Integer.valueOf(m_setDialogValue.getText().toString());
		}
		catch (Exception e) {
			
		}
		MoveTargetCalory(m_nCalory);
		txt_targetcalory.setText(String.format("%s : %dkcal", getResources().getString(R.string.activity_foodrecord_targetcalory), m_nCalory));
		seekbar_Calory.setProgress(m_nCalory);
		
		m_reqType = REQ_TYPE.REQ_SETCALORY;
		new LoadResponseThread(FoodRecordActivity.this).start();
	}
	
	private void onClickSetCancel()
	{
		m_maskLayer.setVisibility(View.INVISIBLE);
		m_setLayer.setVisibility(View.INVISIBLE);
	}
	
	private void onClickMeal(int type)
	{
		m_nCurClickedType = type;
		m_reqType = REQ_TYPE.REQ_GETMEALRECORD;
		new LoadResponseThread(FoodRecordActivity.this).start();
	}

	private void MoveTakeCalory(int cal) {
		float rate = (float)SEEKBAR_WIDTH / (float)MAX_CALORY;

		if ( cal > 4500 )
			cal = 4500;
		
		RelativeLayout.LayoutParams layoutParams;
		
		layoutParams = (RelativeLayout.LayoutParams) rl_takeCalory
				.getLayoutParams();
		float pos = (((float) (PARENT_WIDTH - SEEKBAR_WIDTH) / 2) + (float) cal
				* (float) rate - ((float) NOTE_WIDTH / 2)) * ResolutionSet.fXpro;
		layoutParams.setMargins((int) pos, layoutParams.topMargin,
				layoutParams.rightMargin, layoutParams.bottomMargin);
		rl_takeCalory.setLayoutParams(layoutParams);
	}

	private void MoveTargetCalory(int cal) {
		float rate = (float)SEEKBAR_WIDTH / (float)MAX_CALORY;
		
		m_nCalory = cal;

		if ( cal > 4500 )
			cal = 4500;
		
		RelativeLayout.LayoutParams layoutParams;

		layoutParams = (RelativeLayout.LayoutParams) rl_targetCalory
				.getLayoutParams();
		float pos = (((float) (PARENT_WIDTH - SEEKBAR_WIDTH) / 2) + (float) cal
				* (float) rate - ((float) NOTE_WIDTH / 2)) * ResolutionSet.fXpro;
		layoutParams.setMargins((int) pos, layoutParams.topMargin,
				layoutParams.rightMargin, layoutParams.bottomMargin);
		rl_targetCalory.setLayoutParams(layoutParams);
		
		m_nTotalCalory = m_nCalory - (m_nFoodCalory - m_nSportCalory);
		txt_totalcalory.setText(String.format("%d", m_nTotalCalory));
	}

	private void onClickDateChange(int updown) {
		Global.FoodRecord_CurDate.setDate(Global.FoodRecord_CurDate.getDate()
				+ updown);

		TextView txt_date = (TextView) findViewById(R.id.lbl_foodrecord_date);

		if (Global.FoodRecord_CurDate.getYear() == m_curDate.getYear()
				&& Global.FoodRecord_CurDate.getMonth() == m_curDate.getMonth()
				&& Global.FoodRecord_CurDate.getDate() == m_curDate.getDate())
			txt_date.setText(getResources().getString(R.string.common_today));
		else
			txt_date.setText(String.format("%d-%d-%d",
					1900 + Global.FoodRecord_CurDate.getYear(),
					Global.FoodRecord_CurDate.getMonth() + 1,
					Global.FoodRecord_CurDate.getDate()));

		m_strDate = String.format("%d-%d-%d",
				1900 + Global.FoodRecord_CurDate.getYear(),
				Global.FoodRecord_CurDate.getMonth() + 1,
				Global.FoodRecord_CurDate.getDate());

		readContents();
	}

	private void readContents() {
		m_reqType = REQ_TYPE.REQ_GETFOODRECORD;
		new LoadResponseThread(FoodRecordActivity.this).start();
	}

	private void setFoodRecordAdapter() {
		m_FoodRecordList = new ArrayList<STFoodRecordInfo>();
		
		m_FoodRecordAdapter1 = new FoodRecordAdapter(FoodRecordActivity.this, m_FoodRecordList);
		lv_FoodRecord1.setAdapter(m_FoodRecordAdapter1);
		m_FoodRecordAdapter2 = new FoodRecordAdapter(FoodRecordActivity.this, m_FoodRecordList);
		lv_FoodRecord2.setAdapter(m_FoodRecordAdapter2);
		m_FoodRecordAdapter3 = new FoodRecordAdapter(FoodRecordActivity.this, m_FoodRecordList);
		lv_FoodRecord3.setAdapter(m_FoodRecordAdapter3);
		m_FoodRecordAdapter4 = new FoodRecordAdapter(FoodRecordActivity.this, m_FoodRecordList);
		lv_FoodRecord4.setAdapter(m_FoodRecordAdapter4);
	}

	public void onClickItem(int pos, int type) {
		 STFoodRecordInfo item = m_FoodRecordList.get(pos);
		 Intent foodrecordinfo_activity = new Intent(this, FoodRecordInfoActivity.class);
		 foodrecordinfo_activity.putExtra(FoodRecordInfoActivity.EXTRADATA_RECORDID, item.record_id);
		 foodrecordinfo_activity.putExtra(FoodRecordInfoActivity.EXTRADATA_FOODID, item.foodsub_id);
		 foodrecordinfo_activity.putExtra(FoodRecordInfoActivity.EXTRADATA_IMAGE, item.image);
		 foodrecordinfo_activity.putExtra(FoodRecordInfoActivity.EXTRADATA_CURMASS, item.mass);
		 foodrecordinfo_activity.putExtra(FoodRecordInfoActivity.EXTRADATA_CURUNIT, item.unit);
		 startActivity(foodrecordinfo_activity);
		 finish();
	}

	public void onLongClickItem(View parent, int pos, int type) {
		
		m_maskLayer.setVisibility(View.VISIBLE);
		m_deleteLayer.setVisibility(View.VISIBLE);
		
		m_curClickedItem = pos;
		
		Button fl_delconfirm_ok = (Button)findViewById(R.id.btn_dialog_delconfirm_ok);
		Button fl_delconfirm_cancel = (Button)findViewById(R.id.btn_dialog_delconfirm_cancel);
		
		fl_delconfirm_ok.setOnClickListener(new OnClickListener(){
			public void onClick(View v) { 
				onClickDelConfirmOk(); 
			}
		});
		
		fl_delconfirm_cancel.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				onClickDelConfirmCancel();
			}
		});
	}

	private void onClickDelConfirmOk() {
		onClickDelConfirmCancel();

		m_reqType = REQ_TYPE.REQ_DELETEFOODRECORDINFO;
		new LoadResponseThread(FoodRecordActivity.this).start();
	}

	private void onClickDelConfirmCancel() {
		m_maskLayer.setVisibility(View.INVISIBLE);
		m_deleteLayer.setVisibility(View.INVISIBLE);
	}

	public void refreshUI() {
		super.refreshUI();
		if ( m_reqType == REQ_TYPE.REQ_GETFOODRECORD )
		{
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				txt_takecalory.setText(String.format("%s : %dkcal", getResources().getString(R.string.activity_foodrecord_takecalrory), m_nFoodCalory));
				txt_targetcalory.setText(String.format("%s : %dkcal", getResources().getString(R.string.activity_foodrecord_targetcalory), m_nCalory));
				txt_foodcalory.setText(String.format("%d", m_nFoodCalory));
				txt_sportcalory.setText(String.format("-%d", m_nSportCalory));
				txt_breakfastcalory.setText(String.format("%d", m_nBreakfastCalory));
				txt_lunchcalory.setText(String.format("%d", m_nLunchCalory));
				txt_othercalory.setText(String.format("%d", m_nOtherCalory));
				txt_dinnercalory.setText(String.format("%d", m_nDinnerCalory));
				
				MoveTakeCalory(m_nFoodCalory);
				MoveTargetCalory(m_nCalory);
				seekbar_Calory.setProgress(m_nCalory);
			}
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETMEALRECORD )
		{
			lv_FoodRecord1.setVisibility(View.GONE);
			lv_FoodRecord2.setVisibility(View.GONE);
			lv_FoodRecord3.setVisibility(View.GONE);
			lv_FoodRecord4.setVisibility(View.GONE);
			
			int count = m_FoodRecordList.size();
			float height = ((float)LISTITEM_HEIGHT * (float)ResolutionSet.fYpro + (float)5) * (float)count;
			
			if ( m_nCurClickedType == 0 )
			{
				m_FoodRecordAdapter1.notifyDataSetChanged();
				lv_FoodRecord1.setVisibility(View.VISIBLE);
				
				ViewGroup.LayoutParams layoutParams = lv_FoodRecord1.getLayoutParams();
				layoutParams.height = (int)height;
				lv_FoodRecord1.setLayoutParams(layoutParams);
			}
			else if ( m_nCurClickedType == 1 )
			{
				m_FoodRecordAdapter2.notifyDataSetChanged();
				lv_FoodRecord2.setVisibility(View.VISIBLE);
				
				ViewGroup.LayoutParams layoutParams = lv_FoodRecord2.getLayoutParams();
				layoutParams.height = (int)height;
				lv_FoodRecord2.setLayoutParams(layoutParams);
			}
			else if ( m_nCurClickedType == 2 )
			{
				m_FoodRecordAdapter3.notifyDataSetChanged();
				lv_FoodRecord3.setVisibility(View.VISIBLE);
				
				ViewGroup.LayoutParams layoutParams = lv_FoodRecord3.getLayoutParams();
				layoutParams.height = (int)height;
				lv_FoodRecord3.setLayoutParams(layoutParams);
			}
			else
			{
				m_FoodRecordAdapter4.notifyDataSetChanged();
				lv_FoodRecord4.setVisibility(View.VISIBLE);
				
				ViewGroup.LayoutParams layoutParams = lv_FoodRecord4.getLayoutParams();
				layoutParams.height = (int)height;
				lv_FoodRecord4.setLayoutParams(layoutParams);
			}
		}
		else if ( m_reqType == REQ_TYPE.REQ_SETCALORY )
		{
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				
			}
		}
		else if ( m_reqType == REQ_TYPE.REQ_DELETEFOODRECORDINFO )
		{
			onClickDateChange(0);
			
			m_FoodRecordList.remove(m_curClickedItem);
			
			int count = m_FoodRecordList.size();
			float height = ((float)LISTITEM_HEIGHT * (float)ResolutionSet.fYpro + (float)3.500001) * (float)count;
			
			if ( m_nCurClickedType == 0 )
			{
				m_FoodRecordAdapter1.notifyDataSetChanged();
				lv_FoodRecord1.setVisibility(View.VISIBLE);
				
				ViewGroup.LayoutParams layoutParams = lv_FoodRecord1.getLayoutParams();
				layoutParams.height = (int)height;
				lv_FoodRecord1.setLayoutParams(layoutParams);
			}
			else if ( m_nCurClickedType == 1 )
			{
				m_FoodRecordAdapter2.notifyDataSetChanged();
				lv_FoodRecord2.setVisibility(View.VISIBLE);
				
				ViewGroup.LayoutParams layoutParams = lv_FoodRecord2.getLayoutParams();
				layoutParams.height = (int)height;
				lv_FoodRecord2.setLayoutParams(layoutParams);
			}
			else if ( m_nCurClickedType == 2 )
			{
				m_FoodRecordAdapter3.notifyDataSetChanged();
				lv_FoodRecord3.setVisibility(View.VISIBLE);
				
				ViewGroup.LayoutParams layoutParams = lv_FoodRecord3.getLayoutParams();
				layoutParams.height = (int)height;
				lv_FoodRecord3.setLayoutParams(layoutParams);
			}
			else
			{
				m_FoodRecordAdapter4.notifyDataSetChanged();
				lv_FoodRecord4.setVisibility(View.VISIBLE);
				
				ViewGroup.LayoutParams layoutParams = lv_FoodRecord4.getLayoutParams();
				layoutParams.height = (int)height;
				lv_FoodRecord4.setLayoutParams(layoutParams);
			}
		}
	}

	public void getResponseJSON() {
		try {
			if (m_reqType == REQ_TYPE.REQ_GETFOODRECORD) {
				m_nResponse = ResponseRet.RET_SUCCESS;

				String strRequest = HttpConnUsingJSON.REQ_GETFOODRECORD;
				strRequest += "?uid=" + String.valueOf(Global.Cur_UserId);
				strRequest += "&date=" + m_strDate;

				JSONObject response = m_HttpConnUsingJSON
						.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}

				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					JSONObject dataObject = response
							.getJSONObject(ResponseData.RESPONSE_DATA);

					m_nCalory = dataObject.getInt("calory");
					m_nSportCalory = dataObject.getInt("sport_calory");
					m_nFoodCalory = dataObject.getInt("food_calory");
					m_nBreakfastCalory = dataObject.getInt("breakfast_calory");
					m_nLunchCalory = dataObject.getInt("lunch_calory");
					m_nDinnerCalory = dataObject.getInt("super_calory");
					m_nOtherCalory = dataObject.getInt("dinner_calory");
				}
			} else if (m_reqType == REQ_TYPE.REQ_GETMEALRECORD) {
				m_nResponse = ResponseRet.RET_SUCCESS;

				String strRequest = HttpConnUsingJSON.REQ_GETMEALRECORD;
				strRequest += "?uid=" + String.valueOf(Global.Cur_UserId);
				strRequest += "&date=" + m_strDate;
				strRequest += "&type=" + String.valueOf(m_nCurClickedType);

				JSONObject response = m_HttpConnUsingJSON
						.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}

				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					JSONObject dataObject = response
							.getJSONObject(ResponseData.RESPONSE_DATA);

					int count = dataObject.getInt("count");
					JSONArray dataList = dataObject.getJSONArray("data");
		            
					m_FoodRecordList.clear();
					
		            for (int i = 0; i < count; i++) {
		            	JSONObject tmpObj = (JSONObject) dataList.get(i);
			            STFoodRecordInfo itemInfo = new STFoodRecordInfo();
	
						itemInfo.record_id = tmpObj.getLong("id");
						itemInfo.name = tmpObj.getString("name");
						itemInfo.mass = tmpObj.getInt("mass");
						itemInfo.unit = tmpObj.getInt("unit");
						itemInfo.calory = tmpObj.getInt("calory");
						itemInfo.foodsub_id = tmpObj.getLong("sub_id");
						itemInfo.image = tmpObj.getString("image");
						
						m_FoodRecordList.add(itemInfo);
		            }
				}
			} else if (m_reqType == REQ_TYPE.REQ_DELETEFOODRECORDINFO) {
				m_nResponse = ResponseRet.RET_SUCCESS;

				String strRequest = HttpConnUsingJSON.REQ_DELETEFOODRECORDINFO;

				JSONObject response = m_HttpConnUsingJSON
						.getPostJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}

				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
			} else if (m_reqType == REQ_TYPE.REQ_SETCALORY) {
				m_nResponse = ResponseRet.RET_SUCCESS;

				String strRequest = HttpConnUsingJSON.REQ_SETCALORY;

				JSONObject response = m_HttpConnUsingJSON
						.getPostJSONObject(strRequest);
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
		
		if ( m_reqType == REQ_TYPE.REQ_DELETEFOODRECORDINFO )
		{
			requestObj.put("id", String.valueOf(m_FoodRecordList.get(m_curClickedItem).record_id));
			requestObj.put("uid", String.valueOf(Global.Cur_UserId));
		}
		else if ( m_reqType == REQ_TYPE.REQ_SETCALORY )
		{
			requestObj.put("uid", String.valueOf(Global.Cur_UserId));
			requestObj.put("calory", m_nCalory);
			requestObj.put("date", m_strDate);
		}

		return requestObj;
	}
}
