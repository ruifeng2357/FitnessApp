/* 健康*/
package com.damy.backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import com.google.android.youtube.player.YouTubeBaseActivity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.jiankang.BaseActivity;

public class HttpConnUsingJSON {
	
	//public final static String  BASE_URL = "http://192.168.3.181:8089";
	//public final static String  BASE_IMAGEURL = "http://192.168.3.181:8012/";

    public final static String BASE_URL = "http://54.201.133.228:8080";
    public final static String  BASE_IMAGEURL = "http://54.201.133.228/";

	public final static String  REQ_PREFIX = BASE_URL + "/Service.svc/";
	public final static String  REQ_LOGIN = REQ_PREFIX + "Login";
	public final static String  REQ_REGISTER = REQ_PREFIX + "InsertUser";
    public final static String  REQ_FBREGISTER = REQ_PREFIX + "InsertFBUser";
	public final static String  REQ_EDITUSERINFO = REQ_PREFIX + "EditUserInfo";
	public final static String  REQ_GETBMIBMRDESCRIPTION = REQ_PREFIX + "GetBMIBMRDescription";
	public final static String  REQ_GETPHYSIOLOGYINFO = REQ_PREFIX + "GetPhysiologyInfo";
	public final static String  REQ_SETPHYSIOLOGYINFO = REQ_PREFIX + "InsertPhysiologyInfo";
	public final static String  REQ_GETWEIGHTINFO = REQ_PREFIX + "GetWeightInfo";
	public final static String  REQ_SETWEIGHTINFO = REQ_PREFIX + "InsertWeightInfo";
	public final static String  REQ_GETQALIST = REQ_PREFIX + "GetQAList";
	public final static String  REQ_GETQADETAIL = REQ_PREFIX + "GetQADetail";
	public final static String  REQ_INSERTQUESTION = REQ_PREFIX + "InsertQuestion";
	public final static String  REQ_GETLEVELDESCRIPTION = REQ_PREFIX + "GetLevelDescription";
	public final static String  REQ_GETWEIGHTLIST = REQ_PREFIX + "GetWeightList";
	public final static String  REQ_GETNEWSLIST = REQ_PREFIX + "GetNewsList";
	public final static String  REQ_GETHALTHVIDEOMAINLIST = REQ_PREFIX + "GetHealthVideoMainList";
	public final static String  REQ_GetNEWSINFO = REQ_PREFIX + "GetNewsInfo";
	public final static String  REQ_GETHALTHVIDEOSUBLIST = REQ_PREFIX + "GetHealthVideoSubList";
	public final static String  REQ_GETFOODMAINLIST = REQ_PREFIX + "GetMainFoodList";
	public final static String  REQ_GETFOODSUBLIST = REQ_PREFIX + "GetSubFoodList";
	public final static String  REQ_EDITUSER = REQ_PREFIX + "EditUser";
	public final static String  REQ_GETSPORTRECORDLIST = REQ_PREFIX + "GetSportRecordList";
	public final static String  REQ_GETSPORTLIST = REQ_PREFIX + "GetSportList";
	public final static String  REQ_INSERTSPORTRECORD = REQ_PREFIX + "InsertSportRecord";
	public final static String  REQ_UPDATESPORTRECORD = REQ_PREFIX + "UpdateSportRecord";
	public final static String  REQ_DELETESPORTRECORD = REQ_PREFIX + "DeleteSportRecord";
	public final static String  REQ_GETFOODRECORD = REQ_PREFIX + "GetFoodRecord";
	public final static String  REQ_GETMEALRECORD = REQ_PREFIX + "GetMealRecord";
	public final static String  REQ_INSERTFOODINFO = REQ_PREFIX + "InsertFoodInfo";
	public final static String  REQ_GETFOODRECORDINFO = REQ_PREFIX + "GetFoodRecordInfo";
	public final static String  REQ_GETSUBFOODINFO = REQ_PREFIX + "GetSubFoodInfo";
	public final static String  REQ_UPDATEFOODRECORDINFO = REQ_PREFIX + "UpdateFoodRecordInfo";
	public final static String  REQ_DELETEFOODRECORDINFO = REQ_PREFIX + "DeleteFoodRecordInfo";
	public final static String  REQ_SETCALORY = REQ_PREFIX + "SetCalory";
	public final static String  REQ_GETFACEBOOKDATA = REQ_PREFIX + "GetFacebookData";
	public final static String  REQ_GETALARMLIST = REQ_PREFIX + "GetAlarmList";
	public final static String  REQ_SUBMITALARM = REQ_PREFIX + "SubmitAlarm";
	public final static String  REQ_SETALARM = REQ_PREFIX + "SetAlarm";
	public final static String  REQ_DELETEALARM = REQ_PREFIX + "DeleteAlarm";
	public final static String  REQ_CHANGEPASSWORD = REQ_PREFIX + "ChangePassword";
	
	private BaseActivity mActivity;
    private YouTubeBaseActivity mYouTubeActivity;

	
	public HttpConnUsingJSON(BaseActivity activity) {
		mActivity = activity;
	}

    public HttpConnUsingJSON(YouTubeBaseActivity activity) {
        mYouTubeActivity = activity;
    }

	public String getResponseData(String url) throws JSONException {
		String strResponse = "";
	    // Create a new HttpClient and Post Header
	    HttpParams myParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(myParams, 120000);
	    HttpConnectionParams.setSoTimeout(myParams, 120000);
	    HttpClient httpclient = new DefaultHttpClient(myParams);
	    String json = mActivity.makeRequestJSON().toString();

	    try {

	        HttpPost httppost = new HttpPost(url.toString());
	        httppost.setHeader("Content-Type", "application/json");

	        StringEntity se = new StringEntity(json, "utf-8"); 
	        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	        
	        httppost.setEntity(se); 

	        HttpResponse response = httpclient.execute(httppost);
	        strResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
	    } catch (ClientProtocolException e) {

	    } catch (IOException e) {
	    }
	    
	    return strResponse;
	}

	public JSONObject getGetJSONObject(String strURL) {
		JSONObject object = null;
		try {
	
			URL url = new URL(strURL);
			InputStream is = url.openStream();
			
			int nReadNum = 0;
			String strJSON = "";
            while (nReadNum != -1) {
            	byte[] buffer = new byte[4096];
            	
            	nReadNum = is.read(buffer);
            	if (nReadNum != -1) {
            		strJSON += new String(buffer, 0, nReadNum);
            	}
            }
            
			object = new JSONObject(strJSON);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return object;
	}

	public JSONObject getPostJSONObject(String strURL) {
		JSONObject object = null;
		try {
			String strJSON = getResponseData(strURL);
			object = new JSONObject(strJSON);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
}
