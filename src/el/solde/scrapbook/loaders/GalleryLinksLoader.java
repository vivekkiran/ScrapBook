package el.solde.scrapbook.loaders;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import el.solde.scrapbook.activity.Main;
import el.solde.scrapbook.activity.PictureSelect;
import el.solde.scrapbook.activity.ScrapApp;
import el.solde.scrapbook.adapters.ImageItem;

//Here we create ImageItem for each image from gallery, load there thumbnail url and real url
//return ImageItem[] array with items 
public class GalleryLinksLoader extends AsyncTask<Cursor, Void, ImageItem[]> {

	FragmentActivity activity;
	// sorting parameter
	final String orderBy = MediaStore.Images.Thumbnails.IMAGE_ID;
	// for array which is associated with Gridview
	ImageItem[] images;

	public GalleryLinksLoader() {
		activity = Main.getInstance();
	}

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
						("file://" + getRealPathFromURI(Uri
								.parse(MediaStore.Images.Media.getContentUri(
										"external").toString()
										+ "/"
										+ imageCursor
												.getString(dataColumnIndex2)))));
			}
		}
		return images;
	}

	@Override
	protected void onPostExecute(ImageItem[] result) {
		super.onPostExecute(result);
		// cacheImages
		ScrapApp.CacheGalleryImages(result);
		PictureSelect.getInstance().updateUserInterface(images);
	}

	// transform real image URI to real path
	private String getRealPathFromURI(Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = activity.getContentResolver().query(contentUri, proj,
					null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} catch (CursorIndexOutOfBoundsException ex) {
			return orderBy;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
}
