/* 健康*/
package com.damy.backend;

import android.app.ProgressDialog;
import android.os.Handler;

import com.damy.jiankang.BaseActivity;
import com.damy.jiankang.R;
import com.damy.jiankang.YouTubeFailureRecoveryActivity;
import com.google.android.youtube.player.YouTubeBaseActivity;

public class LoadResponseThread extends Thread{
	private ProgressDialog	mProgressDialg = null;
	private BaseActivity	mActivity = null;
    private YouTubeFailureRecoveryActivity mYouTubeActivity = null;
	private Handler 		mhandler = new Handler();
	
	public LoadResponseThread(BaseActivity activity){
		mActivity = activity;

		mProgressDialg = new ProgressDialog(mActivity);
		mProgressDialg.setCancelable(false);
		mProgressDialg.setCanceledOnTouchOutside(false);
		mProgressDialg.setMessage(mActivity.getResources().getString(R.string.common_waiting));
		mProgressDialg.show();
	}

    public LoadResponseThread(YouTubeFailureRecoveryActivity activity){
        mYouTubeActivity = activity;

        mProgressDialg = new ProgressDialog(mYouTubeActivity);
        mProgressDialg.setCancelable(false);
        mProgressDialg.setCanceledOnTouchOutside(false);
        mProgressDialg.setMessage(mYouTubeActivity.getResources().getString(R.string.common_waiting));
        mProgressDialg.show();
    }

	public void run() {

        if (mActivity != null) {
            mActivity.getResponseJSON();

            mhandler.post(new Runnable() {
                @Override
                public void run() {
                    mProgressDialg.dismiss();

                    mActivity.refreshUI();
                }
            });
        }

        if (mYouTubeActivity != null) {
            mYouTubeActivity.getResponseJSON();

            mhandler.post(new Runnable() {
                @Override
                public void run() {
                    mProgressDialg.dismiss();

                    mYouTubeActivity.refreshUI();
                }
            });
        }

    }
}
