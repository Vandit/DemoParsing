package com.example.jsonparsingdemo.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.example.jsonparsingdemo.R;

/**
 * This Class is used for defining all database methods.
 * 
 * @author Mihir Palkhiwala
 * @version 1.0
 * @date 01022014
 */
public class DBHelper_assets extends SQLiteOpenHelper {

	// private final String TAG = getClass().getSimpleName();
	// private final int VERSOIN = 1;
	private Context context;
	private SQLiteDatabase database;
	private String DATABASE_PATH = "";
	private String DB_NAME;
	private final String TABLE_NEWS = "News";
	private final String TABLE_CRIMEREPORT = "ReportCrime";
	private final String TABLE_BLOGS = "Blogs";
	private final String TABLE_DANGER_SPOT = "DangerSpot";
	private final String TABLE_EMERGENCY_ALERT = "EmergencyAlert";
	private final String TABLE_EVENTS = "Events";
	private final String TABLE_FORUM = "Forum";
	private final String TABLE_FORUM_CATEGORY = "ForumCategory";
	private final String TABLE_POLICE_STATION = "PoliceStation";
	private final String TABLE_REGION = "Region";
	private final String TABLE_COMMENT = "Comment";
	private final String TABLE_GALLERY = "Gallery";
	private final String TABLE_WANTED_PEOPLES = "WantedPeoples";
	private final String TABLE_MISSING_PEOPLES = "MissingPeoples";
	private final String TABLE_SECTION_PAGE_CONFIG = "SectionPageConfig";

	// private SimpleDateFormat sdf = new
	// SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

	@SuppressLint("SdCardPath")
	public DBHelper_assets(Context context) {
		super(context, context.getResources().getString(R.string.db_name), null, 1);
		this.context = context;
		DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
		DB_NAME = context.getResources().getString(R.string.db_name);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	/**
	 * This method will create database in application package /databases
	 * directory when first time application launched
	 **/
	public void createDataBase() throws IOException {
		boolean mDataBaseExist = checkDataBase();
		if (!mDataBaseExist) {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException mIOException) {
				mIOException.printStackTrace();
				throw new Error("Error copying database");
			} finally {
				this.close();
			}
		}
	}

