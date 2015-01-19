/* 健康*/
package com.damy.jiankang;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import com.facebook.*;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.damy.backend.*;
import com.damy.common.Global;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class LoginActivity extends BaseActivity {
    public String FB_Email = "";
    public String FB_LastName = "";
    public String FB_FirstName = "";

	EditText txt_userid;
	EditText txt_password;

	private enum REQ_TYPE {
		REQ_LOGIN, REQ_FACEBOOK
	};
	private REQ_TYPE m_reqType;

	ProgressDialog mDialog;

    private Handler mHandler;
    public static final String[] PERMS = new String[] {  "user_about_me","user_activities","user_birthday","user_checkins","user_education_history","user_events","user_groups","user_hometown","user_interests","user_likes","user_location","user_notes","user_online_presence","user_photo_video_tags","user_photos","user_relationships","user_relationship_details","user_religion_politics","user_status","email","read_insights","read_requests","read_stream","offline_access","publish_checkins","publish_stream","publish_actions" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		m_reqType = REQ_TYPE.REQ_LOGIN;
		
		initActivity(R.id.rl_login);
		
		txt_userid = (EditText)findViewById(R.id.edit_login_userid);
		txt_password = (EditText)findViewById(R.id.edit_login_password);

		initControls();
        FBinit();
		
		Global.registering_flag = false;
	}

	void initControls()
	{
		/*
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_login_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickBack();
			}
		});
		*/
		
		Button btn_login = (Button)findViewById(R.id.btn_login_login);
		Button btn_register = (Button)findViewById(R.id.btn_login_register);
		Button btn_facebook = (Button)findViewById(R.id.btn_login_facebook);
				
		btn_login.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//onSuccessLogin();
				onClickLogin();
			}
		});
		
		btn_register.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickRegister();
			}
		});
		
		btn_facebook.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickFacebook();
			}
		});
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
	
	private void onClickLogin()
	{
		if ( txt_userid.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_userid));
			return;
		}
		
		if ( txt_password.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_password));
			return;
		}

		m_reqType = REQ_TYPE.REQ_LOGIN;
		new LoadResponseThread(LoginActivity.this).start();
	}
	
	private void onClickRegister()
	{
		Intent register_activity = new Intent(this, RegisterActivity.class);
		startActivity(register_activity);	
		finish();
	}
	
	private void onClickFacebook()
	{
		m_reqType = REQ_TYPE.REQ_FACEBOOK;
        Utility.mFacebook.authorize(LoginActivity.this, PERMS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
	}
	
	private void onSuccessLogin()
	{
		Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}

    public void FBinit() {
        Utility.mFacebook = new Facebook(Global.FBAPP_ID);
        Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);

        SessionStore.restore(Utility.mFacebook, this);
        SessionEvents.addAuthListener(new SessionEvents.AuthListener() {
            @Override
            public void onAuthSucceed() {

            }

            @Override
            public void onAuthFail(String error) {

            }
        });

        mHandler = new Handler();

        mDialog =  new ProgressDialog(this);
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setMessage(getString(R.string.loading));
    }

    private class LoginDialogListener implements Facebook.DialogListener {

        public void onComplete(Bundle values) {
            new loginFacebook().execute();
        }

        public void onFacebookError(FacebookError e) {}
        public void onError(DialogError e) {}
        public void onCancel() {}
        public void onDismiss(FbDialog f) {}
    }

    public class loginFacebook extends AsyncTask<Void, Integer, Void> {

        protected void onPreExecute() {
            mDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Utility.mAsyncRunner.request("me", new AsyncFacebookRunner.RequestListener() {

                public void onMalformedURLException(MalformedURLException e,Object state) {
                    mDialog.hide();
                }

                public void onIOException(IOException e, Object state) {
                    mDialog.hide();
                }

                public void onFileNotFoundException(FileNotFoundException e,Object state) {
                    mDialog.hide();
                }

                public void onFacebookError(FacebookError e, Object state) {
                    mDialog.hide();
                }

                public void onComplete(final String response, Object state) {

                    mHandler.post(new Runnable() {
                        public void run() {
                            try {
                                JSONObject json = new JSONObject(response);
                                FB_FirstName = json.getString("first_name");
                                FB_LastName = json.getString("last_name");
                                FB_Email = json.getString("email");

                                m_reqType = REQ_TYPE.REQ_FACEBOOK;
                                new LoadResponseThread(LoginActivity.this).start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });

            return null;
        }

        protected void onPostExecute(Void responseData) {}
        @Override
        public void onProgressUpdate(Integer... values) {}

    }
	
	public void refreshUI() {
		super.refreshUI();

		if (m_reqType == REQ_TYPE.REQ_LOGIN) {
			if (m_nResponse == ResponseRet.RET_SUCCESS) {

				SharedPreferences pref = getSharedPreferences(Global.STR_SETTING, 0);
				SharedPreferences.Editor edit = pref.edit();
				edit.putBoolean(Global.STR_LOGIN, true);
				edit.putString(Global.STR_USERID, Global.Cur_UserLoginId);
				edit.putString(Global.STR_PASSWORD, Global.Cur_UserPassword);
				edit.commit();

				onSuccessLogin();
			}
		}
		else if (m_reqType == REQ_TYPE.REQ_FACEBOOK)
		{
			if (m_nResponse == ResponseRet.RET_SUCCESS) {

				SharedPreferences pref = getSharedPreferences(Global.STR_SETTING, 0);
				SharedPreferences.Editor edit = pref.edit();
				edit.putBoolean(Global.STR_LOGIN, true);
				edit.putString(Global.STR_USERID, Global.Cur_UserLoginId);
				edit.putString(Global.STR_PASSWORD, Global.Cur_UserPassword);
				edit.commit();

                Global.registering_flag = true;

                Global.Cur_UserName = "";
                Global.Cur_UserSex = 1;
                Global.Cur_UserBirthday = "1960-1-1";
                Global.Cur_UserHeight = 170;
                Global.Cur_UserWeight = 60;
                Global.Cur_UserWaistline = 70;
                Global.Cur_UserWeightTarget = 60;
                Global.Cur_UserWaistlineTarget = 70;
                Global.Cur_UserLevel = 0;
                Global.Cur_UserImage = "";

                Intent sex_activity = new Intent(this, SexActivity.class);
                startActivity(sex_activity);
                //onSuccessLogin();
            }
		}
	}
	
	public void getResponseJSON() {
		try {
			if (m_reqType == REQ_TYPE.REQ_LOGIN) {
				m_nResponse = ResponseRet.RET_SUCCESS;

				String strRequest = HttpConnUsingJSON.REQ_LOGIN;
				strRequest += "?userid=" + EncodeToUTF8(txt_userid.getText().toString());
				strRequest += "&password=" + txt_password.getText().toString();


				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);

				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}

				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {

					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);

					Global.Cur_UserId = dataObject.getInt("uid");
					Global.Cur_UserName = dataObject.getString("name");
					Global.Cur_UserSex = dataObject.getInt("sex");
					Global.Cur_UserAge = dataObject.getInt("age");
					Global.Cur_UserBirthday = dataObject.getString("birthday");
					Global.Cur_UserHeight = (int) dataObject.getInt("high");
					Global.Cur_UserLevel = dataObject.getInt("level");
					Global.Cur_UserWaistline = (float) dataObject.getDouble("waist_now");
					Global.Cur_UserWaistlineTarget = (float) dataObject.getDouble("waist_target");
					Global.Cur_UserWeight = (float) dataObject.getDouble("weight_now");
					Global.Cur_UserWeightTarget = (float) dataObject.getDouble("weight_target");
					Global.Cur_UserImage = dataObject.getString("image");
					if (Global.Cur_UserImage.equals("null"))
						Global.Cur_UserImage = "";
					Global.Cur_UserPassword = txt_password.getText().toString();
					Global.Cur_UserLoginId = txt_userid.getText().toString();
				}
			}
			else if (m_reqType == REQ_TYPE.REQ_FACEBOOK)
			{
				m_nResponse = ResponseRet.RET_SUCCESS;

				String strRequest = HttpConnUsingJSON.REQ_FBREGISTER;

				JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);

				if (response == null) {
                    mDialog.dismiss();
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}

				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
                    mDialog.dismiss();

					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);

                    Global.Cur_UserId = dataObject.getInt("uid");
                    Global.Cur_UserLoginId = FB_Email;
                    Global.Cur_UserPassword = "123456";
				}
                else if (m_nResponse == ResponseRet.RET_SUCCFBLOGIN) {
                    mDialog.dismiss();

                    SessionStore.save(Utility.mFacebook, LoginActivity.this);

                    JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);

                    Global.Cur_UserId = dataObject.getInt("uid");
                    Global.Cur_UserLoginId = dataObject.getString("userid");
                    Global.Cur_UserPassword = "123456";

                    SharedPreferences pref = getSharedPreferences(Global.STR_SETTING, 0);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putBoolean(Global.STR_LOGIN, true);
                    edit.putString(Global.STR_USERID, Global.Cur_UserLoginId);
                    edit.putString(Global.STR_PASSWORD, Global.Cur_UserPassword);
                    edit.commit();

                    onSuccessLogin();
                }
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
	
	
	public JSONObject makeRequestJSON() throws JSONException {
		if (m_reqType == REQ_TYPE.REQ_LOGIN)
			return null;
		else if (m_reqType == REQ_TYPE.REQ_FACEBOOK)
		{
			JSONObject requestObj = new JSONObject();

            requestObj.put("userid", FB_Email);
            requestObj.put("password", "123456");

			return requestObj;
		}

		return null;
	}
}
