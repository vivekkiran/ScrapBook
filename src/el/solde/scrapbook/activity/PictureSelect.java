package el.solde.scrapbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import el.solde.scrapbook.adapters.PhotosAdapter;
import el.solde.scrapbook.loaders.FaceBookImagesLoader;
import el.solde.scrapbook.loaders.GalleryLinksLoader;

public class PictureSelect extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	// comunitacion interface between fragments
	FragmentCommunicationListener mCallBack;

	// arrays of images and thumbs for grid
	// private ImageItem[] images;

	// services
	public final static int gallery = 1;
	public final static int facebook = 2;

	// FB session state change listener
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	private UiLifecycleHelper uiHelper;

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

	// type of loader
	private static final int MEDIA_URL_LOADER = 0;
	final String[] columns = { MediaStore.Images.Thumbnails.DATA,
			MediaStore.Images.Thumbnails.IMAGE_ID };

	// sorting parameter
	final String orderBy = MediaStore.Images.Thumbnails.IMAGE_ID;

	DisplayImageOptions options;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallBack = (FragmentCommunicationListener) activity;
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
			// getting cursor with urls, this is async and takes some time
			if (ScrapApp.GetGalleryImages() != null)
				ImagesLoadComplete(gallery);
			else
				getLoaderManager().initLoader(MEDIA_URL_LOADER, null, this);

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
		}
		return view;

	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		/*
		 * Takes action based on the ID of the Loader that's being created
		 */
		switch (loaderID) {
		case MEDIA_URL_LOADER:
			// Returns a new CursorLoader
			return new CursorLoader(getActivity(), // Parent activity context
					MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,// Table
																		// to
																		// query
					columns, // Projection to return
					null, // No selection clause
					null, // No selection arguments
					orderBy // Default sort order
			);
		default:
			// An invalid id was passed in
			return null;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		new GalleryLinksLoader().execute(cursor);
	}

	public void SetSelectedService(Bundle params) {
		int service = params.getInt("service");
		int previousService = params.getInt("previousService");
		if (service != previousService) {
			switch (service) {
			case gallery:
				// here we get last visible service and hide it
				LoginButton authButton = (LoginButton) getActivity()
						.findViewById(R.id.authButton);
				if (authButton.isShown()) {
					authButton.setVisibility(View.GONE);
				}
				if (ScrapApp.GetGalleryImages() != null)
					ImagesLoadComplete(gallery);
				else
					getLoaderManager().initLoader(MEDIA_URL_LOADER, null, this);
				currentService = gallery;
				break;
			case facebook:
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
					LoadFaceBookPhotos(thisSession);
				}
				currentService = facebook;
				break;
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
			LoadFaceBookPhotos(session);
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
	public void ImagesLoadComplete(int _service) {
		if (_service == currentService) {
			switch (_service) {
			case gallery: {
				updateUserInterface(gallery);
			}
			case facebook: {
				updateUserInterface(facebook);
			}
			}
		}
	}

	// method updates images gridview according to service selected
	// THIS METHOS SHOULD NOT BE CALLED DIRECTLY ONLY THROUGH ImagesLoadComplete
	private void updateUserInterface(int _service) {
		GridView gridImg = (GridView) getView().findViewById(
				R.id.gallery_grid_view);
		if (gridImg.getVisibility() == View.GONE) {
			gridImg.setVisibility(View.VISIBLE);
		}
		switch (_service) {
		case gallery: {
			gridImg.setAdapter(new PhotosAdapter(this.getActivity(), ScrapApp
					.GetGalleryImages()));
			break;
		}
		case facebook: {
			gridImg.setAdapter(new PhotosAdapter(this.getActivity(), ScrapApp
					.GetFaceBookImages()));
			break;
		}
		}
	}

	private void LoadFaceBookPhotos(Session session) {
		if (session.isOpened()) {
			if (ScrapApp.GetFaceBookImages() != null) {
				// calling ImagesLoadComplete we check if current service is
				// facebook and if we have to update UI
				ImagesLoadComplete(facebook);
			} else {
				new FaceBookImagesLoader().execute();
			}
		}
	}
}
