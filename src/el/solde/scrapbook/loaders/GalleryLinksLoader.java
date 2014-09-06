package el.solde.scrapbook.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import el.solde.scrapbook.activity.PictureSelect;
import el.solde.scrapbook.activity.ScrapApp;
import el.solde.scrapbook.adapters.ImageItem;

//Here we create ImageItem for each image from gallery, load there thumbnail url and real url
//return ImageItem[] array with items 
//this is some kind of singletone, but every time we've finished checking images in gallery,
//we set instance to NULL to be able to execute once more
public class GalleryLinksLoader extends GeneralImageLoader {

	// type of loader
	final String[] columns = { MediaStore.Images.Media.DATA,
			MediaStore.Images.Media._ID };

	// sorting parameter
	final String orderBy = MediaStore.Images.Media.DATE_MODIFIED;

	// instance of parent fragment
	PictureSelect parFragment;

	private static GalleryLinksLoader instance;

	private GalleryLinksLoader() {
		parFragment = PictureSelect.getInstance();
	}

	public static GalleryLinksLoader GetInstance() {
		if (instance == null) {
			synchronized (GalleryLinksLoader.class) {
				if (instance == null) {
					instance = new GalleryLinksLoader();
				}
			}
		}
		return instance;
	}

	@Override
	protected Void doInBackground(Void... params) {
		ContentResolver contentResolver = parFragment.getActivity()
				.getContentResolver();
		Cursor imageCursor = contentResolver.query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy);
		int cursorCount;// initial size of list of images
		if (imageCursor != null) {
			cursorCount = imageCursor.getCount();
			if (ScrapApp.GetInstance().GetGalleryImages() == null
					|| ScrapApp.GetInstance().GetGalleryImages().size() != cursorCount) {
				ScrapApp.GetInstance().CacheGalleryImages(cursorCount);
				for (int i = 0; i < cursorCount; i++) {
					imageCursor.moveToPosition(i);
					int dataColumnIndex = imageCursor
							.getColumnIndex(MediaStore.Images.Media.DATA);
					int dataColumnIndex2 = imageCursor
							.getColumnIndex(MediaStore.Images.Media._ID);
					String cacheFolder = ScrapApp.GetCacheFolder();

					String thumb_id = imageCursor.getString(dataColumnIndex2);

					String thumb = MediaStore.Images.Thumbnails.getContentUri(
							"external").toString()
							+ File.separator + thumb_id;

					String source = "file://"
							+ imageCursor.getString(dataColumnIndex);

					/*
					 * if file is not available as andoid thumbnail we generate
					 * our own thumbnail, save it to cache folder and return
					 * path to our cache
					 */
					if (!IsFileAvailable(thumb)) {
						if (!new File(cacheFolder + File.separator + thumb_id)
								.exists()) {
							File bigImage = new File(
									imageCursor.getString(dataColumnIndex));
							if (bigImage.exists()) {
								Bitmap thumbnailToSave = DecodeBitmap(bigImage);
								File pictureFile = new File(cacheFolder
										+ File.separator + thumb_id);
								try {
									FileOutputStream fos = new FileOutputStream(
											pictureFile);
									thumbnailToSave
											.compress(
													Bitmap.CompressFormat.JPEG,
													90, fos);
									fos.close();
									thumb = "file://" + cacheFolder
											+ File.separator + thumb_id;
								} catch (FileNotFoundException e) {
									Log.d("ScrapBook",
											"File not found: " + e.getMessage());
								} catch (IOException e) {
									Log.d("ScrapBook", "Error accessing file: "
											+ e.getMessage());
								}
							}
						} else {
							thumb = "file://" + cacheFolder + File.separator
									+ thumb_id;
						}
					}
					ImageItem current = new ImageItem(thumb, source);
					publishProgress(current);

				}
			}

			imageCursor.close();
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(ImageItem... values) {
		// TODO Auto-generated method stub
		ScrapApp.GetInstance().CacheGalleryImage(values[0]);
		// let the UI know about loading finished
		parFragment.OnImagesLoadComplete(PictureSelect.gallery);
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		// let the UI know about loading finished
		parFragment.OnImagesLoadComplete(PictureSelect.gallery);
		instance = null;
	}

	private boolean IsFileAvailable(String path) {
		boolean exists = false;
		ContentResolver cr = GetParentFrament().getActivity()
				.getContentResolver();
		String[] projection = { MediaStore.MediaColumns.DATA };
		Cursor cur = cr.query(Uri.parse(path), projection, null, null, null);
		if (cur != null && cur.moveToFirst()) {
			String filePath = cur.getString(0);
			File ff = new File(filePath);
			exists = ff.exists();
		}
		return exists;
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap DecodeBitmap(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 250;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale++;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
