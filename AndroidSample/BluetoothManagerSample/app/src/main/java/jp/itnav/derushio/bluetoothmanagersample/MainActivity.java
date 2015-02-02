package jp.itnav.derushio.bluetoothmanagersample;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;

import jp.itnav.derushio.bluetoothmanager.BluetoothManagedActivity;


public class MainActivity extends BluetoothManagedActivity {
	// BluetoothManagedActivityを継承している

	private LinearLayout paredDeviceList;
	// ペアリングしたBluetoothDeviceを表示するLinearLayout

	private TextView textViewReadMessage;
	// 受信したメッセージを表示するTextView
	private EditText editTextWriteMessage;
	// 送信するメッセージを設定するEditText

	private TimerHandler timerHandler;
	// タイマー

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		paredDeviceList = (LinearLayout) findViewById(R.id.paredDeviceList);
		textViewReadMessage = (TextView) findViewById(R.id.textViewReadMessage);
		editTextWriteMessage = (EditText) findViewById(R.id.editTextWriteMessage);
		// 描画された画面と変数を関連付け

		Set<BluetoothDevice> paredDevices = getParedDevices();
		// ペアリングされているデバイスを取得
		for (final BluetoothDevice bluetoothDevice : paredDevices) {
			Button button = new Button(this);
			button.setText(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
			button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setTargetDevice(bluetoothDevice);
					connectDevice();
					readMessageStart(100);
					// クリックされたら、クリックしたボタンに対応するBluetoothDeviceに接続、同時にメッセージの受信をスタートする。
				}
			});

			paredDeviceList.addView(button);
		}

		timerHandler = new TimerHandler();
		timerHandler.timerStart(1000);
		// タイマースタート
	}

	@Override
	protected void onPause() {
		super.onPause();
		timerHandler.timerStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		timerHandler.timerStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void writeMessage(View v) {
		editTextWriteMessage.clearFocus();
		writeMessage(editTextWriteMessage.getText().toString());
	}


	private class TimerHandler extends Handler {
		// タイマーを定義するclass
		private boolean isTick = false;
		// タイマーが動いているか
		private long delayMilliSec = 1000;
		// 待ち時間の長さ

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (isDeviceConnected()) {
				textViewReadMessage.setText(readMessage().get(0));
				// メッセージを受信する。
				// メッセージはArrayListの受信時間順に帰ってくるので、.get(0)で最新データが取れる。
			}

			if (isTick == true) {
				sleep();
			}
		}

		public void timerStart(long delayMilliSec) {
			this.delayMilliSec = delayMilliSec;
			isTick = true;
			sleep();
		}

		public void timerStart() {
			timerStart(delayMilliSec);
		}
		// リスタート用のOverLoad

		public void timerStop() {
			isTick = false;
		}

		private void sleep() {
			removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMilliSec);
		}
	}
}
