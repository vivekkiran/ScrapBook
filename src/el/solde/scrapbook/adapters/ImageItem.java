package el.solde.scrapbook.adapters;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ImageItem implements Parcelable {

	private String thumbnail;
	private String source;

	public ImageItem(String thumbnail, String source) {
		this.thumbnail = thumbnail;
		this.source = source;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String toString() {
		// returning thumbnail because that is what will be displayed in the
		// Spinner control
		return (this.thumbnail);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Log.d("parsel", "writingTo..");
		dest.writeString(thumbnail);
		dest.writeString(source);

	}

	public static final Parcelable.Creator<ImageItem> CREATOR = new Parcelable.Creator<ImageItem>() {
		// распаковываем объект из Parcel
		public ImageItem createFromParcel(Parcel in) {
			Log.d("parsel", "createFromParcel");
			return new ImageItem(in);
		}

		public ImageItem[] newArray(int size) {
			return new ImageItem[size];
		}
	};

	private ImageItem(Parcel parcel) {
		Log.d("parsel", "MyObject(Parcel parcel)");
		thumbnail = parcel.readString();
		source = parcel.readString();
	}

}
