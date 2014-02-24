package el.solde.scrapbook.loaders;

import android.database.Cursor;
import android.provider.MediaStore;
import el.solde.scrapbook.activity.PictureSelect;
import el.solde.scrapbook.activity.ScrapApp;
import el.solde.scrapbook.adapters.ImageItem;

//Here we create ImageItem for each image from gallery, load there thumbnail url and real url
//return ImageItem[] array with items 

public class GalleryLinksLoader extends GeneralImageLoader {

	// type of loader
	final String[] columns = { MediaStore.Images.Thumbnails.DATA,
			MediaStore.Images.Thumbnails.IMAGE_ID };

	// sorting parameter
	final String orderBy = MediaStore.Images.Thumbnails.IMAGE_ID;

	// instance of parent fragment
	PictureSelect parFragment;

	// for array which is associated with Gridview
	ImageItem[] images;

	public GalleryLinksLoader() {
		parFragment = PictureSelect.getInstance();
	}

	@Override
	protected ImageItem[] doInBackground(Void... params) {
		if (ScrapApp.GetGalleryImages() != null) {
			images = ScrapApp.GetGalleryImages();
		} else {
			Cursor imageCursor = parFragment
					.getActivity()
					.getContentResolver()
					.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
							columns, // Which columns to return
							null, // Return all rows
							null, orderBy);

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
					publishProgress(images);
				}
			}

			imageCursor.close();
		}
		return images;
	}

	@Override
	protected void onProgressUpdate(ImageItem[]... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		ScrapApp.CacheGalleryImages(values[0]);
		// let the UI know about loading finished
		parFragment.OnImagesLoadComplete(PictureSelect.gallery);
	}

	@Override
	protected void onPostExecute(ImageItem[] result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		// cacheImages
		ScrapApp.CacheGalleryImages(result);
		// let the UI know about loading finished
		parFragment.OnImagesLoadComplete(PictureSelect.gallery);
	}
}
