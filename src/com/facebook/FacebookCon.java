package com.facebook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.AsyncFacebookRunner.RequestListener;
import com.facebook.Facebook.DialogListener;

public class FacebookCon extends Activity {

	public static final int LOGIN = Menu.FIRST;
	public static final int GET_EVENTS = Menu.FIRST + 1;
	public static final int GET_ID = Menu.FIRST + 2;
	public static final int GET_FRIENDSLIST = Menu.FIRST + 3;
	public static final int UPLOAD_PHOTOS = Menu.FIRST + 4;
	public static final String APP_ID = "171282709596690";
	public static final String TAG = "FACEBOOK CONNECT";
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;
	private static final String[] PERMS = new String[] { "publish_stream",
			"offline_access", "user_events", "email", "user_about_me",
			"read_friendlists", "user_photos" };
	private TextView mText;
	private Handler mHandler = new Handler();
	private String userID = null;
	private ArrayList<FbEvent> events = new ArrayList<FbEvent>();
	private LinearLayout eventLayout;
	String names = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (APP_ID == null || APP_ID.equals("")) {
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be "
					+ "specified before running");
		}

		// setup the content view
		initLayout();
		mText.setText("Login Please");

		// setup the facebook session
		mFacebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);

	}

	protected void initLayout() {

		// root view - GRN
		LinearLayout rootView = new LinearLayout(this.getApplicationContext());
		rootView.setOrientation(LinearLayout.VERTICAL);

		this.mText = new TextView(this.getApplicationContext());
		this.mText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		rootView.addView(this.mText);

		this.eventLayout = new LinearLayout(this.getApplicationContext());
		this.eventLayout.setOrientation(LinearLayout.VERTICAL);
		ScrollView sv_obj = new ScrollView(this.getApplicationContext());
		sv_obj.addView(this.eventLayout);
		rootView.addView(sv_obj);
		this.setContentView(rootView);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, FacebookCon.LOGIN, Menu.NONE, "Login Text");
		menu.add(Menu.NONE, FacebookCon.GET_EVENTS, Menu.NONE, "Get Events");
		menu.add(Menu.NONE, FacebookCon.GET_ID, Menu.NONE, "Get UserID");
		menu.add(Menu.NONE, FacebookCon.GET_FRIENDSLIST, Menu.NONE,
				"Get FriendsList");
		menu.add(Menu.NONE, FacebookCon.UPLOAD_PHOTOS, Menu.NONE,
				"Upload PHOTOS");
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem loginItem = menu.findItem(Menu.FIRST);
		MenuItem getEvents = menu.findItem(Menu.FIRST + 1);
		MenuItem getID = menu.findItem(FacebookCon.GET_ID);
		MenuItem getFriendsList = menu.findItem(Menu.FIRST + 3);
		MenuItem uploadphotos = menu.findItem(Menu.FIRST + 4);

		if (mFacebook.isSessionValid()) {
			loginItem.setTitle("Logout");
			getID.setEnabled(true);
			if (userID != null) {
				getEvents.setEnabled(true);
				getFriendsList.setEnabled(true);
				uploadphotos.setEnabled(true);
			} else {
				getEvents.setEnabled(false);
			}

		} else {
			loginItem.setTitle("Login");
			getEvents.setEnabled(false);
			getID.setEnabled(false);
			getFriendsList.setEnabled(false);
			uploadphotos.setEnabled(false);
		}
		loginItem.setEnabled(true);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case FacebookCon.LOGIN:
			if (mFacebook.isSessionValid()) {
				mText.setText("Logging out...");
				AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(
						mFacebook);
				asyncRunner.logout(this, new LogoutRequestListener());
			} else {
				// mFacebook.authorize(this, PERMS, new LoginDialogListener());
				mFacebook.authorize(this, PERMS, Facebook.FORCE_DIALOG_AUTH, // avoids
																				// SSO
						new LoginDialogListener());
			}

			// //Use Below COde

			/*
			 * if (mFacebook.isSessionValid()) { AsyncFacebookRunner asyncRunner
			 * = new AsyncFacebookRunner(mFacebook);
			 * asyncRunner.logout(LoginScreen.this, new
			 * LogoutRequestListener()); } else { mFacebook.authorize(
			 * LoginScreen.this, PERMS, Facebook.FORCE_DIALOG_AUTH, // avoids
			 * SSO new LoginDialogListener()); }
			 */
			break;
		case FacebookCon.GET_EVENTS:
			this.mAsyncRunner.request("me/events", new EventRequestListener());
			break;
		case FacebookCon.GET_ID:
			mAsyncRunner.request("me", new IDRequestListener());
			break;
		case FacebookCon.GET_FRIENDSLIST:
			mAsyncRunner.request("me/friends", new FriendRequestListener());
			break;
		case FacebookCon.UPLOAD_PHOTOS:
			byte[] data = null;
			File sdcard = Environment.getExternalStorageDirectory();
			String ImageName = "Sunset.jpg";
			File file = new File(sdcard, ImageName);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}
			/*
			 * Uri uri = Uri.fromFile(new
			 * File(Environment.getExternalStorageDirectory(), "Sunset.jpg"));
			 * ContentResolver cr = getContentResolver(); InputStream fis=null;
			 * try { fis = cr.openInputStream(uri); } catch
			 * (FileNotFoundException e) {
			 * 
			 * e.printStackTrace(); }
			 */
			Bitmap bi = BitmapFactory.decodeStream(fis);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			data = baos.toByteArray();

			/*
			 * Bitmap temp =
			 * BitmapFactory.decodeResource(getResources(),R.drawable
			 * .facebook_icon); ByteArrayOutputStream baos = new
			 * ByteArrayOutputStream();
			 * temp.compress(Bitmap.CompressFormat.JPEG, 100, baos); data =
			 * baos.toByteArray();
			 */

			if (data != null) {
				Log.d("data null", "data null");

				Bundle params = new Bundle();
				// params.putByteArray(Facebook.TOKEN,
				// "accessToken".getBytes());
				// params.putString("method", "photos.upload");
				// params.putString("message", "picture caption");
				params.putByteArray("message", "picture caption".getBytes());
				params.putByteArray("picture", data);

				mAsyncRunner.request("me/photos", params, "POST",
						new UploadPhotoListener(), null);
			}
		default:
			return false;
		}
		return true;
	}

	private class IDRequestListener implements RequestListener {

		@Override
		public void onComplete(String response, Object state) {
			try {
				// process the response here: executed in background thread
				Log.d(TAG, "Response: " + response.toString());
				JSONObject json = Util.parseJson(response);
				final String id = json.getString("id");

				// then post the processed result back to the UI thread
				// if we do not do this, an runtime exception will be generated
				// e.g. "CalledFromWrongThreadException: Only the original
				// thread that created a view hierarchy can touch its views."
				FacebookCon.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						userID = id;
						mText.setText("Hello there, " + id + "!");
					}
				});
			} catch (JSONException e) {
				Log.w(TAG, "JSON Error in response");
			} catch (FacebookError e) {
				Log.w(TAG, "Facebook Error: " + e.getMessage());
			}
		}

		@Override
		public void onIOException(IOException e, Object state) {

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {

		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {

		}

	}

	private class FriendRequestListener implements RequestListener {

		@Override
		public void onComplete(String response, Object state) {
			try {
				final JSONObject json = new JSONObject(response);
				JSONArray d = json.getJSONArray("data");

				for (int i = 0; i < d.length(); i++) {
					JSONObject event = d.getJSONObject(i);

					names = names + event.getString("name") + "\n";

				}

				// then post the processed result back to the UI thread
				// if we do not do this, an runtime exception will be generated
				// e.g. "CalledFromWrongThreadException: Only the original
				// thread that created a view hierarchy can touch its views."
				FacebookCon.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mText.setText(names);
					}
				});
			} catch (JSONException e) {
				Log.w(TAG, "JSON Error in response");
			}
		}

		@Override
		public void onIOException(IOException e, Object state) {

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {

		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {

		}

	}

	private class UploadPhotoListener implements RequestListener {

		@Override
		public void onComplete(String response, Object state) {
			Log.d("Inside complete", "complete");
			/*
			 * try { final JSONObject json = new JSONObject(response); JSONArray
			 * d = json.getJSONArray("data");
			 * 
			 * for (int i = 0; i < d.length(); i++) { JSONObject event =
			 * d.getJSONObject(i);
			 * 
			 * names = names+ event.getString("name")+"\n";
			 * 
			 * 
			 * }
			 * 
			 * // then post the processed result back to the UI thread // if we
			 * do not do this, an runtime exception will be generated // e.g.
			 * "CalledFromWrongThreadException: Only the original // thread that
			 * created a view hierarchy can touch its views."
			 * FacebookCon.this.runOnUiThread(new Runnable() { public void run()
			 * { mText.setText(names); } }); } catch (JSONException e) {
			 * Log.w(TAG, "JSON Error in response"); }
			 */}

		@Override
		public void onIOException(IOException e, Object state) {

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {

		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {

		}

	}

	private class EventRequestListener implements RequestListener {

		@Override
		public void onComplete(String response, Object state) {
			try {
				// process the response here: executed in background thread
				Log.d(TAG, "Response: " + response.toString());
				final JSONObject json = new JSONObject(response);
				JSONArray d = json.getJSONArray("data");

				for (int i = 0; i < d.length(); i++) {
					JSONObject event = d.getJSONObject(i);
					FbEvent newEvent = new FbEvent(event.getString("id"),
							event.getString("name"),
							event.getString("start_time"),
							event.getString("end_time"),
							event.getString("location"));
					events.add(newEvent);

				}

				// then post the processed result back to the UI thread
				// if we do not do this, an runtime exception will be generated
				// e.g. "CalledFromWrongThreadException: Only the original
				// thread that created a view hierarchy can touch its views."
				FacebookCon.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (FbEvent event : events) {
							TextView view = new TextView(
									getApplicationContext());
							view.setText(event.getTitle());
							view.setTextSize(16);

							eventLayout.addView(view);
						}
					}
				});
			} catch (JSONException e) {
				Log.w(TAG, "JSON Error in response");
			}
		}

		@Override
		public void onIOException(IOException e, Object state) {

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {

		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {

		}

	}

	private class LoginDialogListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			mText.setText("Facebook login successful. Press Menu...");
		}

		@Override
		public void onFacebookError(FacebookError e) {

		}

		@Override
		public void onError(DialogError e) {

		}

		@Override
		public void onCancel() {

		}

		@Override
		public void onDismiss(FbDialog f) {

		}

	}

	private class LogoutRequestListener implements RequestListener {

		@Override
		public void onComplete(String response, Object state) {

			// Dispatch on its own thread
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mText.setText("Logged out");
				}
			});
		}

		@Override
		public void onIOException(IOException e, Object state) {

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {

		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}
}