package el.solde.scrapbook.activity;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import el.solde.scrapbook.adapters.TabsAdapter;

public class Main extends FragmentActivity implements OnClickListener,
		IFragmentCommunicationListener {
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	Tab selectServiceTab;
	Tab selectPicturesTab;
	Tab combineScrapTab;
	Tab saveOrUploadTab;
	LinearLayout tabs_layout;
	// we store the previous service to avoid multiple loads of same data
	private int previousSelectedService;

	private static Main _instance;

	public static Main getInstance() {
		return _instance;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		_instance = this;
		mViewPager = (ViewPager) findViewById(R.id.pager);
		final ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.hide();

		tabs_layout = (LinearLayout) findViewById(R.id.tabs_layout);

		selectServiceTab = bar.newTab();
		selectPicturesTab = bar.newTab();
		combineScrapTab = bar.newTab();
		saveOrUploadTab = bar.newTab();

		mTabsAdapter = new TabsAdapter(this, mViewPager, tabs_layout);
		mTabsAdapter.addTab(selectServiceTab, ServiceSelect.class, null);
		mTabsAdapter.addTab(selectPicturesTab, PictureSelect.class, null);
		mTabsAdapter.addTab(combineScrapTab, CombineScrap.class, null);
		mTabsAdapter.addTab(saveOrUploadTab, SaveOrUpload.class, null);

		if (savedInstanceState != null) {
			bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
		}

		Button select_service_tab = (Button) findViewById(R.id.btn_select_service);
		Button picture_select_tab = (Button) findViewById(R.id.btn_picture_select);
		Button combine_image_tab = (Button) findViewById(R.id.btn_combine_image);
		Button save_or_upload_tab = (Button) findViewById(R.id.btn_save_or_upload);

		select_service_tab.setOnClickListener(this);
		picture_select_tab.setOnClickListener(this);
		combine_image_tab.setOnClickListener(this);
		save_or_upload_tab.setOnClickListener(this);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
	}

	@Override
	public void onClick(View v) {
		// switch to necessary screen when tab is clicked
		switch (v.getId()) {
		case R.id.btn_select_service:
			selectServiceTab.select();
			tabs_layout.setBackgroundResource(R.drawable.tab1_bar);
			StorePreviousService(PictureSelect.GetCurrentService());
			break;
		case R.id.btn_picture_select:
			selectPicturesTab.select();
			tabs_layout.setBackgroundResource(R.drawable.tab2_bar);
			StorePreviousService(PictureSelect.GetCurrentService());
			break;
		case R.id.btn_combine_image:
			combineScrapTab.select();
			tabs_layout.setBackgroundResource(R.drawable.tab3_bar);
			StorePreviousService(PictureSelect.GetCurrentService());
			break;
		case R.id.btn_save_or_upload:
			saveOrUploadTab.select();
			tabs_layout.setBackgroundResource(R.drawable.tab4_bar);
			StorePreviousService(PictureSelect.GetCurrentService());
			break;
		}

	}

	@Override
	public void ServiceSelected(Bundle _params, Object receiverFragment) {

		Object receivedFragment = receiverFragment;
		Bundle params = (Bundle) _params;
		// add a previously selected service to bundle
		params.putInt("previousService", previousSelectedService);
		if (receiverFragment != null) {
			if (receivedFragment.getClass().equals(PictureSelect.class)) {
				PictureSelect picSelectFragment = PictureSelect.getInstance();
				if (picSelectFragment != null) {
					picSelectFragment.SetSelectedService(params);
					selectPicturesTab.select();
					tabs_layout.setBackgroundResource(R.drawable.tab2_bar);
				}
			}

		}
	}

	// save the previously selected service number
	public void StorePreviousService(int serviceId) {
		previousSelectedService = serviceId;
	}
}