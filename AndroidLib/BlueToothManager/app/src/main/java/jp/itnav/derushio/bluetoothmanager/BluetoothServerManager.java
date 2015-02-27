package jp.itnav.derushio.bluetoothmanager;

import android.bluetooth.BluetoothServerSocket;
import android.content.Context;

import java.io.IOException;

/**
 * Created by derushio on 15/02/27.
 */
public class BluetoothServerManager extends BluetoothManagerBase {
	protected BluetoothServerSocket mBluetoothServerSocket;

	public BluetoothServerManager(Context context, int messageMailBoxLength) {
		super(context, messageMailBoxLength);
	}

	public void startBluetoothServer(String name) {
		try {
			mBluetoothServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(name, SPP_UUID);

			Thread acceptThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						mBluetoothSocket = mBluetoothServerSocket.accept();

						mBtInputStream = mBluetoothSocket.getInputStream();
						mBtOutputStream = mBluetoothSocket.getOutputStream();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});

			acceptThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Bluetooth接続をホストする

	public void stopBluetoothServer() {
		Thread stopThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mBluetoothServerSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		stopThread.start();
	}
	// Bluetooth接続ホストを停止する
}
