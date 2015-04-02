package jp.itnav.derushio.btmanager;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Set;

import jp.itnav.derushio.btmanager.timer.TimerHandler;

/**
 * Created by derushio on 14/11/10.
 * BluetoothSPPを利用するときに使う機能を補完する抽象メソッド
 * 継承して使ってください
 */
abstract public class BtManagedActivity extends Activity {

	protected BtSppManager mBtSppManager;
	// Bluetoothを管理する自作クラス

	private TimerHandler mReadMessageTimer;
	// タイマー

	private boolean mMessageReadFlag = false;
	// メッセージ読み込みがスタートしたか

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBtSppManager = new BtSppManager(this, 100);
		mReadMessageTimer = new TimerHandler();
		mReadMessageTimer.setOnTickListener(new TimerHandler.OnTickListener() {
			@Override
			public void onTick() {
				if (isSocketExist()) {
					mBtSppManager.readMessage();
				}
			}
		});
		// 初期化
	}
	// Activityが生成されるときに呼ばれるメソッド（一回のみ）

	@Override
	protected void onResume() {
		super.onResume();

		if (mMessageReadFlag == true) {
			mReadMessageTimer.timerRestart();
			// タイマーをスタートする
		}

		if (isDeviceConnected()) {
			connectDevice();
			// デバイスに繋ぎ直す
		}

	}
	// Activityが復活するときに呼ばれるメソッド（最初にも呼ばれる）

	@Override
	protected void onPause() {
		super.onPause();

		if (mMessageReadFlag == true) {
			mReadMessageTimer.timerStop();
			//タイマーをストップする
		}

		if (isDeviceConnected()) {
			disConnectDevice();
			// デバイスを切断する
		}

	}
	// Activityが一時停止するときに呼ばれるメソッド

	protected Set<BluetoothDevice> getParedDevices() {
		return mBtSppManager.getParedDevices();
	}
	// ペアリングされているデバイスリストを取得

	protected void setTargetDevice(BluetoothDevice targetDevice) {
		mBtSppManager.setTargetDevice(targetDevice);
	}
	// ターゲットにするデバイスをセット

	protected BluetoothDevice getTargetDevice() {
		return mBtSppManager.getTargetDevice();
	}
	// ターゲットされているデバイス情報を取得

	protected boolean isTargetDeviceExists() {
		return mBtSppManager.isTargetDeviceExist();
	}
	// デバイスをターゲットしたか取得

	protected boolean isSocketExist() {
		return mBtSppManager.isBtSocketExists();
	}
	// デバイスとのソケットの準備ができているか確認

	protected boolean isDeviceConnected() {
		return mBtSppManager.isDeviceConnected();
	}
	// デバイスに接続しているかを取得

	protected void showDeviceSelectDialog() {
		final Dialog dialog = new Dialog(this);
		// ダイアログを新規作成(別クラス内からアクセスするためfinal)
		dialog.setTitle("デバイス選択");

		LinearLayout paredDevicesHolder = new LinearLayout(this);
		// 並べるレイアウトを新規作成
		paredDevicesHolder.setOrientation(LinearLayout.VERTICAL);
		// 縦並べに設定
		paredDevicesHolder.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		// レイアウト時の設定をセット

		Set<BluetoothDevice> paredDevices = getParedDevices();
		for (final BluetoothDevice paredDevice : paredDevices) {
			Button button = new Button(this);
			// ボタンを新規作成
			button.setText(paredDevice.getName());
			// デバイス名をボタンにセット
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setTargetDevice(paredDevice);
					connectDevice();
					dialog.dismiss();
				}
			});
			// クリックリスナ(クリックされた時の動作)をセット
			button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			button.setTag(paredDevice);
			// タグとして、BluetoothDeviceをセット

			paredDevicesHolder.addView(button);
			// LinearLayoutにセット
		}
		// ペアされているデバイスを立て並べのボタンにして選択できるようにする

		ScrollView scrollView = new ScrollView(this);
		scrollView.addView(paredDevicesHolder);
		// 縦並びのレイアウトがスクロールできるようにする
		dialog.addContentView(scrollView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		// ダイアログに並べられたボタンをセット

		dialog.show();
		// ダイアログを表示
	}
	// デバイスを選択するダイアログを表示

	protected void connectDevice() {
		mBtSppManager.connectDevice();
	}
	// デバイスに接続

	protected void reConnectDevice() {
		mBtSppManager.reConnectDevice();
	}
	// デバイスに再接続

	protected void disConnectDevice() {
		mBtSppManager.disConnectDevice();
	}
	// デバイスから切断

	protected void readMessageStart(long delayMilliSec) {
		mReadMessageTimer.timerStart(delayMilliSec);
		// タイマーを待ち時間設定情報を付与してスタート
		mMessageReadFlag = true;
		// メッセージリードをスタートしたことを記録する
	}
	// タイマーをスタートし、一定時間ごとにメッセージを受信する

	protected void readMessageStop() {
		mReadMessageTimer.timerStop();
		// タイマーを停止する
		mMessageReadFlag = false;
		// タイマー停止したことを記録する
	}
	// タイマーをストップし、メッセージ受信を停止する

	protected void writeMessage(String message) {
		mBtSppManager.writeMessage(message);
	}
	// メッセージを送信する

	protected ArrayList<String> getMessageMailBox() {
		return mBtSppManager.getMessageMailBox();
	}
	// メッセージを受信しているメールボックスを取得する

	protected void clearMessageMailBox() {
		mBtSppManager.clearMessageMailBox();
	}
	// メールボックスを初期化する

	protected void setShowStatusToast(boolean enable) {
		mBtSppManager.setShowStatusToast(enable);
	}

	protected void setOnConnectAction(final Handler.Callback onConnect) {
		mBtSppManager.setOnConnectAction(onConnect);
	}

	protected void setOnDisConnectAction(final Handler.Callback onDisconnect) {
		Handler.Callback callback = new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				readMessageStop();
				onDisconnect.handleMessage(msg);
				return false;
			}
		};
		mBtSppManager.setOnDisconnectAction(callback);
	}
}