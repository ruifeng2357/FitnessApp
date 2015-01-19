/* 健康*/
package com.damy.jiankang;

import java.util.ArrayList;

import com.google.android.youtube.player.YouTubeInitializationResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeBaseActivity;


import com.damy.Utils.PullToRefreshListView;
import com.damy.adapters.HealthVideoMainAdapter;
import com.damy.adapters.NewsAdapter;
import com.damy.adapters.QuestionAdapter;
import com.damy.adapters.WeightListAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STHealthVideoMainInfo;
import com.damy.datatypes.STNewsInfo;


public class HealthSubActivity extends YouTubeFailureRecoveryActivity implements MyActivityMethods {

	public static final String MAIN_ID = "MAINID";
	public static final String SUB_ID = "SUBID";
	public static final String SUB_URL = "SUBURL";
	public static final String SUB_TITLE = "SUBTITLE";
	public static final String MAIN_TITLE = "MAINTITLE";

    private ImageButton m_imgbtnBack;
	private PullToRefreshListView		m_lvBaseListView;
	private RelativeLayout rl_news;
	private RelativeLayout rl_health;
	private RelativeLayout rl_news_border;
	private RelativeLayout rl_health_border;
	private TextView       txt_news;
	private TextView       txt_health;
	
	private ArrayList<STHealthVideoMainInfo> 	m_BaseHealthList;
	private ArrayList<STNewsInfo> 	            m_BaseNewsList;
	private HealthVideoMainAdapter				m_BaseHealthAdapter = null;
	private NewsAdapter				            m_BaseNewsAdapter = null;
	private ListView					mRealListView;

	private int							m_nCurPageNumber = 1;
	private int							main_id = 0;
	private String						main_title = "";
	private int							sub_id = 0;
	private String                      url = "";
	private String                      title = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_healthsub);
		main_id = getIntent().getIntExtra(MAIN_ID, 0);
		sub_id = getIntent().getIntExtra(SUB_ID, 0);
		url = getIntent().getStringExtra(SUB_URL);
		title = getIntent().getStringExtra(SUB_TITLE);
		main_title = getIntent().getStringExtra(MAIN_TITLE);
        initActivity(R.id.rl_healthsub);
        initControl();
        initHandler();        
        
        
        new LoadResponseThread(HealthSubActivity.this).start();        
	}

    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(url);
        }
    }

    @Override
    public void initControl() {
        m_imgbtnBack = (ImageButton) findViewById(R.id.img_healthsub_back);    
        TextView txt_title = (TextView)findViewById(R.id.lbl_health_title);
        txt_title.setText(title);

        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(YouTubeConfig.DEVELOPER_KEY, this);
    }

    @Override
    public void initHandler() {
        m_imgbtnBack.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	onClickBack();        		                
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
    
    private void onClickBack() {
    	
    	Intent sublist_activity = new Intent(this, HealthSubListActivity.class);
    	sublist_activity.putExtra(HealthSubListActivity.MAIN_ID, main_id);
    	sublist_activity.putExtra(HealthSubListActivity.MAIN_TITLE, main_title);
		startActivity(sublist_activity);	
		finish();
		
	}

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

}