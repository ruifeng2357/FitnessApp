/* 健康*/
package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.damy.datatypes.STNewsInfo;
import com.damy.jiankang.R;
import com.damy.Utils.ResolutionSet1;



public class NewsAdapter extends ArrayAdapter<STNewsInfo> {

	public NewsAdapter(Activity activity, List<STNewsInfo> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_news, null);
		ResolutionSet1._instance.iterateChild(rowView);
		STNewsInfo anItem = getItem(position);
		
		TextView txt_title = (TextView) rowView.findViewById(R.id.txt_news_item_title);
		TextView txt_regtime = (TextView) rowView.findViewById(R.id.txt_news_item_date);
		
		
		txt_title.setText(anItem.title);
		txt_regtime.setText(anItem.regtime);
        
		return rowView;
	}
}
