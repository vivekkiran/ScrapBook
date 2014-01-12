package el.solde.scrapbook.loaders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import el.solde.scrapbook.activity.PictureSelect;
import el.solde.scrapbook.activity.ScrapApp;
import el.solde.scrapbook.adapters.ImageItem;

public class FacebookImagesLoader extends AsyncTask<Void, Void, Void> {

	Session session;
	// for array which is associated with Gridview
	ImageItem[] images;

	public FacebookImagesLoader() {
		images = PictureSelect.getInstance().GetImageItems();
		session = PictureSelect.getInstance().GetFacebookSession();
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		if (session.isOpened()) {
			// Request user data and show the results
			new Request(session, "/me/photos/uploaded", null, HttpMethod.GET,
					new Request.Callback() {
						public void onCompleted(Response response) {
							GraphObject retrieved = response.getGraphObject();
							JSONObject photos = retrieved.getInnerJSONObject();
							try {
								JSONArray photosArr = photos
										.getJSONArray("data");
								images = new ImageItem[photosArr.length()];
								for (int i = 0; i < photosArr.length(); i++) {
									images[i] = new ImageItem(photosArr
											.getJSONObject(i)
											.getString("picture").toString(),
											photosArr.getJSONObject(i)
													.getString("source")
													.toString());
									Log.d("facebook", "Picture Url "
											+ images[i].getThumbnail());
								}
								// cache facebook images
								ScrapApp.CacheFaceBookImages(images);
							} catch (JSONException ex) {

							}

						}
					}).executeAsync();
		}
		return null;
	}

}
