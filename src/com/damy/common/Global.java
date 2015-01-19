/* 健康*/
package com.damy.common;

import java.util.Date;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Global {
	
	
	public final static String 			STR_SETTING = "SETTING";
	public final static String 			STR_LOGIN = "LOGIN";
	public final static String 			STR_USERID = "USERID";
	public final static String 			STR_PASSWORD = "PASSWORD";
	
	public static long 					Cur_UserId = 0;
	public static String                Cur_UserName = "";
	public static int                   Cur_UserSex = -1;
	public static String				Cur_UserBirthday = "";
	public static int					Cur_UserAge = 0;
	public static float					Cur_UserHeight = -1;
	public static float					Cur_UserWeight = -1;
	public static float					Cur_UserWaistline = -1;
	public static float					Cur_UserWeightTarget = -1;
	public static float					Cur_UserWaistlineTarget = -1;
	public static int					Cur_UserLevel = -1;
	public static String                Cur_UserPassword = "";
	public static String                Cur_UserImage = "";
	public static String                Cur_UserLoginId = "";
	
	public static Date					SportRecord_CurDate;
	public static Date					FoodRecord_CurDate;
	public static int					FoodRecord_CurType;
	public static int					AlarmSound_CurSelSound;
	
	public static String				Cur_FileExplorer_SelFile = "";
	
	public static Boolean               registering_flag = false;
	
	public static int 					PAGE_SIZE = 10;	
	
	public static ImageLoader imageLoader = ImageLoader.getInstance();
	
	public static DisplayImageOptions 	options;
    public static DisplayImageOptions 	sport_options;
    public static DisplayImageOptions 	food_options;
    public static DisplayImageOptions 	video_options;

    //public static final String FBAPP_ID = "588065704652272"; // Facebook app Id
    public static final String FBAPP_ID = "847833575257516"; // Facebook app Id
    public static String FB_access_token;
}

