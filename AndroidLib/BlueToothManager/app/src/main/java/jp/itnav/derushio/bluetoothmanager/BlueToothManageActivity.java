package jp.itnav.derushio.bluetoothmanager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by derushio on 14/11/10.
 */
public class BlueToothManageActivity extends Activity {
	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private static BluetoothAdapter bluetoothAdapter;
	private static BluetoothSocket bluetoothSocket;
	private static BluetoothDevice bluetoothDevice;

	private static InputStream inputStream;
	private static OutputStream outputStream;

	protected void connectDevice(String paredDeviceName) {
		int lineIndex = paredDeviceName.indexOf("\n");
		String address = paredDeviceName.substring(lineIndex, paredDeviceName.length());

		for (BluetoothDevice paredDevice : paredDevices) {
			if (address.equals(paredDevice.getAddress())) {
				this.bluetoothDevice = bluetoothDevice;

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
			}
		}
	}

	protected void sendMessage(final String message) {
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

	protected void disConnectDevices() {
		try {
			bluetoothSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Set<BluetoothDevice> paredDevices;

	protected ArrayList<String> getParedDevicesList() {
		ArrayList<String> paredDeviceNames = new ArrayList<String>();
		for (BluetoothDevice paredDevice : paredDevices) {
			String paredDeviceName = paredDevice.getName() + "\n" + paredDevice.getAddress();
			paredDeviceNames.add(paredDeviceName);
		}
		return paredDeviceNames;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		paredDevices = bluetoothAdapter.getBondedDevices();
		getParedDevicesList();
	}
}
