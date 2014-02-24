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
	// for array which is associated with Gridview
	ImageItem[] images;

	@Override
	protected ImageItem[] doInBackground(Void... voids) {
		if (ScrapApp.GetFaceBookImages() != null) {
			// calling ImagesLoadComplete we check if current service is
			// facebook and if we have to update UI
			images = ScrapApp.GetFaceBookImages();
		} else {
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
									images = new ImageItem[photosArr.length()];
									for (int i = 0; i < photosArr.length(); i++) {
										images[i] = new ImageItem(photosArr
												.getJSONObject(i)
												.getString("src").toString(),
												photosArr.getJSONObject(i)
														.getString("src_big")
														.toString());
										publishProgress(images);
									}
								} catch (JSONException ex) {

								}
							}
						}
					}).executeAndWait();
		}
		return images;
	}

	@Override
	protected void onProgressUpdate(ImageItem[]... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		ScrapApp.CacheFaceBookImages(values[0]);
		// let the UI know about loading finished
		parFragment.OnImagesLoadComplete(PictureSelect.facebook);
	}

	@Override
	protected void onPostExecute(ImageItem[] result) {
		super.onPostExecute(result);
		// cacheImages
		ScrapApp.CacheFaceBookImages(result);
		// let the UI know about loading finished
		parFragment.OnImagesLoadComplete(PictureSelect.facebook);
	}

}
