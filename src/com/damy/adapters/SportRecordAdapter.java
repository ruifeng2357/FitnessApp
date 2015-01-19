/* 健康*/
package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.damy.datatypes.STSportRecordInfo;
import com.damy.jiankang.R;
import com.damy.jiankang.SportRecordActivity;
import com.damy.Utils.ResolutionSet;
import com.damy.Utils.ResolutionSet1;


public class SportRecordAdapter extends ArrayAdapter<STSportRecordInfo> {
	
	SportRecordActivity		m_parent;
	 
	public SportRecordAdapter(SportRecordActivity activity, List<STSportRecordInfo> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_sportrecord, null);
		/*
		RelativeLayout rl_row = (RelativeLayout)rowView.findViewById(R.id.rl_item_sportrecord);
		rl_row.setTag(position);
		rl_row.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Integer pos = (Integer)v.getTag();
        		onClickItem(pos);
        	}
        });
		
		rl_row.setOnLongClickListener(new View.OnLongClickListener() {	
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Integer pos = (Integer)v.getTag();
        		onLongClickItem(pos);
				return false;
			}
		});
		*/
		ResolutionSet1._instance.iterateChild(rowView);
		STSportRecordInfo anItem = getItem(position);
		
		TextView txt_name = (TextView) rowView.findViewById(R.id.lbl_item_sportrecord_name);
		TextView txt_calory = (TextView) rowView.findViewById(R.id.lbl_item_sportrecord_calory);
		
		txt_name.setText(anItem.name);
		
		String str_tmp = "";
		
		if ( (anItem.time / 60) > 0 )
			str_tmp += String.format("%d小時", (anItem.time / 60));
		if ( (anItem.time % 60) > 0 )
			str_tmp += String.format(" %d分鐘", (anItem.time % 60));
		
		str_tmp += String.format(" | %d千卡", anItem.calory);
		
		txt_calory.setText(str_tmp);
        
		return rowView;
	}
	/*
	public void onClickItem(int pos)
	{
		m_parent.onClickItem(pos);
	}
	public void onLongClickItem(int pos)
	{
		m_parent.onLongClickItem(pos);
	}
	*/
}
