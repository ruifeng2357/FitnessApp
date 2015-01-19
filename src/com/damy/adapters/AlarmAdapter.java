/* 健康*/
package com.damy.adapters;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.damy.datatypes.STAlarmInfo;
import com.damy.jiankang.AlarmActivity;
import com.damy.jiankang.R;
import com.damy.Utils.ResolutionSet1;


public class AlarmAdapter extends ArrayAdapter<STAlarmInfo> {

	AlarmActivity parentAct;
	ImageView img_switch;
	
	public AlarmAdapter(AlarmActivity activity, List<STAlarmInfo> detailInfos) {
		super(activity, 0, detailInfos);
		
		parentAct = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_alarm, null);
		ResolutionSet1._instance.iterateChild(rowView);
		
		STAlarmInfo anItem = getItem(position);
		
		TextView txt_time = (TextView) rowView.findViewById(R.id.lbl_item_alarm_time);
		TextView txt_repeat = (TextView) rowView.findViewById(R.id.lbl_item_alarm_repeat);
		TextView txt_remain = (TextView) rowView.findViewById(R.id.lbl_item_alarm_remain);
		img_switch = (ImageView) rowView.findViewById(R.id.img_item_alarm_switch);
		
		txt_time.setText(String.format("%02d:%02d", anItem.hour, anItem.minute));
		
		int hour = anItem.hour;
		int minute = anItem.minute;
		Date curDate = new Date();
		
		int total = (hour * 60 + minute) - (curDate.getHours() * 60 + curDate.getMinutes());
		int curWeekday = curDate.getDay();
		boolean isAlarmDay = false;
		
		if ( anItem.repeat_type == 0 )
		{
			txt_repeat.setText("只聲一次");
			isAlarmDay = true;
		}
		else if ( anItem.repeat_type == 1 )
		{
			txt_repeat.setText("每天");
			isAlarmDay = true;
		}
		else if ( anItem.repeat_type == 2 )
		{
			txt_repeat.setText("週一至週五");
			if ( curWeekday >= 1 && curWeekday <= 5 )
				isAlarmDay = true;
		}
		else if ( anItem.repeat_type == 3 )
		{
			String str_repeat[] = anItem.repeat_week.split(",");
			int cnt = str_repeat.length;
			String strRepeat = "";
			int tmp = 0;
			for ( int i = 0; i < cnt; i++ )
			{
				tmp = Integer.valueOf(str_repeat[i]);
				
				if ( tmp == 1 )
					strRepeat += "週日";
				if ( tmp == 2 )
					strRepeat += "週一";
				if ( tmp == 3 )
					strRepeat += "週二";
				if ( tmp == 4 )
					strRepeat += "週三";
				if ( tmp == 5 )
					strRepeat += "週四";
				if ( tmp == 6 )
					strRepeat += "週五";
				if ( tmp == 7 )
					strRepeat += "週六";
				
				
				if ( i < cnt - 1 )
					strRepeat += "、";
				
				if ( tmp - 1 == curWeekday )
					isAlarmDay = true;
			}
			txt_repeat.setText(strRepeat);
		}
		
		if ( isAlarmDay == true )
		{
			if ( total < 0 )
				txt_remain.setText("已經過時");
			else if ( total < 1 )
				txt_remain.setText("不到1分鐘后響鈴");
			else if ( total < 60 )
				txt_remain.setText(String.format("%d分鐘后響鈴", total));
			else
			{
				int offset_hour = total /60;
				int offset_min = total % 60;
				txt_remain.setText(String.format("%d小時%d分鐘后響鈴", offset_hour, offset_min));
			}
		}
		else
			txt_remain.setText("今天没提醒");
		
		if ( anItem.status == 1 )
			img_switch.setImageResource(R.drawable.activity_alarm_switchon);
		else
			img_switch.setImageResource(R.drawable.activity_alarm_switchoff);
        
		img_switch.setTag(position);
		img_switch.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		int pos = (Integer)v.getTag();
        		onClickSwitch(pos);
        	}
        });
		
		return rowView;
	}
	
	private void onClickSwitch(int pos)
	{
		/*
		STAlarmInfo anItem = getItem(pos);
		
		if ( anItem.status == true )
			img_switch.setImageResource(R.drawable.activity_alarm_switchoff);
		else
			img_switch.setImageResource(R.drawable.activity_alarm_switchon);
		*/
		parentAct.onSwitchChanged(pos);
	}
}
