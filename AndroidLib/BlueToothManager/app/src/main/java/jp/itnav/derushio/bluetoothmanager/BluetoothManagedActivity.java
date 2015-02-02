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
	private String targetDeviceName;
	private String targetDeviceAddress;

	private TimerHandler timerHandler;

	private boolean isReadStarted = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bluetoothManager = new BluetoothManager(this);
		timerHandler = new TimerHandler();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (targetDeviceName != null) {
			if (!targetDeviceName.equals("")) {
				connectDevice();
			}
		}

		if (isReadStarted == true) {
			timerHandler.timerStart();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		disConnectDevices();

		if (isReadStarted == true) {
			timerHandler.timerStop();
		}
	}

	protected Set<BluetoothDevice> getParedDevices() {
		return bluetoothManager.getParedDevices();
	}

	protected void setTargetDeviceName(String targetDeviceName, String targetDeviceAddress) {
		this.targetDeviceName = targetDeviceName;
		this.targetDeviceAddress = targetDeviceAddress;
	}

	protected String getTargetDeviceName() {
		return targetDeviceName;
	}

	protected boolean isConnectDevice() {
		return bluetoothManager.isConnectDevice();
	}

	protected void connectDevice() {
		bluetoothManager.connectDevice(targetDeviceAddress);
	}

	protected void disConnectDevices() {
		bluetoothManager.disConnectDevices();
	}

	protected void readMessageStart(long delayMilliSec) {
		timerHandler.timerStart(delayMilliSec);
		isReadStarted = true;
	}

	protected void readMessageStop() {
		timerHandler.timerStop();
		isReadStarted = false;
	}

	protected void writeMessage(String message) {
		bluetoothManager.writeMessage(message);
	}

	protected ArrayList<String> readMessage() {
		return bluetoothManager.getMessageMailBox();
	}

	private class TimerHandler extends Handler {
		// タイマーを定義するclass
		private boolean isTick = false;
		// タイマーが動いているか
		private long delayMilliSec = 1000;
		// 待ち時間の長さ

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (isConnectDevice()) {
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
}
