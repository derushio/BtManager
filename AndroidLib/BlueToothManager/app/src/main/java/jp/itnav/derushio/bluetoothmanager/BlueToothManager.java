package jp.itnav.derushio.bluetoothmanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.Set;
import java.util.UUID;

/**
 * Created by derushio on 14/11/10.
 */
public class BlueToothManager {
	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private String message = "";

	private BluetoothAdapter bluetoothAdapter;
	private BluetoothSocket bluetoothSocket;
	private BluetoothDevice bluetoothDevice;

	private Set<BluetoothDevice> paredDevices;

	public BlueToothManager() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		paredDevices = bluetoothAdapter.getBondedDevices();
	}
}
