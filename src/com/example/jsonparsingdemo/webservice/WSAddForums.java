package ng.com.police.webservice;

import ng.com.police.R;
import ng.com.police.common.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

public class WSAddForums {

	private Context context;
	private String message = "";
	private boolean isSuccess = false;

	private int statusCode;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public WSAddForums(Context context) {
		this.context = context;
	}

	public String getMessage() {
		return message;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	/**
	 * Public method to call from AsyncTask
	 * 
	 * @param category_id
	 * @param topicname
	 * @param topicdescreption
	 */
	public void executeService(final int category_id, final String topicname, final String topicdescreption) {
		parseResponse(WebService.postData(context.getString(R.string.ws_url), generateRequestJson(category_id, topicname, topicdescreption)));
	}

	/**
	 * generate Request Json for WSAddForums
	 * 
	 * @param category_id
	 * @param topicname
	 * @param topicdescreption
	 * @return
	 */
	private String generateRequestJson(int category_id, String topicname, String topicdescreption) {
		final JSONObject jsonObject = new JSONObject();
		final JSONObject paramsJsonObject = new JSONObject();
		try {
			// post_data_string={"method":"createForumTopic","body":[{"lang":"en","userid":"8","device_id":"d4sfs4f65sd4f981dsf9841f98c4erh89t498ryh2212","category_id":"1","topic_name":"this is my fist forum","topic_description":"this is my fist forum"}]}
			paramsJsonObject.put("userid", context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).getString(context.getString(R.string.shared_user_id), "0"));
			paramsJsonObject.put("device_id", Util.getDeviceID(context));
			paramsJsonObject.put("lang", context.getString(R.string.language));
			paramsJsonObject.put("topic_name", topicname);
			paramsJsonObject.put("topic_description", topicdescreption);

			// ser Spiner at category
			paramsJsonObject.put("category_id", category_id);
			jsonObject.put("body", new JSONArray().put(paramsJsonObject));
			jsonObject.put("method", context.getString(R.string.ws_createforumtopic));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	/**
	 * parse Response from WSAddForums
	 * 
	 * @param json
	 */
	private void parseResponse(String json) {
		if (json != null && !json.trim().equals("")) {
			try {
				final JSONObject jsonObject = new JSONObject(json);
				message = jsonObject.getString("message");
				statusCode = jsonObject.getInt("result");
				isSuccess = statusCode == 1 ? true : false;
			} catch (Exception e) {
				message = context.getString(R.string.common_error);
				e.printStackTrace();
			}
		}

	}

}
