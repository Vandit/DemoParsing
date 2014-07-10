package ng.com.police.view;

import ng.com.police.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Purpose : This class is use to display splash screen
 * 
 * @author Vandit Patel
 * @version 1.0
 * @date 01/05/14
 */
public class SplashScreen extends Activity implements OnClickListener {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 2000;
	private SharedPreferences preferences;

	private TextView tvAccept;
	private LinearLayout conditiondialogll;
	private View view;

	private boolean flagAccept = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashscreen);

		preferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
		conditiondialogll = (LinearLayout) findViewById(R.id.activity_condition_ll);
		view = (View) findViewById(R.id.view);
		tvAccept = (TextView) findViewById(R.id.condition_tv_accept);
		tvAccept.setOnClickListener(this);

		if (preferences.getBoolean(getString(R.string.shared_first_time_dialog), false)) {
			conditiondialogll.setVisibility(View.GONE);
			view.setVisibility(View.GONE);
			flagAccept = true;
		}
		new SplashTask(this).execute();
	}

	/**
	 * AsycTask for setting splash screen by sleep thread for some time
	 * 
	 */
	private class SplashTask extends AsyncTask<Void, Void, Void> {

		public SplashTask(Context context) {

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				Thread.sleep(SPLASH_TIME_OUT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (flagAccept) {
				if (!preferences.getString(getString(R.string.shared_user_id), "0").equalsIgnoreCase("0")) {
					final Intent intent = new Intent(SplashScreen.this, DashBoardActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.right_in, R.anim.left_out);
					finish();
				} else {
					Intent intent = new Intent(SplashScreen.this, LoginOptionActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.right_in, R.anim.left_out);
					finish();
				}
			} else {
				view.setVisibility(View.VISIBLE);
				conditiondialogll.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * Handle Click event of views
	 * 
	 * @param v
	 *            - Clicked view
	 **/
	@Override
	public void onClick(View v) {
		if (v == tvAccept) {
			conditiondialogll.setVisibility(View.GONE);
			view.setVisibility(View.GONE);
			flagAccept = true;
			preferences.edit().putBoolean(getString(R.string.shared_first_time_dialog), true).commit();
			Intent intent = new Intent(SplashScreen.this, LoginOptionActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
			finish();
		}

	}

}
