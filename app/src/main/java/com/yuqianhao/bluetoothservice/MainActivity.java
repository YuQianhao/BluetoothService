package com.yuqianhao.bluetoothservice;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yuqianhao.support.bluetooth.AbsBluetoothStateChange;
import com.yuqianhao.support.bluetooth.BluetoothService;
import com.yuqianhao.support.bluetooth.BluetoothServiceConnected;
import com.yuqianhao.support.bluetooth.IBluetoothController;
import com.yuqianhao.support.bluetooth.IBluetoothListenSocket;
import com.yuqianhao.support.bluetooth.IBluetoothStateChange;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button button;
    TextView textView;
    StringBuilder stringBuilder=new StringBuilder();

    IBluetoothController bluetoothController;
    IBluetoothStateChange bluetoothStateChange;
    IBluetoothListenSocket bluetoothListenSocket=new IBluetoothListenSocket.Stub() {
        @Override
        public void onReceiveDataArray(byte[] data) throws RemoteException {
            print("[BluetoothListenSocket]收到的数据："+ new String(data));
            bluetoothController.send("回复的消息".getBytes());
        }
    };


    BluetoothServiceConnected serviceConnection=new BluetoothServiceConnected() {
        @Override
        protected void onConnected(IBluetoothController iBluetoothController) {
            bluetoothController=iBluetoothController;
            print("[BluetoothService]服务绑定成功");
            button.setText("开始扫描");
            try {
                bluetoothController.registerBluetoothChangeListener(bluetoothStateChange);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(button.getText().equals("开始扫描")){
                        bluetoothController.startScan();
                    }
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        });
        textView=findViewById(R.id.textView);
        bluetoothStateChange=new AbsBluetoothStateChange() {
            @Override
            public void onScanningStart() throws RemoteException {
                print("[BluetoothStateChange]开始扫描");
            }

            @Override
            public void onScanningComplete() throws RemoteException {
                print("[BluetoothStateChange]扫描完成");
            }

            @Override
            public void onScanningBluetoothDevice(BluetoothDevice bluetoothDevice) throws RemoteException {
                print("[BluetoothStateChange]扫描到设备："+bluetoothDevice.getName());
                bluetoothController.cancelScan();
                bluetoothController.pair(bluetoothDevice);
            }

            @Override
            public void onBondedDevice(List<BluetoothDevice> bluetoothDevice) throws RemoteException {
                if(bluetoothDevice!=null && bluetoothDevice.size()!=0){
                    print("[BluetoothStateChange]获取到已配对的蓝牙：");
                    for(BluetoothDevice tmp:bluetoothDevice){
                        print(tmp.getName());
                    }
                }
            }

            @Override
            public void onPairSuccess(BluetoothDevice bluetoothDevice) throws RemoteException {
                print("[BluetoothStateChange]配对成功："+bluetoothDevice.getName());
                bluetoothController.connectBluetooth(bluetoothDevice,"2faa7db4-5dbf-11e9-a244-0235d2b38928");
                bluetoothController.listenSocket("AAA","2faa7db4-5dbf-11e9-a244-0235d2b38928",bluetoothListenSocket);
            }

            @Override
            public void onPairing(BluetoothDevice bluetoothDevice) throws RemoteException {
                print("[BluetoothStateChange]正在配对："+bluetoothDevice.getName());
            }

            @Override
            public void onPairFailure(BluetoothDevice bluetoothDevice) throws RemoteException {
                print("[BluetoothStateChange]配对失败："+bluetoothDevice.getName());
            }

            @Override
            public void onConnectFailure(String msg) throws RemoteException {
                print("[BluetoothStateChange]连接失败："+msg);
            }

            @Override
            public void onConnectSuccess() throws RemoteException {
                print("[BluetoothStateChange]连接成功");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while(true){
                                bluetoothController.send("发送的消息".getBytes());
                                Thread.sleep(1000);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onListenSuccess() throws RemoteException {
                print("[BluetoothStateChange]监听成功");
            }

            @Override
            public void onListenFailure(String msg) throws RemoteException {
                print("[BluetoothStateChange]监听失败"+msg);
            }
        };
        bindService(new Intent(this, BluetoothService.class),serviceConnection,BIND_AUTO_CREATE);
    }

    private void print(final Object ...objects){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(Object o:objects){
                    stringBuilder.append(o.toString());
                    stringBuilder.append("\n");
                }
                textView.setText(stringBuilder.toString());
            }
        });
    }
}
