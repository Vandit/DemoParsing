package ng.com.police.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import ng.com.police.PoliceApp;
import ng.com.police.R;
import ng.com.police.common.ConfigureFuseLocation;
import ng.com.police.common.TwitterLogin;
import ng.com.police.common.Util;
import ng.com.police.fragment.GalleryDetailFragment;
import ng.com.police.fragment.GalleryFragment;
import ng.com.police.webservice.WSAddLike;
import ng.com.police.webservice.WebService;
import ng.com.service.PanicReportService;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.util.LruCache;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusShare;

public class BaseActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener {

	/**
	 * DrawerLayout for navigation Drawer
	 */
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ActionBar actionBar;
	private PoliceApp application;
	// Twitter related variables
	private TwitterLogin twitter;
	// Google Plus related variables
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private boolean isWriteLog = false;
	private SharedPreferences preferences;
	/*
	 * private ConnectionResult mConnectionResult; private File file = null;
	 */
	private String textToShare = "", imagePathToShare = "";
	// Facebook Related variables
	private final int FACEBOOK_LOGIN_CODE = 9001;
	private Session session;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private String fbMessageToShare = "";
	private File fbFileToShare = null;
	public static final List<String> PERMISSIONS_PUBLISH = Arrays.asList("publish_actions", "publish_stream");

	public final static String FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION = "POLICEAPP_FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION";
	private BaseActivityReceiver baseActivityReceiver = new BaseActivityReceiver();
	private final IntentFilter INTENT_FILTER = createIntentFilter();
	private String currentModuleTitle = "";
	private File gPlusFileToShare = null;

