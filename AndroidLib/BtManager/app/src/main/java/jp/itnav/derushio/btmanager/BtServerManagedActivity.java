package jp.itnav.derushio.btmanager;

import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by derushio on 15/02/28.
 * Bluetooth通信のうち、SPPによるサーバー、
 * クライアント両方の機能をサポートするクラス。
 */
abstract public class BtServerManagedActivity extends BtManagedActivity {
	private String mBtServerName;
	protected BtServerManager mBtServerManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBtServerManager = new BtServerManager(this, 100);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mBtServerManager.stopBtServer();
	}

	@Override
	protected boolean isSocketExist() {
		return (super.isSocketExist() || mBtServerManager.isBtSocketExists());
	}

	protected boolean isServerStarted() {
		return mBtServerManager.isBtSocketExists();
	}

	@Override
	protected ArrayList<String> getMessageMailBox() {
		if (isServerStarted()) {
			return mBtServerManager.getMessageMailBox();
		} else {
			return mBtSppManager.getMessageMailBox();
		}
	}

	@Override
	protected void writeMessage(String message) {
		if (isServerStarted()) {
			mBtServerManager.writeMessage(message);
		} else {
			mBtSppManager.writeMessage(message);
		}
	}

	@Override
	public void onTick() {
		if (isSocketExist()) {
			if (isServerStarted()) {
				mBtServerManager.readMessage();
			} else {
				mBtSppManager.readMessage();
			}
		}
	}
}
