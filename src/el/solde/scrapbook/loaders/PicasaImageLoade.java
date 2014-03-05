package el.solde.scrapbook.loaders;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.util.AuthenticationException;

import el.solde.scrapbook.adapters.ImageItem;

public class PicasaImageLoade extends GeneralImageLoader {

	@Override
	protected ImageItem[] doInBackground(Void... params) {
		PicasawebService myService = new PicasawebService("el-solde-scrapbook");
		try {
			myService.setUserCredentials("eddyf1xxxer@gmail.com",
					"Tlbr87kjcmMargoshenka");
		} catch (AuthenticationException ex) {
		}
		return null;
	}

}
