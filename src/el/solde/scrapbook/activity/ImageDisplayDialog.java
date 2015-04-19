package el.solde.scrapbook.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import el.solde.scrapbook.adapters.ImageItem;

public class ImageDisplayDialog extends DialogFragment implements
		OnClickListener {
	DisplayImageOptions options;
	String bigImage;
	int imagePosition;

	public ImageDisplayDialog() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ImageItem clickedItem = null;
		imagePosition = getArguments().getInt("imageItem");
		switch (PictureSelect.GetCurrentService()) {
		case PictureSelect.gallery: {
			clickedItem = ScrapApp.GetInstance().GetGalleryImages()
					.get(imagePosition);
			break;
		}
		case PictureSelect.facebook: {
			clickedItem = ScrapApp.GetInstance().GetFaceBookImages()
					.get(imagePosition);
			break;
		}
		case PictureSelect.picasa: {
			clickedItem = ScrapApp.GetInstance().GetPicasaImages()
					.get(imagePosition);
			break;
		}
		case PictureSelect.instagram: {
			clickedItem = ScrapApp.GetInstance().GetInstagramImages()
					.get(imagePosition);
			break;
		}
		}
		// get url to image
		bigImage = clickedItem.source();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.image_display_dialog, null);
		ImageView imageView = (ImageView) dialogView.findViewById(R.id.image);
		ImageButton close_btn = (ImageButton) dialogView
				.findViewById(R.id.remove_image_btn);
		close_btn.setOnClickListener(this);
		ImageButton add_btn = (ImageButton) dialogView
				.findViewById(R.id.add_image_btn);
		add_btn.setOnClickListener(this);
		final ProgressBar spinner = (ProgressBar) dialogView
				.findViewById(R.id.loading);

		options = new DisplayImageOptions.Builder()
				.resetViewBeforeLoading(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.ARGB_8888)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		ScrapApp.getImageLoader().displayImage(bigImage, imageView, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						spinner.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						String message = null;
						switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
						}
						Toast.makeText(getActivity(), message,
								Toast.LENGTH_SHORT).show();

						spinner.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						spinner.setVisibility(View.GONE);
					}
				});

		builder.setView(dialogView);
		return builder.create();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.remove_image_btn: {
			CloseDialog();
			break;
		}
		case R.id.add_image_btn: {
			PictureSelect parentFrag = (PictureSelect) getTargetFragment();
			parentFrag.OnSelectedImagesChange(imagePosition,
					PictureSelect.action_add);
			CloseDialog();
			break;
		}
		}

	}

	public void CloseDialog() {
		this.dismiss();
	}
}
