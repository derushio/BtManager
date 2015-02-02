package jp.itnav.derushio.bluetoothmanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by derushio on 14/11/10.
 */
public class BluetoothManager {
	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// SPP通信のUUID

	private Context context;

	private BluetoothAdapter bluetoothAdapter;
	private BluetoothSocket bluetoothSocket;
	private BluetoothDevice bluetoothDevice;
	private Set<BluetoothDevice> paredDevices;
	// Bluetooth制御用クラス群

	private InputStream inputStream;
	private OutputStream outputStream;
	// 入出力制御

	private Handler onConnect;
	private Handler onDisConnect;
	// 接続時、切断時のHandler

	private ArrayList<String> messageMailBox;
	// Bluetooth通信の受信用メールボックス

	public BluetoothManager(Context context) {
		this.context = context;
		messageMailBox = new ArrayList<String>(100);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		paredDevices = getParedDevices();

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
					case -3:
						Toast.makeText(BluetoothManager.this.context, "NO SUCH DEVICE", Toast.LENGTH_SHORT).show();
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

	public Set<BluetoothDevice> getParedDevices() {
		paredDevices = bluetoothAdapter.getBondedDevices();
		return paredDevices;
	}

	public boolean isConnectDevice() {
		if (bluetoothSocket != null) {
			return bluetoothSocket.isConnected();
		}
		return false;
	}

	public void connectDevice(String paredDeviceAddress) {
		Log.d("target address", paredDeviceAddress);

		for (BluetoothDevice paredDevice : paredDevices) {
			Log.d("paredDevice address", paredDevice.getAddress());
			if (paredDeviceAddress.equals(paredDevice.getAddress())) {
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
		Message message = new Message();
		message.what = -3;
		onConnect.sendMessage(message);
	}

	public void reconnectedDevice() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (bluetoothSocket != null) {
					if (bluetoothSocket.isConnected()) {

					}
				}
			}
		});
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

	public ArrayList<String> getMessageMailBox() {
		return messageMailBox;
	}

	public void writeMessage(final String message) {
		try {
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
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void readMessage() {
		if (bluetoothSocket != null) {
			if (bluetoothSocket.isConnected()) {
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							InputStreamReader reader = new InputStreamReader(inputStream);
							BufferedReader bufferedReader = new BufferedReader(reader);

							String message = bufferedReader.readLine();
							messageMailBox.add(0, message);

						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				});

				thread.start();
			}
		}
	}

	public void setOnConnect(Handler onConnect) {
		this.onConnect = onConnect;
	}

	public void setOnDisConnect(Handler onDisConnect) {
		this.onDisConnect = onDisConnect;
	}
}