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
	// Activity情報

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
		// メールボックス容量を100件に
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// デバイスのデフォルト設定のBluetoothAdapterを使う
		paredDevices = getParedDevices();
		// ペアリングされているデバイスを取得

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
		// 接続時に何らかのメッセージを出すようにする

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
		// 切断時に何らかのメッセージを出すようにする
	}
	// コンストラクタ

	public Set<BluetoothDevice> getParedDevices() {
		paredDevices = bluetoothAdapter.getBondedDevices();
		return paredDevices;
	}
	// ペアリングされているデバイスリストを更新したあとに結果を返す

	public boolean isDeviceConnected() {
		if (bluetoothSocket != null) {
			return bluetoothSocket.isConnected();
		}
		return false;
	}
	// 何らかのデバイスに接続されていたらtrueを返す

	public void connectDevice(String paredDeviceAddress) {
		Log.d("target address", paredDeviceAddress);

		for (BluetoothDevice paredDevice : paredDevices) {
			Log.d("paredDevice address", paredDevice.getAddress());
			if (paredDeviceAddress.equals(paredDevice.getAddress())) {
				// デバイスリストにサーチをかけ、ヒットした場合の処理
				this.bluetoothDevice = paredDevice;
				// クラス内で制御する対象に設定

				Log.d("find", "device");
				// logでデバイスを見つけたことを知らせる

				Thread connect = new Thread(new Runnable() {
					@Override
					public void run() {
						// ここからは時間がかかる処理なので、非同期処理で行う

						Message message = new Message();
						// Handlerに送るメッセージを初期化

						try {
							bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(SPP_UUID);
						} catch (IOException e) {
							message.what = -1;
							onConnect.sendMessage(message);
							// エラーｰ1を送る（ソケットが見つからない）
							e.printStackTrace();
							return;
						}

						if (bluetoothAdapter.isDiscovering()) {
							bluetoothAdapter.cancelDiscovery();
							// 探索中だったらキャンセルする
						}

						try {
							bluetoothSocket.connect();
							message.what = 0;
							onConnect.sendMessage(message);
							// 完了0を送る（接続成功）
						} catch (IOException e1) {
							try {
								bluetoothSocket.close();
							} catch (IOException e2) {
								e2.printStackTrace();
							}
							message.what = -2;
							onConnect.sendMessage(message);
							// エラー-2を送る（デバイスが見つからない）
							return;
						}

						try {
							inputStream = bluetoothSocket.getInputStream();
							outputStream = bluetoothSocket.getOutputStream();
							// 送受信用のStreamを設定
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				});

				connect.start();
				Log.d("ThreadStart", "Connect");
				// Threadをスタートする（非同期処理）;
				return;
			}
		}
		Message message = new Message();
		message.what = -3;
		onConnect.sendMessage(message);
		// エラー-3を送る（そもそもそんなデバイスはペアリングしてない）
	}
	// デバイスに接続する

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
	// デバイスに再接続する
	// TODO そのうち作ります（未完成）

	public void disConnectDevices() {
		if (bluetoothSocket != null) {
			if (bluetoothSocket.isConnected()) {
				Thread disConnect = new Thread(new Runnable() {
					@Override
					public void run() {
						Message message = new Message();
						// メッセージ初期化
						try {
							bluetoothSocket.close();
							message.what = 0;
							onDisConnect.sendMessage(message);
							// 切断成功
						} catch (IOException e) {
							message.what = -1;
							onDisConnect.sendMessage(message);
							e.printStackTrace();
							// 切断失敗
						}
					}
				});

				disConnect.start();
				// 非同期処理開始
			}
		}
	}
	// デバイスから切断する

	public ArrayList<String> getMessageMailBox() {
		if (messageMailBox.size() == 0) {
			ArrayList<String> noMessage = new ArrayList<String>(1);
			noMessage.add(0, "NO READ MESSAGE");
			return noMessage;
		}
		return messageMailBox;
		// 受信したメッセージ郡を返す。 新しい0<------99古い
	}
	// メッセージを受信しているメッセージボックスを取得する

	public void writeMessage(final String message) {
		try {
			if (bluetoothSocket.isConnected()) {
				Thread write = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							outputStream.write(message.getBytes("UTF-8"));
							// OutputStreamを使用し、書き込む
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});

				write.start();
				// 非同期処理スタート
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	// メッセージを送信する

	public void readMessage() {
		if (bluetoothSocket != null) {
			if (bluetoothSocket.isConnected()) {
				Thread read = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							InputStreamReader reader = new InputStreamReader(inputStream);
							BufferedReader bufferedReader = new BufferedReader(reader);

							String message = bufferedReader.readLine();
							messageMailBox.add(0, message);
							// BufferedReaderを使用して1行読み込み、messageMailBoxに突っ込む。

						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				});

				read.start();
				// 非同期処理スタート
			}
		}
	}
	// メッセージを受信し、messageMailBoxに格納する

	public void setOnConnect(Handler onConnect) {
		this.onConnect = onConnect;
	}
	// 接続時のハンドラを設定

	public void setOnDisConnect(Handler onDisConnect) {
		this.onDisConnect = onDisConnect;
	}
	// 切断時のハンドラを設定

}

//	吾輩はやれば出来る子である。
//		  ∩∩
//	    （´･ω･）
//	   ＿|　⊃／(＿＿_
//	　／ └-(＿＿＿_／
//	 ￣￣￣￣￣￣￣
//	やる気はまだない
//
//	　　 ⊂⌒／ヽ-、＿_
//	　／⊂_/＿＿＿＿ ／
//	  ￣￣￣￣￣￣￣