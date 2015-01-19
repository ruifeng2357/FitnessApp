/* 健康*/
package com.damy.jiankang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.damy.Utils.ResolutionSet;
import com.damy.common.Global;
import com.facebook.SessionEvents;
import com.facebook.SessionStore;
import com.facebook.Util;
import com.facebook.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.IOException;
import java.util.Date;


public class MainActivity extends BaseActivity implements MyActivityMethods {

    ImageButton m_imgbtnMenu;

    ImageButton m_imgbtnMeiRiTiZhong;
    ImageButton m_imgbtnShengLiZiLiao;
    ImageButton m_imgbtnYunDongJiLu;
    ImageButton m_imgbtnYinShiZiLiao;
    ImageButton m_imgbtnWeiJiaoZiXun;
    ImageButton m_imgbtnXianShangQA;
    
    RelativeLayout rl_header;
    RelativeLayout rl_menulayer;
    RelativeLayout rl_masklayer;
    RelativeLayout rl_masklayer1;
    
    RelativeLayout rl_menu1;
    RelativeLayout rl_menu2;
    RelativeLayout rl_menu3;
    RelativeLayout rl_menu4;

    boolean isMenuShow;

    Thread thread = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        initActivity(R.id.rlMain);
        
        
        Global.options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory()
		.cacheOnDisc()
		.build();
        
