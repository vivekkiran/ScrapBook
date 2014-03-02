package el.solde.scrapbook.adapters;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageItem implements Parcelable {

	public String thumbnail;
	public String source;

	public ImageItem(String _thumbnail, String _source) {
		thumbnail = _thumbnail;
		source = _source;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(thumbnail);
		dest.writeString(source);

	}

	public static final Parcelable.Creator<ImageItem> CREATOR = new Parcelable.Creator<ImageItem>() {
		public ImageItem createFromParcel(Parcel in) {
			return new ImageItem(in);
		}

		public ImageItem[] newArray(int size) {
			return new ImageItem[size];
		}
	};

	private ImageItem(Parcel parcel) {
		thumbnail = parcel.readString();
		source = parcel.readString();
	}

}
