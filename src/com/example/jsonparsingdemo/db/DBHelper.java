package com.example.jsonparsingdemo.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jsonparsingdemo.R;

/**
 * Class is used for database related operations.
 * 
 */
public class DBHelper extends SQLiteOpenHelper {

	private final String TAG = getClass().getSimpleName();
	private SQLiteDatabase myDataBase;
	private Context myContext;
//	private String TAG = this.getClass().getSimpleName();
	private final String DB_TABLE_CHAT_USER = "CREATE TABLE IF NOT EXISTS chat_user (id INTEGER PRIMARY KEY, user_id TEXT, user_name TEXT, receiver_jabber_id TEXT, profile_image_url TEXT, updated_time NUMERIC, updated_time_diff NUMERIC, is_unfollowed_user NUMERIC DEFAULT(0), display_unfollow_msg NUMERIC DEFAULT(0))";
	private final String DB_TABLE_GROUP_CHAT_HISTORY = "CREATE TABLE IF NOT EXISTS group_chat_history (id INTEGER PRIMARY KEY, is_read NUMERIC DEFAULT(1), group_id TEXT, sender_id TEXT, chat_message TEXT, chat_time TEXT, is_received NUMERIC, updated_time NUMERIC, updated_time_diff NUMERIC)";
	private final String DB_TABLE_GROUP_CHAT_MASTER = "CREATE TABLE IF NOT EXISTS group_chat_master (group_id TEXT PRIMARY KEY, group_name TEXT, group_subject TEXT, group_icon TEXT, createddate TEXT, updateddate TEXT)";
	private final String DB_TABLE_GROUP_INVITATION = "CREATE TABLE IF NOT EXISTS group_invitation (user_id TEXT, group_id TEXT, receiver_id TEXT, group_image_url TEXT, invitation_message TEXT)";
	private final String DB_TABLE_GROUP_MEMBERS = "CREATE TABLE IF NOT EXISTS group_members (group_id TEXT, group_member_id TEXT, group_member_jid TEXT, group_member_name TEXT, group_member_icon_url TEXT, is_owner INTEGER DEFAULT(0))";
	private final String DB_TABLE_SINGLE_CHAT_HISTORY = "CREATE TABLE IF NOT EXISTS single_chat_history (id INTEGER PRIMARY KEY AUTOINCREMENT, sender_id TEXT, receiver_id TEXT, receiver_name TEXT, sender_name TEXT, chat_message TEXT, is_read_message NUMERIC DEFAULT(0), is_received NUMERIC, time NUMERIC)";

	public DBHelper(Context context) {
		super(context, context.getResources().getString(R.string.db_name), null, 1);
		this.myContext = context;
	}
	
