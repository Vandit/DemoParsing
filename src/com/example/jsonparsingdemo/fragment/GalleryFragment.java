package ng.com.police.fragment;

import java.util.ArrayList;

import ng.com.police.PoliceApp;
import ng.com.police.R;
import ng.com.police.adapter.GalleryAdapter;
import ng.com.police.common.Util;
import ng.com.police.db.DBGallery;
import ng.com.police.db.DBHelper;
import ng.com.police.model.GalleryModel;
import ng.com.police.pulltorefresh.PullToRefreshLayout;
import ng.com.police.view.BaseActivity;
import ng.com.police.webservice.WSGetGallery;
import ng.com.police.webservice.WebService;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * Purpose : This class is use for display gallery List, also like, comment and
 * share photo on facebook, twitter and google plus.
 * 
 * @author Vandit Patel
 * @version 1.0
 * @date 01/05/14
 */
public class GalleryFragment extends Fragment implements OnItemClickListener, OnRefreshListener, OnScrollListener {

	private GridView gridview;
	public GalleryAdapter galleryAdapter;
	private DBHelper dbHelper;
	private PoliceApp policeApp;
	private boolean isLoadMore = false;
	private int totalRecords = 0;
	private GetGalleryTask getGalleryTask;
	private TextView emptyStub;
	private EasyTracker tracker;
	private ProgressBar progressBar;
	public Boolean isRefresh = false;
	private ArrayList<GalleryModel> galleryDataList;
	private PullToRefreshLayout mPullToRefreshLayout;
	private WSGetGallery wsGetGallery;
	private String lastRecordTimeStamp = "";
	private boolean isAsyncCallRequired = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_gallery, null);
		initializeComponent(view);
		initializePullToRefresh();
		callGetGalleryTask();
		return view;
	}

	/**
	 * Initialize views and set title
	 * 
	 * @param view
	 **/
	private void initializeComponent(View view) {
		tracker = EasyTracker.getInstance(getActivity());
		((BaseActivity) getActivity()).displayTitle(getString(R.string.home_text_gallery));
		policeApp = (PoliceApp) getActivity().getApplicationContext();
		dbHelper = policeApp.getDbHelper();
		gridview = (GridView) view.findViewById(R.id.fragment_gallery_gridview);
		emptyStub = (TextView) view.findViewById(R.id.fragment_gallery_emptyStub);
		progressBar = (ProgressBar) view.findViewById(R.id.fragment_gallery_progressBar);

		// Now find the PullToRefreshLayout and set it up
		mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);

		wsGetGallery = new WSGetGallery(getActivity());
		galleryDataList = new ArrayList<GalleryModel>();

		gridview.setOnItemClickListener(this);

		if (!dbHelper.isGalleryDataAvailable()) {
			isAsyncCallRequired = true;
			gridview.setOnScrollListener(this);
		} else {
			gridview.setOnScrollListener(null);
			isAsyncCallRequired = false;
		}

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
		((BaseActivity) getActivity()).displayTitle(getString(R.string.home_text_gallery));
		((BaseActivity) getActivity()).displayHomeAsUpEnable(false);
		if (!hidden) {
			gridview.setOnItemClickListener(this);
			if (galleryAdapter != null) {
				galleryAdapter.notifyDataSetChanged();
			}
			if (isRefresh) {
				resetGalleryData();
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		tracker.activityStart(getActivity());
		Util.sendAnalytics(getActivity(), getString(R.string.gog_ana_gallery), getString(R.string.gog_ana_event_category), getString(R.string.gog_ana_event_action), getString(R.string.gog_ana_event_label));
	}

	@Override
	public void onStop() {
		super.onStop();
		tracker.activityStop(getActivity());
	}

	/**
	 * Initialize pull to refresh actionbar components
	 * 
	 **/
	private void initializePullToRefresh() {
		ActionBarPullToRefresh.from(getActivity()).options(Options.create()
		// Here we make the refresh scroll distance to 75% of the GridView
		// height
				.scrollDistance(.25f)
				// Here we define a custom header layout which will be inflated
				// and used
				// .headerLayout(R.layout.customised_header)

				// Here we define a custom header transformer which will alter
				// the header based on the current pull-to-refresh state

				// .headerTransformer(new CustomisedHeaderTransformer())

				.build())
		// Mark All Children as pullable
				.allChildrenArePullable().
				// Set the OnRefreshListener
				listener(this)
				// Here we'll set a custom ViewDelegate
				.useViewDelegate(GalleryFragment.class, new AbsListViewDelegate()).setup(mPullToRefreshLayout);
	}

	/**
	 * Set Gallery list data in to adapter
	 */
	private void setGalleryData() {
		if (galleryDataList != null && galleryDataList.size() > 0) {
			if (galleryAdapter != null) {
				galleryAdapter.notifyDataSetChanged();
			} else {

				galleryAdapter = new GalleryAdapter(getActivity(), galleryDataList, GalleryFragment.this);
				gridview.setAdapter(galleryAdapter);
			}
		} else {
			if (galleryAdapter != null) {
				galleryAdapter.notifyDataSetChanged();
			}
			emptyStub.setVisibility(View.VISIBLE);
		}
		gridview.setOnItemClickListener(this);
		gridview.setOnScrollListener(this);
		isLoadMore = false;
	}

	/**
	 * Reset gallery data and call async task of gallery
	 **/
	private void resetGalleryData() {
		resetSectionTimeStamp();
		isAsyncCallRequired = true;
		callGetGalleryTask();
	}

	/**
	 * Set last fetch record timestamp from database
	 ***/
	private void setLastRecordTimeStamp() {
		lastRecordTimeStamp = dbHelper.getTimeStamp(getString(R.string.section_gallery));
	}

	/**
	 * Reset last fetch record timestamp in to database
	 ***/
	private void resetSectionTimeStamp() {
		Log.e("resetSectionTimeStamp  :", "resetSectionTimeStamp");
		dbHelper.updateSectionPageConfigData("", 0, getString(R.string.section_gallery));
		lastRecordTimeStamp = "";
	}

	/**
	 * Call when actionbar pull to refresh event occured
	 * 
	 * @param view
	 ***/
	@Override
	public void onRefreshStarted(View view) {

		if (WebService.isNetworkAvailable(getActivity())) {
			resetGalleryData();
		} else {
			mPullToRefreshLayout.setRefreshComplete();
			Util.displayDialog(getActivity(), getString(R.string.common_internet), false);
		}

	}

	// call when view need to refresh
	public void getRefresh() {
		isRefresh = true;
	}

	/**
	 * Method for clear gallery data and getting data from DataBase when network
	 * is not there
	 */
	private void getOfflineDataList() {
		if (galleryDataList != null && galleryDataList.size() > 0) {
			galleryDataList.clear();
		}
		getDataFromDB();
		totalRecords = Integer.parseInt(dbHelper.getTotalRecords(getString(R.string.section_gallery)));
	}

	public void getDataFromDB() {
		Cursor cursor = dbHelper.getGalleryDataList();
		GalleryModel model;
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				model = new GalleryModel();
				model.setGalleryId(cursor.getString(cursor.getColumnIndex(DBGallery.ID)));
				model.setImageTitle(cursor.getString(cursor.getColumnIndex(DBGallery.TITLE)));
				model.setImageDescription(cursor.getString(cursor.getColumnIndex(DBGallery.DESCRIPTION)));
				model.setImagePath(cursor.getString(cursor.getColumnIndex(DBGallery.IMAGE_PATH)));
				model.setImageLikeCount(cursor.getString(cursor.getColumnIndex(DBGallery.LIKE_COUNT)));
				model.setImageDownloadCount(cursor.getString(cursor.getColumnIndex(DBGallery.DOWNLOAD_COUNT)));
				model.setImageAddedDate(cursor.getString(cursor.getColumnIndex(DBGallery.CREATED_DATE)));
				model.setImageCommentCount(cursor.getString(cursor.getColumnIndex(DBGallery.COMMENTS_COUNT)));
				model.setImageShortDescription(cursor.getString(cursor.getColumnIndex(DBGallery.SHORT_DESCRIPTION)));
				model.setImageShareUrl(cursor.getString(cursor.getColumnIndex(DBGallery.SHARE_URL)));
				model.setImageSlug(cursor.getString(cursor.getColumnIndex(DBGallery.SLUG)));
				model.setIsLike(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBGallery.IS_LIKE))));
				galleryDataList.add(model);
			}
		}

		if (cursor != null)
			cursor.close();

	}

	/**
	 * method to call GetGalleryTask
	 * 
	 */
	private void callGetGalleryTask() {
		getGalleryTask = new GetGalleryTask();
		getGalleryTask.execute();
	}

	/**
	 * AsyncTask to call WS for getting the data of gallery and display through
	 * adapter
	 * 
	 * @param isAsyncCallRequired
	 *            - true when no data available in DB
	 * @param isLoadMore
	 *            - true when need to load more data
	 */
	private class GetGalleryTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			emptyStub.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
			gridview.setOnItemClickListener(null);
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (isAsyncCallRequired) {
				if (WebService.isNetworkAvailable(getActivity())) {
					isLoadMore = true;
					galleryDataList = wsGetGallery.executeService(lastRecordTimeStamp, galleryDataList);
				} else {
					getOfflineDataList();
				}
			} else {
				getOfflineDataList();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (isCancelled())
				return;
			progressBar.setVisibility(View.GONE);
			if (isAsyncCallRequired) {
				if (WebService.isNetworkAvailable(getActivity())) {
					// Notify PullToRefreshLayout that the refresh has finished
					mPullToRefreshLayout.setRefreshComplete();
					isRefresh = false;
					if (wsGetGallery.isSuccess()) {
						totalRecords = wsGetGallery.getTotalRecord();// Total
																		// Count
																		// from
																		// server
						setGalleryData();
					} else {
						Util.displayDialog(getActivity(), wsGetGallery.getMessage(), false);
					}
				} else {
					setGalleryData();
				}
			} else {
				setGalleryData();
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}

	/**
	 * Call when list view is scrolled and last item of list is visible
	 * 
	 * @param view
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 * @param totalItemCount
	 ***/
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int lastInScreen = firstVisibleItem + visibleItemCount;
		if (WebService.isNetworkAvailable(getActivity())) {
			if ((lastInScreen == totalItemCount) && !isLoadMore) {
				if (galleryDataList != null) {
					if (getGalleryTask != null && (getGalleryTask.getStatus() == AsyncTask.Status.FINISHED)) {
						if (galleryDataList.size() < totalRecords) {
							isAsyncCallRequired = true;
							setLastRecordTimeStamp();
							callGetGalleryTask();
						}
					}
				}
			}
		}

	}

	/**
	 * Call when list view item click generate
	 * 
	 * @param adapterView
	 * @param view
	 * @param pos
	 * @param id
	 ***/
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int pos, long arg3) {
		// Pass Basic detail in Detail Screen
		gridview.setOnItemClickListener(null);
		GalleryDetailFragment detailFragment = new GalleryDetailFragment();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		detailFragment.setGalleryData(galleryDataList, pos);
		transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
		transaction.add(R.id.content_frame, detailFragment, GalleryDetailFragment.class.getSimpleName());
		transaction.addToBackStack(GalleryFragment.class.getSimpleName());
		transaction.hide(this);
		transaction.commit();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 2) {
				resetGalleryData();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (getGalleryTask != null && getGalleryTask.getStatus() == AsyncTask.Status.RUNNING) {
			getGalleryTask.cancel(true);
		}
	}
}