	private IntentFilter createIntentFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION);
		return filter;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		initializeComponent(savedInstanceState);

	}

	/**
	 * method for initializeComponent
	 * 
	 * @param savedInstanceState
	 */
	private void initializeComponent(final Bundle savedInstanceState) {
		preferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
		application = (PoliceApp) getApplicationContext();
		// Google plus initilization and check connected or not
		mPlusClient = application.getMplusClinet();

		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		try {
			session = Session.getActiveSession();
			if (session == null) {
				if (savedInstanceState != null) {
					session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
				}
				if (session == null) {
					session = new Session(this);
				}
				Session.setActiveSession(session);
				if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
					session.openForPublish(new Session.OpenRequest(this).setCallback(statusCallback).setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO).setPermissions(PERMISSIONS_PUBLISH).setRequestCode(FACEBOOK_LOGIN_CODE));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		actionBar.setIcon(null);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		setDrawer();
		registerBaseActivityReceiver();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (gPlusFileToShare != null && gPlusFileToShare.exists()) {
			gPlusFileToShare.delete();
			gPlusFileToShare = null;
		}
	}

	/**
	 * 
	 * method for set drawer and set icon of drawer
	 */
	private void setDrawer() {
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				displayTitle(currentModuleTitle);
			}

			public void onDrawerOpened(View drawerView) {
				SpannableString s = new SpannableString(getString(R.string.home_text_dashboard));
				s.setSpan(new TypefaceSpan(BaseActivity.this, "hv_bold"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				actionBar.setTitle(" " + s);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	/**
	 * method for close drawer.
	 */
	public void closeDrawer() {
		if (mDrawerLayout.isDrawerOpen(getSupportFragmentManager().findFragmentById(R.id.left_drawer).getView()))
			mDrawerLayout.closeDrawers();
	}

	/**
	 * method for set title of current Module open by User
	 * 
	 * @param title
	 */
	public void displayTitle(String title) {
		SpannableString s = new SpannableString(title);
		s.setSpan(new TypefaceSpan(this, "hv_bold"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(" " + s);
		currentModuleTitle = "" + s;
	}

	/**
	 * 
	 * @param isDisplay
	 */
	public void displayHomeAsUpEnable(boolean isDisplay) {
		if (isDisplay) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			mDrawerToggle.setDrawerIndicatorEnabled(false);
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		} else {
			mDrawerToggle.setDrawerIndicatorEnabled(true);
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		}
	}

	public void displayActionBar(boolean isDisplay) {
		if (isDisplay) {
			actionBar.show();
		} else {
			actionBar.hide();
		}
	}

	// method for write log
	public void writeLog(final String msg) {
		if (isWriteLog)
			Log.e(getClass().getName(), msg);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_actionbar, menu);
		menu.findItem(R.id.menu_actionbar_call).setVisible(true);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.menu_actionbar_call:
			callPanicReport();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}


	/**
	 * this class using for roboto font
	 * 
	 * @author indianic
	 * 
	 */
	private class TypefaceSpan extends MetricAffectingSpan {
		/** An <code>LruCache</code> for previously loaded typefaces. */
		private LruCache<String, Typeface> sTypefaceCache = new LruCache<String, Typeface>(12);

		private Typeface mTypeface;

		/**
		 * Load the {@link Typeface} and apply to a {@link Spannable}.
		 */
		public TypefaceSpan(Context context, String typefaceName) {
			mTypeface = sTypefaceCache.get(typefaceName);

			if (mTypeface == null) {
				mTypeface = Typeface.createFromAsset(context.getApplicationContext().getAssets(), String.format("%s.ttf", typefaceName));

				// Cache the loaded Typeface
				sTypefaceCache.put(typefaceName, mTypeface);
			}
		}

		@Override
		public void updateMeasureState(TextPaint p) {
			p.setTypeface(mTypeface);

			// Note: This flag is required for proper typeface rendering
			p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
		}

		@Override
		public void updateDrawState(TextPaint tp) {
			tp.setTypeface(mTypeface);

			// Note: This flag is required for proper typeface rendering
			tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
		}
	}

	protected void registerBaseActivityReceiver() {
		registerReceiver(baseActivityReceiver, INTENT_FILTER);
	}

	protected void unRegisterBaseActivityReceiver() {
		unregisterReceiver(baseActivityReceiver);
	}

	private class BaseActivityReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION)) {
				finish();
			}
		}
	}

	/**
	 * Start Region FaceBook
	 */

	private void publisStatus() {
		if (session.isOpened()) {

			final ProgressDialog mProgressDialog = ProgressDialog.show(BaseActivity.this, "", getString(R.string.common_loading_sharing));

			Bundle params = new Bundle();
			params.putString("message", fbMessageToShare);
			// params.putString("link", "www.google.com");

			Request.Callback callback = new Request.Callback() {
				public void onCompleted(com.facebook.Response response) {

					if (mProgressDialog != null && mProgressDialog.isShowing())
						mProgressDialog.cancel();

					String postId = null;
					if (response.getGraphObject() != null && response.getGraphObject().getInnerJSONObject() != null) {

						JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
						try {
							postId = graphResponse.getString("id");
						} catch (JSONException e) {
							writeLog("JSON error " + e.getMessage());
						}
					}
					FacebookRequestError error = response.getError();
					if (error != null) {
						writeLog("Facebook Error:" + error.getErrorMessage().toString());
						Util.displayDialog(BaseActivity.this, getString(R.string.alert_share_fb_fail), false);
					} else if (postId != null) {
						Util.displayDialog(BaseActivity.this, getString(R.string.alert_share_fb_success), false);
					}
				}
			};

			Request request = new Request(session, "me/feed", params, HttpMethod.POST, callback);

			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}
	}

	public void publishStory() {

		if (session.isOpened()) {

			final ProgressDialog mProgressDialog = ProgressDialog.show(this, "", "Please wait !!!");

			// Part 1: create callback to get URL of uploaded photo
			Request.Callback uploadPhotoRequestCallback = new Request.Callback() {
				@Override
				public void onCompleted(Response response) {
					// safety check
					if (mProgressDialog != null && mProgressDialog.isShowing())
						mProgressDialog.cancel();

					if (response.getError() != null) { // [IF Failed Posting]
					} // [ENDIF Failed Posting]

					String postId = null;
					if (response.getGraphObject() != null && response.getGraphObject().getInnerJSONObject() != null) {

						JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
						try {
							postId = graphResponse.getString("id");
						} catch (JSONException e) {
							// writeLog("JSON error " + e.getMessage());
						}
					}
					FacebookRequestError error = response.getError();
					if (error != null) {

						Util.displayDialog(BaseActivity.this, "Facebook sharing failed.\n Please try later.", false);

					} else if (postId != null) {

						Util.displayDialog(BaseActivity.this, "Photo successfully shared.", false);
						fbFileToShare = null;
						fbMessageToShare = "";

					}

				} // [END onCompleted]
			};

			Request request;
			try {
				request = Request.newUploadPhotoRequest(session, fbFileToShare, uploadPhotoRequestCallback);
				final Bundle params = request.getParameters();

				params.putString("name", fbMessageToShare);

				request.setParameters(params);
				request.executeAsync();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void shareToFacebook(final String message, File file) {
		if (!WebService.isNetworkAvailable(BaseActivity.this)) {
			Util.displayDialog(BaseActivity.this, getString(R.string.common_internet), false);
			return;
		}
		fbMessageToShare = message;
		fbFileToShare = file;
		if (session != null && session.isOpened()) {
			if (!hasPublishPermission()) {
				session.requestNewPublishPermissions(new Session.NewPermissionsRequest(BaseActivity.this, PERMISSIONS_PUBLISH).setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO).setRequestCode(FACEBOOK_LOGIN_CODE));
			} else {
				if (file == null || !file.exists()) {
					publisStatus();
				} else {
					publishStory();
				}
			}
		} else if (session.getState() == SessionState.CLOSED_LOGIN_FAILED) {

			session.closeAndClearTokenInformation();
			session = new Session(this);
			Session.setActiveSession(session);
			session.openForPublish(new Session.OpenRequest(this).setCallback(statusCallback).setPermissions(PERMISSIONS_PUBLISH).setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO).setRequestCode(FACEBOOK_LOGIN_CODE));
		} else {
			try {
				if (!session.isOpened()) {
					session.openForPublish(new Session.OpenRequest(this).setCallback(statusCallback).setPermissions(PERMISSIONS_PUBLISH).setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO).setRequestCode(FACEBOOK_LOGIN_CODE));
				} else {
					Session.openActiveSession(this, true, statusCallback);
				}
			} catch (Exception e) {
				writeLog(e.getMessage().toString());
			}
		}

	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			getFBUserData(session);

		}
	}

	private boolean hasPublishPermission() {
		Session session = Session.getActiveSession();
		return session != null && session.getPermissions().contains("publish_actions");
	}

	@SuppressWarnings("deprecation")
	public void getFBUserData(Session session) {

		if (session.isOpened()) {

			Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, com.facebook.Response response) {
					if (user != null) {
						writeLog(user.getName());
						writeLog(user.getId());
					}
				}
			});
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	/**
	 * End Region FaceBook
	 */

	/**
	 * Region Start Twitter
	 */

	/**
	 * Twitter sharing call Check Twitter object is not null and if connected
	 * than directly post or connect again.
	 */
	public void getTwitterCall(String textToShare, String imagePathToShare) {
		if (twitter != null) {
			if (twitter.isConnected()) {
				twitter.postToTwitter(this, textToShare, imagePathToShare);
			} else {
				if (WebService.isNetworkAvailable(this)) {
					onClickTwitterLogin(textToShare, imagePathToShare);
				} else {
					Util.displayDialog(this, getString(R.string.common_internet), false);
				}
			}
		} else {
			if (WebService.isNetworkAvailable(this)) {
				onClickTwitterLogin(textToShare, imagePathToShare);
			} else {
				Util.displayDialog(this, getString(R.string.common_internet), false);
			}
		}
	}

	/**
	 * Twitter Work Create Twitter object and check for login
	 */
	private void onClickTwitterLogin(String textToShare, String imagePathToShare) {
		twitter = new TwitterLogin(this);
		application.setTwitterLogin(twitter);
		twitter.checkLoginForTwitter(this, textToShare, imagePathToShare);
	}

	/**
	 * Region End Twitter
	 */

	/**
	 * Region Start Google Plus
	 */

	/**
	 * Method used for when click on Google Plus share
	 */
	public void onClickGooglePlus(String textToShare, String imagePathToShare) {
		this.textToShare = textToShare;
		this.imagePathToShare = imagePathToShare;
		if (WebService.isNetworkAvailable(this)) {
			int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

			if (resultCode == ConnectionResult.SUCCESS) {
				onClickGoogleLogin();
			} else {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1).show();
			}
		} else {
			Util.displayDialog(this, getString(R.string.common_internet), false);
		}
	}

	// Google Plus Integration

	private void onClickGoogleLogin() {

		mPlusClient = new PlusClient.Builder(this, this, this).build();
		// Progress bar to be displayed if the connection failure is not
		// resolved.
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage(getString(R.string.common_loading_sharing));
		mConnectionProgressDialog.show();
		mPlusClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mConnectionProgressDialog.isShowing()) {
			// The user clicked the sign-in button already. Start to resolve
			// connection errors. Wait until onConnected() to dismiss the
			// connection dialog.
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mPlusClient.connect();
				}
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		if (mPlusClient != null && mPlusClient.isConnected()) {
			application.setMplusClinet(mPlusClient);
			if (mConnectionProgressDialog.isShowing()) {
				mConnectionProgressDialog.dismiss();
			}
			shareToGooglePlus();
		}
	}

	/**
	 * Sharing to Google Plus
	 */
	private void shareToGooglePlus() {
		// Launch the Google+ share dialog with attribution to your app.
		try {
			if (TextUtils.isEmpty(imagePathToShare)) {
				Intent shareIntent = new PlusShare.Builder(this).setType("text/plain").setText(this.textToShare).getIntent();
				startActivity(shareIntent);
			} else {
				try {
					String imageName = "police_" + System.currentTimeMillis() + ".png";

					copyFile(imagePathToShare, getExternalCacheDir() + File.separator + imageName);

					gPlusFileToShare = new File(getExternalCacheDir() + File.separator + imageName);
					Intent shareIntent = new PlusShare.Builder(this).setType("image/jpeg").setStream(Uri.fromFile(gPlusFileToShare)).setText(getString(R.string.app_name)).getIntent();
					startActivityForResult(shareIntent, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	public static boolean copyFile(String from, String to) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(from);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(from);
				FileOutputStream fs = new FileOutputStream(to);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Region End Google Plus
	 */

	@Override
	public void onDisconnected() {
	}

	public void callPanicReport(){
		if (application.latitude.equals("") && application.longitude.equals("")) {
			new ConfigureFuseLocation(this).executeLocation();
		} else {
			if (!Util.isProviderAvailed(BaseActivity.this)) {
				Util.buildAlertMessageNoGps(BaseActivity.this);
			} else if (((PoliceApp) getApplicationContext()).latitude.equalsIgnoreCase("") || ((PoliceApp) getApplicationContext()).longitude.equalsIgnoreCase("")) {
				Util.displayDialog(BaseActivity.this, getString(R.string.common_location_error), false);
			} else if (WebService.isNetworkAvailable(BaseActivity.this)) {
				if (!preferences.getBoolean(getString(R.string.panic_running), false)) {
					startService(new Intent(BaseActivity.this, PanicReportService.class));
				}
			} else if (!WebService.isNetworkAvailable(BaseActivity.this)) {
				Util.displayDialog(this, getString(R.string.common_internet), false);
			}
		}
	}

	public void callAddLikeTask(String galleryId, int isLike) {
		if (WebService.isNetworkAvailable(this)) {
			if (getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).getString(getString(R.string.shared_user_id), "0").equalsIgnoreCase("0")) {
				Util.displayDialogNotLogin(this, getString(R.string.pleaselogin), false);
			} else {
				if (isLike == 0) {
					new AddLikePhotoTask().execute(galleryId);
				} else {
					Util.displayDialog(this, getString(R.string.alert_photo_like), false);
				}
			}
		} else {
			Util.displayDialog(this, getString(R.string.common_internet), false);
		}
	}

	/**
	 * AsyncTask to call WS for like photo
	 * 
	 * @author indianic
	 * 
	 */
	private class AddLikePhotoTask extends AsyncTask<String, Void, Void> {

		private WSAddLike wsAddLike;
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(BaseActivity.this, "", getString(R.string.common_loading), true, false, new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					if (dialog != null) {
						dialog.dismiss();
					}
				}
			});
			progressDialog.setCanceledOnTouchOutside(false);
		}

		@Override
		protected Void doInBackground(String... params) {
			wsAddLike = new WSAddLike(BaseActivity.this);
			wsAddLike.executeService(params[0], 1);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			if (wsAddLike.isSuccess()) {
				if (getSupportFragmentManager().findFragmentByTag(GalleryDetailFragment.class.getSimpleName()) != null) {
					((GalleryDetailFragment) getSupportFragmentManager().findFragmentByTag(GalleryDetailFragment.class.getSimpleName())).updateLike();
				} else if (getSupportFragmentManager().findFragmentByTag(GalleryFragment.class.getSimpleName()) != null) {
					((GalleryFragment) getSupportFragmentManager().findFragmentByTag(GalleryFragment.class.getSimpleName())).galleryAdapter.updateLike();
				}
				// resetGalleryData();
			} else {
				Util.displayDialog(BaseActivity.this, wsAddLike.getMessage(), false);
			}

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
			// mConnectionResult = null;
			mPlusClient.connect();
		} else if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_CANCELED) {
			if (mConnectionProgressDialog != null && mConnectionProgressDialog.isShowing()) {
				mConnectionProgressDialog.dismiss();
			}
		} else if (resultCode == RESULT_OK && requestCode == FACEBOOK_LOGIN_CODE) {
			if (hasPublishPermission()) {
				publisStatus();
			}
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}

}
