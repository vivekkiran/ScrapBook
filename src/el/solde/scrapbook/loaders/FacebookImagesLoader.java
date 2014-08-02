package el.solde.scrapbook.loaders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.provider.MediaStore;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import el.solde.scrapbook.activity.PictureSelect;
import el.solde.scrapbook.activity.ScrapApp;
import el.solde.scrapbook.adapters.ImageItem;

//Here we create ImageItem for each image from gallery, load there thumbnail url and real url
//return ImageItem[] array with items 
public class FacebookImagesLoader extends GeneralImageLoader {

	// instance of parent fragment
	PictureSelect parFragment;

	public FacebookImagesLoader() {
		parFragment = GetParentFrament();
	}

	// sorting parameter
	final String orderBy = MediaStore.Images.Thumbnails.IMAGE_ID;

	@Override
	protected Void doInBackground(Void... voids) {
		if (ScrapApp.GetInstance().GetFaceBookImages() == null) {
			String fqlQuery = "SELECT src, src_big FROM photo WHERE aid IN (SELECT aid FROM album WHERE owner=me())";
			Bundle params = new Bundle();
			params.putString("q", fqlQuery);
			Session session = parFragment.GetFacebookSession();

			new Request(session, "/fql", params, HttpMethod.GET,
					new Request.Callback() {
						public void onCompleted(Response response) {
							GraphObject retrieved = response.getGraphObject();
							if (retrieved != null) {
								JSONObject photos = retrieved
										.getInnerJSONObject();
								try {
									JSONArray photosArr = photos
											.getJSONArray("data");
									for (int i = 0; i < photosArr.length(); i++) {
										ImageItem current = new ImageItem(
												photosArr.getJSONObject(i)
														.getString("src")
														.toString(), photosArr
														.getJSONObject(i)
														.getString("src_big")
														.toString());
										publishProgress(current);
									}
								} catch (JSONException ex) {

								}
							}
						}
					}).executeAndWait();
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(ImageItem... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		ScrapApp.GetInstance().CacheFaceBookImage(values[0]);
		// let the UI know about loading finished
		parFragment.OnImagesLoadComplete(PictureSelect.facebook);
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		// let the UI know about loading finished
		parFragment.OnImagesLoadComplete(PictureSelect.facebook);
	}

}
