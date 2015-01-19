/* �亙熒*/
package com.damy.jiankang;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.json.JSONException;
import org.json.JSONObject;
import com.damy.Utils.Base64;
import com.damy.Utils.RulerSliderView;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;

import android.net.Uri;
import android.os.Bundle;
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

public class FoodRecordInfoActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETSUBFOODINFO, REQ_INSERTFOODRECORD, REQ_UPDATEFOODRECORD};
	private REQ_TYPE				m_reqType;
	
	private String 					m_szSelPath = "";
	private Uri 					m_szSelUri = null;
	private int 					REQUEST_PHOTO = 0;
	
	public static String			EXTRADATA_RECORDID = "FOODRECORDINFO_RECORDID";
	public static String			EXTRADATA_FOODID = "FOODRECORDINFO_SUBID";
	public static String			EXTRADATA_IMAGE = "FOODRECORDINFO_IMAGE";
	public static String			EXTRADATA_CURMASS = "FOODRECORDINFO_CURMASS";
	public static String			EXTRADATA_CURUNIT = "FOODRECORDINFO_CURUNIT";
	
	private Long					m_nRecordId;
	private Long					m_nFoodId;
	private String					m_strName;
	private String					m_strImage;
	private int						m_nMass;
	private int						m_nMassCalory;
	private int						m_nCount;
	private int						m_nCountCalory;
	private int						m_nCurMass;
	private int						m_nCurUnit;
	private String					m_strUploadImage;
	
	private TextView				txt_calory;
	private TextView				txt_mass;
	private ImageView				img_uploadimg;
	private RulerSliderView			ruler_value;
	
	private Button 					btn_unitgram;
	private Button 					btn_unitamount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_foodrecordinfo);
		
		initActivity(R.id.rl_foodrecordinfo);
		
		initControls();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_foodrecordinfo_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		Button btn_save = (Button)findViewById(R.id.btn_foodrecordinfo_save);
		Button btn_photo = (Button)findViewById(R.id.btn_foodrecordinfo_photo);
		
		btn_save.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSave();
        	}
        });
		
		btn_photo.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPhotoSelect();
        	}
        });
		
		m_nRecordId = getIntent().getLongExtra(EXTRADATA_RECORDID, 0);
		m_nFoodId = getIntent().getLongExtra(EXTRADATA_FOODID, 0);
		
		txt_calory = (TextView)findViewById(R.id.lbl_foodrecordinfo_foodcalory);
		txt_mass = (TextView)findViewById(R.id.lbl_foodrecordinfo_foodmass);
		img_uploadimg = (ImageView)findViewById(R.id.img_foodrecordinfo_uploadimg);
		ruler_value = (RulerSliderView)findViewById(R.id.ruler_foodrecordinfo_value);
		
		if ( m_nRecordId > 0 )
		{
			m_nCurMass = getIntent().getIntExtra(EXTRADATA_CURMASS, 0);
			m_nCurUnit = getIntent().getIntExtra(EXTRADATA_CURUNIT, 0);
			m_strUploadImage = getIntent().getStringExtra(EXTRADATA_IMAGE);
			
			if ( m_strUploadImage.length() > 0 )
			{
				try
		        {
					Global.imageLoader.displayImage(HttpConnUsingJSON.BASE_IMAGEURL + m_strUploadImage, img_uploadimg, Global.food_options);
		        }
		        catch (Exception ex)
		        {
		            ex.printStackTrace();
		        }
			}
		}
		else
		{
			m_nCurMass = 0;
			m_nCurUnit = 0;
		}
		
		ruler_value.setCurValue(m_nCurMass);
		
		if ( m_nCurUnit == 0 )
			ruler_value.setUnitStr(getResources().getString(R.string.common_gram));
		else if ( m_nCurUnit == 1 )
			ruler_value.setUnitStr(getResources().getString(R.string.common_amount));
		else
			ruler_value.setUnitStr(getResources().getString(R.string.common_calory));
		
		btn_unitgram = (Button)findViewById(R.id.btn_foodrecordinfo_massgram);
		btn_unitamount = (Button)findViewById(R.id.btn_foodrecordinfo_massamount);
		
		btn_unitgram.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		m_nCurUnit = 0;
        		onCurUnitChange();
        		
        		btn_unitgram.setBackgroundResource(R.drawable.btn_green_d);
        		btn_unitamount.setBackgroundResource(R.drawable.btn_green_u);
        	}
        });
		
		btn_unitamount.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		m_nCurUnit = 1;
        		onCurUnitChange();
        		
        		btn_unitgram.setBackgroundResource(R.drawable.btn_green_u);
        		btn_unitamount.setBackgroundResource(R.drawable.btn_green_d);
        	}
        });
		
		readContents();
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
		Intent foodrecord_activity = new Intent(this, FoodRecordActivity.class);
		startActivity(foodrecord_activity);	
		finish();
	}
	
	private void readContents()
	{
		m_reqType = REQ_TYPE.REQ_GETSUBFOODINFO;
		new LoadResponseThread(FoodRecordInfoActivity.this).start();
	}
	
	private void onClickSave()
	{
		if ( ruler_value.getCurValue() == 0 )
		{
			showToastMessage(getResources().getString(R.string.activity_foodrecordinfo_errorselectamount));
			return;
		}
		
		if ( m_nRecordId > 0 )
			m_reqType = REQ_TYPE.REQ_UPDATEFOODRECORD;
		else
			m_reqType = REQ_TYPE.REQ_INSERTFOODRECORD;
		
		new LoadResponseThread(FoodRecordInfoActivity.this).start();
	}
	
	private void onClickPhotoSelect()
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
				img_uploadimg.setImageURI(m_szSelUri);
			}
		}
	}

	private void updateUserImageWithPath(String szPath) {
		try {

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap bitmap = BitmapFactory.decodeFile(szPath, options);

			img_uploadimg.setImageBitmap(bitmap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void onCurUnitChange()
	{
		int nMass = 0;
		int nCal = 0;
		String strUnit = "";
		
		if ( m_nCurUnit == 0 || m_nCurUnit == 2 )
		{
			nMass = m_nMass;
			nCal = m_nMassCalory;
			strUnit = getResources().getString(R.string.common_gram);
		}
		else
		{
			nMass = m_nCount;
			nCal = m_nCountCalory;
			strUnit = getResources().getString(R.string.common_amount);
		}
		
		txt_calory.setText(String.format("%d", nCal));
		txt_mass.setText(String.format("%s ( %d%s%s )", getResources().getString(R.string.common_calory), nMass, strUnit, getResources().getString(R.string.activity_foodrecordinfo_calorysuffix)));
		
		if ( m_nCurUnit == 0 )
			ruler_value.setUnitStr(getResources().getString(R.string.common_gram));
		else if ( m_nCurUnit == 1 )
			ruler_value.setUnitStr(getResources().getString(R.string.common_amount));
		else
			ruler_value.setUnitStr(getResources().getString(R.string.common_calory));
		
		ruler_value.invalidate();
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETSUBFOODINFO )
		{
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				TextView txt_name = (TextView)findViewById(R.id.lbl_foodrecordinfo_foodname);
				ImageView img_icon = (ImageView)findViewById(R.id.img_foodrecordinfo_foodimg);
				
				txt_name.setText(m_strName);
				try
		        {
					Global.imageLoader.displayImage(HttpConnUsingJSON.BASE_IMAGEURL+ m_strImage, img_icon, Global.food_options);
		        }
		        catch (Exception ex)
		        {
		            ex.printStackTrace();
		        }
				
				onCurUnitChange();
			 }
		}
		else if ( m_reqType == REQ_TYPE.REQ_INSERTFOODRECORD || m_reqType == REQ_TYPE.REQ_UPDATEFOODRECORD )
		{
			Intent intent = new Intent(FoodRecordInfoActivity.this, FoodRecordActivity.class);
            startActivity(intent);
            finish();
		}
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETSUBFOODINFO )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;				
				
				String strRequest = HttpConnUsingJSON.REQ_GETSUBFOODINFO;				
				strRequest += "?id=" + String.valueOf(m_nFoodId);
				
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
				
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
	            if (m_nResponse == ResponseRet.RET_SUCCESS) {
	            	JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
					
	            	m_strName = dataObject.getString("name");
	            	m_strImage = dataObject.getString("image");
					m_nMass = dataObject.getInt("mass");
					m_nMassCalory = dataObject.getInt("mass_calory");
					m_nCount = dataObject.getInt("count");
					m_nCountCalory = dataObject.getInt("count_calory");
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_INSERTFOODRECORD )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_INSERTFOODINFO;
				
				JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
	            	
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_UPDATEFOODRECORD )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_UPDATEFOODRECORDINFO;
				
				JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
	            	
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
	
	public JSONObject makeRequestJSON() throws JSONException {
		
		JSONObject requestObj = new JSONObject();
		
		if ( m_reqType == REQ_TYPE.REQ_UPDATEFOODRECORD )
			requestObj.put("id", String.valueOf(m_nRecordId));
		
		requestObj.put("sub_id", String.valueOf(m_nFoodId));
		requestObj.put("mass", String.valueOf(ruler_value.getCurValue()));
		requestObj.put("unit", String.valueOf(m_nCurUnit));
		requestObj.put("type", String.valueOf(Global.FoodRecord_CurType));
		requestObj.put("date", String.format("%d-%d-%d", Global.FoodRecord_CurDate.getYear() + 1900, Global.FoodRecord_CurDate.getMonth() + 1, Global.FoodRecord_CurDate.getDate()));
		requestObj.put("uid", String.valueOf(Global.Cur_UserId));
		if (m_szSelPath.equals("") && m_szSelUri == null) {
			requestObj.put("image", "");
		}
		else
		{
			try {
				if (m_szSelPath != null && !m_szSelPath.equals("")) {
					File file = new File(m_szSelPath);
					FileInputStream fis = new FileInputStream(file);
					byte[] buff = new byte[(int) file.length()];
					fis.read(buff, 0, (int) file.length());
					String imageURI = Base64.encodeBytes(buff);
					requestObj.put("image", imageURI);
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
					requestObj.put("image", imgEncoded);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return requestObj;
	}
}
