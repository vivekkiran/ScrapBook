package el.solde.scrapbook.loaders;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.GphotoEntry;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import el.solde.scrapbook.activity.PictureSelect;
import el.solde.scrapbook.activity.ScrapApp;
import el.solde.scrapbook.adapters.ImageItem;
import el.solde.scrapbook.picasa.PicasaLoginDialog;

public class PicasaImagesLoader extends GeneralImageLoader {

	private AlbumFeed feed;
	// for array which is associated with Gridview
	ImageItem[] images;
	// instance of parent fragment
	PictureSelect parFragment;
	// instance of loader
	private static PicasaImagesLoader _instance;

	private String login, password;

	public PicasaImagesLoader() {
		// TODO Auto-generated constructor stub
		parFragment = GetParentFrament();
		_instance = this;
		login = ScrapApp.GetPreference().getString("login", "");
		password = ScrapApp.GetPreference().getString("password", "");
	}

	public PicasaImagesLoader(String _login, String _password) {
		// TODO Auto-generated constructor stub
		parFragment = GetParentFrament();
		_instance = this;
		login = _login;
		password = _password;
	}

	@Override
	protected ImageItem[] doInBackground(Void... params) {
		// if images are cacher - show them from cache
		if (ScrapApp.GetPicasaImages() != null) {
			images = ScrapApp.GetPicasaImages();
		} else {
			// if credentials were saved - proceed with them
			if (login != null && login != "" && password != null
					& password != "") {
				// get feed with pictures
				feed = GetUserMedia(login, password);
				if (feed != null) {
					List<GphotoEntry> Gphoto = (List<GphotoEntry>) feed
							.getEntries();
					images = new ImageItem[Gphoto.size()];
					for (int i = 0; i < Gphoto.size(); i++) {
						PhotoEntry photo = new PhotoEntry(Gphoto.get(i));
						images[i] = new ImageItem(photo.getMediaThumbnails()
								.get(0).getUrl(), photo.getMediaContents()
								.get(0).getUrl());
						publishProgress(images);
					}
				}
			}
			// no saved credentials - show dialog
			else {
				PicasaLoginDialog login = new PicasaLoginDialog();
				login.show(parFragment.getFragmentManager(), "Picasa Login");
			}
		}
		return images;
	}

	@Override
	protected void onProgressUpdate(ImageItem[]... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		ScrapApp.CachePicasaImages(values[0]);
		// let the UI know about loading finished
		parFragment.OnImagesLoadComplete(PictureSelect.picasa);
	}

	@Override
	protected void onPostExecute(ImageItem[] result) {
		super.onPostExecute(result);
		// cacheImages
		ScrapApp.CachePicasaImages(result);
		// let the UI know about loading finished
		parFragment.OnImagesLoadComplete(PictureSelect.picasa);
	}

	// performs login and returns AlbumFeed
	public AlbumFeed GetUserMedia(String _login, String _pass) {
		String login = _login;
		String password = _pass;
		AlbumFeed feed = null;
		PicasawebService picasa = new PicasawebService("el-solde-scrapbook");
		try {
			picasa.setUserCredentials(login, password);
			URL feedUrl = new URL(
					"https://picasaweb.google.com/data/feed/api/user/default?kind=photo&thumbsize=144&imgmax=1024");
			feed = picasa.getFeed(feedUrl, AlbumFeed.class);
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return feed;
	}

	public static PicasaImagesLoader GetInstance() {
		return _instance;
	}
}
