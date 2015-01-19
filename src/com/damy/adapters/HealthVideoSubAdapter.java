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
import com.damy.datatypes.STHealthVideoSubInfo;
import com.damy.jiankang.R;
import com.damy.Utils.ResolutionSet1;


public class HealthVideoSubAdapter extends ArrayAdapter<STHealthVideoSubInfo> {
		 
	public HealthVideoSubAdapter(Activity activity, List<STHealthVideoSubInfo> detailInfos) {
		super(activity, 0, detailInfos);
		 
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_health_main, null);
		ResolutionSet1._instance.iterateChild(rowView);
		STHealthVideoSubInfo anItem = getItem(position);
		
		TextView txt_title = (TextView) rowView.findViewById(R.id.txt_health_main_item_title);
		ImageView img = (ImageView) rowView.findViewById(R.id.img_health_main_item_image);
		
		
		txt_title.setText(anItem.title);
        try
        {
            Global.imageLoader.displayImage(HttpConnUsingJSON.BASE_IMAGEURL + anItem.image, img, Global.video_options);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
		return rowView;
	}
}
