package el.solde.scrapbook.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class ServiceSelect extends Fragment implements OnClickListener {

	FragmentCommunicationListener mCallBack;

	public static ServiceSelect newInstance(String title) {

		ServiceSelect pageFragment = new ServiceSelect();
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		pageFragment.setArguments(bundle);
		return pageFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.service_select, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// LinearLayout servLt = (LinearLayout) getActivity().findViewById(
		// R.id.service_buttons_container);
		ImageButton gallery = (ImageButton) getActivity().findViewById(
				R.id.btn_gallery);
		gallery.setOnClickListener(this);
		ImageButton facebook = (ImageButton) getActivity().findViewById(
				R.id.btn_facebook);
		facebook.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final int gallery = 1;
		final int facebook = 2;
		Bundle par = new Bundle(); // bundle with parameters
		switch (v.getId()) {
		case R.id.btn_camera:
			break;
		case R.id.btn_gallery:
			par.putInt("service", gallery);
			mCallBack.ServiceSelected(par, PictureSelect.getInstance());
			mCallBack.StorePreviousService(PictureSelect.GetCurrentService());
			break;
		case R.id.btn_facebook:
			par.putInt("service", facebook);
			mCallBack.ServiceSelected(par, PictureSelect.getInstance());
			mCallBack.StorePreviousService(PictureSelect.GetCurrentService());
			break;
		}

	}

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

}
