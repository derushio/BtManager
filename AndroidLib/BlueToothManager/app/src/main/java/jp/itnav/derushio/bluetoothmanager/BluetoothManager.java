package jp.itnav.derushio.bluetoothmanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by derushio on 14/11/10.
 */
public class BluetoothManager {
	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private BluetoothAdapter bluetoothAdapter;
	private BluetoothSocket bluetoothSocket;
	private BluetoothDevice bluetoothDevice;

	private InputStream inputStream;
	private OutputStream outputStream;

	public void connectDevice(String paredDeviceName) {
		int lineIndex = paredDeviceName.indexOf("\n");
		String address = paredDeviceName.substring(lineIndex + 1, paredDeviceName.length());

		Log.d("target address", address);

		for (BluetoothDevice paredDevice : paredDevices) {
			Log.d("paredDevice address", paredDevice.getAddress());
			if (address.equals(paredDevice.getAddress())) {
				this.bluetoothDevice = paredDevice;

				Log.d("find", "device");

				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(SPP_UUID);
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (bluetoothAdapter.isDiscovering()) {
							bluetoothAdapter.cancelDiscovery();
						}

						try {
							bluetoothSocket.connect();
						} catch (IOException e1) {
							try {
								bluetoothSocket.close();
							} catch (IOException e2) {
								e2.printStackTrace();
							}
							return;
						}

						try {
							inputStream = bluetoothSocket.getInputStream();
							outputStream = bluetoothSocket.getOutputStream();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});

				thread.start();
				Log.d("ThreadStart", "ThreadStart");
				return;
			}
		}
	}

	public void writeMessage(final String message) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					outputStream.write(message.getBytes("UTF-8"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		thread.start();
	}

	public void disConnectDevices() {
		if (bluetoothSocket != null) {
			if (bluetoothSocket.isConnected()) {
				try {
					bluetoothSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static Set<BluetoothDevice> paredDevices;

	public ArrayList<String> getParedDeviceNames() {
		ArrayList<String> paredDeviceNames = new ArrayList<String>();
		for (BluetoothDevice paredDevice : paredDevices) {
			String paredDeviceName = paredDevice.getName() + "\n" + paredDevice.getAddress();
			paredDeviceNames.add(paredDeviceName);
		}
		return paredDeviceNames;
	}

	public BluetoothManager() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		paredDevices = bluetoothAdapter.getBondedDevices();
		getParedDeviceNames();
	}
}
