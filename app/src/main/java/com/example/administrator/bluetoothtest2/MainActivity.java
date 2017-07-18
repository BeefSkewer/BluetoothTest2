package com.example.administrator.bluetoothtest2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private BluetoothAdapter mBluetoothAdapter;
    private final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String blueAddress = "20:13:11:22:05:23";//蓝牙模块的MAC地址
    private TextView show_rssi;
    private int period=0;//获得rssi的次数
    private Button find;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show_rssi=(TextView)findViewById(R.id.show_rssi);

        find = (Button) findViewById(R.id.find);
        find.setOnClickListener(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 设置广播信息过滤
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);//每搜索到一个设备就会发送一个该广播
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//当全部搜索完后发送该广播
        filter.setPriority(Integer.MAX_VALUE);//设置优先级
         // 注册蓝牙搜索广播接收者，接收并处理搜索结果
        this.registerReceiver(receiver, filter);

        mBluetoothAdapter.startDiscovery();
    }
    public void onClick(View v) {
        if (v.getId() == R.id.find) {
            //如果当前在搜索，就先取消搜索
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();

            }
            //开启搜索
            mBluetoothAdapter.startDiscovery();

        }
    }
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("rssi","Receive1 pass");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("rssi","Receive2 pass");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Log.d("rssi","信号强度："+ String.valueOf
                            (intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE))+"次数为："+period);
                    show_rssi.setText("信号强度："+ String.valueOf
                            (intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE))+"次数为："+period);
                    period++;
                    if (mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.cancelDiscovery();

                    }
                    //开启搜索
                    mBluetoothAdapter.startDiscovery();
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("rssi","finish pass");
                //已搜素完成
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();

                }
                //开启搜索
                mBluetoothAdapter.startDiscovery();
            }
        }
    };
    protected void onDestroy(){
        super.onDestroy();
        //timerTask.cancel();
        unregisterReceiver(receiver);

    }
}
