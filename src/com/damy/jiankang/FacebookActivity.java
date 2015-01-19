/* 健康*/
package com.damy.jiankang;

import java.util.ArrayList;

import android.util.Log;
import com.facebook.Facebook;
import com.facebook.SessionStore;
import com.facebook.Util;
import com.facebook.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STFacebookImageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FacebookActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETFACEBOOKDATA};
	private REQ_TYPE				m_reqType;
	
	private String 					m_szSelPath = "";
	private Uri 					m_szSelUri = null;
	private int 					REQUEST_PHOTO = 0;
	
	private int						MAX_IMAGE_COUNT = 9;
	
	public static String			EXTRADATA_DATE = "SPORTRECORDINFO_DATE";
	public static String			EXTRADATA_TYPE = "SPORTRECORDINFO_TYPE";
	
	private String					m_strCurDate;
	private int						m_nCurType;
	private int						m_nCurClicked;
	
	private String					m_strMessage;
	private String					m_strImage[];
	
	private EditText				edit_title;
	private EditText				edit_body;
	
	private ImageView				img_Image[];
	
	private RelativeLayout			rl_imgdetail;
	private ImageView				img_imgdetailimg;
	
	
	private ArrayList<STFacebookImageInfo>   m_ImageInfoList;

    Button btn_save = null;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook);
		
		initActivity(R.id.rl_facebook);

		initControls();
		readContents();

        Thread.UncaughtExceptionHandler mUEHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(mUEHandler);
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_facebook_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		btn_save = (Button)findViewById(R.id.btn_facebook_save);
        btn_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSave();
            }
        });
		
		edit_title = (EditText)findViewById(R.id.edit_facebook_name);
		edit_body = (EditText)findViewById(R.id.edit_facebook_body);
		edit_body.setEnabled(true);
		
		rl_imgdetail = (RelativeLayout)findViewById(R.id.rl_facebook_imgdetail);
		img_imgdetailimg = (ImageView)findViewById(R.id.img_facebook_imgdetailimg);
		ImageButton img_imgdetailback = (ImageButton)findViewById(R.id.imgbtn_facebook_imgdetailback);
		Button img_imgdetaildel = (Button)findViewById(R.id.btn_facebook_imgdetaildel);
		
		img_imgdetailback.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDetailBack();
        	}
        });
		
		img_imgdetaildel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDetailDel();
        	}
        });
		
		rl_imgdetail.setVisibility(View.INVISIBLE);
		
		img_Image = new ImageView[MAX_IMAGE_COUNT];
		m_strImage = new String[MAX_IMAGE_COUNT];
		
		img_Image[0] = (ImageView)findViewById(R.id.img_facebook_img1);
		img_Image[1] = (ImageView)findViewById(R.id.img_facebook_img2);
		img_Image[2] = (ImageView)findViewById(R.id.img_facebook_img3);
		img_Image[3] = (ImageView)findViewById(R.id.img_facebook_img4);
		img_Image[4] = (ImageView)findViewById(R.id.img_facebook_img5);
		img_Image[5] = (ImageView)findViewById(R.id.img_facebook_img6);
		img_Image[6] = (ImageView)findViewById(R.id.img_facebook_img7);
		img_Image[7] = (ImageView)findViewById(R.id.img_facebook_img8);
		img_Image[8] = (ImageView)findViewById(R.id.img_facebook_img9);
		
		img_Image[0].setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickImageView(0);
        	}
        });
		img_Image[1].setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickImageView(1);
        	}
        });
		img_Image[2].setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickImageView(2);
        	}
        });
		img_Image[3].setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickImageView(3);
        	}
        });
		img_Image[4].setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickImageView(4);
        	}
        });
		img_Image[5].setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickImageView(5);
        	}
        });
		img_Image[6].setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickImageView(6);
        	}
        });
		img_Image[7].setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickImageView(7);
        	}
        });
		img_Image[8].setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickImageView(8);
        	}
        });
		
		int i;
		for ( i = 1; i < MAX_IMAGE_COUNT; i++ )
		{
			img_Image[i].setVisibility(View.GONE);
		}
		
		m_strCurDate = getIntent().getStringExtra(EXTRADATA_DATE);
		m_nCurType = getIntent().getIntExtra(EXTRADATA_TYPE, 0);
		
		m_ImageInfoList = new ArrayList<STFacebookImageInfo>();
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
		finish();
	}
	
	private void readContents()
	{
		m_reqType = REQ_TYPE.REQ_GETFACEBOOKDATA;
		new LoadResponseThread(FacebookActivity.this).start();
	}

	private void onClickSave()
	{
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try {
                    if (Utility.mFacebook == null)
                    {
                        Utility.mFacebook = new Facebook(Global.FBAPP_ID);
                    }

                    SessionStore.restore(Utility.mFacebook, FacebookActivity.this);

                    //String response = Utility.mFacebook.request("me");
                    String response = "";
                    Bundle parameters = new Bundle();
                    parameters.putString("message", "AAA");
                    parameters.putString("description", "test test test");
                    parameters.putString("caption", "Caption");
                    parameters.putString("name", "Name");

                    if (Utility.mFacebook.isSessionValid()) {
                        response = Utility.mFacebook.request("me", parameters, "POST");

                        if (response == null || response.equals("") || response.equals("false")) {
                            Log.v("Error", "Blank response");
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
	}
	
	private void onClickDetailBack()
	{
		rl_imgdetail.setVisibility(View.INVISIBLE);
	}
	
	private void onClickDetailDel()
	{
		m_ImageInfoList.remove(m_nCurClicked);
		drawImageView(m_nCurClicked);
		rl_imgdetail.setVisibility(View.INVISIBLE);
	}
	
	private void onClickImageView(int ind)
	{
		if ( ind == m_ImageInfoList.size() )
		{
			onClickPhotoSelect();
		}
		else
		{
			STFacebookImageInfo item = m_ImageInfoList.get(ind);
			drawImageView(img_imgdetailimg, item);
			rl_imgdetail.setVisibility(View.VISIBLE);
			m_nCurClicked = ind;
		}
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
			
			STFacebookImageInfo item = new STFacebookImageInfo();
			item.isNetwork = false;
			
			if (objPath != null) {
				m_szSelPath = (String) objPath;
				item.isPath = true;
				item.imgPath = m_szSelPath;
			}

			if (objUri != null) {
				m_szSelUri = (Uri) objUri;
				item.isPath = false;
				item.imgUri = m_szSelUri;
			}
			
			if ( objPath != null || objUri != null )
			{
				m_ImageInfoList.add(item);
				drawImageView(m_ImageInfoList.size() - 1);
			}
		}
	}
	/*
	private void updateUserImageWithPath(String szPath) {
		try {

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap bitmap = BitmapFactory.decodeFile(szPath, options);

			img_Image[m_nCurImageCount].setImageBitmap(bitmap);
			onNewImageSelected();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	*/
	
	private void drawImageView(ImageView view, STFacebookImageInfo item)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		
		view.setImageResource(R.drawable.activity_facebook_plus);
		
		if ( item.isNetwork )
		{
			try
	        {
				Global.imageLoader.displayImage(HttpConnUsingJSON.BASE_IMAGEURL + item.imgPath, view, Global.options);
	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();
	        }
		}
		else
		{
			if ( item.isPath )
			{
				try {
					Bitmap bitmap = BitmapFactory.decodeFile(item.imgPath, options);
					view.setImageBitmap(bitmap);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			else
			{
				view.setImageURI(item.imgUri);
			}
		}
	}
	
	private void drawImageView(int nPos)
	{
		int count = m_ImageInfoList.size();
		
		int i = 0;
		
		STFacebookImageInfo item;
		
		for ( i = nPos; i < count; i++ )
		{
			item = m_ImageInfoList.get(i);
			drawImageView(img_Image[i], item);
			img_Image[i].setVisibility(View.VISIBLE);
		}
		
		if ( count < MAX_IMAGE_COUNT )
		{
			img_Image[count].setImageResource(R.drawable.activity_facebook_plus);
			img_Image[count].setVisibility(View.VISIBLE);
			
			for ( i = count + 1; i < MAX_IMAGE_COUNT; i++ )
			{
				img_Image[i].setVisibility(View.GONE);
			}
		}
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETFACEBOOKDATA )
		{
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				drawImageView(0);
				
				edit_body.setText(m_strMessage);
			}
		}
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETFACEBOOKDATA )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETFACEBOOKDATA;
				strRequest += "?uid=" + String.valueOf(Global.Cur_UserId);
				strRequest += "&date=" + m_strCurDate;
				strRequest += "&type=" + String.valueOf(m_nCurType);
				
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
					
					m_strMessage = dataObject.getString("message");
					
					if ( m_nCurType == 1 )
					{
						int count = dataObject.getInt("count");
						
			            JSONArray dataList = dataObject.getJSONArray("data");
			            
			            int ind = 0;
			            String tmp = "";
			            for (int i = 0; i < count; i++) {
			            	tmp = dataList.getString(i);
			            	
			            	if ( !tmp.equals("") )
			            	{
			            		ind++;
				            	m_strImage[ind] = tmp;
				            	STFacebookImageInfo item = new STFacebookImageInfo();
				            	item.isNetwork = true;
				            	item.isPath = true;
				            	item.imgPath = m_strImage[ind];
				            	m_ImageInfoList.add(item);
			            	}
			            	
			            }
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
	
	public JSONObject makeRequestJSON() throws JSONException {
		return null;
	}
}
