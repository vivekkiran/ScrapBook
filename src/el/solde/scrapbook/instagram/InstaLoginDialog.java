package el.solde.scrapbook.instagram;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;
import el.solde.scrapbook.activity.R;
import el.solde.scrapbook.activity.ScrapApp;

public class InstaLoginDialog extends DialogFragment {

	private WebView webView;
	static final int margin = 4;
	static final int padding = 2;
	private FragmentActivity context;
	private String request_token;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String client_id = getString(R.string.insta_client_id);
		final String insta_uri = getString(R.string.insta_url);
		String url = "https://instagram.com/oauth/authorize/?client_id="
				+ client_id + "&redirect_uri=" + insta_uri
				+ "&response_type=token;";
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.insta_login_dialog, null);
		webView = (WebView) dialogView.findViewById(R.id.loginWebView);
		EditText edit = (EditText) dialogView.findViewById(R.id.edit);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith(insta_uri)) {
					String parts[] = url.split("=");
					// This is our request token
					ScrapApp.SavePreference("insta_token", parts[1]);
					CloseDialog();
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// Users will be notified in case there's an error (i.e. no
				// internet connection)
				Toast.makeText(context,
						"some crap has happened =(" + description,
						Toast.LENGTH_SHORT).show();
			}
		});
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSaveFormData(false);
		webView.requestFocus(View.FOCUS_DOWN);
		edit.setFocusable(true);
		edit.requestFocus();
		webView.loadUrl(url);
		builder.setView(dialogView);
		return builder.create();

	}

	public void CloseDialog() {
		this.dismiss();
	}
}
