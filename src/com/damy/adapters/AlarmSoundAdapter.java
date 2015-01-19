/* 健康*/
package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.damy.backend.HttpConnUsingJSON;
import com.damy.common.Global;
import com.damy.datatypes.STSportRecordInfo;
import com.damy.datatypes.STSportTypeInfo;
import com.damy.jiankang.R;
import com.damy.jiankang.SportRecordActivity;
import com.damy.jiankang.SportTypeActivity;
import com.damy.Utils.ResolutionSet;
import com.damy.Utils.ResolutionSet1;


public class AlarmSoundAdapter extends ArrayAdapter<String> {
	
	public AlarmSoundAdapter(Activity activity, List<String> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_alarmsound, null);
		ResolutionSet1._instance.iterateChild(rowView.findViewById(R.id.rl_item_alarmsound));
		String anItem = getItem(position);
		
		TextView txt_name = (TextView) rowView.findViewById(R.id.lbl_item_alarmsound_title);
		
		txt_name.setText(anItem);
		
		return rowView;
	}
}
