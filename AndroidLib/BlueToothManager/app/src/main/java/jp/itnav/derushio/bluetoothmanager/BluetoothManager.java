package jp.itnav.derushio.bluetoothmanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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

	private Context context;

	private BluetoothAdapter bluetoothAdapter;
	private BluetoothSocket bluetoothSocket;
	private BluetoothDevice bluetoothDevice;

	private InputStream inputStream;
	private OutputStream outputStream;

	private Handler onConnect;
	private Handler onDisConnect;

	public void setOnConnect(Handler onConnect) {
		this.onConnect = onConnect;
	}

	public void setOnDisConnect(Handler onDisConnect) {
		this.onDisConnect = onDisConnect;
	}

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

						Message message = new Message();

						try {
							bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(SPP_UUID);
						} catch (IOException e) {
							message.what = -1;
							onConnect.sendMessage(message);
							e.printStackTrace();
						}

						if (bluetoothAdapter.isDiscovering()) {
							bluetoothAdapter.cancelDiscovery();
						}

						try {
							bluetoothSocket.connect();
							message.what = 0;
							onConnect.sendMessage(message);
						} catch (IOException e1) {
							try {
								bluetoothSocket.close();
							} catch (IOException e2) {
								e2.printStackTrace();
							}
							message.what = -2;
							onConnect.sendMessage(message);
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
		if (bluetoothSocket.isConnected()) {
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
	}

	public void disConnectDevices() {
		if (bluetoothSocket != null) {
			if (bluetoothSocket.isConnected()) {
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						Message message = new Message();
						try {
							bluetoothSocket.close();
							message.what = 0;
							onDisConnect.sendMessage(message);
						} catch (IOException e) {
							message.what = -1;
							onDisConnect.sendMessage(message);
							e.printStackTrace();
						}

					}
				});

				thread.start();
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

	public Set<BluetoothDevice> getParedDevices() {
		return bluetoothAdapter.getBondedDevices();
	}

	public BluetoothManager(Context context) {
		this.context = context;
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		paredDevices = bluetoothAdapter.getBondedDevices();
		getParedDeviceNames();

		onConnect = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {

				switch (msg.what) {
					case 0:
						Toast.makeText(BluetoothManager.this.context, "CONNECT" + " " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
						break;
					case -1:
						Toast.makeText(BluetoothManager.this.context, "NOT FOUND SOCKET", Toast.LENGTH_SHORT).show();
						break;
					case -2:
						Toast.makeText(BluetoothManager.this.context, "NOT FOUND" + " " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
						break;
					default:
						break;
				}
				return false;
			}
		});

		onDisConnect = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
					case 0:
						Toast.makeText(BluetoothManager.this.context, "DISCONNECT" + " " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
						break;
					case -1:
						Toast.makeText(BluetoothManager.this.context, "NOT FOUND" + " " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
						break;
					default:
						break;
				}
				return false;
			}
		});
	}
}
