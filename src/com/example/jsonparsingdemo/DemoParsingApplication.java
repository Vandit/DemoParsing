package com.example.jsonparsingdemo;

import android.app.Application;

import com.example.jsonparsingdemo.db.DBHelper;

/**
 * Purpose:This class is use for handle common data.
 * 
 * @author Vandit Patel
 * @version 1.0
 * @date 01/05/14
 */
public class DemoParsingApplication extends Application {

	private DBHelper dbHelper;
//	public ImageLoader imageLoader;
//	public DisplayImageOptions imageOptions;

	/**
	 * Called when application start 
	 ***/
	@Override
	public void onCreate() {
		super.onCreate();
		// Initialize database
//		dbHelper = new DBHelper(this);
//		try {
//			// Create Database when assets available
//			dbHelper.createDataBase();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		// Open Database
		dbHelper.openDataBase();
		// Configure Universal Image Loader
//		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).threadPoolSize(5).threadPriority(Thread.MIN_PRIORITY + 2).memoryCacheSize(2500000).memoryCache(new FIFOLimitedMemoryCache(2400000)).denyCacheImageMultipleSizesInMemory()
//				.denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new HashCodeFileNameGenerator()).build();
//		// Initialize ImageLoader 
//		imageLoader = ImageLoader.getInstance();
//		imageLoader.init(config);
	}

	/**
	 *  Retrieve instance of database
	 **/
	public DBHelper getDbHelper() {
		return dbHelper;
	}


	/**
	 *  Retrieve instance of imageOptions for thumb images
	 **/
//	public DisplayImageOptions getImageOptions() {
//		if (imageOptions == null)
//			imageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default3).showImageForEmptyUri(R.drawable.default3).showImageOnFail(R.drawable.default3).cacheInMemory(true).cacheOnDisc(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).build();
//
//		return imageOptions;
//	}

	/**
	 *  Retrieve instance of imageOptions for full images
	 **/
//	public DisplayImageOptions getFullImageOptions() {
//		if (imageOptions == null)
//			imageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default1).showImageForEmptyUri(R.drawable.default1).showImageOnFail(R.drawable.default1).cacheInMemory(true).cacheOnDisc(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).build();
//
//		return imageOptions;
//	}


	/**
	 *  Call when application is close
	 **/
	@Override
	public void onTerminate() {
		super.onTerminate();
		if (dbHelper != null) {
			dbHelper.close();
		}
	}

}