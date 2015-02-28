package jp.itnav.derushio.btmanager.timer;

import android.os.Handler;
import android.os.Message;

/**
 * Created by derushio on 15/02/27.
 * タイマーを定義するclass
 */
public class TimerHandler extends Handler {
	private boolean mTickFlag = false;
	// タイマーが動いているか
	private long mDelayMilliSec = 1000;
	// 待ち時間の長さ

	public interface OnTickListener {
		public void onTick();
	}
	// タイマーがチック(一回り)した時のインターフェース

	private OnTickListener mOnTickListener = new OnTickListener() {
		@Override
		public void onTick() {
			return;
		}
	};
	// 空白のインターフェース(ぬるぽ回避)

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);

		if (mTickFlag) {
			mOnTickListener.onTick();
			sleep();
		}
		// ストップ命令が着ていなければ実行
		// もう一度待つ
	}
	// メッセージを受け取った時(チックした時)の処理

	public void timerStart(long delayMilliSec) {
		this.mDelayMilliSec = delayMilliSec;
		mTickFlag = true;
		sleep();
	}
	// タイマーをスタートする

	public void timerRestart() {
		timerStart(mDelayMilliSec);
	}
	// リスタート用のオーバーロード

	public void timerStop() {
		mTickFlag = false;
	}
	// タイマーを停止する

	private void sleep() {
		removeMessages(0);
		sendMessageDelayed(obtainMessage(0), mDelayMilliSec);
	}
	// ディレイした後に自分にメッセージを送る

	public void setOnTickListener(OnTickListener onTickListener) {
		mOnTickListener = onTickListener;
	}
	// チック時の処理を外部から設定
}
