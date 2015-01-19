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


public class SportTypeAdapter extends ArrayAdapter<STSportTypeInfo> {
	
	SportTypeActivity		m_parent;
	 
	public SportTypeAdapter(SportTypeActivity activity, List<STSportTypeInfo> detailInfos) {
		super(activity, 0, detailInfos);
		
		m_parent = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_sporttype, null);
		ResolutionSet1._instance.iterateChild(rowView.findViewById(R.id.rl_item_sporttype));
		STSportTypeInfo anItem = getItem(position);
		
		TextView txt_name = (TextView) rowView.findViewById(R.id.lbl_item_sporttype_name);
		ImageView img_icon = (ImageView) rowView.findViewById(R.id.img_item_sporttype_icon);
		
		txt_name.setText(anItem.name);
		if ( anItem.image.length() > 0 )
		{
			try
	        {
	            Global.imageLoader.displayImage(HttpConnUsingJSON.BASE_IMAGEURL + anItem.image, img_icon, Global.sport_options);
	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();
	        }
		}
		        
		return rowView;
	}
}
