package jp.itnav.derushio.bluetoothmanager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends BluetoothManagedActivity {

	private LinearLayout paredDeviceHolder;
	private ArrayList<TextView> paredDeviceTextViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		paredDeviceHolder = (LinearLayout) findViewById(R.id.paredDeviceHolder);
		paredDeviceTextViews = new ArrayList<TextView>();

		ArrayList<String> paredDeviceNames = getParedDeviceNames();

		for (String paredDeviceName : paredDeviceNames) {
			TextView textView = new TextView(this);
			textView.setText(paredDeviceName);
			paredDeviceHolder.addView(textView);
			paredDeviceTextViews.add(textView);
		}
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
}