        Global.sport_options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.common_sport)
		.showImageForEmptyUri(R.drawable.common_sport)
		.showImageOnFail(R.drawable.common_sport)
		.cacheInMemory()
		.cacheOnDisc()
		.build();
        
        Global.food_options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.food_sample)
		.showImageForEmptyUri(R.drawable.food_sample)
		.showImageOnFail(R.drawable.food_sample)
		.cacheInMemory()
		.cacheOnDisc()
		.build();
        
        Global.video_options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.video_sample)
		.showImageForEmptyUri(R.drawable.video_sample)
		.showImageOnFail(R.drawable.video_sample)
		.cacheInMemory()
		.cacheOnDisc()
		.build();
        
        Global.registering_flag = false;
        initControl();
        initHandler();
	}

    @Override
    public void initControl() {
        m_imgbtnMenu = (ImageButton) findViewById(R.id.imgbtnMenu);
        m_imgbtnMeiRiTiZhong = (ImageButton) findViewById(R.id.imgbtnMeiRiTiZhong);
        m_imgbtnShengLiZiLiao = (ImageButton) findViewById(R.id.imgbtnShengLiZiLiao);
        m_imgbtnYunDongJiLu = (ImageButton) findViewById(R.id.imgbtnYunDongJiLu);
        m_imgbtnYinShiZiLiao = (ImageButton) findViewById(R.id.imgbtnYinShiZiLiao);
        m_imgbtnWeiJiaoZiXun = (ImageButton) findViewById(R.id.imgbtnWeiJiaoZiXun);
        m_imgbtnXianShangQA = (ImageButton) findViewById(R.id.imgbtnXianShangQA);
        
        rl_header = (RelativeLayout)findViewById(R.id.rlHeader);
        rl_menulayer = (RelativeLayout)findViewById(R.id.rl_menu);
        rl_masklayer = (RelativeLayout)findViewById(R.id.rl_mask);
        rl_masklayer1 = (RelativeLayout)findViewById(R.id.rl_mask1);
        
        rl_menu1 = (RelativeLayout)findViewById(R.id.rl_menu_persondata);
        rl_menu2 = (RelativeLayout)findViewById(R.id.rl_menu_recorddata);
        rl_menu3 = (RelativeLayout)findViewById(R.id.rl_menu_setting);
        rl_menu4 = (RelativeLayout)findViewById(R.id.rl_menu_logout);
        
        initMenuPan();
    }

    @Override
    public void initHandler() {
        m_imgbtnMenu.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
	             showMenuPan(500, null);
            }
        });
        
        rl_masklayer.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	hideMenuPan(500, null);
            }
        });
        
        rl_masklayer1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	hideMenuPan(500, null);
            }
        });

        m_imgbtnMeiRiTiZhong.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   Intent intent = new Intent(MainActivity.this, MeiRiTiZhongActivity.class);
                startActivity(intent);*/
            	Intent intent = new Intent(MainActivity.this, WeightinfoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        m_imgbtnShengLiZiLiao.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PhysiologyActivity.class);
                startActivity(intent);
                finish();
            }
        });

        m_imgbtnYunDongJiLu.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	Global.SportRecord_CurDate = new Date();
            	
            	if ( Global.SportRecord_CurDate.getHours() < 3 )
            		Global.SportRecord_CurDate.setDate(Global.SportRecord_CurDate.getDate() - 1);
            	
            	Intent intent = new Intent(MainActivity.this, SportRecordActivity.class);
                startActivity(intent);
            }
        });

        m_imgbtnYinShiZiLiao.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	Global.FoodRecord_CurDate = new Date();
            	
            	if ( Global.FoodRecord_CurDate.getHours() < 3 )
            		Global.FoodRecord_CurDate.setDate(Global.FoodRecord_CurDate.getDate() - 1);
            	
                Intent intent = new Intent(MainActivity.this, FoodRecordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        m_imgbtnWeiJiaoZiXun.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HealthVideoListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        m_imgbtnXianShangQA.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	Intent intent = new Intent(MainActivity.this, QuestionlistActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
        rl_menu1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	Intent intent = new Intent(MainActivity.this, MyInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
        rl_menu2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	Intent intent = new Intent(MainActivity.this, WeightInfoListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
        rl_menu3.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
        rl_menu4.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences(Global.STR_SETTING, 0);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(Global.STR_LOGIN, false);        
                edit.putString(Global.STR_USERID, "");
                edit.putString(Global.STR_PASSWORD, "");
                edit.commit();

                thread = new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (Utility.mFacebook != null) {
                                        SessionEvents.addLogoutListener(new SessionEvents.LogoutListener() {
                                            @Override
                                            public void onLogoutBegin() {
                                            }

                                            @Override
                                            public void onLogoutFinish() {
                                            }
                                        });
                                        Utility.mFacebook.logout(MainActivity.this);
                                    }

                                    SessionStore.clear(MainActivity.this);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );

                thread.start();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
    }
    
    
    public void initMenuPan()
	{
		float destPos = ResolutionSet.fYpro * (95 + 320);
		
		ObjectAnimator mover1 = ObjectAnimator.ofFloat(rl_menulayer, "translationY", 0f, -destPos);
		mover1.setDuration(0);
		
		AnimatorSet animatorSet = new AnimatorSet();
		
		animatorSet.play(mover1);
		animatorSet.start();
		
		rl_masklayer.setVisibility(View.INVISIBLE);
		rl_masklayer1.setVisibility(View.INVISIBLE);
		
		isMenuShow = false;
	}
	
	public void showMenuPan(int nDuration, Animator.AnimatorListener listener)
	{
		if (isMenuShow)
			return;
		
		float destPos = ResolutionSet.fYpro * (95 + 320);
		
		rl_masklayer.setVisibility(View.VISIBLE);
		rl_masklayer1.setVisibility(View.VISIBLE);
		
		ObjectAnimator mover1 = ObjectAnimator.ofFloat(rl_menulayer, "translationY", -destPos, 0f);
		mover1.setDuration(nDuration);
		
		AnimatorSet animatorSet = new AnimatorSet();
		
		if (listener != null)
			animatorSet.addListener(listener);
		
		animatorSet.play(mover1);
		animatorSet.start();
		
		isMenuShow = true;
	}
	
	public void hideMenuPan(int nDuration, Animator.AnimatorListener listener)
	{
		if (!isMenuShow)
			return;
		
		float destPos = ResolutionSet.fYpro * (95 + 320);
		
		ObjectAnimator mover1 = ObjectAnimator.ofFloat(rl_menulayer, "translationY", 0f, -destPos);
		mover1.setDuration(nDuration);
		
		AnimatorSet animatorSet = new AnimatorSet();
		
		if (listener != null)
			animatorSet.addListener(listener);
		
		animatorSet.play(mover1);
		animatorSet.start();
		
		rl_masklayer.setVisibility(View.INVISIBLE);
		rl_masklayer1.setVisibility(View.INVISIBLE);
		
		isMenuShow = false;
	}
}
