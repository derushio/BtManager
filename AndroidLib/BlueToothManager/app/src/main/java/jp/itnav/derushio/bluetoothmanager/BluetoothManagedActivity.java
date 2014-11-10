package jp.itnav.derushio.bluetoothmanager;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by derushio on 14/11/10.
 */
public class BluetoothManagedActivity extends Activity {

	private BluetoothManager bluetoothManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bluetoothManager = new BluetoothManager();
	}

	protected ArrayList<String> getParedDeviceNames() {
		return bluetoothManager.getParedDeviceNames();
	}

	protected void connectDevice(String paredDeviceName) {
		bluetoothManager.connectDevice(paredDeviceName);
	}

	protected void writeMessage(String message) {
		bluetoothManager.writeMessage(message);
	}

	protected void disConnectDevices() {
		bluetoothManager.disConnectDevices();
	}
}
