package el.solde.scrapbook.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import el.solde.scrapbook.activity.R;
import el.solde.scrapbook.activity.ScrapApp;

public class SelectedPicturesAdapter extends BaseAdapter {

	private List<ImageItem> items;
	private LayoutInflater inflater;
	private FrameLayout listItem;
	private ImageLoader imageLoader;

	public SelectedPicturesAdapter(Context _context) {
		inflater = LayoutInflater.from(_context);
		imageLoader = ScrapApp.getImageLoader();
		items = new ArrayList<ImageItem>();
	}

	@Override
	public int getCount() {
		if (items != null)
			return items.size();
		else
			return 0;
	}

	// returns item of ImageItem type
	@Override
	public ImageItem getItem(int position) {
		int _position = position;
		if (getCount() != 0) {
			return items.get(_position);
		} else
			return null;
	}

	public void Remove(int position) {
		if (getCount() != 0)
			items.remove(position);
	}

	public void Add(ImageItem object) {
		items.add(object);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.pinned_selected_item, null);
		}
		ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
		try {
			imageLoader
					.displayImage(items.get(position).thumbnail(), imageView);

		} catch (NullPointerException npe) {
			Log.d("exception", "NullPointer: " + npe.getMessage());
		}

		return convertView;

	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

}
