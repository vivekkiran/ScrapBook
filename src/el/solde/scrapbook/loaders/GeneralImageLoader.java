package el.solde.scrapbook.loaders;

import android.os.AsyncTask;
import el.solde.scrapbook.activity.PictureSelect;
import el.solde.scrapbook.adapters.ImageItem;

public abstract class GeneralImageLoader extends
		AsyncTask<Void, Integer, ImageItem[]> {

	private PictureSelect parFragment;

	public GeneralImageLoader() {
		parFragment = PictureSelect.getInstance();
	}

	protected PictureSelect GetParentFrament() {
		return parFragment;
	}

}
