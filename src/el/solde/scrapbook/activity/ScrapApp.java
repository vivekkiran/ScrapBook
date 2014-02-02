package el.solde.scrapbook.activity;

import java.io.File;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap.Config;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import el.solde.scrapbook.adapters.ImageItem;

public class ScrapApp extends Application {

	private static ImageLoader imageLoader;
	private static SharedPreferences pref;
	private static Editor editPref;
	private static ImageItem[] galleryImages;
	private static ImageItem[] faceBookImages;
	private static ImageItem[] picasaImages;
	private static ImageItem[] flickrImages;

	@Override
	public void onCreate() {
		super.onCreate();

		// get preferences and settings
		pref = getSharedPreferences("el.solde.scrapbook", MODE_PRIVATE);

		// if directories aren't created - create'em all :), otherwise proceed
		// with initialization
		if (!pref.getBoolean("folders_created", false)) {
			// folder dir
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File directory = new File(
						Environment.getExternalStorageDirectory()
								+ File.separator + "ScrapBook");
				if (!directory.exists()) {
					directory.mkdirs();
					// cache dir
					directory = new File(
							Environment.getExternalStorageDirectory()
									+ File.separator + "ScrapBook"
									+ File.separator + "Cache");
					directory.mkdirs();
					editPref = pref.edit();
					editPref.putBoolean("folders_created", true);
					editPref.commit();
				}
			}
		}
		{
			init();
		}
	}

	public static ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void init() {
		File cacheDir = StorageUtils.getOwnCacheDirectory(
				getApplicationContext(),
				Environment.getExternalStorageDirectory() + File.separator
						+ "ScrapBook" + File.separator + "Cache");
		// Create global configuration and initialize ImageLoader with this
		// configuration
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				// .showStubImage(R.drawable.btn_save_insta)
				// .showImageForEmptyUri(R.drawable.btn_save_insta)
				.bitmapConfig(Config.RGB_565).cacheOnDisc(true)
				.cacheInMemory(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).threadPoolSize(8).threadPriority(5)
				.discCache(new UnlimitedDiscCache(cacheDir))
				.defaultDisplayImageOptions(options).writeDebugLogs().build();

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}

	// cache urls to images while app is alive
	public static void CacheGalleryImages(ImageItem[] _images) {
		galleryImages = _images;
	}

	// get cachedImages or null
	public static ImageItem[] GetGalleryImages() {
		if (galleryImages != null)
			return galleryImages;
		else
			return null;
	}

	// cache urls to images while app is alive
	public static void CacheFaceBookImages(ImageItem[] _images) {
		faceBookImages = _images;
	}

	// get cachedImages or null
	public static ImageItem[] GetFaceBookImages() {
		if (faceBookImages != null)
			return faceBookImages;
		else
			return null;
	}

	// cache urls to images while app is alive
	public static void CachePicasaImages(ImageItem[] _images) {
		picasaImages = _images;
	}

	// get cachedImages or null
	public static ImageItem[] GetPicasaImages() {
		if (flickrImages != null)
			return picasaImages;
		else
			return null;
	}

	// cache urls to images while app is alive
	public static void CacheFlickrImages(ImageItem[] _images) {
		flickrImages = _images;
	}

	// get cachedImages or null
	public static ImageItem[] GetFlickrImages() {
		if (flickrImages != null)
			return flickrImages;
		else
			return null;
	}

}
