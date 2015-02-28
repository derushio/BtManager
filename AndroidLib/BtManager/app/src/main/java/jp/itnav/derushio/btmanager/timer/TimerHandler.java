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

	private OnTickListener mOnTickListener = new OnTickListener() {
		@Override
		public void onTick() {
			return;
		}
	};

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);

		mOnTickListener.onTick();

		if (mTickFlag) {
			sleep();
		}
	}

	public void timerStart(long delayMilliSec) {
		this.mDelayMilliSec = delayMilliSec;
		mTickFlag = true;
		sleep();
	}

	public void timerRestart() {
		timerStart(mDelayMilliSec);
	}
	// リスタート用のOverLoad

	public void timerStop() {
		mTickFlag = false;
	}

	private void sleep() {
		removeMessages(0);
		sendMessageDelayed(obtainMessage(0), mDelayMilliSec);
	}

	public void setOnTickListener(OnTickListener onTickListener) {
		mOnTickListener = onTickListener;
	}
}
