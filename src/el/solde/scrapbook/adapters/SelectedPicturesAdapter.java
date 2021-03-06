package el.solde.scrapbook.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import el.solde.scrapbook.activity.PictureSelect;
import el.solde.scrapbook.activity.R;
import el.solde.scrapbook.activity.ScrapApp;

public class SelectedPicturesAdapter extends BaseAdapter {

	private List<ImageItem> items;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	public SelectedPicturesAdapter(Context _context) {
		inflater = LayoutInflater.from(_context);
		imageLoader = ScrapApp.getImageLoader();
		items = new ArrayList<ImageItem>();
	}

	public SelectedPicturesAdapter(Context _context, List<ImageItem> _items) {
		inflater = LayoutInflater.from(_context);
		imageLoader = ScrapApp.getImageLoader();
		items = _items;
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
		// this adds item also to global array in scrapapp, because items holds
		// ref to ScrapApp.GetInstance().GetSelectedItems()
		items.add(object);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.pinned_selected_item, null);
		}
		ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
		ImageButton remove_btn = (ImageButton) convertView
				.findViewById(R.id.pin);
		final int _position = position;
		remove_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PictureSelect.getInstance().OnSelectedImagesChange(_position,
						PictureSelect.action_delete);
			}
		});

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
