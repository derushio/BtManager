package jp.itnav.derushio.bluetoothmanager;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Message;

import java.io.IOException;
import java.util.Set;

/**
 * Created by derushio on 14/11/10.
 */
public class BluetoothSppManager extends BluetoothManagerBase {
	// SPP通信のUUID

	protected Set<BluetoothDevice> mParedDevices;
	protected BluetoothDevice mTargetDevice;
	// Bluetooth制御用クラス群

	public BluetoothSppManager(Context context, int messageBoxLength) {
		super(context, messageBoxLength);
		mParedDevices = getParedDevices();
		// ペアリングされているデバイスを取得
	}
	// コンストラクタ

	public Set<BluetoothDevice> getParedDevices() {
		mParedDevices = mBluetoothAdapter.getBondedDevices();
		return mParedDevices;
	}
	// ペアリングされているデバイスリストを更新したあとに結果を返す

	public boolean isTargetDeviceExist() {
		if (mTargetDevice != null) {
			return true;
		}
		return false;
	}
	// ターゲットデバイスが存在するか確認

	public void setTargetDevice(BluetoothDevice mTargetDevice) {
		this.mTargetDevice = mTargetDevice;
	}
	// ターゲットするデバイスを設定

	public BluetoothDevice getTargetDevice() throws NullPointerException {
		return mTargetDevice;
	}
	// ターゲットされているデバイスを取得設定されていないとぬるぽ吐くので、try/catchしてください

	public void connectDevice() {
		final Message message = new Message();
		// Handlerに送るメッセージを初期化
		if (isTargetDeviceExist()) {

			if (isDeviceConnected()) {
				message.what = 1;
				mOnConnect.sendMessage(message);
				return;
				// すでに接続している
			}

			Thread connect = new Thread(new Runnable() {
				@Override
				public void run() {
					// ここからは時間がかかる処理なので、非同期処理で行う

					try {
						mBluetoothSocket = mTargetDevice.createRfcommSocketToServiceRecord(SPP_UUID);
						// SPP(Serial Port Profile)を設定
					} catch (IOException e) {
						// IOExceptionをキャッチした
						e.printStackTrace();
						// エラーメッセージを吐く

						message.what = -1;
						mOnConnect.sendMessage(message);
						// エラーｰ1を送る(ソケットが見つからない)
						return;
						// 設定を作れなかったのでこれ以上実行する必要がない
					}

					if (mBluetoothAdapter.isDiscovering()) {
						mBluetoothAdapter.cancelDiscovery();
						// 探索中だったらキャンセルする
					}

					try {
						mBluetoothSocket.connect();
						// 接続する

						//できたらこれ以下の処理が走る
						message.what = 0;
						mOnConnect.sendMessage(message);
						// 完了0を送る(接続成功)
					} catch (IOException e) {
						// IOExceptionをキャッチ(接続できない)
						e.printStackTrace();
						message.what = -2;
						mOnConnect.sendMessage(message);
						// エラー-2を送る(デバイスが見つからない)
						return;
						// 接続できなかったのでこれ以上する必要はない
					}

					try {
						mBtInputStream = mBluetoothSocket.getInputStream();
						mBtOutputStream = mBluetoothSocket.getOutputStream();
						// 送受信用のStreamを設定
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			});

			connect.start();
			// Threadをスタートする(非同期処理);
		} else {
			message.what = -3;
			mOnConnect.sendMessage(message);
			// そのようなデバイスとはペアリングしていない
		}
	}
	// デバイスに接続する

	public void disConnectDevice(final boolean reconnect) {
		final Message message = new Message();
		// メッセージ初期化
		if (isDeviceConnected()) {
			Thread disConnect = new Thread(new Runnable() {
				@Override
				public void run() {
					// ここからは時間がかかる処理なので、非同期処理で行う
					try {
						mBluetoothSocket.close();
						// 切断する

						//できたらこれ以下の処理が走る
						message.what = 0;
						mOnDisConnect.sendMessage(message);
						// 切断成功
					} catch (IOException e) {
						message.what = -1;
						mOnDisConnect.sendMessage(message);
						e.printStackTrace();
						// 切断失敗
					}

					if (reconnect) {
						connectDevice();
						// 接続し直す
					}
				}
			});

			disConnect.start();
			// 非同期処理開始
		} else {
			message.what = -2;
			mOnDisConnect.sendMessage(message);
			// デバイスに接続されていないエラーを投げる
			if (reconnect) {
				connectDevice();
				// 接続し直す
			}
		}
	}
	// デバイスから切断する

	public void reConnectDevice() {
		disConnectDevice(true);
	}
	// デバイスに再接続する

	public void disConnectDevice() {
		disConnectDevice(false);
	}
	// デバイスから切断する(オーバーロード)
}

//	吾輩はやれば出来る子である。
//		  ∩∩
//	    (´･ω･)
//	   ＿|　⊃／(＿＿_
//	　／ └-(＿＿＿_／
//	 ￣￣￣￣￣￣￣
//	やる気はまだない
//
//	　　 ⊂⌒／ヽ-、＿_
//	　／⊂_/＿＿＿＿ ／
//	  ￣￣￣￣￣￣￣