package el.solde.scrapbook.activity;

import java.util.ArrayList;
import java.util.List;

import el.solde.scrapbook.adapters.ImageItem;

public class ScrapProject {
	private List<ImageItem> selectedImages;

	public ScrapProject() {
		selectedImages = new ArrayList<ImageItem>();
	}

	public ScrapProject(List<ImageItem> _selectedImages) {
		this.selectedImages = _selectedImages;
	}

	public List<ImageItem> GetSelectedImages() {
		return this.selectedImages;
	}

	public void AddImage(ImageItem _imageItem) {
		selectedImages.add(_imageItem);
	}

	public void RemoveImage(int location) {
		selectedImages.remove(location);
	}

	public void RemoveImage(ImageItem _imageItem) {
		selectedImages.remove(_imageItem);
	}
}
