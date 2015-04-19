package el.solde.scrapbook.activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import el.solde.scrapbook.adapters.ImageItem;
import el.solde.scrapbook.adapters.PhotosAdapter;
import el.solde.scrapbook.adapters.SelectedPicturesAdapter;
import el.solde.scrapbook.loaders.FacebookImagesLoader;
import el.solde.scrapbook.loaders.GalleryLinksLoader;
import el.solde.scrapbook.loaders.InstagramImagesLoader;
import el.solde.scrapbook.loaders.PicasaImagesLoader;

public class PictureSelect extends Fragment {

	// communitacion interface between fragments
	IFragmentCommunicationListener mCallBack;

	// arrays of images and thumbs for grid
	// private ImageItem[] images;

	// services
	public final static int gallery = 1;
	public final static int facebook = 2;
	public final static int picasa = 3;
	public final static int instagram = 4;
	// selected images actions
	public final static int action_delete = 0;
	public final static int action_add = 1;

	// Hold a reference to the current animator,
	// so that it can be canceled mid-way.
	private Animator mCurrentAnimator;

	// Retrieve and cache the system's default "short" animation time.
	private int mShortAnimationDuration;

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

	private static PictureSelect _instance = null;

	private SelectedPicturesAdapter selectedPicsAdapter = null;

	private ListView selectedListView = null;

	private HorizontalListView selectedHorizontalListView = null;

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
		if (currentService == gallery) {
			if (GalleryLinksLoader.GetInstance().getStatus() != AsyncTask.Status.RUNNING)
				GalleryLinksLoader.GetInstance().execute();
		}
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
		mShortAnimationDuration = getResources().getInteger(
				android.R.integer.config_shortAnimTime);
		// adapter for selected items
		// if ScrapApp.selectedItems is not empty then something has been
		// selected
		// and it can be just orientation change
		selectedPicsAdapter = new SelectedPicturesAdapter(getActivity(),
				ScrapApp.GetInstance().GetSelectedItems());
		if (getResources().getConfiguration().orientation == getResources()
				.getConfiguration().ORIENTATION_LANDSCAPE) {
			selectedListView = (ListView) view
					.findViewById(R.id.selected_pics_list);
			selectedListView.setAdapter(selectedPicsAdapter);
		} else {
			selectedHorizontalListView = (HorizontalListView) view
					.findViewById(R.id.selected_pics_list_H);
			selectedHorizontalListView.setAdapter(selectedPicsAdapter);
		}
		switch (currentService) {
		case gallery: {
			if (GalleryLinksLoader.GetInstance().getStatus() != AsyncTask.Status.RUNNING)
				GalleryLinksLoader.GetInstance().execute();
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
			if (PicasaImagesLoader.GetInstance().getStatus() != AsyncTask.Status.RUNNING)
				PicasaImagesLoader.GetInstance().execute();
			break;
		}
		case instagram: {
			if (InstagramImagesLoader.GetInstance().getStatus() != AsyncTask.Status.RUNNING)
				InstagramImagesLoader.GetInstance().execute();
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
				if (GalleryLinksLoader.GetInstance().getStatus() != AsyncTask.Status.RUNNING)
					GalleryLinksLoader.GetInstance().execute();
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
					if (FacebookImagesLoader.GetInstance().getStatus() != AsyncTask.Status.RUNNING)
						FacebookImagesLoader.GetInstance().execute();
				}
				break;
			}
			case picasa: {
				currentService = picasa;
				// check if we have images in cache
				if (PicasaImagesLoader.GetInstance().getStatus() != AsyncTask.Status.RUNNING)
					PicasaImagesLoader.GetInstance().execute();
				break;
			}
			case instagram: {
				currentService = instagram;
				if (InstagramImagesLoader.GetInstance().getStatus() != AsyncTask.Status.RUNNING)
					InstagramImagesLoader.GetInstance().execute();
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
			if (FacebookImagesLoader.GetInstance().getStatus() != AsyncTask.Status.RUNNING) {
				FacebookImagesLoader.GetInstance().execute();
			}
		} else if (state.isClosed()) {
			if (!loginButton.isShown()) {
				loginButton.setVisibility(View.VISIBLE);
			}
		}
	}

	// method is called when changes occur to selected images list
	public void OnSelectedImagesChange(int _imagePosition, int _action) {
		updateSelectedImages(_imagePosition, _action);
	}

	// depending on action add or remove item from SelectedImagesList
	private void updateSelectedImages(int _imagePosition, int _action) {
		switch (_action) {
		case action_add: {
			// based on selected service we add item to SelectedList
			ImageItem item = null;
			switch (GetCurrentService()) {
			case gallery: {
				item = ScrapApp.GetInstance().GetGalleryImages()
						.get(_imagePosition);
				selectedPicsAdapter.Add(item);

				break;
			}
			case facebook: {
				item = ScrapApp.GetInstance().GetFaceBookImages()
						.get(_imagePosition);
				selectedPicsAdapter.Add(item);

				break;
			}
			case picasa: {
				item = ScrapApp.GetInstance().GetPicasaImages()
						.get(_imagePosition);
				selectedPicsAdapter.Add(item);

				break;
			}
			case instagram: {
				item = ScrapApp.GetInstance().GetInstagramImages()
						.get(_imagePosition);
				selectedPicsAdapter.Add(item);

				break;
			}
			}
			selectedPicsAdapter.notifyDataSetChanged();
			break;
		}

		case action_delete: {
			selectedPicsAdapter.Remove(_imagePosition);
			selectedPicsAdapter.notifyDataSetChanged();
			break;
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
					imgDisplay.setTargetFragment(getInstance(), 0);
					Bundle params = new Bundle();
					params.putInt("imageItem", position);
					// params.putString("imageUrl", clickedItem.source());
					imgDisplay.setArguments(params);
					imgDisplay.show(getFragmentManager(), null);
					// zoomImageFromThumb(view);
				}
			}
		});
		if (gridImg.getVisibility() == View.GONE) {
			gridImg.setVisibility(View.VISIBLE);
		}
		switch (service) {
		case gallery: {
			photosAdapter.SetImagesToShow(ScrapApp.GetInstance()
					.GetGalleryImages());
			photosAdapter.notifyDataSetChanged();
			gridImg.setAdapter(photosAdapter);
			break;
		}
		case facebook: {
			photosAdapter.SetImagesToShow(ScrapApp.GetInstance()
					.GetFaceBookImages());
			photosAdapter.notifyDataSetChanged();
			gridImg.setAdapter(photosAdapter);
			break;
		}
		case picasa: {
			photosAdapter.SetImagesToShow(ScrapApp.GetInstance()
					.GetPicasaImages());
			photosAdapter.notifyDataSetChanged();
			gridImg.setAdapter(photosAdapter);
			break;
		}
		case instagram: {
			photosAdapter.SetImagesToShow(ScrapApp.GetInstance()
					.GetInstagramImages());
			photosAdapter.notifyDataSetChanged();
			gridImg.setAdapter(photosAdapter);
			break;
		}
		}
	}

}
