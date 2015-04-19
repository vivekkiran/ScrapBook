package el.solde.scrapbook.activity;

import android.os.Bundle;

public interface IFragmentCommunicationListener {

	public void ServiceSelected(Bundle params, Object receiverFragment);

	public void StorePreviousService(int serviceId);

}
