package ng.com.police.webservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;

import ng.com.police.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class WebService {

	/**
	 * Method is used for checking network availability.
	 * 
	 * @author Mihir Palkhiwala
	 * @param context
	 * @return isNetAvailable: boolean true for Internet availability, false
	 *         otherwise
	 */

	public static boolean isNetworkAvailable(Context context) {
		boolean isNetAvailable = false;
		if (context != null) {
			final ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (mConnectivityManager != null) {
				boolean mobileNetwork = false;
				boolean wifiNetwork = false;

				boolean mobileNetworkConnecetd = false;
				boolean wifiNetworkConnecetd = false;

				final NetworkInfo mobileInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				final NetworkInfo wifiInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

				if (mobileInfo != null) {
					mobileNetwork = mobileInfo.isAvailable();
				}

				if (wifiInfo != null) {
					wifiNetwork = wifiInfo.isAvailable();
				}

				if (wifiNetwork || mobileNetwork) {
					if (mobileInfo != null)
						mobileNetworkConnecetd = mobileInfo.isConnectedOrConnecting();
					wifiNetworkConnecetd = wifiInfo.isConnectedOrConnecting();
				}

				isNetAvailable = (mobileNetworkConnecetd || wifiNetworkConnecetd);
			}
			/*
			 * if (!isNetAvailable) { Util.displayDialog(context,
			 * context.getString(R.string.common_internet), false); }
			 */
		}

		return isNetAvailable;
	}

	/**
	 * Used for getting data from given URL.
	 * 
	 * @param nURL
	 * @return String response from web service
	 * @throws Exception
	 */
	public static String getData(Context context, String nURL) throws Exception {
		String mResult = "";
		BufferedReader in = null;
		try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 2400000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 2400000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpGet request = new HttpGet();
			request.setURI(new URI(nURL));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			mResult = sb.toString();
		} catch (SocketException e) {
			return context.getString(R.string.http_parsing_error);
		} catch (UnknownHostException e) {
			return context.getString(R.string.http_parsing_error);
		} catch (Exception e) {
			return e.toString();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
		}
		return mResult;
	}

	public static String postData(String url, String json) {
		String response = "";
		try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 2400000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 2400000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			final HttpPost httppost = new HttpPost(url);
			httppost.setHeader("Content-type", "application/x-www-form-urlencoded");
			final HttpClient httpclient = new DefaultHttpClient(httpParameters);
			
			final ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
			values.add(new BasicNameValuePair("post_data_string", json));

			try {
				httppost.setEntity(new UrlEncodedFormEntity(values));
			} catch (Exception e) {
				e.printStackTrace();
				return "Connection problem with the server.";
			}
			final HttpResponse httpResponse = httpclient.execute(httppost);
			response = convertInputStreamToString(httpResponse);
			Log.v("Request",""+json);
			Log.v("Response",""+response);
		} catch (SocketException e) {
			return "Connection problem with the server.";
		} catch (UnknownHostException e) {
			return "Connection problem with the server.";
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return "Connection problem with the server.";
		} catch (IOException e) {
			e.printStackTrace();
			return "Connection problem with the server.";
		}
		return response;
	}

	public static String postDataImage(final String url, final String json, String attachmentPath, String attachmentTag) {

		String response = "";
		try {
			final HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);

			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			entity.addPart("post_data_string", new StringBody(json));

			if (attachmentPath != null) {
				if(attachmentTag != null && attachmentTag.contains("img")){
					attachmentTag = "img";
				}
				entity.addPart(attachmentTag, new FileBody(new File(attachmentPath)));
				System.out.println("Key = " + attachmentTag + ", Value = " + attachmentPath);
			}
			httppost.setEntity(entity);

			HttpResponse httpResponse = httpclient.execute(httppost);
			response = convertInputStreamToString(httpResponse);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * It will convert repose coming from server into String
	 * 
	 * @param response
	 *            Response of Server
	 * @return Converted string data
	 */

	public static String convertInputStreamToString(HttpResponse response) {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			final StringBuffer stringBuffer = new StringBuffer("");
			String line = "";
			final String LineSeparator = System.getProperty("line.separator");

			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line + LineSeparator);
			}
			return stringBuffer.toString();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}
}
