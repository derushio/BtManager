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
	private TimerHandler timerHandler;

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
	protected void onStop() {
		super.onStop();
		timerHandler = null;
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

	protected void readMessageStart(long delayMilliSec) {

		timerHandler = new TimerHandler();
		timerHandler.sleep(delayMilliSec);

	}

	public class TimerHandler extends Handler {
		private long delayMilliSec;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					final String message = bluetoothManager.readMessage();

					mainLooperHandler.post(new Runnable() {
						@Override
						public void run() {
							onReadMessageFinished(message);
						}
					});
				}
			});

			thread.start();

			if (timerHandler != null) {
				sleep(delayMilliSec);
			}
		}

		public void sleep(long delayMilliSec) {
			this.delayMilliSec = delayMilliSec;
			removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMilliSec);
		}
	}

	abstract protected void onReadMessageFinished(String message);

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
