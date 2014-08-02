package el.solde.scrapbook.loaders;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import el.solde.scrapbook.activity.PictureSelect;
import el.solde.scrapbook.activity.ScrapApp;
import el.solde.scrapbook.adapters.ImageItem;
import el.solde.scrapbook.instagram.InstaLoginDialog;

public class InstagramImagesLoader extends GeneralImageLoader {

	private String insta_token;
	private String insta_id;
	private PictureSelect parFragment;

	public InstagramImagesLoader() {
		parFragment = GetParentFrament();
		if (ScrapApp.GetPreference().getString("insta_token", "") == "") {
			InstaLoginDialog insta_login = new InstaLoginDialog();
			insta_login.show(parFragment.getFragmentManager(),
					"InstagramLoginDialog");
		} else {
			insta_token = ScrapApp.GetPreference().getString("insta_token", "");
			insta_id = ScrapApp.GetPreference().getString("insta_id", "");
		}
	}

	// for array which is associated with Gridview

	@Override
	protected Void doInBackground(Void... voids) {
		// TODO Auto-generated method stub
		if (ScrapApp.GetInstance().GetInstagramImages() == null) {
			if (insta_token != "" && insta_token != null) {
				if (insta_id != "" && insta_id != null) {
					// we have all data - get user pictures
					GetUserMedia(insta_token,
							"https://api.instagram.com/v1/users/" + insta_id
									+ "/media/recent?access_token="
									+ insta_token + "&count=-1");
				} else {
					// no user ID - we have to get userID first and then get
					// media
					GetUserMedia(insta_token,
							"https://api.instagram.com/v1/users/"
									+ GetUserId(insta_token)
									+ "/media/recent?access_token="
									+ insta_token + "&count=-1");

				}
			}
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(ImageItem... values) {
		super.onProgressUpdate(values);
		ScrapApp.GetInstance().CacheInstagramImage(values[0]);
		parFragment.OnImagesLoadComplete(PictureSelect.instagram);
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		// let UI know that everything uploaded
		parFragment.OnImagesLoadComplete(PictureSelect.instagram);

	}

	// this method gets user Media. uses access token and user id as paramters
	protected Void GetUserMedia(String _insta_token, String url) {
		// Creating HTTP client
		DefaultHttpClient httpClient = new DefaultHttpClient();

		// Creating HTTP Post
		String post_url = url;
		HttpGet httpget = new HttpGet(post_url);
		// Making HTTP Request
		try {

			HttpResponse response = httpClient.execute(httpget);
			String responseBody = EntityUtils.toString(response.getEntity());
			JSONObject retrieved = new JSONObject(responseBody);
			JSONArray imageObjs = retrieved.getJSONArray("data");
			// get every data JSON object, get images object, get thumbnail and
			// standart resolution of image
			for (int r = 0; r < imageObjs.length(); r++) {
				// get "images" Object
				ImageItem current = new ImageItem(imageObjs.getJSONObject(r)
						.getJSONObject("images").getJSONObject("thumbnail")
						.getString("url"), imageObjs.getJSONObject(r)
						.getJSONObject("images")
						.getJSONObject("standard_resolution").getString("url"));
				publishProgress(current);

			}

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// this method gets user ID for using in future requests
	protected String GetUserId(String _insta_token) {
		String user_id = null;
		// Creating HTTP client
		DefaultHttpClient httpClient = new DefaultHttpClient();

		// Creating HTTP Post
		String post_url = "https://api.instagram.com/v1/users/self?access_token="
				+ insta_token;
		HttpGet httpget = new HttpGet(post_url);

		// Making HTTP Request
		try {

			HttpResponse response = httpClient.execute(httpget);
			String responseBody = EntityUtils.toString(response.getEntity());
			JSONObject retrieved = new JSONObject(responseBody);
			JSONObject data = retrieved.getJSONObject("data");
			user_id = data.getString("id");
			ScrapApp.SavePreference("insta_id", user_id);

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user_id;
	}
}
