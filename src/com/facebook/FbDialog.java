/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Facebook.DialogListener;

@SuppressLint("SetJavaScriptEnabled")
public class FbDialog extends Dialog {

	static final int FB_BLUE = 0xFF6D84B4;
	static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT);
	static final int MARGIN = 4;
	static final int PADDING = 2;
	static final String DISPLAY_STRING = "touch";
	static final String FB_ICON = "icon.png";

	private String mUrl;
	private DialogListener mListener;

	private WebView mWebView;
	private RelativeLayout mContent;
	private RelativeLayout loadingLayout;

	public FbDialog(Context context, String url, DialogListener listener) {

		super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
		mUrl = url;
		mListener = listener;
	}

	DialogInterface.OnCancelListener listener = new OnCancelListener() {

		@Override
		public void onCancel(DialogInterface dialog) {
			mListener.onCancel();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContent = new RelativeLayout(getContext());
		setUpWebView();
		setUpLoading();
		addContentView(mContent, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		this.setOnCancelListener(listener);
	}

	private void setUpWebView() {

		Log.v("FACEBOOK", mUrl);

		mWebView = new WebView(getContext());

		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		mWebView.setId(8923526);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setWebViewClient(new FbDialog.FbWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(mUrl);
		mWebView.setLayoutParams(param);
		mContent.addView(mWebView);
	}

	private void setUpLoading() {
		loadingLayout = new RelativeLayout(getContext());
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 0, 0, 50);
		layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, mWebView.getId());
		//--loadingLayout.setBackgroundResource(R.drawable.loadingbar);
		loadingLayout.setLayoutParams(layoutParams);

		TextView loadingTxt = new TextView(getContext());
		loadingTxt.setId(1234567);
		loadingTxt.setText("Loading ...");
		loadingTxt.setTextColor(Color.GRAY);
		loadingTxt.setTextSize(15);
		RelativeLayout.LayoutParams txtParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		txtParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		loadingTxt.setLayoutParams(txtParams);

		ProgressBar loadingBar = new ProgressBar(getContext(), null,
				android.R.attr.progressBarStyleSmall);
		RelativeLayout.LayoutParams barParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		barParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		barParams.addRule(RelativeLayout.LEFT_OF, loadingTxt.getId());
		barParams.setMargins(0, 0, 10, 0);
		loadingBar.setLayoutParams(barParams);
		loadingLayout.addView(loadingTxt);
		loadingLayout.addView(loadingBar);
		loadingLayout.setVisibility(View.INVISIBLE);
		mContent.addView(loadingLayout);
	}

	private class FbWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d("Facebook-WebView", "Redirect URL: " + url);
			if (url.startsWith(Facebook.REDIRECT_URI)) {
				Bundle values = Util.parseUrl(url);

				String error = values.getString("error");
				if (error == null) {
					error = values.getString("error_type");
				}

				if (error == null) {
					mListener.onComplete(values);
				} else if (error.equals("access_denied")
						|| error.equals("OAuthAccessDeniedException")) {
					mListener.onCancel();
				} else {
					mListener.onFacebookError(new FacebookError(error));
				}

				FbDialog.this.dismiss();
				return true;
			} else if (url.startsWith(Facebook.CANCEL_URI)) {
				mListener.onCancel();
				FbDialog.this.dismiss();
				return true;
			} else if (url.contains(DISPLAY_STRING)) {
				return false;
			}
			// launch non-dialog URLs in a full browser
			getContext().startActivity(
					new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			Log.e("FACEBOOK", "ERROR code :: " + errorCode + " DESC :: "
					+ description);
			loadingLayout.setVisibility(View.GONE);
			mContent.setEnabled(true);
			mContent.setClickable(true);
			mContent.setOnTouchListener(null);
			mWebView.setOnTouchListener(null);
			mListener.onError(new DialogError(description, errorCode,
					failingUrl));
			FbDialog.this.dismiss();

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d("Facebook-WebView", "Webview loading URL: " + url);
			loadingLayout.setVisibility(View.VISIBLE);
			mContent.setEnabled(false);
			mContent.setClickable(false);
			mContent.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
			mWebView.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			// String title = mWebView.getTitle();
			loadingLayout.setVisibility(View.GONE);
			mContent.setEnabled(true);
			mContent.setClickable(true);
			mContent.setOnTouchListener(null);
			mWebView.setOnTouchListener(null);
			Log.e("FACEBOOK", "onPageFinished code :: " + url);
		}

	}
}
