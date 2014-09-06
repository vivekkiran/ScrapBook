package el.solde.scrapbook.picasa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
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
				ScrapApp.SavePreference("login", email.getText().toString());
				ScrapApp.SavePreference("password", password.getText()
						.toString());
				if (PicasaImagesLoader.GetInstance().getStatus() != AsyncTask.Status.RUNNING)
					PicasaImagesLoader.GetInstance().execute();
				CloseDialog();
			}
		});
		builder.setView(dialogView);
		return builder.create();
	}

	public void CloseDialog() {
		this.dismiss();
	}
}
