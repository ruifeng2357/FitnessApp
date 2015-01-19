/* 健康*/
package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.damy.datatypes.STWeighListInfo;
import com.damy.jiankang.R;
import com.damy.Utils.ResolutionSet1;



public class WeightListAdapter extends ArrayAdapter<STWeighListInfo> {

	public WeightListAdapter(Activity activity, List<STWeighListInfo> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_weightinfo, null);
		ResolutionSet1._instance.iterateChild(rowView);
		STWeighListInfo anItem = getItem(position);
		
		TextView txt_date = (TextView) rowView.findViewById(R.id.txt_weightinfo_item_date);
		TextView txt_weight = (TextView) rowView.findViewById(R.id.txt_weightinfo_item_weight);
		TextView txt_waist = (TextView) rowView.findViewById(R.id.txt_weightinfo_item_waist);
		TextView txt_calory = (TextView) rowView.findViewById(R.id.txt_weightinfo_item_calory);
		
		txt_date.setText(anItem.date);
		txt_weight.setText(anItem.weight);
		txt_waist.setText(anItem.waist);
		txt_calory.setText(anItem.calory);		
		
        
		return rowView;
	}
}
