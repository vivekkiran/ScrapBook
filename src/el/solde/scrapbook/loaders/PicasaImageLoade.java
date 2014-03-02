package el.solde.scrapbook.loaders;

import com.google.gdata.client.photos.PicasawebService;

import el.solde.scrapbook.adapters.ImageItem;

public class PicasaImageLoade extends GeneralImageLoader {

	@Override
	protected ImageItem[] doInBackground(Void... params) {
		PicasawebService myService = new PicasawebService("el-solde-scrapbook");

		return null;
	}

}
