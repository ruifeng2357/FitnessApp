/* 健康*/
package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.damy.backend.HttpConnUsingJSON;
import com.damy.common.Global;
import com.damy.datatypes.STSportRecordInfo;
import com.damy.datatypes.STQuestionDetailInfo;
import com.damy.jiankang.R;
import com.damy.jiankang.SportRecordActivity;
import com.damy.jiankang.QuestionDetailActivity;
import com.damy.Utils.ResolutionSet;
import com.damy.Utils.ResolutionSet1;


public class QuestionDetailAdapter extends ArrayAdapter<STQuestionDetailInfo> {
	
	QuestionDetailActivity		m_parent;
	 
	public QuestionDetailAdapter(QuestionDetailActivity activity, List<STQuestionDetailInfo> detailInfos) {
		super(activity, 0, detailInfos);
		
		m_parent = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_questiondetail, null);
		ResolutionSet1._instance.iterateChild(rowView.findViewById(R.id.rl_item_questiondetail));
		STQuestionDetailInfo anItem = getItem(position);
		
		TextView txt_date = (TextView) rowView.findViewById(R.id.lbl_item_questiondetail_time);
		RelativeLayout rl_answer = (RelativeLayout) rowView.findViewById(R.id.rl_item_questiondetail_answer);
		RelativeLayout rl_question = (RelativeLayout) rowView.findViewById(R.id.rl_item_questiondetail_question);
		TextView txt_answerbody = (TextView) rowView.findViewById(R.id.lbl_item_questiondetail_answerbody);
		TextView txt_questionbody = (TextView) rowView.findViewById(R.id.lbl_item_questiondetail_questionbody);
		
		txt_date.setText(anItem.regtime);
		if ( anItem.type == 0 )
		{
			rl_question.setVisibility(View.GONE);
			txt_answerbody.setText(anItem.content);
		}
		else
		{
			rl_answer.setVisibility(View.GONE);
			txt_questionbody.setText(anItem.content);
		}
		        
		return rowView;
	}
}
