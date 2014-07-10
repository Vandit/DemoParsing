package ng.com.police.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ng.com.police.PoliceApp;
import ng.com.police.R;
import ng.com.police.common.Util;
import ng.com.police.db.DBHelper;
import ng.com.police.model.CustomGalleryDataModel;
import ng.com.police.view.BaseActivity;
import ng.com.police.webservice.WSReportCrime;
import ng.com.police.webservice.WebService;
import ng.com.service.ReportCrimeAttachmentService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * Purpose : This class is use for submit report crime data
 * 
 * @author Vandit Patel
 * @version 1.0
 * @date 01/05/14
 */
public class ReportCrimeFragment extends Fragment implements OnClickListener, OnEditorActionListener, OnFocusChangeListener {

	private EasyTracker tracker;
	private EditText edtMessage;
	private EditText edtLocation;
	private EditText edtSubject;
	private TextView txtImageCount;
	private TextView txtVideoCount;
	private CheckedTextView chkCurrentLocation;
	private FrameLayout frmImageLayout;
	private FrameLayout frmVideoLayout;
	private ArrayList<CustomGalleryDataModel> selectedImagesList;
	private final int MEDIA_TYPE_VIDEO = 2;
	private final int VIDEO_GALLERY_OPEN_ACTIVITY_REQUEST_CODE = 100;
	private final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 101;
	private Uri outputFile = null;
	private String selectedVideoPath;
	private PoliceApp policeApp;
	private String reportLatitude = "";
	private String reportLongitude = "";
	private String placeAddress = "";
	private final String key_video = "video";
	private SendReportCrimeTask sendReportCrimeTask;
	private LoadCurrentAddressDataTask currentAddressDataTask;
	private ProgressBar progressBar;
	private MediaScannerConnection mediaScannerConnection;
	private String crimeId = "";
	private String userID = "";
	private DBHelper dbHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_report_crime, null);
		initializeComponent(view);
		userID = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).getString(getString(R.string.shared_user_id), "0");
		setCurrentLocation();

		return view;
	}

	/**
	 * Initialize views and set title
	 * 
	 * @param view
	 **/
	private void initializeComponent(View view) {
		((BaseActivity) getActivity()).displayTitle(getString(R.string.home_text_reportcrime));
		tracker = EasyTracker.getInstance(getActivity());
		policeApp = (PoliceApp) getActivity().getApplicationContext();
		chkCurrentLocation = (CheckedTextView) view.findViewById(R.id.fragment_report_crime_checkbox);
		edtLocation = (EditText) view.findViewById(R.id.fragment_report_crime_edt_location);
		edtMessage = (EditText) view.findViewById(R.id.fragment_report_crime_edt_message);
		edtSubject = (EditText) view.findViewById(R.id.fragment_report_crime_edt_subject);
		txtImageCount = (TextView) view.findViewById(R.id.fragment_report_crime_txt_image_count);
		txtVideoCount = (TextView) view.findViewById(R.id.fragment_report_crime_txt_video_count);
		frmImageLayout = (FrameLayout) view.findViewById(R.id.fragment_report_crime_frm_image);
		frmVideoLayout = (FrameLayout) view.findViewById(R.id.fragment_report_crime_frm_video);
		progressBar = (ProgressBar) view.findViewById(R.id.fragment_report_crime_progressbar);
		chkCurrentLocation.setOnClickListener(this);
		frmImageLayout.setOnClickListener(this);
		frmVideoLayout.setOnClickListener(this);
		edtLocation.setOnFocusChangeListener(this);
		setHasOptionsMenu(true);
	}

	/**
	 * Call when fragment is display and hidden
	 * 
	 * @param hidden
	 *            - boolean parameter to check fragment hide or not
	 **/
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		((BaseActivity) getActivity()).displayTitle(getString(R.string.home_text_reportcrime));
		((BaseActivity) getActivity()).displayHomeAsUpEnable(false);
	}

	/**
	 * Create Option menu
	 * 
	 * @param menu
	 * @param inflater
	 **/
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_actionbar, menu);
		menu.findItem(R.id.menu_actionbar_done_report).setVisible(true);
		menu.findItem(R.id.menu_actionbar_call).setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);

	}

	/**
	 * handle Menu Item Click
	 * 
	 * @param item
	 **/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_actionbar_done_report) {
			callReportCrimeTask();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		super.onStart();
		tracker.activityStart(getActivity());
		Util.sendAnalytics(getActivity(), getString(R.string.gog_ana_report_crime), getString(R.string.gog_ana_event_category), getString(R.string.gog_ana_event_action), getString(R.string.gog_ana_event_label));
	}

	@Override
	public void onStop() {
		super.onStop();
		tracker.activityStop(getActivity());
	}

	/**
	 * Handle Click event of views
	 * 
	 * @param v
	 *            - Clicked view
	 **/
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_report_crime_checkbox:
			if (chkCurrentLocation.isChecked()) {
				edtLocation.setFocusable(false);
			} else {
				edtLocation.setFocusable(true);
			}
			setCurrentLocation();
			break;
		case R.id.fragment_report_crime_frm_image:
			Util.hideSoftKeyboard(getActivity());
			callCustomGalleryFragment();
			break;
		case R.id.fragment_report_crime_frm_video:
			if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
				Util.displayDialog(getActivity(), getActivity().getString(R.string.message_no_camera), false);
			} else {
				displayOptionDialog();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Call report crime data task
	 * 
	 **/
	private void callReportCrimeTask() {
		if (!getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).getString(getString(R.string.shared_user_id), "0").equalsIgnoreCase("0")) {
			if (WebService.isNetworkAvailable(getActivity())) {
				if (isAllFieldValidated()) {
					sendReportCrimeTask = new SendReportCrimeTask();
					sendReportCrimeTask.execute();
				}
			} else {
				Util.displayDialog(getActivity(), getString(R.string.common_internet), false);
			}

		} else {
			Util.displayDialogNotLogin(getActivity(), getString(R.string.pleaselogin), false);
		}
	}

	/**
	 * Call current address data task
	 * 
	 **/
	private void callLoadCurrentAddressDataTask() {
		if (WebService.isNetworkAvailable(getActivity())) {
			currentAddressDataTask = new LoadCurrentAddressDataTask();
			currentAddressDataTask.execute(new Double[] { Double.valueOf(reportLatitude), Double.valueOf(reportLongitude) });
		} else {
			Util.displayDialog(getActivity(), getString(R.string.common_internet), false);
		}

	}

	/**
	 * Check for location and set location data
	 * 
	 **/
	private void setCurrentLocation() {
		if (!chkCurrentLocation.isChecked()) {
			if (!Util.isProviderAvailed(getActivity())) {
				Util.buildAlertMessageNoGps(getActivity());
				((CheckedTextView) chkCurrentLocation).toggle();
			} else if (policeApp.latitude.equalsIgnoreCase("") || policeApp.longitude.equalsIgnoreCase("")) {
				Util.displayDialog(getActivity(), getString(R.string.common_location_error), false);
			} else {
				reportLatitude = policeApp.latitude;
				reportLongitude = policeApp.longitude;
				callLoadCurrentAddressDataTask();
			}
		} else {
			((CheckedTextView) chkCurrentLocation).toggle();
			edtLocation.setText("");
		}
	}

	/**
	 * Method for check all data in every edit text are valid or not
	 * 
	 */
	private boolean isAllFieldValidated() {
		boolean isValid = false;
		if (edtSubject.getText().length() == 0) {
			Util.displayDialog(getActivity(), getString(R.string.report_crime_enter_subject), false);
			edtSubject.requestFocus();
		} else if (edtLocation.getText().length() == 0) {
			Util.displayDialog(getActivity(), getString(R.string.report_crime_enter_location), false);
			edtLocation.requestFocus();
		} else if (edtMessage.getText().length() == 0) {
			Util.displayDialog(getActivity(), getString(R.string.report_crime_enter_message), false);
			edtMessage.requestFocus();
		} else {
			isValid = true;
		}
		return isValid;
	}

	/**
	 * Method to call CustomGallery fragment for select images
	 */
	private void callCustomGalleryFragment() {
		CustomGalleryFragment customGalleryFragment = new CustomGalleryFragment();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		customGalleryFragment.setSelectedImagesData(selectedImagesList);
		transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
		transaction.add(R.id.content_frame, customGalleryFragment);
		transaction.addToBackStack(ReportCrimeFragment.class.getSimpleName());
		transaction.hide(this);
		transaction.commit();
	}

	/**
	 * Set selected images count and selected images data
	 * 
	 * @param imagesList
	 *            - Assign images data list
	 **/
	public void setSelectedImagesData(ArrayList<CustomGalleryDataModel> imagesList) {
		selectedImagesList = new ArrayList<CustomGalleryDataModel>();
		selectedImagesList = imagesList;
		if (selectedImagesList != null && selectedImagesList.size() > 0) {
			txtImageCount.setText("" + selectedImagesList.size());
			txtImageCount.setVisibility(View.VISIBLE);
		} else {
			txtImageCount.setText("");
			txtImageCount.setVisibility(View.GONE);
		}
	}

	/**
	 * Method to record video
	 * 
	 */
	public void recordVideo() {
		Boolean isSDPresent = Util.checkSDCardAvalibility();
		if (isSDPresent) {
			// create new Intent
			Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			outputFile = Util.getOutputMediaFileUri(MEDIA_TYPE_VIDEO, getActivity());
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFile);
			intent.putExtra("android.intent.extra.durationLimit", 30);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			// start the Video Capture Intent
			startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
		} else {
			Util.displayDialog(getActivity(), getString(R.string.sdcardnotavilable), false);
		}
	}

	/**
	 * Method to open video gallery
	 * 
	 */
	public void openVideoGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("video/*");
		startActivityForResult(intent, VIDEO_GALLERY_OPEN_ACTIVITY_REQUEST_CODE);
	}

	/**
	 * Get real path from URI in Activity Result
	 * 
	 * @param contentURI
	 */
	private String getPathFromURI(Uri contentURI) {
		Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
		if (cursor == null) {
			return contentURI.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			return cursor.getString(idx);
		}
	}

	/**
	 * Open Camera and Gallery dialog
	 * 
	 */
	public void displayOptionDialog() {
		final Dialog videoOptionDialog = new Dialog(getActivity());
		videoOptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		videoOptionDialog.setContentView(R.layout.dialog_video_options);
		TextView txtVideoCapture = (TextView) videoOptionDialog.findViewById(R.id.dialog_video_options_txt_capture);
		TextView txtVideoGallery = (TextView) videoOptionDialog.findViewById(R.id.dialog_video_options_txt_gallery);
		ImageButton btnCancel = (ImageButton) videoOptionDialog.findViewById(R.id.dialog_video_options_btn_cancel);
		txtVideoCapture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				videoOptionDialog.dismiss();
				// Record video option
				recordVideo();
			}
		});
		txtVideoGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				videoOptionDialog.dismiss();
				// Open video gallery option
				openVideoGallery();
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				videoOptionDialog.dismiss();
			}
		});
		videoOptionDialog.show();
	}

	/**
	 * AsyncTask to call WS for send generated crime report
	 * 
	 */
	private class SendReportCrimeTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog dialog;
		private WSReportCrime wsReportCrime = new WSReportCrime(getActivity());

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(getActivity(), "", getString(R.string.common_loading), true, false, new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					if (dialog != null) {
						dialog.dismiss();
					}
				}
			});
			dialog.setCanceledOnTouchOutside(false);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			getActivity().finish();
		}

		@Override
		protected Void doInBackground(Void... params) {
			wsReportCrime.executeService(edtSubject.getText().toString().trim(), edtMessage.getText().toString().trim(), reportLatitude, reportLongitude, edtLocation.getText().toString().trim());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss progress dialog
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			if (wsReportCrime.isSuccess()) {
				clearData();
				crimeId = wsReportCrime.getCrimeid();
				// Add attachment data in DB after successful submission of text
				// data
				addAttachmentDataInDB();
				Util.displayDialog(getActivity(), wsReportCrime.getMessage(), true);
				// Start pending report attachment service
				startPendingReportCrimeService();
			} else {
				if (wsReportCrime.getStatusCode() == 2) {
					Util.displayDialogUnauthorizeUser(getActivity(), wsReportCrime.getMessage(), true);
				} else {
					Util.displayDialog(getActivity(), wsReportCrime.getMessage(), false);
				}
			}
		}

	}

	/**
	 * Method for start service which send the attachments of pending reports in
	 * background
	 * 
	 */
	private void startPendingReportCrimeService() {
		Intent crimeReportIntent = new Intent(getActivity().getApplicationContext(), ReportCrimeAttachmentService.class);
		getActivity().startService(crimeReportIntent);
	}

	/**
	 * Method to insert data in database
	 * */
	@SuppressWarnings("unused")
	private void addAttachmentDataInDB() {
		Map<String, String> attachmentList = null;
		dbHelper = ((PoliceApp) getActivity().getApplicationContext()).getDbHelper();
		if (selectedImagesList != null) {
			attachmentList = Util.getCompressedImagesPathList(getActivity(), selectedImagesList);
		}
		if (selectedVideoPath != null && !selectedVideoPath.equals("")) {
			if (attachmentList == null) {
				attachmentList = new HashMap<String, String>();
			}
			attachmentList.put(key_video, selectedVideoPath);
		}
		if (attachmentList != null && attachmentList.size() > 0) {

			for (Map.Entry<String, String> entry : attachmentList.entrySet()) {
				if (entry != null) {
					System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
					long isInsert = dbHelper.insertCrimeReport(userID, crimeId, entry.getValue(), entry.getKey());
					dbHelper.copyDatabaseToSdCard();
				}
			}
		}
	}

	/**
	 * Clear All view data
	 * 
	 ***/
	private void clearData() {
		edtSubject.setText("");
		edtMessage.setText("");
		edtLocation.setText("");
		txtImageCount.setVisibility(View.GONE);
		txtVideoCount.setVisibility(View.GONE);
		txtImageCount.setText("");
		txtVideoCount.setText("");
		chkCurrentLocation.setChecked(false);
		reportLatitude = "";
		reportLongitude = "";
		edtSubject.requestFocus();
	}

	/**
	 * Set the count when video file is added
	 ***/
	private void setVideoCount() {
		if (selectedVideoPath != null && !selectedVideoPath.equalsIgnoreCase("")) {
			txtVideoCount.setText("" + 1);
			txtVideoCount.setVisibility(View.VISIBLE);
		} else {
			txtVideoCount.setText("");
			txtVideoCount.setVisibility(View.GONE);
		}
	}

	/**
	 * This class call the web-service to get address of the location Store all
	 * needed info into preference Show address into edit box Point to that
	 * location on the map
	 */
	private class LoadCurrentAddressDataTask extends AsyncTask<Double, Void, Void> {
		private String responseMsg;

		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Double... params) {

			String mFinalPassingUrl = getString(R.string.address_url) + params[0] + "," + params[1] + "&language=" + Locale.getDefault() + "&sensor=true";
			String mReturnedString = "";
			JSONObject mRetrievedJson = null;
			try {
				mReturnedString = WebService.getData(getActivity(), mFinalPassingUrl);
				if (!mReturnedString.equals("")) {
					try {
						mRetrievedJson = new JSONObject(mReturnedString);
						responseMsg = parseCurrentAddressJSONObject(mRetrievedJson);
					} catch (JSONException e) {
						responseMsg = getResources().getString(R.string.http_parsing_error);
					}
				} else if (mReturnedString.equals("-1") || mReturnedString.equals("-2")) {

					responseMsg = getResources().getString(R.string.http_parsing_error);
				} else {
					responseMsg = getResources().getString(R.string.Invalid_data);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (isCancelled())
				return;
			progressBar.setVisibility(View.GONE);
			edtLocation.setText("");
			if (responseMsg.equalsIgnoreCase("success")) {
				((CheckedTextView) chkCurrentLocation).toggle();
				edtLocation.setText(placeAddress);
			} else {
				if (responseMsg.trim().equals("No Response")) {
					Util.displayDialog(getActivity(), getString(R.string.alert_no_data), false);
				} else {
					Util.displayDialog(getActivity(), responseMsg, false);
				}
			}
			super.onPostExecute(result);
		}
	}

	/**
	 * Get address, locality, country of the place
	 * 
	 * @param nJsonObject
	 * @return success if properly executed otherwise error message
	 */
	private String parseCurrentAddressJSONObject(JSONObject nJsonObject) {
		String status;
		try {
			if (nJsonObject.getString("status").equalsIgnoreCase("OK")) {
				JSONArray jArray = nJsonObject.getJSONArray("results");
				JSONObject geo = jArray.getJSONObject(0);
				placeAddress = geo.getString("formatted_address");

				if (placeAddress == null || placeAddress.equals("")) {

					JSONObject geoLoca = jArray.getJSONObject(0);
					JSONArray addressComponent = geoLoca.getJSONArray("address_components");

					String streetAddress = "";
					String streetArea = "";
					String streetNumber = "";
					String cityName = "";
					String countryName = "";
					String stateName = "";
					String postalCode = "";

					for (int j = 0; j < addressComponent.length(); j++) {
						JSONObject type = addressComponent.getJSONObject(j);
						JSONArray typedata = type.getJSONArray("types");
						for (int k = 0; k < typedata.length(); k++) {
							if (typedata.get(k).equals("locality")) {
								cityName = type.getString("long_name");
							}
							if (typedata.get(k).equals("country")) {
								countryName = type.getString("long_name");
								// isoCode = type.getString("short_name");
							}
							if (typedata.get(k).equals("route")) {

								streetAddress = type.getString("long_name");
							}
							if (typedata.get(k).equals("street_number")) {

								streetNumber = type.getString("long_name");
							}
							if (typedata.get(k).equals("sublocality")) {

								streetArea = type.getString("long_name");
							}
							if (typedata.get(k).equals("administrative_area_level_1")) {

								stateName = type.getString("long_name");
							}
							if (typedata.get(k).equals("postal_code")) {

								postalCode = type.getString("long_name");
							}
						}
					}

					if (!streetNumber.equals("")) {
						setAddress(streetNumber);
					}
					if (!streetAddress.equals("")) {
						setAddress(streetAddress);
					}
					if (!streetArea.equals("")) {
						setAddress(streetArea);
					}
					if (!cityName.equals("")) {
						setAddress(cityName);
					}
					if (!stateName.equals("")) {
						setAddress(stateName);
					}
					if (!countryName.equals("")) {
						setAddress(countryName);
					}
					if (!postalCode.equals("")) {
						setAddress(postalCode);
					}
				}
				status = "Success";
				return status;

			} else if (nJsonObject.getString("status").equals("ZERO_RESULTS")) {
				// status = "ZERO_RESULTS";
				status = getResources().getString(R.string.alert_no_data);
				return status;
			} else {
				status = getResources().getString(R.string.Invalid_data);
				return status;
			}

		} catch (JSONException e) {
			status = getResources().getString(R.string.http_parsing_error);
			return status;
		}
	}

	/**
	 * Set Address which will get from service
	 ***/
	private void setAddress(String fieldValue) {
		if (!TextUtils.isEmpty(placeAddress) && !placeAddress.equals("")) {
			placeAddress = placeAddress + "," + fieldValue;
		} else {
			placeAddress = fieldValue;
		}
	}

	/**
	 * We need to notify the MediaScanner when a new file is created. In this
	 * way all the gallery applications will be notified too.
	 * 
	 */
	private void startMediaScanner() {
		mediaScannerConnection = new MediaScannerConnection(getActivity(), mediaScannerConnectionClient);
		mediaScannerConnection.connect();
	}

	/**
	 * Create media scanner connection and scan all files in sdcard
	 * 
	 * @param path
	 * @param uri
	 **/
	private MediaScannerConnectionClient mediaScannerConnectionClient = new MediaScannerConnectionClient() {

		@Override
		public void onMediaScannerConnected() {
			mediaScannerConnection.scanFile(selectedVideoPath, null);
		}

		@Override
		public void onScanCompleted(String path, Uri uri) {
			if (path.equals(selectedVideoPath))
				mediaScannerConnection.disconnect();
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/* Video Capture Result */
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
				// Video captured and saved to fileUri specified in the Intent
				selectedVideoPath = outputFile.toString();
				if (selectedVideoPath != null && !selectedVideoPath.equals("")) {
					if (selectedVideoPath.contains("file://")) {
						selectedVideoPath = selectedVideoPath.replace("file://", "");
					}
				}
				setVideoCount();
				startMediaScanner();
			}
			/* Video Gallery Picker Result */
			if (requestCode == VIDEO_GALLERY_OPEN_ACTIVITY_REQUEST_CODE) {
				Cursor cursor = MediaStore.Video.query(getActivity().getContentResolver(), data.getData(), new String[] { MediaStore.Video.VideoColumns.DURATION });
				cursor.moveToFirst();
				if (cursor.getString(cursor.getColumnIndex("duration")) != null) {
					long duration = Long.parseLong(cursor.getString(cursor.getColumnIndex("duration")));
					if (TimeUnit.MILLISECONDS.toSeconds(duration) <= 30) {
						selectedVideoPath = getPathFromURI(data.getData()).toString();
						setVideoCount();
					} else {
						Util.displayDialog(getActivity(), getString(R.string.alert_video_duration_limit), false);
					}
				}
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (currentAddressDataTask != null && currentAddressDataTask.getStatus() == AsyncTask.Status.RUNNING) {
			currentAddressDataTask.cancel(true);
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// chkCurrentLocation.setChecked(false);
		return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
	}
}