	/** This method checks whether database is exists or not **/
	private boolean checkDataBase() {
		try {
			final String mPath = DATABASE_PATH + DB_NAME;
			final File file = new File(mPath);
			if (file.exists())
				return true;
			else
				return false;
		} catch (SQLiteException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This method will copy database from /assets directory to application
	 * package /databases directory
	 **/
	private void copyDataBase() throws IOException {
		try {
			InputStream mInputStream = context.getAssets().open(DB_NAME);
			String outFileName = DATABASE_PATH + DB_NAME;
			OutputStream mOutputStream = new FileOutputStream(outFileName);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = mInputStream.read(buffer)) > 0) {
				mOutputStream.write(buffer, 0, length);
			}
			mOutputStream.flush();
			mOutputStream.close();
			mInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method is used for copying database on to sd card.
	 */
	public void copyDatabaseToSdCard() {
		try {
			File f1 = new File(DATABASE_PATH + DB_NAME);
			if (f1.exists()) {
				File f2 = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + DB_NAME);
				f2.createNewFile();
				InputStream in = new FileInputStream(f1);
				OutputStream out = new FileOutputStream(f2);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** This method open database for operations **/
	public void openDataBase() throws SQLException {
		String mPath = DATABASE_PATH + DB_NAME;
		database = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READWRITE);
	}

	/** This method close database connection and released occupied memory **/
	@Override
	public synchronized void close() {
		if (database != null) {
			database.close();
		}
		SQLiteDatabase.releaseMemory();
		super.close();
	}

/*	*//** This method clear database memory **//*
	public void clearAllDatabase() {
		database.delete(TABLE_NEWS, null, null);
		database.delete(TABLE_BLOGS, null, null);
		database.delete(TABLE_DANGER_SPOT, null, null);
		database.delete(TABLE_EMERGENCY_ALERT, null, null);
		database.delete(TABLE_EVENTS, null, null);
		database.delete(TABLE_FORUM, null, null);
		database.delete(TABLE_FORUM_CATEGORY, null, null);
		database.delete(TABLE_POLICE_STATION, null, null);
		database.delete(TABLE_REGION, null, null);
		database.delete(TABLE_COMMENT, null, null);
	}

	*//**
	 * method for inserting value to the News Table
	 * 
	 * @param id
	 * @param title
	 * @param description
	 * @param image_path
	 * @param created_by
	 * @param created_date
	 * @param share_url
	 * @return
	 *//*
	public long insertNews(final String id, final String title, final String description, final String image_path, final String created_by, final String created_date, final String share_url) {
		final ContentValues values = new ContentValues();

		values.put(DBNews.ID, id);
		values.put(DBNews.TITLE, title);
		values.put(DBNews.DESCRIPTION, description);
		values.put(DBNews.IMAGE_PATH, image_path);
		values.put(DBNews.SHORTDESCRIPTION, created_by);
		values.put(DBNews.CREATED_DATE, created_date);
		values.put(DBNews.SHARE_URL, share_url);

		return database.insert(TABLE_NEWS, null, values);
	}

	*//**
	 * method for delet data from News Table this method is call when new data
	 * from WS is available
	 * 
	 * @return
	 *//*
	public int deleteNews() {
		return database.delete(TABLE_NEWS, null, null);
	}

	*//**
	 * method for inserting data in CrimeReport table this data are use when
	 * Service for submit Attachment is called
	 * 
	 * @param UserId
	 * @param id
	 * @param attachmentPath
	 * @param tag
	 * @return
	 *//*
	public long insertCrimeReport(final String UserId, final String id, final String attachmentPath, final String tag) {
		final ContentValues values = new ContentValues();

		values.put(DBCrimeReport.REPORTID, id);
		values.put(DBCrimeReport.USERID, UserId);
		values.put(DBCrimeReport.ATTECHMENT, attachmentPath);
		values.put(DBCrimeReport.ATTECHMENTTAG, tag);

		return database.insert(TABLE_CRIMEREPORT, null, values);
	}

	*//**
	 * method for check is there any record in the database
	 * 
	 * @return
	 *//*
	public long isPendingReportsAvailable() {
		int reportdata = 0;
		final Cursor cursor = database.rawQuery("Select * from " + TABLE_CRIMEREPORT + " LIMIT 1", null);
		if (cursor != null)
			reportdata = cursor.getCount();
		return reportdata;
	}

	*//**
	 * method for delete data of specific crimeID from the CrimeReport Table
	 * 
	 * @param crimeId
	 * @return
	 *//*
	public int deleteCrimeReport(String crimeId) {
		return database.delete(TABLE_CRIMEREPORT, DBCrimeReport.REPORTID + "=?", new String[] { crimeId });
	}

	*//**
	 * method for getting crime report pending it search data from CrimeReport
	 * Table
	 * 
	 * @return
	 *//*
	public ArrayList<ReportCrimeModel> getPendingCrimeReports() {
		Cursor cursor;
		ArrayList<ReportCrimeModel> crimeReportList = new ArrayList<ReportCrimeModel>();
		ReportCrimeModel crimeModel;
		cursor = database.query(TABLE_CRIMEREPORT, null, null, null, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				crimeModel = new ReportCrimeModel();
				crimeModel.setAttachmentPath(cursor.getString(cursor.getColumnIndex(DBCrimeReport.ATTECHMENT)));
				crimeModel.setAttachmentTag(cursor.getString(cursor.getColumnIndex(DBCrimeReport.ATTECHMENTTAG)));
				crimeModel.setCrimeId(cursor.getString(cursor.getColumnIndex(DBCrimeReport.REPORTID)));
				crimeModel.setUserId(cursor.getString(cursor.getColumnIndex(DBCrimeReport.USERID)));
				crimeReportList.add(crimeModel);
			}
		}
		if (cursor != null) {
			cursor.close();
		}
		return crimeReportList;
	}

	*//**
	 * method for getting news data from TABLE_NEWS
	 * 
	 * @return
	 *//*
	public Cursor getNewsDataList() {
		Cursor cursor = database.query(TABLE_NEWS, null, null, null, null, null, null, null);
		return cursor;
	}

	public boolean isNewsDataAvailable() {
		boolean isDataExist = false;
		final Cursor cursor = database.rawQuery("Select * from " + TABLE_NEWS + " LIMIT 1", null);
		if (cursor != null && cursor.getCount() > 0) {
			isDataExist = true;
			cursor.close();
		}
		return isDataExist;
	}

	*//**
	 * method for inserting data to Blogs in table blogs
	 * 
	 * @param id
	 * @param title
	 * @param description
	 * @param image_path
	 * @param created_date
	 * @param comments
	 * @param short_description
	 * @param share_url
	 * @return
	 *//*
	public long insertBlogs(final String id, final String title, final String description, final String image_path,  final String created_date, final String comments,
			final String short_description, final String share_url) {
		final ContentValues values = new ContentValues();

		values.put(DBBlogs.ID, id);
		values.put(DBBlogs.TITLE, title);
		values.put(DBBlogs.DESCRIPTION, description);
		values.put(DBBlogs.IMAGE_PATH, image_path);
//		values.put(DBBlogs.CREATED_BY, created_by);
		values.put(DBBlogs.CREATED_DATE, created_date);
		values.put(DBBlogs.COMMENTS, comments);
		values.put(DBBlogs.SHORT_DESCRIPTION, short_description);
		values.put(DBBlogs.SHARE_URL, share_url);
		return database.insert(TABLE_BLOGS, null, values);
	}

	*//**
	 * delete data from TABLE_BLOGD when data from WS is available
	 * 
	 * @return
	 *//*
	public int deleteBlogs() {
		return database.delete(TABLE_BLOGS, null, null);
	}

	*//**
	 * method to get all data of TABLE_BLOGS
	 * 
	 * @return
	 *//*
	public Cursor getBlogsDataList() {
		Cursor cursor = database.query(TABLE_BLOGS, null, null, null, null, null, null, null);
		return cursor;
	}

	*//**
	 * method for check if data in TABLE_BLOGS is available or not
	 * 
	 * @return
	 *//*
	public boolean isBlogDataAvailable() {
		boolean isDataExist = false;
		final Cursor cursor = database.rawQuery("Select * from " + TABLE_BLOGS + " LIMIT 1", null);
		if (cursor != null && cursor.getCount() > 0) {
			isDataExist = true;
			cursor.close();
		}
		return isDataExist;
	}

	*//**
	 * method for inserting data to Missing Peoples in table MissingPeoples
	 * 
	 * @param id
	 * @param title
	 * @param description
	 * @param image_path
	 * @param created_date
	 * @param contact
	 * @param address
	 * @param short_description
	 * @param share_url
	 * @return
	 *//*
	public long insertMissingPeoples(final String id, final String title, final String description, final String image_path,  final String created_date,
			final String short_description, final String share_url, final String contact) {
		final ContentValues values = new ContentValues();
		values.put(DBMissingPeoples.ID, id);
		values.put(DBMissingPeoples.TITLE, title);
		values.put(DBMissingPeoples.DESCRIPTION, description);
		values.put(DBMissingPeoples.IMAGE_PATH, image_path);
		values.put(DBMissingPeoples.CREATED_DATE, created_date);
		values.put(DBMissingPeoples.SHORT_DESCRIPTION, short_description);
		values.put(DBMissingPeoples.SHARE_URL, share_url);
		values.put(DBMissingPeoples.CONTACT, contact);
//		values.put(DBMissingPeoples.ADDRESS, address);
		return database.insert(TABLE_MISSING_PEOPLES, null, values);
	}

	*//**
	 * delete data from TABLE_MISSING_PEOPLE when data from WS is available
	 * 
	 * @return
	 *//*
	public int deleteMissingPeople() {
		return database.delete(TABLE_MISSING_PEOPLES, null, null);
	}

	*//**
	 * method to get all data of TABLE_MISSING_PEOPLE
	 * 
	 * @return
	 *//*
	public Cursor getMissingPeoplesDataList() {
		Cursor cursor = database.query(TABLE_MISSING_PEOPLES, null, null, null, null, null, null, null);
		return cursor;
	}

	*//**
	 * method for check if data in TABLE_MISSING_PEOPLE is available or not
	 * 
	 * @return
	 *//*
	public boolean isMissingPeoplesDataAvailable() {
		boolean isDataExist = false;
		final Cursor cursor = database.rawQuery("Select * from " + TABLE_MISSING_PEOPLES + " LIMIT 1", null);
		if (cursor != null && cursor.getCount() > 0) {
			isDataExist = true;
			cursor.close();
		}
		return isDataExist;
	}
	

	
	*//**
	 * method for inserting data to Wanted Peoples in table WantedPeoples
	 * 
	 * @param id
	 * @param title
	 * @param description
	 * @param image_path
	 * @param created_date
	 * @param contact
	 * @param short_description
	 * @param share_url
	 * @return
	 *//*
	public long insertWantedPeoples(final String id, final String title, final String description, final String image_path,  final String created_date,
			final String short_description, final String share_url, final String contact) {
		final ContentValues values = new ContentValues();
		values.put(DBWantedPeoples.ID, id);
		values.put(DBWantedPeoples.TITLE, title);
		values.put(DBWantedPeoples.DESCRIPTION, description);
		values.put(DBWantedPeoples.IMAGE_PATH, image_path);
		values.put(DBWantedPeoples.CREATED_DATE, created_date);
		values.put(DBWantedPeoples.SHORT_DESCRIPTION, short_description);
		values.put(DBWantedPeoples.SHARE_URL, share_url);
		values.put(DBWantedPeoples.CONTACT, contact);
		return database.insert(TABLE_WANTED_PEOPLES, null, values);
	}

	*//**
	 * delete data from TABLE_WANTED_PEOPLE when data from WS is available
	 * 
	 * @return
	 *//*
	public int deleteWantedPeople() {
		return database.delete(TABLE_WANTED_PEOPLES, null, null);
	}

	*//**
	 * method to get all data of TABLE_WANTED_PEOPLE
	 * 
	 * @return
	 *//*
	public Cursor getWantedPeoplesDataList() {
		Cursor cursor = database.query(TABLE_WANTED_PEOPLES, null, null, null, null, null, null, null);
		return cursor;
	}

	*//**
	 * method for check if data in TABLE_WANTED_PEOPLE is available or not
	 * 
	 * @return
	 *//*
	public boolean isWantedPeoplesDataAvailable() {
		boolean isDataExist = false;
		final Cursor cursor = database.rawQuery("Select * from " + TABLE_WANTED_PEOPLES + " LIMIT 1", null);
		if (cursor != null && cursor.getCount() > 0) {
			isDataExist = true;
			cursor.close();
		}
		return isDataExist;
	}
	*//**
	 * method for inserting data to table Gallery
	 * 
	 * @param id
	 * @param title
	 * @param description
	 * @param image_path
	 * @param created_by
	 * @param created_date
	 * @param comments_count
	 * @param like_count
	 * @param download_count
	 * @param slug
	 * @param short_description
	 * @param share_url
	 * @return
	 *//*
	public long insertGalleryData(final String id, final String title, final String description, final String image_path, final String created_date, final String comments_count,
			final String short_description, final String share_url, final String download_count, final String like_count, final String slug, int isLike) {
		final ContentValues values = new ContentValues();
		values.put(DBGallery.ID, id);
		values.put(DBGallery.TITLE, title);
		values.put(DBGallery.DESCRIPTION, description);
		values.put(DBGallery.IMAGE_PATH, image_path);
		values.put(DBGallery.CREATED_DATE, created_date);
		values.put(DBGallery.COMMENTS_COUNT, comments_count);
		values.put(DBGallery.LIKE_COUNT, like_count);
		values.put(DBGallery.DOWNLOAD_COUNT, download_count);
		values.put(DBGallery.SLUG, slug);
		values.put(DBGallery.SHORT_DESCRIPTION, short_description);
		values.put(DBGallery.SHARE_URL, share_url);
		values.put(DBGallery.IS_LIKE, isLike);
		return database.insert(TABLE_GALLERY, null, values);
	}

	*//**
	 * delete data from TABLE_GALLERY when data from WS is available
	 * 
	 * @return
	 *//*
	public int deleteGallery() {
		return database.delete(TABLE_GALLERY, null, null);
	}

	*//**
	 * method to get all data of TABLE_GALLERY
	 * 
	 * @return
	 *//*
	public Cursor getGalleryDataList() {
		Cursor cursor = database.query(TABLE_GALLERY, null, null, null, null, null, null, null);
		return cursor;
	}

	*//**
	 * method for check if data in TABLE_GALLERY is available or not
	 * 
	 * @return
	 *//*
	public boolean isGalleryDataAvailable() {
		boolean isDataExist = false;
		final Cursor cursor = database.rawQuery("Select * from " + TABLE_GALLERY + " LIMIT 1", null);
		if (cursor != null && cursor.getCount() > 0) {
			isDataExist = true;
			cursor.close();
		}
		return isDataExist;
	}
	
	
	
	
	
	*//**
	 * method for inserting data in TABLE_FORUMS
	 * 
	 * @param id
	 * @param title
	 * @param description
	 * @param created_date
	 * @param comments
	 * @param short_description
	 * @param share_url
	 * @return
	 *//*
	public long insertForums(final String id, final String title, final String description, final String created_date, final String comments,
			final String short_description, final String share_url) {
		final ContentValues values = new ContentValues();

		values.put(DBForums.ID, id);
		values.put(DBForums.TITLE, title);
		values.put(DBForums.DESCRIPTION, description);
//		values.put(DBForums.CATEGORY_NAME, category_name);
//		values.put(DBForums.CREATED_BY, created_by);
		values.put(DBForums.CREATED_DATE, created_date);
		values.put(DBForums.COMMENTS, comments);
		values.put(DBForums.SHORT_DESCRIPTION, short_description);
		values.put(DBForums.SHARE_URL, share_url);
		return database.insert(TABLE_FORUM, null, values);
	}

	*//**
	 * method to delete data form TABLE_FORUMS if new data from WS is available
	 * 
	 * @return
	 *//*
	public int deleteForums() {
		return database.delete(TABLE_FORUM, null, null);
	}

	*//**
	 * method to check where the data in TABLE_FORUMS is available or not
	 * 
	 * @return
	 *//*
	public boolean isForumDataAvailable() {
		boolean isDataExist = false;
		final Cursor cursor = database.rawQuery("Select * from " + TABLE_FORUM + " LIMIT 1", null);
		if (cursor != null && cursor.getCount() > 0) {
			isDataExist = true;
			cursor.close();
		}
		return isDataExist;
	}

	*//**
	 * method to get the data from TABLE_FORUM at off line mode
	 * 
	 * @return
	 *//*
	public Cursor getForums() {
		Cursor cursor = database.query(TABLE_FORUM, null, null, null, null, null, null, null);
		return cursor;
	}

	*//**
	 * method for insert Forums Category to the TABLE_FORUM_CATEGORY
	 * 
	 * @param id
	 * @param name
	 * @return
	 *//*
	public long insertForumsCategory(final String id, final String name) {
		final ContentValues values = new ContentValues();

		values.put(DBForumCategoty.ID, id);
		values.put(DBForumCategoty.NAME, name);

		return database.insert(TABLE_FORUM_CATEGORY, null, values);
	}

	*//**
	 * method for delete data from TABLE_FORUM_CATEGORY this method is call when
	 * new data is available by WS
	 * 
	 * @return
	 *//*
	public int deleteForumsCategory() {
		return database.delete(TABLE_FORUM_CATEGORY, null, null);
	}

	*//**
	 * method for getting data from the TABLE_FORUM_CATEGORY
	 * 
	 * @return
	 *//*
	public ArrayList<ForumCategotyModel> getForumsCategory() {
		ArrayList<ForumCategotyModel> forumsCategoryModels = new ArrayList<ForumCategotyModel>();
		final Cursor cursor = database.query(TABLE_FORUM_CATEGORY, null, null, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			ForumCategotyModel model;
			while (cursor.moveToNext()) {
				model = new ForumCategotyModel();
				model.setId(cursor.getInt(cursor.getColumnIndex(DBForumCategoty.ID)));
				model.setName(cursor.getString(cursor.getColumnIndex(DBForumCategoty.NAME)));
				forumsCategoryModels.add(model);
			}
		}

		if (cursor != null)
			cursor.close();

		return forumsCategoryModels;
	}

	*//**
	 * method for inserting the data to TABLE_REGION
	 * 
	 * @param id
	 * @param name
	 * @return
	 *//*
	public long insertRegion(final String id, final String name) {
		final ContentValues values = new ContentValues();

		values.put(DBRegion.ID, id);
		values.put(DBRegion.NAME, name);

		return database.insert(TABLE_REGION, null, values);
	}

	*//**
	 * method for delete data from TABLE_REGION when new data are available from
	 * the WS
	 * 
	 * @return
	 *//*
	public int deleteRegion() {
		return database.delete(TABLE_REGION, null, null);
	}

	*//**
	 * method for getting data from TABLE_REGION this method return ArrayList of
	 * RegionModel class
	 * 
	 * @return
	 *//*
	public ArrayList<RegionModel> getRegion() {
		ArrayList<RegionModel> regionModels = new ArrayList<RegionModel>();
		final Cursor cursor = database.query(TABLE_REGION, null, null, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			RegionModel model;
			while (cursor.moveToNext()) {
				model = new RegionModel();
				model.setId(cursor.getString(cursor.getColumnIndex(DBRegion.ID)));
				model.setName(cursor.getString(cursor.getColumnIndex(DBRegion.NAME)));
				regionModels.add(model);
			}
		}

		if (cursor != null)
			cursor.close();

		return regionModels;
	}

	*//**
	 * method for inserting data to the TABLE_EVENTS this method is called when
	 * data are available by WS
	 * 
	 * @param id
	 * @param title
	 * @param description
	 * @param image_path
	 * @param created_by
	 * @param start_date
	 * @param end_date
	 * @param short_description
	 * @param share_url
	 * @param current_month
	 * @param event_location
	 * @return
	 *//*
	public long insertEvents(final String id, final String title, final String description, final String image_path, final String created_by, final String start_date, final String end_date,
			final String short_description, final String share_url, final String current_month, String event_location) {
		final ContentValues values = new ContentValues();

		values.put(DBEvents.ID, id);
		values.put(DBEvents.TITLE, title);
		values.put(DBEvents.DESCRIPTION, description);
		values.put(DBEvents.IMAGE_PATH, image_path);
		values.put(DBEvents.CREATED_BY, created_by);
		values.put(DBEvents.START_DATE, start_date);
		values.put(DBEvents.END_DATE, end_date);
		values.put(DBEvents.SHORT_DESCRIPTION, short_description);
		values.put(DBEvents.SHARE_URL, share_url);
		values.put(DBEvents.CURRENT_MONTH, current_month);
		values.put(DBEvents.LOCATION, event_location);

		return database.insert(TABLE_EVENTS, null, values);
	}

	*//**
	 * method for deleting data of TABLE_EVENTS when new data from WS is
	 * available
	 * 
	 * @param current_month
	 * @return
	 *//*
	public int deleteEvents(final String current_month) {
		return database.delete(TABLE_EVENTS, DBEvents.CURRENT_MONTH + "=?", new String[] { current_month });
	}

	*//**
	 * 
	 * @param selectedDate
	 * @return
	 *//*
	public ArrayList<EventsModel> getCalendarEventsDataList(final String selectedDate) {
		ArrayList<EventsModel> eventsModels = new ArrayList<EventsModel>();
		Cursor cursor = database.rawQuery("select * from " + TABLE_EVENTS + " where " + DBEvents.START_DATE + " BETWEEN '" + selectedDate + " 00:00:00" + "'" + " AND '" + selectedDate + " 23:00:59"
				+ "'" + " ORDER BY " + DBEvents.START_DATE + " ASC", null);

		// final Cursor cursor = database.query(TABLE_EVENTS, null,
		// DBEvents.CURRENT_MONTH+"=?", new String[]{current_month}, null, null,
		// null, null);
		if (cursor.getCount() > 0) {
			EventsModel model;
			while (cursor.moveToNext()) {
				model = new EventsModel();
				model.setId(cursor.getString(cursor.getColumnIndex(DBEvents.ID)));
				model.setEventTitle(cursor.getString(cursor.getColumnIndex(DBEvents.TITLE)));
				model.setEventDescription(cursor.getString(cursor.getColumnIndex(DBEvents.DESCRIPTION)));
				model.setEventImagePath(cursor.getString(cursor.getColumnIndex(DBEvents.IMAGE_PATH)));
				model.setEventCreatedBy(cursor.getString(cursor.getColumnIndex(DBEvents.CREATED_BY)));
				model.setEventStartDate(cursor.getString(cursor.getColumnIndex(DBEvents.START_DATE)));
				model.setEventEndDate(cursor.getString(cursor.getColumnIndex(DBEvents.END_DATE)));
				model.setEventShortDescription(cursor.getString(cursor.getColumnIndex(DBEvents.SHORT_DESCRIPTION)));
				model.setEventShareUrl(cursor.getString(cursor.getColumnIndex(DBEvents.SHARE_URL)));
				model.setEventCurrentMonth(cursor.getString(cursor.getColumnIndex(DBEvents.CURRENT_MONTH)));
				model.setEventLocation(cursor.getString(cursor.getColumnIndex(DBEvents.LOCATION)));
				eventsModels.add(model);
			}
		}

		if (cursor != null)
			cursor.close();

		return eventsModels;
	}

	*//**
	 * method for inserting data of DangerSpot in TABLE_DANGER_SPOT when data is
	 * available from WS
	 * 
	 * @param id
	 * @param title
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @param created_by
	 * @param created_date
	 * @return
	 *//*
	public long insertDangerSpot(final String id, final String title, final String latitude, final String longitude, final String radius, final String created_by, final String created_date) {
		final ContentValues values = new ContentValues();

		values.put(DBDangerSpot.ID, id);
		values.put(DBDangerSpot.TITLE, title);
		values.put(DBDangerSpot.LATITUDE, latitude);
		values.put(DBDangerSpot.LONGITUDE, longitude);
		values.put(DBDangerSpot.RADIUS, radius);
		values.put(DBDangerSpot.CREATED_BY, created_by);
		values.put(DBDangerSpot.CREATED_DATE, created_date);

		return database.insert(TABLE_DANGER_SPOT, null, values);
	}

	*//**
	 * method for delete Danger Spot from TABLE_DANGER_SPOT method call when
	 * data is available from WS
	 * 
	 * @return
	 *//*
	public int deleteDangerSpot() {
		return database.delete(TABLE_DANGER_SPOT, null, null);
	}

	public ArrayList<DangerSpotModel> getDangerSpot() {
		ArrayList<DangerSpotModel> dangerSpotModels = new ArrayList<DangerSpotModel>();
		final Cursor cursor = database.query(TABLE_DANGER_SPOT, null, null, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			DangerSpotModel model;
			while (cursor.moveToNext()) {
				model = new DangerSpotModel();
				model.setId(cursor.getString(cursor.getColumnIndex(DBDangerSpot.ID)));
				model.setTitle(cursor.getString(cursor.getColumnIndex(DBDangerSpot.TITLE)));
				model.setLatitude(cursor.getString(cursor.getColumnIndex(DBDangerSpot.LATITUDE)));
				model.setLongitude(cursor.getString(cursor.getColumnIndex(DBDangerSpot.LONGITUDE)));
				model.setRadius(cursor.getString(cursor.getColumnIndex(DBDangerSpot.RADIUS)));
				model.setCreated_by(cursor.getString(cursor.getColumnIndex(DBDangerSpot.CREATED_BY)));
				model.setCreated_date(cursor.getString(cursor.getColumnIndex(DBDangerSpot.CREATED_DATE)));
				dangerSpotModels.add(model);
			}
		}

		if (cursor != null)
			cursor.close();

		return dangerSpotModels;
	}

	*//**
	 * method for insertEmergencyAlert to the TABLE_EMERGENCY_ALERT
	 * 
	 * @param id
	 * @param title
	 * @param region_name
	 * @param created_by
	 * @param created_date
	 * @param short_description
	 * @param share_url
	 * @return
	 *//*
	public long insertEmergencyAlert(final String id, final String title, final String region_name, final String created_by, final String created_date, final String short_description,
			final String share_url, final String description) {
		final ContentValues values = new ContentValues();

		values.put(DBEmergencyAlert.ID, id);
		values.put(DBEmergencyAlert.TITLE, title);
		values.put(DBEmergencyAlert.REGION_NAME, region_name);
		values.put(DBEmergencyAlert.CREATED_BY, created_by);
		values.put(DBEmergencyAlert.CREATED_DATE, created_date);
		values.put(DBEmergencyAlert.SHORT_DESCRIPTION, short_description);
		values.put(DBEmergencyAlert.DESCRIPTION, description);
		values.put(DBEmergencyAlert.SHARE_URL, share_url);

		return database.insert(TABLE_EMERGENCY_ALERT, null, values);
	}

	*//**
	 * method for delete from TABLE_EMERGENCY_ALERT when new data from WS id
	 * available
	 * 
	 * @return
	 *//*
	public int deleteEmergencyAlert() {
		return database.delete(TABLE_EMERGENCY_ALERT, null, null);
	}

	*//**
	 * method for getting data of TABLE_EMERGENCY_ALERT
	 * 
	 * @return
	 *//*
	public Cursor getEmergencyAlert() {
		// ArrayList<EmergencyAlertModel> dangerSpotModels = new
		// ArrayList<EmergencyAlertModel>();
		Cursor cursor = database.query(TABLE_EMERGENCY_ALERT, null, null, null, null, null, null, null);
		// if (cursor.getCount() > 0) {
		// EmergencyAlertModel model;
		// while (cursor.moveToNext()) {
		//
		// model = new EmergencyAlertModel();
		// model.setId(cursor.getString(cursor.getColumnIndex(DBEmergencyAlert.ID)));
		// model.setTitle(cursor.getString(cursor.getColumnIndex(DBEmergencyAlert.TITLE)));
		// model.setRegion_name(cursor.getString(cursor.getColumnIndex(DBEmergencyAlert.REGION_NAME)));
		// model.setCreated_by(cursor.getString(cursor.getColumnIndex(DBEmergencyAlert.CREATED_BY)));
		// //
		// model.setCreated_date(DateUtils.getRelativeTimeSpanString(sdf.parse(cursor.getString(cursor.getColumnIndex(DBEmergencyAlert.CREATED_DATE))).getTime()).toString());
		// model.setCreated_date(cursor.getString(cursor.getColumnIndex(DBEmergencyAlert.CREATED_DATE)));
		// model.setShort_description(cursor.getString(cursor.getColumnIndex(DBEmergencyAlert.SHORT_DESCRIPTION)));
		// model.setShare_url(cursor.getString(cursor.getColumnIndex(DBEmergencyAlert.SHARE_URL)));
		// dangerSpotModels.add(model);
		// }
		// }
		//
		// if (cursor != null)
		// cursor.close();

		return cursor;
	}

	*//**
	 * method for check data in TABLE_EMERGENCY_ALERT
	 * 
	 * @return
	 *//*
	public boolean isEmergencyAlertDataAvailable() {
		boolean isDataExist = false;
		final Cursor cursor = database.rawQuery("Select * from " + TABLE_EMERGENCY_ALERT + " LIMIT 1", null);
		if (cursor != null && cursor.getCount() > 0) {
			isDataExist = true;
			cursor.close();
		}
		return isDataExist;
	}

	*//**
	 * method for inserting data TABLE_POLICE_STATION when data is available by
	 * WS
	 * 
	 * @param id
	 * @param name
	 * @param address
	 * @param city
	 * @param zipcode
	 * @param latitude
	 * @param longitude
	 * @param mobile
	 * @param phone
	 * @param email
	 * @param police_head
	 * @param image_path
	 * @param is_near_by
	 * @return
	 *//*
	public long insertPoliceStation(final String id, final String name, final String address, final String city, final String zipcode, final String latitude, final String longitude,
			final String mobile, final String phone, final String email, final String police_head, final String image_path, final String is_near_by) {

		final ContentValues values = new ContentValues();

		values.put(DBPoliceStation.ID, id);
		values.put(DBPoliceStation.NAME, name);
		values.put(DBPoliceStation.ADDRESS, address);
		values.put(DBPoliceStation.CITY, city);
		values.put(DBPoliceStation.ZIPCODE, zipcode);
		values.put(DBPoliceStation.LATITUDE, latitude);
		values.put(DBPoliceStation.LONGITUDE, longitude);
		values.put(DBPoliceStation.MOBILE, mobile);
		values.put(DBPoliceStation.PHONE, phone);
		values.put(DBPoliceStation.EMAIL, email);
		values.put(DBPoliceStation.POLICE_HEAD, police_head);
		values.put(DBPoliceStation.IMAGE_PATH, image_path);
		values.put(DBPoliceStation.IS_NEAR_BY, is_near_by);

		return database.insert(TABLE_POLICE_STATION, null, values);
	}

	*//**
	 * method for deleting data from TABLE_POLICE_STATION
	 * 
	 * @return
	 *//*
	public int deletePoliceStation() {
		return database.delete(TABLE_POLICE_STATION, null, null);
	}

	public ArrayList<PoliceStationModel> getSearchPoliceStation(String searchString) {
		ArrayList<PoliceStationModel> policeStationModels = new ArrayList<PoliceStationModel>();
		final Cursor cursor = database.query(TABLE_POLICE_STATION, null, DBPoliceStation.NAME + " LIKE ? OR " + DBPoliceStation.ZIPCODE + " LIKE ?", new String[] { "%" + searchString + "%",
				"%" + searchString + "%" }, null, null, null, null);
		if (cursor.getCount() > 0) {
			PoliceStationModel model;
			while (cursor.moveToNext()) {
				model = new PoliceStationModel();
				model.setId(cursor.getString(cursor.getColumnIndex(DBPoliceStation.ID)));
				model.setName(cursor.getString(cursor.getColumnIndex(DBPoliceStation.NAME)));
				model.setAddress(cursor.getString(cursor.getColumnIndex(DBPoliceStation.ADDRESS)));
				model.setCity(cursor.getString(cursor.getColumnIndex(DBPoliceStation.CITY)));
				model.setZipcode(cursor.getString(cursor.getColumnIndex(DBPoliceStation.ZIPCODE)));
				model.setLatitude(cursor.getString(cursor.getColumnIndex(DBPoliceStation.LATITUDE)));
				model.setLongitude(cursor.getString(cursor.getColumnIndex(DBPoliceStation.LONGITUDE)));
				model.setMobile(cursor.getString(cursor.getColumnIndex(DBPoliceStation.MOBILE)));
				model.setPhone(cursor.getString(cursor.getColumnIndex(DBPoliceStation.PHONE)));
				model.setEmail(cursor.getString(cursor.getColumnIndex(DBPoliceStation.EMAIL)));
				model.setPolice_head(cursor.getString(cursor.getColumnIndex(DBPoliceStation.POLICE_HEAD)));
				model.setImage_path(cursor.getString(cursor.getColumnIndex(DBPoliceStation.IMAGE_PATH)));
				model.setIs_near_by(cursor.getInt(cursor.getColumnIndex(DBPoliceStation.IS_NEAR_BY)));

				policeStationModels.add(model);
			}
		}

		if (cursor != null)
			cursor.close();

		return policeStationModels;
	}

	*//**
	 * method for getting data from TABLE_POLICE_STATION this method return
	 * ArrayList of type PoliceStationModel
	 * 
	 * @return
	 *//*
	public ArrayList<PoliceStationModel> getPoliceStation() {
		ArrayList<PoliceStationModel> policeStationModels = new ArrayList<PoliceStationModel>();
		final Cursor cursor = database.query(TABLE_POLICE_STATION, null, null, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			PoliceStationModel model;
			while (cursor.moveToNext()) {
				model = new PoliceStationModel();
				model.setId(cursor.getString(cursor.getColumnIndex(DBPoliceStation.ID)));
				model.setName(cursor.getString(cursor.getColumnIndex(DBPoliceStation.NAME)));
				model.setAddress(cursor.getString(cursor.getColumnIndex(DBPoliceStation.ADDRESS)));
				model.setCity(cursor.getString(cursor.getColumnIndex(DBPoliceStation.CITY)));
				model.setZipcode(cursor.getString(cursor.getColumnIndex(DBPoliceStation.ZIPCODE)));
				model.setLatitude(cursor.getString(cursor.getColumnIndex(DBPoliceStation.LATITUDE)));
				model.setLongitude(cursor.getString(cursor.getColumnIndex(DBPoliceStation.LONGITUDE)));
				model.setMobile(cursor.getString(cursor.getColumnIndex(DBPoliceStation.MOBILE)));
				model.setPhone(cursor.getString(cursor.getColumnIndex(DBPoliceStation.PHONE)));
				model.setEmail(cursor.getString(cursor.getColumnIndex(DBPoliceStation.EMAIL)));
				model.setPolice_head(cursor.getString(cursor.getColumnIndex(DBPoliceStation.POLICE_HEAD)));
				model.setImage_path(cursor.getString(cursor.getColumnIndex(DBPoliceStation.IMAGE_PATH)));
				model.setIs_near_by(cursor.getInt(cursor.getColumnIndex(DBPoliceStation.IS_NEAR_BY)));

				policeStationModels.add(model);
			}
		}

		if (cursor != null)
			cursor.close();

		return policeStationModels;
	}

	*//**
	 * method for insertComment to TABLE_COMMENT
	 * 
	 * @param id
	 * @param comment_id
	 * @param comment
	 * @param comment_type
	 * @param created_date
	 * @param created_by
	 * @return
	 *//*
	public long insertComment(final String id, final String comment_id, final String comment, final int comment_type, final String created_date, final String created_by) {

		final ContentValues values = new ContentValues();

		values.put(DBComment.ID, id);
		values.put(DBComment.COMMENT_ID, comment_id);
		values.put(DBComment.COMMENT, comment);
		values.put(DBComment.COMMENT_TYPE, comment_type);
		values.put(DBComment.CREATED_DATE, created_date);
		values.put(DBComment.CREATED_BY, created_by);

		return database.insert(TABLE_COMMENT, null, values);
	}

	*//**
	 * method for delete data from TABLE_COMMENT
	 * 
	 * @param forumOrBlogId
	 * @param comment_type
	 * @return
	 *//*
	public int deleteComment(final String forumOrBlogId, final int comment_type) {
		return database.delete(TABLE_COMMENT, DBComment.ID + "=? AND " + DBComment.COMMENT_TYPE + "=?", new String[] { forumOrBlogId, String.valueOf(comment_type) });
	}

	*//**
	 * method for get comment from TABLE_COMMENT this method return cursor
	 * 
	 * @param forumOrBlogId
	 * @param comment_type
	 * @return
	 * @throws ParseException
	 *//*
	public Cursor getComment(final String forumOrBlogId, final int comment_type) throws ParseException {
		Cursor cursor = database.query(TABLE_COMMENT, null, DBComment.ID + "=? AND " + DBComment.COMMENT_TYPE + "=?", new String[] { forumOrBlogId, String.valueOf(comment_type) }, null, null, null,
				null);
		return cursor;
	}

	*//**
	 * Update inserted SectionPageConfig data in to SectionPageConfig table
	 * 
	 * @return long
	 *//*
	public long updateSectionPageConfigData(String timeStamp, int totalRecords, String sectionName) {

		final ContentValues values = new ContentValues();
		values.put(DBSectionPageConfig.TIMESTAMPS, timeStamp);
		values.put(DBSectionPageConfig.TOTAL_RECORDS, totalRecords);
		values.put(DBSectionPageConfig.SECTION_NAME, sectionName);
		return database.update(TABLE_SECTION_PAGE_CONFIG, values, DBSectionPageConfig.SECTION_NAME + "='" + sectionName + "'", null);

	}

	*//**
	 * Retrieve timestamp data from SectionPageConfig table
	 * 
	 * @return String timeStamp
	 *//*
	public String getTimeStamp(String sectionName) {
		Cursor cursor = null;
		String timeStamp = "";
		try {

			cursor = database.rawQuery("select * from " + TABLE_SECTION_PAGE_CONFIG + " where " + DBSectionPageConfig.SECTION_NAME + "='" + sectionName + "'", null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				timeStamp = cursor.getString(cursor.getColumnIndex(DBSectionPageConfig.TIMESTAMPS));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return timeStamp;

	}

	*//**
	 * Retrieve total records data from SectionPageConfig table
	 * 
	 * @return String totalRecords
	 *//*
	public String getTotalRecords(String sectionName) {
		Cursor cursor = null;
		String totalRecords = "";
		try {

			cursor = database.rawQuery("select * from " + TABLE_SECTION_PAGE_CONFIG + " where " + DBSectionPageConfig.SECTION_NAME + "='" + sectionName + "'", null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				totalRecords = cursor.getString(cursor.getColumnIndex(DBSectionPageConfig.TOTAL_RECORDS));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return totalRecords;

	}

	*//**
	 * Update all SectionPageConfig data in to SectionPageConfig table
	 * 
	 * @return long
	 *//*
	public long updateAllSectionPageConfigData(String timeStamp, int totalRecords) {

		final ContentValues values = new ContentValues();
		values.put(DBSectionPageConfig.TIMESTAMPS, timeStamp);
		values.put(DBSectionPageConfig.TOTAL_RECORDS, totalRecords);
		return database.update(TABLE_SECTION_PAGE_CONFIG, values, null, null);

	}
	
	public long updateGalleryLike(String galleryId, String likeCount, String isLike) {
		final ContentValues values = new ContentValues();
		values.put(DBGallery.LIKE_COUNT, likeCount);
		values.put(DBGallery.IS_LIKE, isLike);

		return database.update(TABLE_GALLERY, values, DBGallery.ID + "=?", new String[] { galleryId });
	}
	
	public long updateGalleryCommentsCount(String galleryId, String commentCount) {
		final ContentValues values = new ContentValues();
		values.put(DBGallery.COMMENTS_COUNT, commentCount);

		return database.update(TABLE_GALLERY, values, DBGallery.ID + "=?", new String[] { galleryId });
	}
	
	public void deleteAllTableData(){
		updateAllSectionPageConfigData("",0);
		database.delete(TABLE_BLOGS, null, null);
		database.delete(TABLE_COMMENT, null, null);
		database.delete(TABLE_CRIMEREPORT, null, null);
		database.delete(TABLE_FORUM, null, null);
		database.delete(TABLE_EVENTS, null, null);
		database.delete(TABLE_FORUM_CATEGORY, null, null);
		database.delete(TABLE_GALLERY, null, null);
		database.delete(TABLE_NEWS, null, null);
		database.delete(TABLE_MISSING_PEOPLES, null, null);
		database.delete(TABLE_WANTED_PEOPLES, null, null);
		database.delete(TABLE_POLICE_STATION, null, null);
		database.delete(TABLE_REGION, null, null);
	}*/
}