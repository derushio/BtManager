package jp.itnav.derushio.btmanager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

import jp.itnav.derushio.btmanager.timer.TimerHandler;

/**
 * Created by derushio on 15/02/28.
 * Bluetooth通信のうち、SPPによるサーバー、
 * クライアント両方の機能をサポートするクラス。
 */
abstract public class BtServerManagedActivity extends BtManagedActivity {
	private String mBtServerName;
	protected BtServerManager mBtServerManager;

	private TimerHandler mReadServerMessageTimer;
	// タイマー

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBtServerManager = new BtServerManager(this, 100);
		mReadServerMessageTimer = new TimerHandler();
		mReadServerMessageTimer.setOnTickListener(new TimerHandler.OnTickListener() {
			@Override
			public void onTick() {
				if (isServerSocketExist()) {
					mBtServerManager.readMessage();
				}
			}
		});
		// 初期化
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		readServerMessageStop();
		mBtServerManager.stopBtServer();
	}

	protected boolean isServerSocketExist() {
		return mBtServerManager.isBtSocketExists();
	}

	public void startBtServer(final String btServerName) {
		mBtServerManager.startBtServer(btServerName);
	}
	// Bluetooth通信をホストする

	public void stopBtServer() {
		mBtServerManager.stopBtServer();
	}
	// Bluetooth通信ホストを停止する

	public void restartBtServer() {
		mBtServerManager.restartBtServer();
	}
	// Bluetooth通信ホストを再スタートする

	protected ArrayList<String> getServerMessageMailBox() {
		return mBtServerManager.getMessageMailBox();
	}

	protected void readServerMessageStart(long delayMilliSec) {
		mReadServerMessageTimer.timerStart(delayMilliSec);
		// タイマーを待ち時間設定情報を付与してスタート
	}
	// タイマーをスタートし、一定時間ごとにメッセージを受信する

	protected void readServerMessageStop() {
		mReadServerMessageTimer.timerStop();
		// タイマーを停止する
	}
	// タイマーをストップし、メッセージ受信を停止する

	protected void writeServerMessage(String message) {
		mBtServerManager.writeMessage(message);
	}

	protected void setServerShowStatusToast(boolean enable) {
		mBtServerManager.setShowStatusToast(enable);
	}

	protected void setOnServerConnectAction(final Handler.Callback onConnect) {
		mBtServerManager.setOnConnectAction(onConnect);
	}

	protected void setOnServerDisConnectAction(final Handler.Callback onDisconnect) {
		Handler.Callback callback = new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				readServerMessageStop();
				onDisconnect.handleMessage(msg);
				return false;
			}
		};
		mBtServerManager.setOnDisconnectAction(callback);
	}
}