	public void openDataBase() throws SQLException {
		// --- Open the database---
		myDataBase = this.getWritableDatabase();
		//myDataBase = SQLiteDatabase.openDatabase(myContext.getString(R.string.DB_NAME), null, SQLiteDatabase.OPEN_READWRITE);
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase myDataBase) {
		myDataBase.execSQL(DB_TABLE_CHAT_USER);
		myDataBase.execSQL(DB_TABLE_GROUP_CHAT_HISTORY);
		myDataBase.execSQL(DB_TABLE_GROUP_CHAT_MASTER);
		myDataBase.execSQL(DB_TABLE_GROUP_INVITATION);
		myDataBase.execSQL(DB_TABLE_GROUP_MEMBERS);
		myDataBase.execSQL(DB_TABLE_SINGLE_CHAT_HISTORY);
		this.myDataBase = myDataBase;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
/*	
	*//**
	 * Method is used for inserting chat history to database
	 * 
	 * @param sender_id
	 * @param recevier_id
	 * @param receiver_name
	 * @param chat_message
	 * @param chatMessageReceived
     * @param is_read_message
     * @param time
	 *//*
	public long insertChat(final String sender_id, final String recevier_id, final String receiver_name, final String sender_name, final String chat_message, final int chatMessageReceived, final int is_read_message, final String time) {
		long id = 0;
		ContentValues values;
		try {
			values = new ContentValues();
			values.put("receiver_id", recevier_id);
			values.put("sender_id", sender_id);
			values.put("receiver_name", receiver_name);
			values.put("sender_name", sender_name);
			values.put("chat_message", chat_message);
			values.put("is_received", chatMessageReceived);
			values.put("is_read_message", is_read_message);
			values.put("time", time);
			id = myDataBase.insert(myContext.getString(R.string.db_table_single_chat_history), null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}
	
	public long insertChatUser(final String user_id, final String user_name, final String receiver_jabber_id,
			final String profile_image_url, final long updated_time) {
		long id = 0;
		ContentValues values;
		try {
			values = new ContentValues();
			values.put("user_id", user_id);
			values.put("user_name", user_name);
			values.put("receiver_jabber_id", receiver_jabber_id);
			values.put("profile_image_url", profile_image_url);
			values.put("updated_time", updated_time);
			id = myDataBase.insert(myContext.getString(R.string.db_table_chat_user), null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}
	
	public void updateUserFollowStatus(long id, int followUser, int followUserTitle) {
		try {
			final ContentValues values = new ContentValues();
			values.put("is_unfollowed_user", followUser);
			values.put("display_unfollow_msg", followUserTitle);
			myDataBase.update(myContext.getString(R.string.db_table_chat_user), values,
					"id=?", new String[] {String.valueOf(id)});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int updateChatUser(final String user_id, final long updated_time) {
		ContentValues values;
		int id = 0;
		try {
			values = new ContentValues();
			values.put("updated_time", updated_time);
			values.put("updated_time_diff", System.currentTimeMillis() - updated_time);
			id = myDataBase.update(myContext.getString(R.string.db_table_chat_user), values, "user_id='" + user_id + "'", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return id;
	}
	
	public long insertGroupChatMaster(final String group_id, final String group_name, final String group_subject,
			final String group_icon, final String creadedDate, boolean isEdit) {
		long id = 0;
		ContentValues values;
		try {
			values = new ContentValues();
			values.put("group_id", group_id);
			values.put("group_name", group_name);
			values.put("group_subject", group_subject);
			values.put("group_icon", group_icon);
			values.put("createddate", creadedDate);
			values.put("updateddate", ChatUtils.convertCurrentTimeToGmt("yyyyMMdd'T'HH:mm:ss"));
			if ( isEdit ) {
				myDataBase.update(myContext.getString(R.string.db_table_group_chat_master), values,
						"group_id=?", new String[]{group_id});
			} else {
				myDataBase.insert(myContext.getString(R.string.db_table_group_chat_master), null, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}
	
	public long insertGroupChatHistory(final String group_id, final String sender_id, final String chat_message,
			final String chat_time, final int is_received) {
		long id = 0;
		try {
			if ( !isGroupHistoryAvailable(group_id, sender_id, chat_message, chat_time) ) {
				final ContentValues values = new ContentValues();
				values.put("group_id", group_id);
				values.put("sender_id", sender_id);
				values.put("chat_message", chat_message);
				values.put("chat_time", chat_time);
				values.put("is_received", is_received);
				id = myDataBase.insert(myContext.getString(R.string.db_table_group_chat_history), null, values);
				
				values.clear();
				
				//	Update chat time
				values.put("updateddate", ChatUtils.convertCurrentTimeToGmt("yyyyMMdd'T'HH:mm:ss"));
				myDataBase.update(myContext.getString(R.string.db_table_group_chat_master), values, 
						"group_id=?", new String[]{group_id});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}
	
	public boolean isGroupHistoryAvailable(String groupId, String senderId, String chatMsg, String chatTime) {
		Cursor cursor = null;
		try {
			cursor = myDataBase.query(myContext.getString(R.string.db_table_group_chat_history), new String[]{"*"},
					"group_id=? and sender_id=? and chat_message=? and chat_time=?", 
					new String[] {groupId, senderId, chatMsg, String.valueOf(chatTime)}, null, null, null);
			if ( cursor.getCount() > 0 ) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( cursor != null && !cursor.isClosed() ) {
				cursor.close();
			}
		}
		return false;
	}
	
	public ArrayList<SingleChatModel> getSingleChatHistory(final String sender_id, final String receiver_id, long lastId) {
		final ArrayList<SingleChatModel> singleChatModels = new ArrayList<SingleChatModel>();
		Cursor cursor = null;
		try {
			if ( lastId == 0 ) {
				cursor = myDataBase.rawQuery("select * from " + myContext.getString(R.string.db_table_single_chat_history) 
						+ " where sender_id = '" + sender_id + "'" + " and receiver_id = '" + receiver_id 
						+ "' order by time DESC limit 0,30", null);
			} else {
				cursor = myDataBase.rawQuery("select * from " + myContext.getString(R.string.db_table_single_chat_history) 
						+ " where sender_id = '" + sender_id + "'" + " and receiver_id = '" + receiver_id + "' and id < " + lastId
						+ " order by time DESC limit 0,30", null);
			}
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss", Locale.getDefault());
			if(cursor.getCount() > 0) {
				SingleChatModel chatModel = null;
				while(cursor.moveToNext()) {
					chatModel = new SingleChatModel();
					chatModel.setId(cursor.getLong(cursor.getColumnIndex("id")));
					chatModel.setReceiverId(cursor.getString(cursor.getColumnIndex("receiver_id")));
					chatModel.setSenderId(cursor.getString(cursor.getColumnIndex("sender_id")));
					chatModel.setReceiverName(cursor.getString(cursor.getColumnIndex("receiver_name")));
					chatModel.setSenderName(cursor.getString(cursor.getColumnIndex("sender_name")));
					chatModel.setChatMessage(cursor.getString(cursor.getColumnIndex("chat_message")));
					chatModel.setIsRead(cursor.getInt(cursor.getColumnIndex("is_read_message")));
					chatModel.setChatMessageReceived(cursor.getInt(cursor.getColumnIndex("is_received")));
	//				chatModel.setTime(ChatUtils.getChatTime(myContext, cursor.getLong(cursor.getColumnIndex("time"))));
					chatModel.setTime(Util.getTimeSince(cursor.getString(cursor.getColumnIndex("time")), sdf));
					singleChatModels.add(0, chatModel);
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		return singleChatModels;
	}
	
	public ArrayList<GroupChatMasterModel> getAllGroups() {
		final ArrayList<GroupChatMasterModel> arrayList = new ArrayList<GroupChatMasterModel>();
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss", Locale.getDefault());
		 //+ " ORDER BY updated_time_diff DESC"
		final Cursor cursor = myDataBase.query(myContext.getString(R.string.db_table_group_chat_master),
				new String[]{"*"}, null, null, null, null, "updateddate DESC");
		if(cursor.getCount() > 0) {
			GroupChatMasterModel groupChatMasterModel = null;
			String[] lastMsgs;
			
			while(cursor.moveToNext()) {
				groupChatMasterModel = new GroupChatMasterModel();
				groupChatMasterModel.setGroupId(cursor.getString(cursor.getColumnIndex("group_id")));
				groupChatMasterModel.setGroupName(cursor.getString(cursor.getColumnIndex("group_name")));
				groupChatMasterModel.setGroupSubject(cursor.getString(cursor.getColumnIndex("group_subject")));
				groupChatMasterModel.setGroupIcon(cursor.getString(cursor.getColumnIndex("group_icon")));
				groupChatMasterModel.setUnreadMsgs(getGroupUnReadMsgs(groupChatMasterModel.getGroupId()));;
				
				lastMsgs = getLastGroupMessage(groupChatMasterModel.getGroupId());
				if ( lastMsgs != null && lastMsgs.length == 2 ) {
					groupChatMasterModel.setLastMessage(lastMsgs[0]);
					if ( !TextUtils.isEmpty(lastMsgs[1]) ) {
						groupChatMasterModel.setTime(Util.getTimeSince(lastMsgs[1], sdf));
					} else {
						groupChatMasterModel.setTime(lastMsgs[1]);
					}
				}
				arrayList.add(groupChatMasterModel);
			}
		}
		if(cursor != null) {
			cursor.close();
		}
		return arrayList;
	}
	
	public String getGroupUnReadMsgs( String groupJId ) {
		Cursor cursor = null;
		String msgsCount = "0";
		
		try {
			cursor = myDataBase.query(myContext.getString(R.string.db_table_group_chat_history), new String[] {"*"},
					"group_id=? and is_read=1", new String[]{groupJId}, null, null, null);
			if ( cursor != null && cursor.getCount() > 0 ) {
				cursor.moveToFirst();
				msgsCount = "" + cursor.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( cursor != null && !cursor.isClosed() ){
				cursor.close();
			}
		}
		return msgsCount;
	}
	
	public void updateGroupCount( String groupJId ) {
		try {
			final ContentValues values = new ContentValues();
			values.put("is_read", 0);
			
			myDataBase.update(myContext.getString(R.string.db_table_group_chat_history), values,
					"group_id=?",  new String[]{groupJId});
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public String[] getLastGroupMessage(String groupJId) {
		String lastMessage[] = new String[2];
		
		Cursor cursor = null;
		
		try {
			cursor = myDataBase.query(myContext.getString(R.string.db_table_group_chat_history), new String[] {"*"},
					"group_id=?", new String[]{groupJId}, null, null, "chat_time DESC", "1");
			if ( cursor != null && cursor.getCount() > 0 ) {
				cursor.moveToFirst();
				lastMessage[0] = cursor.getString(cursor.getColumnIndex("chat_message"));
				lastMessage[1] = cursor.getString(cursor.getColumnIndex("chat_time"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( cursor != null && !cursor.isClosed() ){
				cursor.close();
			}
		}
		return lastMessage;
	}
	
	public ArrayList<GroupChatHistoryModel> getGroupChatHistory(final String group_id, long lastId ) {
		
		final ArrayList<GroupChatHistoryModel> arrayList = new ArrayList<GroupChatHistoryModel>();
		Cursor cursor = null;
		try {
			if ( lastId == 0 ) {
				cursor = myDataBase.query(myContext.getString(R.string.db_table_group_chat_history), new String[] {"*"},
						"group_id=?", new String[]{group_id }, null, null, "chat_time DESC", "0,30");
			} else {
				cursor = myDataBase.query(myContext.getString(R.string.db_table_group_chat_history), new String[] {"*"},
						"group_id=? and id<?", new String[]{group_id, String.valueOf(lastId)}, null, null, "chat_time DESC", "0,30");
			}
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss", Locale.getDefault());
			Log.e(TAG, "cursor size : " + cursor.getCount());
			if(cursor.getCount() > 0) {
//				cursor.moveToLast();
				GroupChatHistoryModel groupChatHistoryModel = null;
				String userName = "";
				while(cursor.moveToNext()) {
					groupChatHistoryModel = new GroupChatHistoryModel();
					groupChatHistoryModel.setId(cursor.getLong(cursor.getColumnIndex("id")));
					groupChatHistoryModel.setGroupId(cursor.getString(cursor.getColumnIndex("group_id")));
					groupChatHistoryModel.setSenderId(cursor.getString(cursor.getColumnIndex("sender_id")));
					groupChatHistoryModel.setChatMessage(cursor.getString(cursor.getColumnIndex("chat_message")));
					groupChatHistoryModel.setChatTime(Util.getTimeSince(cursor.getString(cursor.getColumnIndex("chat_time")), sdf));
	                groupChatHistoryModel.setChatMessageReceived(cursor.getInt(cursor.getColumnIndex("is_received")));
	                groupChatHistoryModel.setUserImage(getUserImage(groupChatHistoryModel.getSenderId()));
	                
	                userName = getUserName(groupChatHistoryModel.getSenderId().replace("@" + myContext.getString(R.string.server_name), ""));
	                if ( TextUtils.isEmpty(userName) ) {
	                	userName = myContext.getString(R.string.anonymous_user);
	                }
	                groupChatHistoryModel.setUserName(userName);
					arrayList.add(0, groupChatHistoryModel);
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		return arrayList;
	}
	
	public String getUserImage( String userJId ) {
		String image = "";
		Cursor cursor = null;
		userJId = userJId.replace("@" + myContext.getString(R.string.server_name), "");
		
		try {
			cursor = myDataBase.query(myContext.getString(R.string.db_table_chat_user), new String[] {"profile_image_url"},
					"user_id=?", new String[]{userJId}, null, null, null);
			if ( cursor != null && cursor.getCount() > 0 ) {
				cursor.moveToFirst();
				image = cursor.getString(cursor.getColumnIndex("profile_image_url"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( cursor != null && !cursor.isClosed() ){
				cursor.close();
			}
		}
		return image;
	}
	
	public String getUnreadMessageCount(final String receiver_name) {
		String count = "";
		
		try {
			final SharedPreferences preferences = ((HinterApplication)myContext).getSharedPreferences();
			final String userId = preferences.getString(myContext.getString(R.string.pref_user_id), "");
			final Cursor cursor = myDataBase.rawQuery("select count(*) AS unread from " + myContext.getString(R.string.db_table_single_chat_history) 
					+ " where receiver_id='" + receiver_name + "' and is_read_message=1 and sender_id='" + userId +"'", null);
//			Log.v("TAG", "getUnreadMessageCount : " + cursor.getCount() + "\t" + receiver_name);
			
			if(cursor.getCount() > 0) {
				cursor.moveToFirst();
				count = cursor.getString(cursor.getColumnIndex("unread"));
//				Log.e("TAG", "getUnreadMessageCount : " + count);
			}
			if(cursor != null) {
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public ArrayList<ChatUseListModel> getChatUserList(final boolean isAllFriends) {
		final ArrayList<ChatUseListModel> arrayList = new ArrayList<ChatUseListModel>();
		int i = 0;
		ChatUseListModel chatUseListModel = null;
		Cursor cursor;

		if ( isAllFriends ) {
			cursor = myDataBase.rawQuery("select * from " + myContext.getString(R.string.db_table_chat_user) + " order by updated_time_diff ASC", null);
			if(cursor.getCount() > 0) {
				String userid = "", lastSeenTime = "";
				while(cursor.moveToNext()) {
					userid = cursor.getString(cursor.getColumnIndex("user_id"));
					if(isSingleChatUserExists(userid)) {
						chatUseListModel = getLastMessage(userid);
						arrayList.add(chatUseListModel);
					} else {
						chatUseListModel = new ChatUseListModel();
						chatUseListModel.setChatId(i);
						chatUseListModel.setUserId(userid);
						chatUseListModel.setChatMessage("");
						chatUseListModel.setChatUserName(cursor.getString(cursor.getColumnIndex("user_name")));
						chatUseListModel.setImageResource(0);
						chatUseListModel.setJabberId(cursor.getString(cursor.getColumnIndex("receiver_jabber_id")));
						
						//lastSeenTime = ChatUtils.getChatTime(
						chatUseListModel.setLastChatMessageTime(lastSeenTime);
						chatUseListModel.setThumbImageUrl(cursor.getString(cursor.getColumnIndex("profile_image_url")));
						chatUseListModel.setFollowedUser(cursor.getInt(cursor.getColumnIndex("is_unfollowed_user")));
						chatUseListModel.setFollowedUserTitle(cursor.getInt(cursor.getColumnIndex("display_unfollow_msg")));
						arrayList.add(chatUseListModel);
					}
					i++;
				}
			}
		} else {
			final SharedPreferences preferences = ((HinterApplication)myContext).getSharedPreferences();
			final String userId = preferences.getString(myContext.getString(R.string.pref_user_id), "");
			cursor = myDataBase.query(myContext.getString(R.string.db_table_single_chat_history), new String[] {"*"},
					"sender_id=?", new String[]{userId}, "receiver_id", null, "time desc");
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss", Locale.getDefault());
			while(cursor.moveToNext()) {
//				String userid = cursor.getString(cursor.getColumnIndex("user_id"));
//				chatUseListModel = getLastMessage(userid);
				
				chatUseListModel = new ChatUseListModel();
				chatUseListModel.setUserId(cursor.getString(cursor.getColumnIndex("receiver_id")));
				chatUseListModel.setSenderName(cursor.getString(cursor.getColumnIndex("sender_name")));
				chatUseListModel.setReceiverName(cursor.getString(cursor.getColumnIndex("receiver_name")));
				chatUseListModel.setJabberId(cursor.getString(cursor.getColumnIndex("receiver_id")));
				chatUseListModel.setChatMessage(cursor.getString(cursor.getColumnIndex("chat_message")));
				chatUseListModel.setLastChatMessageTime(Util.getTimeSince(cursor.getString(cursor.getColumnIndex("time")), sdf));
				chatUseListModel.setMessageCount(getUnreadMessageCount(chatUseListModel.getUserId()));
				
				final FollowFollowingModel followFollowingModel = getUserDetail(chatUseListModel.getUserId());
				if(followFollowingModel!=null){
					chatUseListModel.setFollowedUser(followFollowingModel.getFollowedUser());
					chatUseListModel.setFollowedUserTitle(followFollowingModel.getFollowedUserTitle());					
					chatUseListModel.setChatUserName(followFollowingModel.getName());
					chatUseListModel.setThumbImageUrl(followFollowingModel.getImgUrl());
					arrayList.add(chatUseListModel);
				}
			}
		}
		if(cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return arrayList;
	}
	
	@SuppressWarnings("deprecation")
	public String getLastSeenActivity(final String jabber_id) {
		String lastSeenTime = "";
		try {
			LastActivity activity = LastActivity.getLastActivity(application.getXmppConnection(), jabber_id + "@" + myContext.getString(R.string.server_name));
			lastSeenTime = ChatUtils.getLastSeenTime(myContext, activity.lastActivity);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return lastSeenTime;
	}
	
	@SuppressWarnings("deprecation")
	public long getLastSeenLong(final String jabber_id) {
		long lastSeenTime = 0;
		try {
			LastActivity activity = LastActivity.getLastActivity(application.getXmppConnection(), jabber_id + "@" + myContext.getString(R.string.server_name));
			lastSeenTime = activity.lastActivity;
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return lastSeenTime;
	}

	private ChatUseListModel getLastMessage(final String user_id) {
		ChatUseListModel chatUseListModel = null;
		final SharedPreferences preferences = ((HinterApplication)myContext).getSharedPreferences();
		final String userId = preferences.getString(myContext.getString(R.string.pref_user_id), "");
		final Cursor cursor = myDataBase.rawQuery("select chat_message, receiver_id, time, user_id, user_name, receiver_jabber_id, profile_image_url from single_chat_history AS C1 JOIN chat_user AS C2 ON C1.receiver_id=C2.receiver_jabber_id where C2.user_id='"+ user_id + "' and sender_id='" + userId + "' ORDER BY C1.time DESC limit 1", null);
		if(cursor.getCount() > 0) {
			cursor.moveToFirst();
			chatUseListModel = new ChatUseListModel();
			chatUseListModel.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
			chatUseListModel.setChatMessage(cursor.getString(cursor.getColumnIndex("chat_message")));
			chatUseListModel.setChatUserName(cursor.getString(cursor.getColumnIndex("user_name")));
			chatUseListModel.setJabberId(cursor.getString(cursor.getColumnIndex("receiver_jabber_id")));
			chatUseListModel.setThumbImageUrl(cursor.getString(cursor.getColumnIndex("profile_image_url")));
			String lastSeenTime = getLastSeenActivity(chatUseListModel.getJabberId());
			if(lastSeenTime.startsWith("0")) {
//			Log.e("TAG", "Time : " + cursor.getLong(cursor.getColumnIndex("time")));
			String lastSeenTime = ChatUtils.getLastTime(myContext, cursor.getLong(cursor.getColumnIndex("time")));
			//}
			chatUseListModel.setLastChatMessageTime(lastSeenTime);
		}
		if(cursor != null) {
			cursor.close();
		}
		return chatUseListModel;
	}
	
	private boolean isSingleChatUserExists(final String user_id) {
		boolean isUserExists = false;
		final SharedPreferences preferences = ((HinterApplication)myContext).getSharedPreferences();
		final String userId = preferences.getString(myContext.getString(R.string.pref_user_id), "");
		final Cursor cursor = myDataBase.rawQuery("select * from " + myContext.getString(R.string.db_table_single_chat_history) + " where receiver_id='" + user_id + "' and sender_id='"+ userId + "'", null);
		if(cursor.getCount() > 0) {
			isUserExists = true;
		}
		if(cursor != null) {
			cursor.close();
		}
		return isUserExists;
	}
	
	public boolean isUserExists(final String user_id) {
		boolean isUserExists = false;
		final Cursor cursor = myDataBase.rawQuery("select * from " + myContext.getString(R.string.db_table_chat_user) + " where user_id='" + user_id + "'", null);
		if(cursor.getCount() > 0) {
			isUserExists = true;
		}
		if(cursor != null) {
			cursor.close();
		}
		return isUserExists;
	}
	
	public void deleteUser(String user_id) {
		try {
			myDataBase.delete(myContext.getString(R.string.db_table_chat_user),"user_id=?",new String[]{user_id});			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public String getUserName(final String id) {
		String userName = "";
		final Cursor cursor = myDataBase.query(myContext.getString(R.string.db_table_chat_user), new String[] {"*"}, "user_id='" + id + "'", null, null, null, null);
		if(cursor.getCount() > 0) {
			cursor.moveToFirst();
			userName = cursor.getString(cursor.getColumnIndex("user_name"));
		}
		return userName;
	}
	
	public int updateChatUser(final String user_id, final String user_name, final String receiver_jabber_id, final String profile_image_url) {
		final ContentValues values = new ContentValues();
		values.put("user_name", user_name);
		values.put("receiver_jabber_id", receiver_jabber_id);
		values.put("profile_image_url", profile_image_url);
		values.put("is_unfollowed_user", 0);
		values.put("display_unfollow_msg", 0);
		
		return myDataBase.update(myContext.getString(R.string.db_table_chat_user), values, "user_id='" + user_id + "'", null);
	}
	
	private String getTime(final String user_id) {
		String chatTime = "--:--";
		final Cursor cursor = myDataBase.query(myContext.getString(R.string.db_table_single_chat_history), new String[] {"*"}, "sender_id='" + user_id + "' limit 1", null, null, null, null);
		if(cursor.getCount() > 0) {
			cursor.moveToFirst();
			chatTime = "" + cursor.getLong(cursor.getColumnIndex("time"));
		}
		if(cursor != null) {
			cursor.close();
		}
		return chatTime;
	}
	
	public int deleteChat(long id) {
		return myDataBase.delete(myContext.getString(R.string.db_table_single_chat_history), "time='" + id + "'", null);
	}
	
	public boolean leaveGroup(final String groupJabberId) {
		try {
			myDataBase.delete(myContext.getString(R.string.db_table_group_chat_master), "group_id='" + groupJabberId + "'", null);
			myDataBase.delete(myContext.getString(R.string.db_table_group_chat_history), "group_id='" + groupJabberId + "'", null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public long insertInvitations(final String user_id, final String group_id, final String receiver_id, final String group_image_url, final String invitation_message) {
		final ContentValues values = new ContentValues();
		values.put("user_id", user_id);
		values.put("group_id", group_id);
		values.put("receiver_id", receiver_id);
		values.put("group_image_url", group_image_url);
		values.put("invitation_message", invitation_message);
		return myDataBase.insert(myContext.getString(R.string.db_table_group_invitation), null, values);
	}
	
	public long insertGroupMembers(String group_id, String group_member_id, String group_member_jid, 
			final String group_member_icon_url, final int is_owner, String memberName) {
		final ContentValues values = new ContentValues();
		values.put("group_id", group_id);
		values.put("group_member_id", group_member_id);
		values.put("group_member_jid", group_member_jid);
		values.put("group_member_icon_url", group_member_icon_url);
		values.put("is_owner", is_owner);
		values.put("group_member_name", memberName);
		return myDataBase.insert(myContext.getString(R.string.db_table_group_members), null, values);
	}
	
	public int deleteInvitation(final String user_id, final String group_id) {
		return myDataBase.delete(myContext.getString(R.string.db_table_group_invitation), "user_id='" + user_id + "' and group_id='" + group_id + "'", null);
	}
	
	public int removeGroupMember(final String group_id, final String group_member_id) {
		return myDataBase.delete(myContext.getString(R.string.db_table_group_members), "group_id='" + group_id + "' and group_member_id='" + group_member_id + "'", null);
	}
	
	public String getImageUrl(final String user_id) {
		final Cursor cursor = myDataBase.rawQuery("select * from " + myContext.getString(R.string.db_table_chat_user) + " where user_id='" + user_id + "' IN (select * from )", null);
		if(cursor != null) {
			cursor.close();
		}
		return "";
	}
	
	public int updateMessageStatus(final long id, int readStatus) {
		final ContentValues values = new ContentValues();
		values.put("is_read_message", readStatus);
		return myDataBase.update(myContext.getString(R.string.db_table_single_chat_history), values, "id=" + id, null);
	}
	
	public int updateGroupMessageStatus(final long id, int readStatus) {
		final ContentValues values = new ContentValues();
		values.put("is_read", readStatus);
		return myDataBase.update(myContext.getString(R.string.db_table_group_chat_history), values, "id=" + id, null);
	}
	
	public int updateMessageStatus(final String senderName) {
		final SharedPreferences preferences = ((HinterApplication)myContext).getSharedPreferences();
		final String userId = preferences.getString(myContext.getString(R.string.pref_user_id), "");
		
		final ContentValues values = new ContentValues();
		values.put("is_read_message", 0);
		return myDataBase.update(myContext.getString(R.string.db_table_single_chat_history), values, 
				"receiver_id='" + senderName + "' and sender_id='" + userId + "'" , null);
	}
	
	public ArrayList<FollowFollowingModel> getGroupMembers( String group_id, int isOwner ) {
		final ArrayList<FollowFollowingModel> followFollowingModels = new  ArrayList<FollowFollowingModel>();
		Cursor cursor = null;
		try {
			cursor = myDataBase.query(myContext.getString(R.string.db_table_group_members), 
					new String[] {"*"}, "group_id=? and is_owner=?", 
					new String[] {group_id, String.valueOf(isOwner)}, null, null, null);
			
//			Log.e("DB", "jid :" + group_id +", Count : " + cursor.getCount());
			FollowFollowingModel followFollowingModel;
			while ( cursor.moveToNext() ) {
				followFollowingModel = new FollowFollowingModel(); 
				
//				followFollowingModel.setUserId(cursor.getString(cursor.getColumnIndex("group_id")));
				followFollowingModel.setUserId(cursor.getString(cursor.getColumnIndex("group_member_id")));
				followFollowingModel.setUserJId(cursor.getString(cursor.getColumnIndex("group_member_jid")));
				followFollowingModel.setImgUrl(cursor.getString(cursor.getColumnIndex("group_member_icon_url")));
				followFollowingModel.setOwner(cursor.getInt(cursor.getColumnIndex("is_owner")));
				followFollowingModel.setName(cursor.getString(cursor.getColumnIndex("group_member_name")));
				followFollowingModel.setMember(true);
				followFollowingModel.setSelected(true);
				followFollowingModel.setDisabled(true);
				
//				Log.e("DB", "getUserId :" + followFollowingModel.getUserId());
				
				followFollowingModels.add(followFollowingModel);
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( cursor != null && !cursor.isClosed() ) {
				cursor.close();
			}
		}
		followFollowingModels.trimToSize();
		return followFollowingModels;
	}
	
	public boolean checkUserIsAdmin ( String roomName, String userJid ) {
		Cursor cursor = null;
		try {
			cursor = myDataBase.query(myContext.getString(R.string.db_table_group_members), 
					new String[] {"*"}, "group_id=? and group_member_jid=? and is_owner = 1", 
					new String[] {roomName, userJid}, null, null, null);
			
			if ( cursor != null && cursor.getCount() > 0 ) {
				return true;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( cursor != null && !cursor.isClosed() ) {
				cursor.close();
			}
		}
		return false;
	}
	
	public void removeGroupUser ( String roomName, String userJid ) {
		try {
			int deleted = myDataBase.delete(myContext.getString(R.string.db_table_group_members), 
					 "group_id=? and group_member_jid=?", new String[] {roomName, userJid});
//			Log.e("DB", "isDeleted : " + deleted);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void leaveRoom( String roomName ) {
		try {
			int groupDeleted = myDataBase.delete(myContext.getString(R.string.db_table_group_chat_master), 
					 "group_id=?", new String[] {roomName});
//			Log.e("DB", "isDeleted : " + groupDeleted);
			
			int deleted = myDataBase.delete(myContext.getString(R.string.db_table_group_members), 
					 "group_id=?", new String[] {roomName});
//			Log.e("DB", "isDeleted : " + deleted);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public GroupChatMasterModel getGroupDetail ( String roonName ) {
		final Cursor cursor = myDataBase.query(myContext.getString(R.string.db_table_group_chat_master), new String[]{"*"},
				"group_id=?", new String[]{roonName}, null, null, null);
		final GroupChatMasterModel groupChatMasterModel = new GroupChatMasterModel();
		if(cursor.getCount() > 0) {
			while(cursor.moveToNext()) {
				groupChatMasterModel.setGroupId(cursor.getString(cursor.getColumnIndex("group_id")));
				groupChatMasterModel.setGroupName(cursor.getString(cursor.getColumnIndex("group_name")));
				groupChatMasterModel.setGroupSubject(cursor.getString(cursor.getColumnIndex("group_subject")));
				groupChatMasterModel.setGroupIcon(cursor.getString(cursor.getColumnIndex("group_icon")));
			}
		}
		if(cursor != null) {
			cursor.close();
		}
		return groupChatMasterModel;
	}
	
	public void insertAllGroups( JSONArray groupArray ) {
		
		try {
			if ( groupArray != null ) {
				myDataBase.beginTransaction();
				
//				myDataBase.delete(myContext.getString(R.string.db_table_group_chat_master), null, null);
//				myDataBase.delete(myContext.getString(R.string.db_table_group_members), null, null);
				
				JSONObject jsonGroupDetails;
				String groupId = "";
				for (int i = 0; i < groupArray.length(); ++i) {
					jsonGroupDetails = groupArray.getJSONObject(i);
					groupId = jsonGroupDetails.getString("group_id");
					
					//	Delete Group and Members data
					myDataBase.delete(myContext.getString(R.string.db_table_group_chat_master), 
							"group_id=?", new String[]{groupId});
					myDataBase.delete(myContext.getString(R.string.db_table_group_members), 
							"group_id=?", new String[]{groupId});
					
					insertGroupChatMaster(groupId, jsonGroupDetails.getString("group_name"),
							jsonGroupDetails.getString("group_name"), jsonGroupDetails.getString("group_icon"),
							jsonGroupDetails.getString("created"), false);
					
					
					//	Add Admin
					FollowFollowingModel followFollowingModel;
					String adminJabberId = jsonGroupDetails.getString("admin_id");
					String adminId = adminJabberId.replace("@" + myContext.getString(R.string.server_name), "");
					
					followFollowingModel = getUserDetail(adminId);
					final SharedPreferences preferences = ((HinterApplication)(myContext.
							getApplicationContext())).getSharedPreferences();
					String userId = preferences.getString(myContext.getString(R.string.pref_user_id), "Unknown");
					
					if ( followFollowingModel != null ) {
						insertGroupMembers(groupId, adminId, adminJabberId,
							followFollowingModel.getImgUrl(), 1, followFollowingModel.getName());
					} else if ( userId.equalsIgnoreCase(adminId) ) {
						String firstName = preferences.getString(myContext.getString(R.string.pref_profile_first_name), "");
						String lastName = preferences.getString(myContext.getString(R.string.pref_profile_last_name), "");
						String userImagePath = "";
						
						if ( preferences.getString(myContext.getString(R.string.pref_user_type), "").toLowerCase().
								equalsIgnoreCase(myContext.getString(R.string.user_type_business).toLowerCase()) ) {
								userImagePath = Util.generateImageUrl(myContext, myContext.getString(R.string.ws_image_directory_business_logo), 
					         			preferences.getString(myContext.getString(R.string.pref_profile_pic_url), ""));
						} else {
								userImagePath = Util.generateImageUrl(myContext, myContext.getString(R.string.ws_image_directory_user), 
					         			preferences.getString(myContext.getString(R.string.pref_profile_pic_url), ""));
						}
						
						insertGroupMembers(groupId, adminId, adminJabberId,
								userImagePath, 1, firstName + " " + lastName);
					} else {
						insertGroupMembers(groupId, adminId, adminJabberId, "", 1, "Unkonwn");
					}
					
					//	Add Members
					String[] members = jsonGroupDetails.getString("members").split(",");
					String memberId = "";
					
//					Log.e("DB", jsonGroupDetails.getString("members") + ", members length : " + members.length);
					
					for (int j = 0; j < members.length; j++) {
						memberId = members[j].replace("@" + myContext.getString(R.string.server_name), "");
						followFollowingModel = getUserDetail(memberId);
						
						if ( followFollowingModel != null ) {
							insertGroupMembers(groupId, memberId, members[j],
								followFollowingModel.getImgUrl(), 0, followFollowingModel.getName());
						} else if ( memberId.equalsIgnoreCase(userId) ) {
							String firstName = preferences.getString(myContext.getString(R.string.pref_profile_first_name), "");
							String lastName = preferences.getString(myContext.getString(R.string.pref_profile_last_name), "");
							String userImagePath = "";
							
							if ( preferences.getString(myContext.getString(R.string.pref_user_type), "").toLowerCase().
									equalsIgnoreCase(myContext.getString(R.string.user_type_business).toLowerCase()) ) {
									userImagePath = Util.generateImageUrl(myContext, myContext.getString(R.string.ws_image_directory_business_logo), 
						         			preferences.getString(myContext.getString(R.string.pref_profile_pic_url), ""));
							} else {
									userImagePath = Util.generateImageUrl(myContext, myContext.getString(R.string.ws_image_directory_user), 
						         			preferences.getString(myContext.getString(R.string.pref_profile_pic_url), ""));
							}
							
							insertGroupMembers(groupId, memberId, members[j],
									userImagePath, 0, firstName + " " + lastName);
						} else {
							if ( !TextUtils.isEmpty(memberId) && !TextUtils.isEmpty(members[j]) ) {
								insertGroupMembers(groupId, memberId, members[j], "", 0, "Unkonwn");
							}
						}
					}
				}
				
				myDataBase.setTransactionSuccessful();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			myDataBase.endTransaction();
		}
	}
	
	public FollowFollowingModel getUserDetail( String userId ) {
		Cursor cursor = null;
		FollowFollowingModel followFollowingModel = null;
		try {
			cursor = myDataBase.query(myContext.getString(R.string.db_table_chat_user), new String[] {"*"},
					"user_id=?", new String[] {userId}, null, null, null);
			if(cursor.getCount() > 0) {
				followFollowingModel = new FollowFollowingModel();
				cursor.moveToFirst();
				followFollowingModel.setId(cursor.getLong(cursor.getColumnIndex("id")));
				followFollowingModel.setName(cursor.getString(cursor.getColumnIndex("user_name")));
				followFollowingModel.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
				followFollowingModel.setUserJId(cursor.getString(cursor.getColumnIndex("user_id")) 
						+ "@" + myContext.getString(R.string.server_name));
				followFollowingModel.setImgUrl(cursor.getString(cursor.getColumnIndex("profile_image_url")));
				followFollowingModel.setFollowedUser(cursor.getInt(cursor.getColumnIndex("is_unfollowed_user")));
				followFollowingModel.setFollowedUserTitle(cursor.getInt(cursor.getColumnIndex("display_unfollow_msg")));
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( cursor != null && !cursor.isClosed() ) {
				cursor.close();
			}
		}
		return followFollowingModel;
	}
	
	public void flushData() {
		
		try {
			myDataBase.delete(myContext.getString(R.string.db_table_chat_user), null, null);
			myDataBase.delete(myContext.getString(R.string.db_table_group_chat_history), null, null);
			myDataBase.delete(myContext.getString(R.string.db_table_group_chat_master), null, null);
			myDataBase.delete(myContext.getString(R.string.db_table_group_invitation), null, null);
			myDataBase.delete(myContext.getString(R.string.db_table_group_members), null, null);
//			myDataBase.delete(myContext.getString(R.string.db_table_single_chat_history), null, null);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	*//**
	 * Method is used for copying database on to sd card.
	 * 
	 *//*
	public void copyDatabaseToSdCard() {
		try {
			File f1 = new File("data/data/" + myContext.getPackageName() + "/databases/chat_history.db");
			if (f1.exists()) {
				Log.v(TAG, "Absolute File path : " + Environment.getExternalStorageDirectory().getAbsoluteFile() + "/chat_history.db");
				File f2 = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/chat_history.db");
				if(f2.exists()) {
					f2.delete();
				}
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
	
	public int getUnreadChatMsgsCount() {
		Cursor cursorSingleChat = null;
		Cursor cursorGroupChat = null;
		int count = 0;
		
		try {
			final SharedPreferences preferences = ((HinterApplication)myContext).getSharedPreferences();
			final String userId = preferences.getString(myContext.getString(R.string.pref_user_id), "");
			
			cursorSingleChat = myDataBase.query(myContext.getString(R.string.db_table_single_chat_history),
					new String[]{"id"}, "is_read_message=1 and sender_id=?", new String[]{userId}, null, null, null);
			
			if ( cursorSingleChat != null ) {
				count += cursorSingleChat.getCount();
			}
			
			cursorGroupChat = myDataBase.query(myContext.getString(R.string.db_table_group_chat_history),
					new String[]{"id"}, "is_read=1", null, null, null, null);
			
			if ( cursorGroupChat != null ) {
				count += cursorGroupChat.getCount();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( cursorSingleChat != null && !cursorSingleChat.isClosed() ) {
				cursorSingleChat.close();
			}
			
			if ( cursorGroupChat != null && !cursorGroupChat.isClosed() ) {
				cursorGroupChat.close();
			}
		}
		
		return count;
	}*/
}