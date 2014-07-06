package el.solde.scrapbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import el.solde.scrapbook.adapters.ImageItem;
import el.solde.scrapbook.adapters.PhotosAdapter;
import el.solde.scrapbook.loaders.FacebookImagesLoader;
import el.solde.scrapbook.loaders.GalleryLinksLoader;
import el.solde.scrapbook.loaders.InstagramImagesLoader;
import el.solde.scrapbook.loaders.PicasaImagesLoader;

public class PictureSelect extends Fragment {

	// comunitacion interface between fragments
	IFragmentCommunicationListener mCallBack;

	// arrays of images and thumbs for grid
	// private ImageItem[] images;

	// services
	public final static int gallery = 1;
	public final static int facebook = 2;
	public final static int picasa = 3;
	public final static int instagram = 4;

	// FB session state change listener
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	private UiLifecycleHelper uiHelper;

	private PhotosAdapter photosAdapter = null;

	// visible layout by defult - gallery
	private static int currentService = gallery;

	private static PictureSelect _instance;

	public static PictureSelect newInstance(String title) {

		PictureSelect pageFragment = new PictureSelect();
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		pageFragment.setArguments(bundle);
		return pageFragment;
	}

	public static PictureSelect getInstance() {
		return _instance;
	}

	public Session GetFacebookSession() {
		return Session.getActiveSession();
	}

	public static int GetCurrentService() {
		return currentService;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallBack = (IFragmentCommunicationListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ServiceSelectListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
		_instance = this;

	}

	@Override
	public void onResume() {
		super.onResume();
		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}
		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater
				.inflate(R.layout.pictures_select, container, false);
		switch (currentService) {
		case gallery: {
			new GalleryLinksLoader().execute();
			break;
		}
		case facebook: {
			LoginButton authButton = (LoginButton) view
					.findViewById(R.id.authButton);
			// To allow the fragment to receive the onActivityResult() call
			// setFragment() method on the LoginButton instance.
			authButton.setFragment(this);
			authButton.setReadPermissions("user_photos");
			break;
		}
		case picasa: {
			// check if we have images in cache
			PicasaImagesLoader loader = new PicasaImagesLoader();
			loader.execute();
			break;
		}
		case instagram: {
			InstagramImagesLoader insta = new InstagramImagesLoader();
			insta.execute();
			break;
		}
		}
		return view;

	}

	public void SetSelectedService(Bundle params) {
		int service = params.getInt("service");
		int previousService = params.getInt("previousService");
		if (service != previousService) {
			switch (service) {
			case gallery: {
				currentService = gallery;
				// here we get last visible service and hide it
				LoginButton authButton = (LoginButton) getActivity()
						.findViewById(R.id.authButton);
				if (authButton.isShown()) {
					authButton.setVisibility(View.GONE);
				}
				new GalleryLinksLoader().execute();
				break;
			}
			case facebook: {
				currentService = facebook;
				LoginButton authButton1 = (LoginButton) getActivity()
						.findViewById(R.id.authButton);
				// To allow the fragment to receive the onActivityResult() call
				// setFragment() method on the Facebook LoginButton instance.
				authButton1.setFragment(this);
				authButton1.setReadPermissions("user_photos");
				Session thisSession = Session.getActiveSession();
				if (!thisSession.isOpened()) {
					authButton1.setVisibility(View.VISIBLE);
					GridView gridImg = (GridView) getView().findViewById(
							R.id.gallery_grid_view);
					gridImg.setVisibility(View.GONE);
				} else {
					authButton1.setVisibility(View.GONE);
					new FacebookImagesLoader().execute();
				}
				break;
			}
			case picasa: {
				currentService = picasa;
				// check if we have images in cache
				PicasaImagesLoader loader = new PicasaImagesLoader();
				loader.execute();
				break;
			}
			case instagram: {
				currentService = instagram;
				InstagramImagesLoader insta = new InstagramImagesLoader();
				insta.execute();
				break;
			}
			}
		} else {
			currentService = service;
		}
	}

	// Facebook Part
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		LoginButton loginButton = (LoginButton) getActivity().findViewById(
				R.id.authButton);
		if (state.isOpened()) {
			if (loginButton.isShown()) {
				loginButton.setVisibility(View.GONE);
			}
			new FacebookImagesLoader().execute();
		} else if (state.isClosed()) {
			if (!loginButton.isShown()) {
				loginButton.setVisibility(View.VISIBLE);
			}
		}
	}

	// this method is called by every image loader, when loading is complete and
	// and images links are cached in SrapApp
	// _service - this shows what Loader called method, if it equals
	// currentService, then update UI
	public void OnImagesLoadComplete(int _service) {
		int service = _service;
		if (service == currentService)
			updateUserInterface(service);
	}

	// method updates images gridview according to service selected
	// THIS METHOS SHOULD NOT BE CALLED DIRECTLY ONLY THROUGH ImagesLoadComplete
	private void updateUserInterface(int _service) {
		final int service = _service;
		if (photosAdapter == null) {
			// create adapter and set context
			photosAdapter = new PhotosAdapter();
			photosAdapter.SetContext(this.getActivity());
		}
		GridView gridImg = (GridView) getView().findViewById(
				R.id.gallery_grid_view);
		gridImg.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ImageItem clickedItem = photosAdapter.getItem(position);
				if (clickedItem != null) {
					ImageDisplayDialog imgDisplay = new ImageDisplayDialog();
					Bundle params = new Bundle();
					params.putString("imageUrl", clickedItem.source());
					imgDisplay.setArguments(params);
					imgDisplay.show(getFragmentManager(), null);
				}
			}
		});
		if (gridImg.getVisibility() == View.GONE) {
			gridImg.setVisibility(View.VISIBLE);
		}
		switch (service) {
		case gallery: {
			photosAdapter.SetImagesToShow(ScrapApp.GetGalleryImages());
			photosAdapter.notifyDataSetChanged();
			gridImg.setAdapter(photosAdapter);
			break;
		}
		case facebook: {
			photosAdapter.SetImagesToShow(ScrapApp.GetFaceBookImages());
			photosAdapter.notifyDataSetChanged();
			gridImg.setAdapter(photosAdapter);
			break;
		}
		case picasa: {
			photosAdapter.SetImagesToShow(ScrapApp.GetPicasaImages());
			photosAdapter.notifyDataSetChanged();
			gridImg.setAdapter(photosAdapter);
			break;
		}
		case instagram: {
			photosAdapter.SetImagesToShow(ScrapApp.GetInstagramImages());
			photosAdapter.notifyDataSetChanged();
			gridImg.setAdapter(photosAdapter);
			break;
		}
		}
	}

}
