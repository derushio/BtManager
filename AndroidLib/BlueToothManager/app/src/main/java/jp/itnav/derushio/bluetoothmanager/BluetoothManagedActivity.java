package jp.itnav.derushio.bluetoothmanager;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by derushio on 14/11/10.
 */
abstract public class BluetoothManagedActivity extends Activity {

	private BluetoothManager bluetoothManager;
	// Bluetoothを管理する自作クラス

	private BluetoothDevice targetDevice;
	// ターゲットされているデバイスの情報

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
		if (targetDevice != null) {
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

		disConnectDevices();
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
		this.targetDevice = targetDevice;
	}
	// ターゲットにするデバイスをセット

	protected BluetoothDevice getTargetDevice() {
		return targetDevice;
	}
	// ターゲットされているデバイス情報を取得

	protected boolean isDeviceConnected() {
		return bluetoothManager.isDeviceConnected();
	}
	// デバイスに接続しているかを取得

	protected void connectDevice() {
		bluetoothManager.connectDevice(targetDevice.getAddress());
	}
	// デバイスに接続

	protected void disConnectDevices() {
		bluetoothManager.disConnectDevices();
	}
	// デバイスから切断

	protected void readMessageStart(long delayMilliSec) {
		timerHandler.timerStart(delayMilliSec);
		isReadMessageStarted = true;
	}
	// タイマーをスタートし、一定時間ごとにメッセージを受信する


	protected void readMessageStop() {
		timerHandler.timerStop();
		isReadMessageStarted = false;
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

	public void setOnConnect(Handler onConnect) {
		bluetoothManager.setOnConnect(onConnect);
	}
	// 接続時のハンドラを設定

	public void setOnDisConnect(Handler onDisConnect) {
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

			if (isTick == true) {
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
