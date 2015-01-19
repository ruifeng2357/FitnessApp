/* 健康*/
package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.damy.datatypes.STFoodMainInfo;
import com.damy.jiankang.R;
import com.damy.Utils.ResolutionSet;
import com.damy.Utils.ResolutionSet1;


public class FoodMainAdapter extends ArrayAdapter<STFoodMainInfo> {
	
	 
	public FoodMainAdapter(Activity activity, List<STFoodMainInfo> detailInfos) {
		super(activity, 0, detailInfos);		 
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_food_main, null);
		ResolutionSet1._instance.iterateChild(rowView);
		STFoodMainInfo anItem = getItem(position);
		
		TextView txt_title = (TextView) rowView.findViewById(R.id.txt_food_main_item_title);
				
		txt_title.setText(anItem.title);
                
		return rowView;
	}
}
