package ng.com.police.adapter;

import java.io.File;
import java.util.ArrayList;

import ng.com.police.PoliceApp;
import ng.com.police.R;
import ng.com.police.common.DownloadManagerActivity;
import ng.com.police.common.Util;
import ng.com.police.db.DBHelper;
import ng.com.police.fragment.CommentDialogFragment;
import ng.com.police.fragment.GalleryFragment;
import ng.com.police.model.GalleryModel;
import ng.com.police.view.BaseActivity;
import ng.com.police.webservice.WebService;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Purpose:This class is use for set gallery data in to list.
 * 
 * @author Vandit Patel
 * @version 1.0
 * @date 01/05/14
 */
public class GalleryAdapter extends BaseAdapter {

	private ArrayList<GalleryModel> galleryDataList;
	private Activity mContext;
	private LayoutInflater inflater;
	private ViewHolder holder;
	private PoliceApp policeApp;
	private GalleryFragment galleryFragment;
	private int pos;
	private DBHelper dbHelper;

	/**
	 * parameterized constructor
	 * 
	 * @param context
	 *            - defines current activity
	 * @param galleryList
	 *            - defines object of type ListData which stores detail of
	 *            gallery data
	 * @param galleryFragment
	 *            - Instance of {@link GalleryFragment}
	 */
	public GalleryAdapter(Activity context, ArrayList<GalleryModel> galleryList, GalleryFragment galleryFragment) {
		this.galleryDataList = galleryList;
		this.mContext = context;
		inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		policeApp = (PoliceApp) mContext.getApplicationContext();
		dbHelper = policeApp.getDbHelper();
		this.galleryFragment = galleryFragment;
	}

	@Override
	public int getCount() {
		return galleryDataList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < galleryDataList.size())
			return galleryDataList.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		holder = new ViewHolder();
		try {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.row_gallery, null);
				holder.imgHolder = (ImageView) convertView.findViewById(R.id.row_gallery_img_background);
				holder.txtDescription = (TextView) convertView.findViewById(R.id.row_gallery_txt_desc);
				holder.txtComment = (TextView) convertView.findViewById(R.id.row_gallery_txt_comment);
				holder.txtLike = (TextView) convertView.findViewById(R.id.row_gallery_txt_like);

				holder.imgDownload = (ImageView) convertView.findViewById(R.id.row_gallery_img_download);
				holder.imgFacebook = (ImageView) convertView.findViewById(R.id.row_gallery_img_facebook);
				holder.imgGPlus = (ImageView) convertView.findViewById(R.id.row_gallery_img_google_plus);
				holder.imgTwitter = (ImageView) convertView.findViewById(R.id.row_gallery_img_twitter);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final GalleryModel galleryModel = galleryDataList.get(position);
			holder.imgHolder.setTag(galleryModel.getImagePath());
			if (galleryModel.getIsLike() == 0) {
				holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.icn_like), null, null, null);
			} else {
				holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.icn_like_selected), null, null, null);
			}
			policeApp.imageLoader.displayImage(holder.imgHolder.getTag().toString(), holder.imgHolder, policeApp.getFullImageOptions());

			holder.txtDescription.setText(Html.fromHtml(galleryModel.getImageShortDescription()));
			holder.txtComment.setText(galleryModel.getImageCommentCount());
			holder.txtLike.setText(galleryModel.getImageLikeCount());
			// Handle click event of download
			holder.imgDownload.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (WebService.isNetworkAvailable(mContext)) {
						if (galleryModel.getImagePath() != null && !galleryModel.getImagePath().equals("")) {
							new DownloadManagerActivity(mContext, galleryModel.getImagePath().toString(), "Photo_" + System.currentTimeMillis() + ".jpeg", mContext.getString(R.string.common_download_photo));
						}
					} else {
						Util.displayDialog(mContext, mContext.getString(R.string.common_internet), false);
					}
				}
			});
			// Handle click event of facebook
			holder.imgFacebook.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					shareSocialMedia(holder.txtDescription.getText().toString().trim(), galleryModel.getImagePath(), 1);
				}
			});
			// Handle click event of twitter
			holder.imgTwitter.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					shareSocialMedia(holder.txtDescription.getText().toString().trim(), galleryModel.getImagePath(), 2);

				}
			});
			// Handle click event of google plus
			holder.imgGPlus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					shareSocialMedia(holder.txtDescription.getText().toString().trim(), galleryModel.getImagePath(), 3);
				}
			});
			// Handle click event of like
			holder.txtLike.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					pos = position;
					((BaseActivity) mContext).callAddLikeTask(galleryModel.getGalleryId(), galleryModel.getIsLike());
				}
			});
			// Handle click event of comment
			holder.txtComment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (WebService.isNetworkAvailable(mContext)) {
						CommentDialogFragment commentDialogFragment = new CommentDialogFragment();
						Bundle bundle = new Bundle();
						bundle.putString(mContext.getString(R.string.extra_id), galleryModel.getGalleryId());
						bundle.putInt(mContext.getString(R.string.extra_module_id), 2);
						commentDialogFragment.setArguments(bundle);
						commentDialogFragment.setTargetFragment(galleryFragment, 2);
						commentDialogFragment.show(galleryFragment.getFragmentManager(), null);
					} else {
						Util.displayDialog(mContext, mContext.getString(R.string.common_internet), false);
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	private class ViewHolder {
		private TextView txtDescription;
		private TextView txtLike;
		private TextView txtComment;
		private ImageView imgDownload;
		private ImageView imgFacebook;
		private ImageView imgTwitter;
		private ImageView imgGPlus;
		private ImageView imgHolder;
	}

	/**
	 * Share data over social media as per selection
	 * 
	 * @param message
	 *            - message to share on social media
	 * @param imagePath
	 *            - path of image share to social media
	 * @param mediaId
	 *            - selected social media
	 **/
	private void shareSocialMedia(String message, String imagePath, int mediaId) {
		final File cachedImage = policeApp.imageLoader.getDiscCache().get(imagePath);
		if (WebService.isNetworkAvailable(mContext)) {
			if (mediaId == 1) {
				((BaseActivity) mContext).shareToFacebook(message, cachedImage);
			} else if (mediaId == 2) {
				if (message.length() >= 115) {
					((BaseActivity) mContext).getTwitterCall(message.substring(0, 115), cachedImage.toString());
				} else {
					((BaseActivity) mContext).getTwitterCall(message, cachedImage.toString());
				}
			} else if (mediaId == 3) {
				((BaseActivity) mContext).onClickGooglePlus(message, cachedImage.toString());
			}
		} else {
			Util.displayDialog(mContext, mContext.getString(R.string.common_internet), false);
		}

	}

	/**
	 * Update like count of image on web end and db also
	 */
	public void updateLike() {
		galleryDataList.get(pos).setIsLike(1);
		galleryDataList.get(pos).setImageLikeCount(String.valueOf(Integer.valueOf(galleryDataList.get(pos).getImageLikeCount()) + 1));
		dbHelper.updateGalleryLike(galleryDataList.get(pos).getGalleryId(), galleryDataList.get(pos).getImageLikeCount(), "1");
		notifyDataSetChanged();
	}

}
