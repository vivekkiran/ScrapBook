package el.solde.scrapbook.loaders;

import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import el.solde.scrapbook.activity.PictureSelect;
import el.solde.scrapbook.activity.ScrapApp;
import el.solde.scrapbook.adapters.ImageItem;

//Here we create ImageItem for each image from gallery, load there thumbnail url and real url
//return ImageItem[] array with items 
public class GalleryLinksLoader extends AsyncTask<Cursor, Void, ImageItem[]> {

	// instance of parent fragment
	PictureSelect parFragment;

	public GalleryLinksLoader() {
		parFragment = PictureSelect.getInstance();
	}

	// sorting parameter
	final String orderBy = MediaStore.Images.Thumbnails.IMAGE_ID;
	// for array which is associated with Gridview
	ImageItem[] images;

	@Override
	protected ImageItem[] doInBackground(Cursor... cursor) {
		Cursor imageCursor = cursor[0];
		// put the thumbnails and sources
		images = new ImageItem[imageCursor.getCount()];
		if (imageCursor != null) {
			for (int i = 0; i < imageCursor.getCount(); i++) {
				imageCursor.moveToPosition(i);
				int dataColumnIndex = imageCursor
						.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
				int dataColumnIndex2 = imageCursor
						.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
				images[i] = new ImageItem(
						("file://" + imageCursor.getString(dataColumnIndex)),
						MediaStore.Images.Media.getContentUri("external")
								.toString()
								+ "/"
								+ imageCursor.getString(dataColumnIndex2));
			}
		}
		return images;
	}

	@Override
	protected void onPostExecute(ImageItem[] result) {
		super.onPostExecute(result);
		// cacheImages
		ScrapApp.CacheGalleryImages(result);
		// let the UI know about loading finished
		parFragment.ImagesLoadComplete(PictureSelect.gallery);
	}
}
