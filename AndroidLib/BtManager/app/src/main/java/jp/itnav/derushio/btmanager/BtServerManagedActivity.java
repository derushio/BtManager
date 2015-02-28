package jp.itnav.derushio.btmanager;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by derushio on 15/02/28.
 */
abstract public class BtServerManagedActivity extends Activity {
	private String mBtServerName;
	protected BtServerManager btServerManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		btServerManager = new BtServerManager(this, 100);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (btServerManager.isBtSocketExists()) {
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		btServerManager.stopBtServer();
	}
}
