package ng.com.service;

import java.io.File;
import java.util.ArrayList;

import ng.com.police.PoliceApp;
import ng.com.police.R;
import ng.com.police.db.DBHelper;
import ng.com.police.model.ReportCrimeModel;
import ng.com.police.webservice.WSReportCrimeAttechment;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;

/**
 * Purpose : This class is use for send pending reports attachment data on
 * server and delete attachments from database on successful response.
 * 
 * @author Vandit Patel
 * @version 1.0
 * @date 01/05/14
 */
public class ReportCrimeAttachmentService extends IntentService {

	private DBHelper dbHelper;

	public ReportCrimeAttachmentService() {
		super("ReportCrimeAttachmentService");
	}

	public ReportCrimeAttachmentService(String name) {
		super(name);

	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		dbHelper = ((PoliceApp) getApplicationContext()).getDbHelper();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// Task for Call WSReportCrimeAttachment
	private class ReportCrimeAttachmenTask extends AsyncTask<ReportCrimeModel, Void, Void> {

		private WSReportCrimeAttechment wsReportCrimeAttechment = new WSReportCrimeAttechment(getApplicationContext());
		private String crimeId = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Void doInBackground(ReportCrimeModel... reportCrimeModel) {
			if (reportCrimeModel[0] != null && reportCrimeModel.length > 0) {
				crimeId = reportCrimeModel[0].getCrimeId();
				wsReportCrimeAttechment.executeService(reportCrimeModel[0].getCrimeId(), getApplication().getString(R.string.language), reportCrimeModel[0].getAttachmentPath(), reportCrimeModel[0].getAttachmentTag(), reportCrimeModel[0].getUserId());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (wsReportCrimeAttechment.isSuccess()) {
				// Delete particular crime report
				dbHelper.deleteCrimeReport(crimeId);
			}

		}

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// there is data in Table then call WS
		if (dbHelper.isPendingReportsAvailable() > 0) {
			// Retrieve all attachments from database
			ArrayList<ReportCrimeModel> reportCrimeList = dbHelper.getPendingCrimeReports();
			if (reportCrimeList != null && reportCrimeList.size() > 0) {
				for (int j = 0; j < reportCrimeList.size(); j++) {
					// Check attachment is exist in sdcard or not
					if (new File(reportCrimeList.get(j).getAttachmentPath()).exists()) {
						// Send attachment on server from database if path exist
						new ReportCrimeAttachmenTask().execute(reportCrimeList.get(j));
					} else {
						// Delete attachment from database if path not exist
						dbHelper.deleteCrimeReport(reportCrimeList.get(j).getCrimeId());
					}
				}
			}
		} else {
			stopSelf();
		}
	}

}
