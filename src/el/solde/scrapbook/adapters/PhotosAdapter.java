package el.solde.scrapbook.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import el.solde.scrapbook.activity.R;
import el.solde.scrapbook.activity.ScrapApp;

public class PhotosAdapter extends BaseAdapter {

	ImageItem[] mList;
	LayoutInflater mInflater;
	Context mContext;
	ImageLoader imageLoader;

	public PhotosAdapter() {
		imageLoader = ScrapApp.getImageLoader();
	}

	public void SetContext(Context context) {
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	public void SetImagesToShow(ImageItem[] images) {
		this.mList = images;
	}

	public PhotosAdapter(Context context, ImageItem[] _images) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		this.mList = _images;
		imageLoader = ScrapApp.getImageLoader();
	}

	@Override
	public int getCount() {
		if (mList != null)
			return mList.length;
		else
			return 0;
	}

	// returns item of ImageItem type
	@Override
	public ImageItem getItem(int position) {
		int _position = position;
		if (getCount() != 0) {
			return mList[_position];
		} else
			return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.pic_grid_item, null);
		}
		final ImageView imageView = (ImageView) convertView
				.findViewById(R.id.image);
		try {
			imageLoader.displayImage(mList[position].thumbnail(), imageView);
		} catch (NullPointerException npe) {
			Log.d("exception", "NullPointer: " + npe.getMessage());
		}

		return convertView;

	}
}
