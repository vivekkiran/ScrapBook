package el.solde.scrapbook.adapters;

public class ImageItem {

	private String Thumbnail;
	private String Source;

	public ImageItem(String _thumbnail, String _source) {
		Thumbnail = _thumbnail;
		Source = _source;
	}

	public String source() {
		return this.Source;
	}

	public String thumbnail() {
		return this.Thumbnail;
	}

}
