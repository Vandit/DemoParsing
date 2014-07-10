package ng.com.police.view;

import ng.com.police.R;
import ng.com.police.fragment.ReportCrimeFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
/**
 * Purpose : This class is use to call report crime fragment
 * 
 * @author Vandit Patel
 * @version 1.0
 * @date 01/05/14
 */
public class ReportCrimeActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		ReportCrimeFragment reportCrimeFragment = new ReportCrimeFragment();
		fragmentTransaction.add(R.id.content_frame, reportCrimeFragment,ReportCrimeFragment.class.getSimpleName());
		fragmentTransaction.commit();
	}
}
