package ng.com.police.view;

import ng.com.police.R;
import ng.com.police.fragment.GalleryFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
/**
 * Purpose : This class is use to call gallery fragment
 * 
 * @author Vandit Patel
 * @version 1.0
 * @date 01/05/14
 */
public class GalleryActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		GalleryFragment galleryFragment = new GalleryFragment();
		fragmentTransaction.add(R.id.content_frame, galleryFragment, GalleryFragment.class.getSimpleName());
		fragmentTransaction.commit();
	}
}
