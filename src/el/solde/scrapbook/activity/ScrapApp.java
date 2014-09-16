package el.solde.scrapbook.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import el.solde.scrapbook.adapters.ImageItem;

public class ScrapApp extends Application {

	private static ImageLoader imageLoader;
	private static SharedPreferences pref;
	private static Editor editPref;
	private static ScrapApp instance = null;
	private List<ImageItem> galleryImages = null;
	private List<ImageItem> faceBookImages = null;
	private List<ImageItem> picasaImages = null;
	private List<ImageItem> instagramImages = null;
	private List<ImageItem> selectedImages = null;
	private static ScrapProject CurrentScrapProject = new ScrapProject();
	private static String cacheFolder = null;

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
								+ File.separator + "ScrapBook" + File.separator
								+ "Cache");
				if (!directory.exists()) {
					directory.mkdirs();
					editPref = pref.edit();
					editPref.putBoolean("folders_created", true);
					editPref.commit();
				}
				File last_scrap = new File(
						Environment.getExternalStorageDirectory()
								+ File.separator + "ScrapBook" + File.separator
								+ "last.scp");
				if (!last_scrap.exists()) {
					try {
						last_scrap.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		{
			init();
		}
		instance = this;
	}

	public static ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void init() {
		cacheFolder = Environment.getExternalStorageDirectory()
				+ File.separator + "ScrapBook" + File.separator + "Cache";
		File cacheDir = new File(cacheFolder);
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

	public ScrapProject GetScrapProject() {
		return CurrentScrapProject;
	}

	// saves selected image to list of selected images
	public void AddSelectedItem(ImageItem _image) {
		if (selectedImages != null)
			selectedImages.add(_image);
		else {
			selectedImages = new ArrayList<ImageItem>();
			selectedImages.add(_image);
		}
	}

	public List<ImageItem> GetSelectedItems() {
		if (selectedImages != null)
			return selectedImages;
		else
			return null;
	}

	// removes selected image from list of selected images
	public void RemoveSelectedItem(int index) {
		if (selectedImages != null) {
			selectedImages.remove(index);
		}
	}

	// cache urls to images while app is alive
	public void CacheGalleryImages(List<ImageItem> _images) {
		galleryImages = _images;
	}

	// cache urls to images while app is alive
	public void CacheGalleryImages(int size) {
		galleryImages = new ArrayList<ImageItem>(size);
	}

	public void CacheGalleryImage(ImageItem _image) {
		if (galleryImages != null)
			galleryImages.add(_image);
		else {
			galleryImages = new ArrayList<ImageItem>();
			galleryImages.add(_image);
		}
	}

	// get cachedImages or null
	public List<ImageItem> GetGalleryImages() {
		if (galleryImages != null)
			return galleryImages;
		else
			return null;
	}

	// cache urls to images while app is alive
	public void CacheFaceBookImages(List<ImageItem> _images) {
		faceBookImages = _images;
	}

	// cache urls to images while app is alive
	public void CacheFaceBookImages(int size) {
		faceBookImages = new ArrayList<ImageItem>(size);
	}

	public void CacheFaceBookImage(ImageItem _image) {
		if (faceBookImages != null)
			faceBookImages.add(_image);
		else {
			faceBookImages = new ArrayList<ImageItem>();
			faceBookImages.add(_image);
		}
	}

	// get cachedImages or null
	public List<ImageItem> GetFaceBookImages() {
		if (faceBookImages != null)
			return faceBookImages;
		else
			return null;
	}

	// cache urls to images while app is alive
	public void CachePicasaImages(List<ImageItem> _images) {
		picasaImages = _images;
	}

	// cache urls to images while app is alive
	public void CachePicasaImages(int size) {
		picasaImages = new ArrayList<ImageItem>(size);
	}

	public void CachePicasaImage(ImageItem _image) {
		if (picasaImages != null)
			picasaImages.add(_image);
		else {
			picasaImages = new ArrayList<ImageItem>();
			picasaImages.add(_image);
		}
	}

	// get cachedImages or null
	public List<ImageItem> GetPicasaImages() {
		if (picasaImages != null)
			return picasaImages;
		else
			return null;
	}

	// cache urls to images while app is alive
	public void CacheInstagramImages(List<ImageItem> _images) {
		instagramImages = _images;
	}

	// cache urls to images while app is alive
	public void CacheInstagramImages(int size) {
		instagramImages = new ArrayList<ImageItem>(size);
	}

	public void CacheInstagramImage(ImageItem _image) {
		if (instagramImages != null)
			instagramImages.add(_image);
		else {
			instagramImages = new ArrayList<ImageItem>();
			instagramImages.add(_image);
		}

	}

	// get cachedImages or null
	public List<ImageItem> GetInstagramImages() {
		if (instagramImages != null)
			return instagramImages;
		else
			return null;
	}

	public static void SavePreference(String name, Object preference) {
		editPref = pref.edit();
		if (preference.getClass().equals(Integer.TYPE)) {
			editPref.putInt(name, (Integer) preference);
		}
		if (preference.getClass().equals(String.class)) {
			editPref.putString(name, (String) preference);
		}
		if (preference.getClass().equals(Boolean.TYPE)) {
			editPref.putBoolean(name, (Boolean) preference);
		}
		editPref.commit();
	}

	public static SharedPreferences GetPreference() {
		return pref;
	}

	public static ScrapApp GetInstance() {
		return instance;
	}

	public static String GetCacheFolder() {
		return cacheFolder;
	}

}
