package ng.com.service;

import ng.com.police.PoliceApp;
import ng.com.police.R;
import ng.com.police.view.DialogPanicReport;
import ng.com.police.webservice.WSReportPanic;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;

/**
 * Purpose : This class is use for submit panic report in background
 * 
 * @author Vandit Patel
 * @version 1.0
 * @date 01/05/14
 */
public class PanicReportService extends IntentService {

	private int numPanic = 0;
	SharedPreferences preferences;

	public PanicReportService() {
		super("PanicReportService");
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		preferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
	}

	@Override
	public void onDestroy() {
		// policeApp.setPanicRunning(true);
		super.onDestroy();
	}

	/**
	 * Call Panic Web Service By Device maximum 3 times if it not success in 1
	 * or 2 time
	 */
	private class PanicTask extends AsyncTask<Void, Void, Void> {

		private WSReportPanic wsReportPanic = new WSReportPanic(getApplicationContext());

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (preferences != null)
				preferences.edit().putBoolean(getString(R.string.panic_running), true).commit();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Void doInBackground(Void... params) {
			wsReportPanic.executeService(((PoliceApp) getApplicationContext()).latitude, ((PoliceApp) getApplicationContext()).longitude);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (wsReportPanic.isPanicSuccess()) {
				Intent intent = new Intent(PanicReportService.this, DialogPanicReport.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				numPanic = 0;
				if (preferences != null   )
					preferences.edit().putBoolean(getString(R.string.panic_running), false).commit();
				stopSelf();
			} else {
				callPanicreport();
			}
		}

		private void callPanicreport() {
			if (numPanic < 3) {
				new PanicTask().execute();
				numPanic++;
			} else {
				numPanic = 0;
			}
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		new PanicTask().execute();
		// Handler for stop Service in 30 seconds
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				preferences.edit().putBoolean(getString(R.string.panic_running), false).commit();
				stopSelf();
			}
		}, 30000);
	}

}
