/* 健康*/
package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.damy.datatypes.STQuestionInfo;
import com.damy.jiankang.R;
import com.damy.Utils.ResolutionSet1;


public class QuestionAdapter extends ArrayAdapter<STQuestionInfo> {

	public QuestionAdapter(Activity activity, List<STQuestionInfo> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_question, null);
		ResolutionSet1._instance.iterateChild(rowView);
		STQuestionInfo anItem = getItem(position);
		
		TextView txt_title = (TextView) rowView.findViewById(R.id.txt_q_item_title);
		TextView txt_regtime = (TextView) rowView.findViewById(R.id.txt_q_item_date);
		TextView txt_badge = (TextView) rowView.findViewById(R.id.txt_bage_view);
		
		txt_title.setText(anItem.content);
		txt_regtime.setText(anItem.regtime);
		
		if ( anItem.count > 0 )
			txt_badge.setText(String.valueOf(anItem.count));
		else
			txt_badge.setVisibility(View.INVISIBLE);
        
		return rowView;
	}
}
