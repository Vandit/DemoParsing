package ng.com.police.adapter;

import java.util.ArrayList;

import ng.com.police.PoliceApp;
import ng.com.police.R;
import ng.com.police.common.Util;
import ng.com.police.fragment.BlogFragment;
import ng.com.police.fragment.CommentDialogFragment;
import ng.com.police.model.BlogsModel;
import ng.com.police.view.BaseActivity;
import ng.com.police.webservice.WebService;
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
 * Purpose:This class is use for set blog data in to list.
 * 
 * @author Vandit Patel
 * @version 1.0
 * @date 01/05/14
 */
public class BlogAdapter extends BaseAdapter {

	private ArrayList<BlogsModel> list = new ArrayList<BlogsModel>();
	private Context mContext;
	private LayoutInflater inflater;
	private ViewHolder holder;
	private PoliceApp policeApp;
	private BlogFragment blogFragment;

	/**
	 * parameterized constructor
	 * 
	 * @param context
	 *            - defines current activity
	 * @param arrayList
	 *            - defines object of type ListData which stores detail of
	 *            blog
	 * @param blogFragment
	 *            - Instance of {@link BlogFragment}
	 */
	public BlogAdapter(Context context, ArrayList<BlogsModel> arrayList, BlogFragment blogFragment) {
		this.list = arrayList;
		this.mContext = context;
		inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		policeApp = (PoliceApp) mContext.getApplicationContext();
		this.blogFragment = blogFragment;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < list.size())
			return list.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		private TextView txtTitle, txtDetail, txtTime, txtComment;
		private ImageView btnFacebook, btnTwitter, btnGPlus, imgHolder;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		holder = new ViewHolder();
		try {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.row_blog, null);
				holder.imgHolder = (ImageView) convertView.findViewById(R.id.row_blog_imgPolice);
				holder.txtTitle = (TextView) convertView.findViewById(R.id.row_blog_tvTitle);
				holder.txtDetail = (TextView) convertView.findViewById(R.id.row_blog_tvDetail);
				holder.txtComment = (TextView) convertView.findViewById(R.id.row_blog_tvComent);
				holder.txtTime = (TextView) convertView.findViewById(R.id.row_blog_tvTime);
				holder.btnTwitter = (ImageView) convertView.findViewById(R.id.row_blog_btnTwitter);
				holder.btnGPlus = (ImageView) convertView.findViewById(R.id.row_blog_btnGPlus);
				holder.btnFacebook = (ImageView) convertView.findViewById(R.id.row_blog_btnFacebook);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.imgHolder.setTag(list.get(position).getFullimage_path());
			policeApp.imageLoader.displayImage(holder.imgHolder.getTag().toString(), holder.imgHolder, policeApp.getFullImageOptions());

			holder.txtTitle.setText(list.get(position).getTitle());
			holder.txtDetail.setText(Html.fromHtml(list.get(position).getShort_description()));
			holder.txtTime.setText(list.get(position).getCreated_date());
			holder.txtComment.setText(list.get(position).getComments());

			// Handle click event of comments
			holder.txtComment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (WebService.isNetworkAvailable(mContext)) {
						CommentDialogFragment commentDialogFragment = new CommentDialogFragment();
						Bundle bundle = new Bundle();
						bundle.putString(mContext.getString(R.string.extra_id), list.get(position).getId());
						bundle.putInt(mContext.getString(R.string.extra_module_id), 0);
						commentDialogFragment.setArguments(bundle);
						commentDialogFragment.setTargetFragment(blogFragment, 2);
						commentDialogFragment.show(blogFragment.getFragmentManager(), null);
					} else {
						Util.displayDialog(mContext, mContext.getString(R.string.common_internet), false);
					}
				}

			});
			// Handle click event of Facebook
			holder.btnFacebook.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((BaseActivity) mContext).shareToFacebook(list.get(position).getShare_url(), null);
					notifyDataSetChanged();
				}
			});
			// Handle click event of twitter
			holder.btnTwitter.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((BaseActivity) mContext).getTwitterCall(list.get(position).getShare_url(), "");
					notifyDataSetChanged();
				}
			});
			// Handle click event of google plus
			holder.btnGPlus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((BaseActivity) mContext).onClickGooglePlus(list.get(position).getShare_url(), "");
					notifyDataSetChanged();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

}
