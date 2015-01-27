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

	private Handler mainLooperHandler;
	protected SensorTimerHandler sensorTimerHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bluetoothManager = new BluetoothManager(this);
		mainLooperHandler = new Handler();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (targetDeviceName != null) {
			if (!targetDeviceName.equals("")) {
				connectDevice();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		sensorTimerHandler = null;
		disConnectDevices();
	}

	protected void setTargetDeviceName(String targetDeviceName) {
		this.targetDeviceName = targetDeviceName;
	}

	protected String getTargetDeviceName() {
		return targetDeviceName;
	}

	protected void connectDevice() {
		bluetoothManager.connectDevice(targetDeviceName);
	}

	protected void writeMessage(String message) {
		bluetoothManager.writeMessage(message);
	}

	protected ArrayList<String> readMessage() {
		return bluetoothManager.getMessageMailBox();
	}

	protected void readMessageStart(long delayMilliSec) {

		sensorTimerHandler = new SensorTimerHandler();
		sensorTimerHandler.sleep(delayMilliSec);

	}

	public class SensorTimerHandler extends Handler {
		private long delayMilliSec;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (sensorTimerHandler != null) {
				sleep(delayMilliSec);
			}

			bluetoothManager.readMessage();
		}

		public void sleep(long delayMilliSec) {
			this.delayMilliSec = delayMilliSec;
			removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMilliSec);
		}
	}

	protected void disConnectDevices() {
		bluetoothManager.disConnectDevices();
	}

	protected Set<BluetoothDevice> getParedDevices() {
		return bluetoothManager.getParedDevices();
	}

	protected ArrayList<String> getParedDeviceNames() {
		return bluetoothManager.getParedDeviceNames();
	}

}
