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
import com.damy.datatypes.STFoodMainInfo;
import com.damy.datatypes.STFoodSubInfo;
import com.damy.jiankang.R;
import com.damy.Utils.ResolutionSet;
import com.damy.Utils.ResolutionSet1;


public class FoodSubAdapter extends ArrayAdapter<STFoodSubInfo> {
	
	 
	public FoodSubAdapter(Activity activity, List<STFoodSubInfo> detailInfos) {
		super(activity, 0, detailInfos);		 
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_food_sub, null);
		ResolutionSet1._instance.iterateChild(rowView);
		STFoodSubInfo anItem = getItem(position);
		
		TextView txt_title = (TextView) rowView.findViewById(R.id.txt_food_sub_item_title);
		TextView txt_calory = (TextView) rowView.findViewById(R.id.txt_food_sub_calory);
		TextView txt_mass = (TextView) rowView.findViewById(R.id.txt_food_sub_mass);
				
		txt_title.setText(anItem.title);
		txt_calory.setText(anItem.mass_calory);
		
		String unit = "克";
		txt_mass.setText("大卡/" + anItem.mass + unit);
		
		ImageView img = (ImageView) rowView.findViewById(R.id.img_food_sub_item_image);
		
		
		
		txt_title.setText(anItem.title);
		if ( anItem.image.length() > 0 )
		{
	        try
	        {
	            Global.imageLoader.displayImage(HttpConnUsingJSON.BASE_IMAGEURL + anItem.image, img, Global.food_options);
	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();
	        }
		}
        
		return rowView;
	}
}
