package el.solde.scrapbook.loaders;

import java.util.List;

import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.GphotoEntry;
import com.google.gdata.data.photos.PhotoEntry;

import el.solde.scrapbook.activity.PictureSelect;
import el.solde.scrapbook.activity.ScrapApp;
import el.solde.scrapbook.adapters.ImageItem;

public class PicasaImagesLoader extends GeneralImageLoader {

	private AlbumFeed feed;
	// for array which is associated with Gridview
	ImageItem[] images;
	// instance of parent fragment
	PictureSelect parFragment;

	public PicasaImagesLoader() {
		// TODO Auto-generated constructor stub
		parFragment = GetParentFrament();
	}

	public PicasaImagesLoader(AlbumFeed _feed) {
		this.feed = _feed;
		parFragment = GetParentFrament();
	}

	@Override
	protected ImageItem[] doInBackground(Void... params) {
		if (ScrapApp.GetPicasaImages() != null) {
			images = ScrapApp.GetPicasaImages();
		} else {
			if (feed != null) {
				List<GphotoEntry> Gphoto = (List<GphotoEntry>) feed
						.getEntries();
				images = new ImageItem[Gphoto.size()];
				for (int i = 0; i < Gphoto.size(); i++) {
					PhotoEntry photo = new PhotoEntry(Gphoto.get(i));
					images[i] = new ImageItem(photo.getMediaContents().get(0)
							.getUrl(), photo.getMediaThumbnails().get(0)
							.getUrl());
					publishProgress(images);
				}
			}
		}
		return images;
	}

	@Override
	protected void onProgressUpdate(ImageItem[]... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		ScrapApp.CacheFaceBookImages(values[0]);
		// let the UI know about loading finished
		parFragment.OnImagesLoadComplete(PictureSelect.picasa);
	}

	@Override
	protected void onPostExecute(ImageItem[] result) {
		super.onPostExecute(result);
		// cacheImages
		ScrapApp.CacheFaceBookImages(result);
		// let the UI know about loading finished
		parFragment.OnImagesLoadComplete(PictureSelect.picasa);
	}

}
