/* 健康*/
package com.damy.jiankang;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.Base64;
import com.damy.Utils.ResolutionSet;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyInfoActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_EDITUSERINFO};
	
	private REQ_TYPE				m_reqType;
	
	private String 					m_szSelPath = "";
	private Uri 					m_szSelUri = null;
	private int 					REQUEST_PHOTO = 0;
	
	private ImageView 				img_photo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myinfo);
		
		initActivity(R.id.rl_myinfo);
		
		initControls();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_myinfo_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		img_photo = (ImageView)findViewById(R.id.img_myinfo_myphoto);
		img_photo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickChangePhoto();
			}
		});
		if ( Global.Cur_UserImage.length() > 0 )
		{
			try
	        {
				Global.imageLoader.displayImage(HttpConnUsingJSON.BASE_IMAGEURL + Global.Cur_UserImage, img_photo, Global.options);
	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();
	        }
		}
		else
		{
			if ( Global.Cur_UserSex == 0 )
				img_photo.setImageResource(R.drawable.activity_sex_man_d);
			else
				img_photo.setImageResource(R.drawable.activity_sex_woman_d);
		}
		
		TextView lbl_username = (TextView)findViewById(R.id.lbl_myinfo_changeusername);
		lbl_username.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickName();				
			}
		});
		if ( Global.Cur_UserName.length() > 0 )
			lbl_username.setText(Global.Cur_UserName);
		else
			lbl_username.setText(getResources().getString(R.string.activity_myinfo_username));
		
		TextView lbl_userid = (TextView)findViewById(R.id.lbl_myinfo_username);
		lbl_userid.setText(Global.Cur_UserLoginId);
		
		TextView lbl_password = (TextView)findViewById(R.id.lbl_myinfo_changepassword);
		
		lbl_password.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickPassword();
			}
		});
		
		RelativeLayout rl_bmi = (RelativeLayout)findViewById(R.id.rl_myinfo_bmi);
		
		rl_bmi.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onClickBmi();
			}
		});
		
		TextView txt_bmi = (TextView)findViewById(R.id.lbl_myinfo_bmi);
		
		float fBMI = Global.Cur_UserWeight / (((float)Global.Cur_UserHeight / 100) * ((float)Global.Cur_UserHeight / 100));
		txt_bmi.setText(String.format("%.01f", fBMI));
		
		RelativeLayout rl_bmr = (RelativeLayout)findViewById(R.id.rl_myinfo_bmr);
		
		rl_bmr.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onClickBmi();
			}
		});
		
		double fBMR = 0;
		if ( Global.Cur_UserSex == 0 )
			fBMR = (13.7 * Global.Cur_UserWeight) + (5 * (float)Global.Cur_UserHeight) - (6.8 * Global.Cur_UserAge) + 66;
		else
			fBMR = (9.6 * Global.Cur_UserWeight) + (1.8 * (float)Global.Cur_UserHeight) - (4.7 * Global.Cur_UserAge) + 655;
		
		TextView txt_bmr = (TextView)findViewById(R.id.lbl_myinfo_bmr);
		txt_bmr.setText(String.format("%.02f", fBMR));
		
		RelativeLayout rl_level = (RelativeLayout)findViewById(R.id.rl_myinfo_level);
		
		rl_level.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickLevel();
			}
		});
		
		TextView txt_level = (TextView)findViewById(R.id.lbl_myinfo_level);
		ImageView img_level = (ImageView)findViewById(R.id.img_myinfo_level);
		
		String level = "";
		switch (Global.Cur_UserLevel) {
			case 0:
				level = getResources().getString(R.string.activity_dailysportintensity_low);
				img_level.setImageResource(R.drawable.activity_dailysportintensity_snail);
				break;
			case 1:
				level = getResources().getString(R.string.activity_dailysportintensity_middle);
				img_level.setImageResource(R.drawable.activity_dailysportintensity_turtle);
				break;
			case 2:
				level = getResources().getString(R.string.activity_dailysportintensity_high);
				img_level.setImageResource(R.drawable.activity_dailysportintensity_rabbit);
				break;
			case 3:
				level = getResources().getString(R.string.activity_dailysportintensity_veryhigh);
				img_level.setImageResource(R.drawable.activity_dailysportintensity_leopard);
				break;

		default:
			level = getResources().getString(R.string.activity_dailysportintensity_low);		
			img_level.setImageResource(R.drawable.activity_dailysportintensity_snail);
			break;
		}
		
		txt_level.setText(level);
		
		RelativeLayout rl_sex = (RelativeLayout)findViewById(R.id.rl_myinfo_sex);
		
		rl_sex.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onClickSex();
			}
		});
		
		TextView txt_sex = (TextView)findViewById(R.id.lbl_myinfo_sex);
		
		if (Global.Cur_UserSex == 0)
			txt_sex.setText(getResources().getString(R.string.activity_sex_btn_man));
		else
			txt_sex.setText(getResources().getString(R.string.activity_sex_btn_woman));
		
		RelativeLayout rl_birthday = (RelativeLayout)findViewById(R.id.rl_myinfo_birthday);
		
		rl_birthday.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onClickBirthday();
			}
		});
		
		TextView txt_birthday = (TextView)findViewById(R.id.lbl_myinfo_birthday);
		
		txt_birthday.setText(Global.Cur_UserBirthday + " (" + Global.Cur_UserAge + getResources().getString(R.string.common_old) + ")");
		
		RelativeLayout rl_height = (RelativeLayout)findViewById(R.id.rl_myinfo_height);
		
		rl_height.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onClickHeight();
			}
		});
		
		TextView txt_height = (TextView)findViewById(R.id.lbl_myinfo_height);
		txt_height.setText(String.format("%.01f %s", Global.Cur_UserHeight, getResources().getString(R.string.common_unit_centimeter)));
		
		RelativeLayout rl_weight = (RelativeLayout)findViewById(R.id.rl_myinfo_curweight);
		
		rl_weight.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onClickCurWeight();
			}
		});
		
		TextView txt_curweight = (TextView)findViewById(R.id.lbl_myinfo_curweight);
		txt_curweight.setText(String.format("%.01f %s", Global.Cur_UserWeight, getResources().getString(R.string.common_unit_gongjin)));
		
		RelativeLayout rl_weighttarget = (RelativeLayout)findViewById(R.id.rl_myinfo_targetweight);
		
		rl_weighttarget.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onClickTargetWeight();
			}
		});
		
		TextView txt_targetweight = (TextView)findViewById(R.id.lbl_myinfo_targetweight);
		txt_targetweight.setText(String.format("%.01f %s", Global.Cur_UserWeightTarget, getResources().getString(R.string.common_unit_gongjin)));
		
		RelativeLayout rl_waist = (RelativeLayout)findViewById(R.id.rl_myinfo_curwaistline);
		
		rl_waist.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onClickCurWaist();
			}
		});
		
		TextView txt_curwaist = (TextView)findViewById(R.id.lbl_myinfo_curwaistline);
		txt_curwaist.setText(String.format("%.01f %s", Global.Cur_UserWaistline, getResources().getString(R.string.common_unit_centimeter)));
		
		RelativeLayout rl_waisttarget = (RelativeLayout)findViewById(R.id.rl_myinfo_targetwaistline);
		
		rl_waisttarget.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onClickTargetWaist();
			}
		});
		
		TextView txt_targetwaist = (TextView)findViewById(R.id.lbl_myinfo_targetwaistline);
		txt_targetwaist.setText(String.format("%.01f %s", Global.Cur_UserWaistlineTarget, getResources().getString(R.string.common_unit_centimeter)));
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	onClickBack();
        	return true;
        }
        return false;
    }
	
	private void onClickBack()
	{
		Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}
	
	private void onClickChangePhoto()
	{
		Intent intent = new Intent(this, SelectPhotoActivity.class);
		startActivityForResult(intent, REQUEST_PHOTO);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_PHOTO && resultCode == RESULT_OK)
			updateUserImage(data);
	}
	
	private void updateUserImage(Intent data) {
		if (data.getIntExtra(SelectPhotoActivity.szRetCode, -999) == SelectPhotoActivity.nRetSuccess) {
			Object objPath = data.getExtras()
					.get(SelectPhotoActivity.szRetPath);
			Object objUri = data.getExtras().get(SelectPhotoActivity.szRetUri);

			if (objPath != null) {
				m_szSelPath = (String) objPath;
				updateUserImageWithPath(m_szSelPath);
			}

			if (objUri != null) {
				m_szSelUri = (Uri) objUri;
				img_photo.setImageURI(m_szSelUri);
			}
		}
		
		m_reqType = REQ_TYPE.REQ_EDITUSERINFO;
		new LoadResponseThread(MyInfoActivity.this).start();
	}

	private void updateUserImageWithPath(String szPath) {
		try {

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap bitmap = BitmapFactory.decodeFile(szPath, options);

			img_photo.setImageBitmap(bitmap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void onClickName()
	{
		Intent name_activity = new Intent(this, NamesettingActivity.class);
		startActivity(name_activity);	
		finish();	
	}
	
	private void onClickPassword()
	{
		Intent password_activity = new Intent(this, ChangePasswordActivity.class);
		startActivity(password_activity);	
		finish();	
	}
	
	private void onClickBmi()
	{
		Intent bmi_activity = new Intent(this, HealthinfoActivity.class);
		startActivity(bmi_activity);	
		finish();	
	}
	
	private void onClickLevel()
	{
		Intent level_activity = new Intent(this, DailySportIntensityActivity.class);
		startActivity(level_activity);	
		finish();	
	}
	
	private void onClickSex()
	{
		Intent sex_activity = new Intent(this, SexActivity.class);
		startActivity(sex_activity);	
		finish();	
	}
	
	private void onClickBirthday()
	{
		Intent birthday_activity = new Intent(this, BirthdayActivity.class);
		startActivity(birthday_activity);	
		finish();	
	}
	
	private void onClickHeight()
	{
		Intent height_activity = new Intent(this, HeightActivity.class);
		startActivity(height_activity);	
		finish();	
	}
	
	private void onClickCurWeight()
	{
		Intent curweight_activity = new Intent(this, WeightActivity.class);
		startActivity(curweight_activity);	
		finish();	
	}
	
	private void onClickTargetWeight()
	{
		Intent targetweight_activity = new Intent(this, TargetWeightActivity.class);
		startActivity(targetweight_activity);	
		finish();	
	}
	
	private void onClickCurWaist()
	{
		Intent curwaist_activity = new Intent(this, WaistlineActivity.class);
		startActivity(curwaist_activity);	
		finish();	
	}
	
	private void onClickTargetWaist()
	{
		Intent targetwaist_activity = new Intent(this, TargetWaistlineActivity.class);
		startActivity(targetwaist_activity);	
		finish();	
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			
		 }
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_EDITUSERINFO )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_EDITUSERINFO;
	
				JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);
				
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
				
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
	            if (m_nResponse == ResponseRet.RET_SUCCESS) {            	
	            	JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
	            	
	            	Global.Cur_UserImage = dataObject.getString("image");
	            	if ( Global.Cur_UserImage.equals("null") )
	            		Global.Cur_UserImage = "";
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
	
	
	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();		
    	
		if (m_szSelPath.equals("") && m_szSelUri == null)
			return null;
		
		requestObj.put("uid", Global.Cur_UserId);
		requestObj.put("type", "image");
		
		try {
			if (m_szSelPath != null && !m_szSelPath.equals("")) {
				File file = new File(m_szSelPath);
				FileInputStream fis = new FileInputStream(file);
				byte[] buff = new byte[(int) file.length()];
				fis.read(buff, 0, (int) file.length());
				String imageURI = Base64.encodeBytes(buff);
				requestObj.put("data", imageURI);
				fis.close();
			} else if (m_szSelUri != null) {
				InputStream fis = null;
				Bitmap bmp = null;
				fis = getContentResolver().openInputStream(m_szSelUri);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				bmp = BitmapFactory.decodeStream(fis, null, options);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				String imgEncoded = Base64.encodeBytes(byteArray);
				requestObj.put("data", imgEncoded);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return requestObj;
	}
}
