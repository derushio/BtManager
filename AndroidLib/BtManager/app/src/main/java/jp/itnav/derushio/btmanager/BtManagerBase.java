package jp.itnav.derushio.btmanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by derushio on 15/02/27.
 */
public abstract class BtManagerBase {
	public static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	protected Context mContext;
	// Activity情報

	protected BluetoothAdapter mBtAdapter;
	protected BluetoothSocket mBtSocket;
	// Bluetoothモジュールとの通信をするためのクラス

	protected Handler mOnConnect;
	protected Handler mOnDisConnect;
	// 接続時、切断時のハンドラ(どこでsendMessageしてもMainLooper{UI Thread}で実行されます)

	protected InputStream mBtInputStream;
	protected OutputStream mBtOutputStream;
	// 入出力制御

	protected ArrayList<String> mMessageMailBox;
	// Bluetooth通信の受信用メールボックス

	public BtManagerBase(Context context, int messageMailBoxLength) {
		mContext = context;
		mMessageMailBox = new ArrayList<String>(messageMailBoxLength);
		// メールボックス容量

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		// デバイスのデフォルト設定のBluetoothAdapterを使う

		mOnConnect = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
					case 1:
						Toast.makeText(mContext, "既に接続されています", Toast.LENGTH_SHORT).show();
						break;
					case 0:
						Toast.makeText(mContext, "接続完了", Toast.LENGTH_SHORT).show();
						break;
					case -1:
						Toast.makeText(mContext, "Bluetooth Socketが使えません", Toast.LENGTH_SHORT).show();
						break;
					case -2:
						Toast.makeText(mContext, "接続デバイスを見つけられません", Toast.LENGTH_SHORT).show();
						break;
					case -3:
						Toast.makeText(mContext, "そのようなデバイスとペアリングしていません", Toast.LENGTH_SHORT).show();
						break;
					default:
						Toast.makeText(mContext, "不明なエラー", Toast.LENGTH_SHORT).show();
						break;
				}
				return false;
			}
		});
		// 接続時に何らかのメッセージを出すようにする

		mOnDisConnect = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
					case 0:
						Toast.makeText(mContext, "切断完了", Toast.LENGTH_SHORT).show();
						break;
					case -1:
						Toast.makeText(mContext, "切断デバイスを見つけられません", Toast.LENGTH_SHORT).show();
						break;
					case -2:
						Toast.makeText(mContext, "接続していません", Toast.LENGTH_SHORT).show();
						break;
					default:
						Toast.makeText(mContext, "不明なエラー", Toast.LENGTH_SHORT).show();
						break;
				}
				return false;
			}
		});
		// 切断時に何らかのメッセージを出すようにする
	}
	// コンストラクタ

	public boolean isSocketExists() {
		if (mBtSocket != null) {
			return true;
		}
		return false;
	}
	// ソケットが存在しているか確認(通信がスタートしているかどうか)

	public boolean isDeviceConnected() {
		if (isSocketExists()) {
			return mBtSocket.isConnected();
		}
		return false;
	}
	// 何らかのデバイスに接続されているか確認

	public void writeMessage(final String message) {
		if (isDeviceConnected()) {
			Thread write = new Thread(new Runnable() {
				@Override
				public void run() {
					// ここからは時間がかかる処理なので、非同期処理で行う
					try {
						mBtOutputStream.write(message.getBytes("UTF-8"));
						// OutputStreamを使用し、Bluetooth通信に書き込む
					} catch (IOException e) {
						e.printStackTrace();
						// エラーを吐く
					}
				}
			});

			write.start();
			// 非同期処理スタート
		}
	}
	// メッセージを送信する

	public void readMessage() {
		if (isDeviceConnected()) {
			Thread read = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// ここからは時間がかかる処理なので、非同期処理で行う
						InputStreamReader reader = new InputStreamReader(mBtInputStream);
						// InputStreamを読み込むクラス
						BufferedReader bufferedReader = new BufferedReader(reader);
						// 自動的に読み込んでくれるクラス(バッファの実装をしなくて良い)

						// TODO 二行以上送られた時の処理を考えておく
						String message = bufferedReader.readLine();
						// 一行読み込む
						mMessageMailBox.add(0, message);
						// BufferedReaderを使用して1行読み込み、messageMailBoxに突っ込む。

					} catch (IOException e) {
						e.printStackTrace();
						// エラーを吐く
					}
				}
			});

			read.start();
			// 非同期処理スタート
		}
	}
	// メッセージを受信し、messageMailBoxに格納する

	public ArrayList<String> getMessageMailBox() {
		if (mMessageMailBox.size() == 0) {
			// メッセージボックスに何も入っていなかった場合
			ArrayList<String> noMessage = new ArrayList<String>(1);
			noMessage.add(0, "NO READ MESSAGE");
			return noMessage;
			// メッセージを受信していなかった場合はNO READ MESSAGEを返す
		}
		return mMessageMailBox;
		// 受信したメッセージ郡を返す。 新しい0<------99古い
	}
	// メッセージを受信しているメッセージボックスを取得する

	public void setOnConnect(Handler mOnConnect) {
		this.mOnConnect = mOnConnect;
	}
	// 接続時のハンドラを設定

	public void setOnDisConnect(Handler mOnDisConnect) {
		this.mOnDisConnect = mOnDisConnect;
	}
	// 切断時のハンドラを設定
}
