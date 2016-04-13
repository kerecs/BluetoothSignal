package kerecs.bluetoothsignal;

import java.util.ArrayList;
import java.util.UUID;

import android.bluetooth.BluetoothSocket;
import android.os.CountDownTimer;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothDevice bluetoothDevice;
	private BluetoothSocket bluetoothSocket;
	private boolean OpenBluetoothFlag = false;
	private IntentFilter localIntentFilter;

	private ArrayList<Device> pairedListData, newDevicesListData;
	private ListView pairedListView, newDevicesListView;
	private DevicesAdapter pairedDevicesAdapter, newDevicesDevicesAdapter;

//	private float[][] distanceData;
	//工具
	private CountDownTimer timer;
	private Handler handler;
	private boolean flag = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pairedListData = new ArrayList<>();
		newDevicesListData = new ArrayList<>();
		pairedListView = (ListView) findViewById(R.id.paired_devices);
		newDevicesListView = (ListView) findViewById(R.id.new_devices);
		pairedDevicesAdapter = new DevicesAdapter(this, R.layout.device_information, pairedListData);
		newDevicesDevicesAdapter = new DevicesAdapter(this, R.layout.device_information, newDevicesListData);
		pairedListView.setAdapter(pairedDevicesAdapter);
		newDevicesListView.setAdapter(newDevicesDevicesAdapter);

		// Get the local Bluetooth adapter
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		//扫描蓝牙设备
		if (bluetoothAdapter == null) {
			Toast.makeText(MainActivity.this, "蓝牙设备不可用！", Toast.LENGTH_SHORT).show();
			finish();
		}
		else if (bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}

		//持续监听搜索蓝牙
		timer = new CountDownTimer(24 * 60 * 60 * 1000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				if (bluetoothAdapter.isDiscovering()) {
					bluetoothAdapter.cancelDiscovery();
				}
				bluetoothAdapter.startDiscovery();
				localIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);//("android.bluetooth.device.action.FOUND");
				registerReceiver(new BroadcastReceiver() {
					public void onReceive(Context context, Intent intent) {
						String action = intent.getAction();
						BluetoothDevice localBluetoothDevice = null;
						//未绑定设备
						if (BluetoothDevice.ACTION_FOUND.equals(action)) {
							localBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);//("android.bluetooth.device.extra.DEVICE");
							if (localBluetoothDevice != null && localBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {//未绑定设备
								Device tempDevice = new Device(localBluetoothDevice.getName(), localBluetoothDevice.getAddress(), intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI));
								flag = false;
								for(int i = 0;i < newDevicesListData.size();i++){
									Device device = newDevicesListData.get(i);
									if(device.getMac().equals(tempDevice.getMac())) {
										newDevicesListData.set(i, tempDevice);
										newDevicesDevicesAdapter.notifyDataSetChanged();
										flag = true;
										break;
									}
								}
								if(!flag){
									newDevicesListData.add(tempDevice);
									newDevicesDevicesAdapter.notifyDataSetChanged();
								}
							}
						}
						//已绑定设备 == 12
						do {
							do {
								if (localBluetoothDevice != null){
									Device tempDevice = new Device(localBluetoothDevice.getName(), localBluetoothDevice.getAddress(), intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI));
									flag = false;
									for(int i = 0;i < pairedListData.size();i++) {
										Device device = pairedListData.get(i);
										if(device.getMac().equals(tempDevice.getMac())){
											pairedListData.set(i, tempDevice);
											pairedDevicesAdapter.notifyDataSetChanged();
											flag = true;
											break;
										}
									}
									if(!flag){
										pairedListData.add(tempDevice);
										pairedDevicesAdapter.notifyDataSetChanged();
									}
								}
								return;
							} while (!BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action));
						} while (newDevicesListData.size() != 0);
					}
				}, localIntentFilter);
			}
			@Override
			public void onFinish() {

			}
		}.start();


		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 0:
						break;
					case 1:
						pairedDevicesAdapter.notifyDataSetChanged();
						break;
					case 2:
						newDevicesDevicesAdapter.notifyDataSetChanged();
						break;
				}
				super.handleMessage(msg);
			}
		};
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bluetoothAdapter.isDiscovering()) {
			bluetoothAdapter.cancelDiscovery();
		}
	}

}
