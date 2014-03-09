package el.solde.scrapbook.picasa;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import el.solde.scrapbook.activity.R;
import el.solde.scrapbook.activity.ScrapApp;
import el.solde.scrapbook.loaders.PicasaImagesLoader;

public class PicasaLoginDialog extends DialogFragment {
	ProgressBar spinner;
	CheckBox rememberMe;

	public PicasaLoginDialog() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// get url to image
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.picasa_login_dialog, null);
		spinner = (ProgressBar) dialogView.findViewById(R.id.loading);
		ImageButton logBtn = (ImageButton) dialogView
				.findViewById(R.id.loginButton);
		final EditText email = (EditText) dialogView.findViewById(R.id.loginTB);
		final EditText password = (EditText) dialogView
				.findViewById(R.id.passwordTB);
		rememberMe = (CheckBox) dialogView.findViewById(R.id.rememberMe);
		// check if we have saved Credentials, if have just call GetUserMedia
		// with saved credentials
		// if we don't have - show login dialog
		if (ScrapApp.GetPreference().getString("login", "") != "") {
			logBtn.setVisibility(View.GONE);
			email.setVisibility(View.GONE);
			password.setVisibility(View.GONE);
			rememberMe.setVisibility(View.GONE);
			AlbumFeed feed = GetUserMedia(
					ScrapApp.GetPreference().getString("login", ""), ScrapApp
							.GetPreference().getString("password", ""));
			if (feed != null) {
				PicasaImagesLoader loader = new PicasaImagesLoader(feed);
				loader.execute();
			}
			CloseDialog();
		} else {
			email.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					EditText e1 = (EditText) v;
					if (e1.getText().toString().equalsIgnoreCase("Email")) {
						e1.setText("");
					}
				}
			});

			password.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					EditText e1 = (EditText) v;
					if (e1.getText().toString().equalsIgnoreCase("Password")) {
						e1.setTransformationMethod(PasswordTransformationMethod
								.getInstance());// switch to password view
						e1.setText("");
					}
				}
			});

			logBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlbumFeed feed = GetUserMedia(email.getText().toString(),
							password.getText().toString());
					if (feed != null) {
						PicasaImagesLoader loader = new PicasaImagesLoader(feed);
						loader.execute();
					}
					CloseDialog();
				}
			});
		}
		builder.setView(dialogView);
		return builder.create();
	}

	// performs login and returns AlbumFeed
	public AlbumFeed GetUserMedia(String _login, String _pass) {
		String login = _login;
		String password = _pass;
		AlbumFeed feed = null;
		PicasawebService picasa = new PicasawebService("el-solde-scrapbook");
		try {
			spinner.setVisibility(View.VISIBLE);
			picasa.setUserCredentials(login, password);
			if (rememberMe.isChecked()) {
				ScrapApp.SavePreference("login", login);
				ScrapApp.SavePreference("password", password);
			}
			URL feedUrl = new URL(
					"https://picasaweb.google.com/data/feed/api/user/default?kind=photo&thumbsize=144&imgmax=1024");
			feed = picasa.getFeed(feedUrl, AlbumFeed.class);
			spinner.setVisibility(View.GONE);
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return feed;
	}

	public void CloseDialog() {
		this.dismiss();
	}
}
