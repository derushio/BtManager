package jp.itnav.derushio.bluetoothmanager;

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

/**
 * Created by derushio on 14/11/10.
 */
abstract public class BluetoothManagedActivity extends Activity {
	/**
	 * Bluetoothを利用するときに使う機能を補完する抽象メソッド
	 * 継承して使ってください
	 */

	private BluetoothManager bluetoothManager;
	// Bluetoothを管理する自作クラス

	private TimerHandler timerHandler;
	// タイマー

	private boolean isReadMessageStarted = false;
	// メッセージ読み込みがスタートしたか

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bluetoothManager = new BluetoothManager(this);
		timerHandler = new TimerHandler();
		// 初期化
	}
	// Activityが生成されるときに呼ばれるメソッド（一回のみ）

	@Override
	protected void onResume() {
		super.onResume();
		if (isDeviceConnected()) {
			connectDevice();
			// デバイスに繋ぎ直す
		}

		if (isReadMessageStarted == true) {
			timerHandler.timerStart();
			// タイマーをスタートする
		}
	}
	// Activityが復活するときに呼ばれるメソッド（最初にも呼ばれる）

	@Override
	protected void onPause() {
		super.onPause();

		disConnectDevice();
		// デバイスを切断する

		if (isReadMessageStarted == true) {
			timerHandler.timerStop();
			//タイマーをストップする
		}
	}
	// Activityが一時停止するときに呼ばれるメソッド

	protected Set<BluetoothDevice> getParedDevices() {
		return bluetoothManager.getParedDevices();
	}
	// ペアリングされているデバイスリストを取得

	protected void setTargetDevice(BluetoothDevice targetDevice) {
		bluetoothManager.setTargetDevice(targetDevice);
	}
	// ターゲットにするデバイスをセット

	protected BluetoothDevice getTargetDevice() {
		return bluetoothManager.getTargetDevice();
	}
	// ターゲットされているデバイス情報を取得

	protected boolean isDeviceConnected() {
		return bluetoothManager.isDeviceConnected();
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
		bluetoothManager.connectDevice();
	}
	// デバイスに接続

	protected void reConnectDevice() {
		bluetoothManager.reConnectDevice();
	}
	// デバイスに再接続

	protected void disConnectDevice() {
		bluetoothManager.disConnectDevice();
	}
	// デバイスから切断

	protected void readMessageStart(long delayMilliSec) {
		timerHandler.timerStart(delayMilliSec);
		// タイマーを待ち時間設定情報を付与してスタート
		isReadMessageStarted = true;
		// メッセージリードをスタートしたことを記録する
	}
	// タイマーをスタートし、一定時間ごとにメッセージを受信する

	protected void readMessageStop() {
		timerHandler.timerStop();
		// タイマーを停止する
		isReadMessageStarted = false;
		// タイマー停止したことを記録する
	}
	// タイマーをストップし、メッセージ受信を停止する

	protected void writeMessage(String message) {
		bluetoothManager.writeMessage(message);
	}
	// メッセージを送信する

	protected ArrayList<String> readMessage() {
		return bluetoothManager.getMessageMailBox();
	}
	// メッセージを受信しているメールボックスを取得する

	protected void setOnConnect(Handler onConnect) {
		bluetoothManager.setOnConnect(onConnect);
	}
	// 接続時のハンドラを設定

	protected void setOnDisConnect(Handler onDisConnect) {
		bluetoothManager.setOnDisConnect(onDisConnect);
	}
	// 切断時のハンドラを設定

	private class TimerHandler extends Handler {
		// タイマーを定義するclass
		private boolean isTick = false;
		// タイマーが動いているか
		private long delayMilliSec = 1000;
		// 待ち時間の長さ

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (isDeviceConnected()) {
				bluetoothManager.readMessage();
				// メッセージを受信する。
			}

			if (isTick) {
				sleep();
			}
		}

		public void timerStart(long delayMilliSec) {
			this.delayMilliSec = delayMilliSec;
			isTick = true;
			sleep();
		}

		public void timerStart() {
			timerStart(delayMilliSec);
		}
		// リスタート用のOverLoad

		public void timerStop() {
			isTick = false;
		}

		private void sleep() {
			removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMilliSec);
		}
	}
	// タイマー定義
}