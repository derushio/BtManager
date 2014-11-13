package jp.itnav.derushio.bluetoothmanager;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by derushio on 14/11/10.
 */
public class BluetoothManagedActivity extends Activity {

	private BluetoothManager bluetoothManager;
	private String targetDeviceName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bluetoothManager = new BluetoothManager(this);
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
