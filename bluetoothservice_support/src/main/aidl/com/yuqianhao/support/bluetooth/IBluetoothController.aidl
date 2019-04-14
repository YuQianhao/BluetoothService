// IBluetoothController.aidl
package com.yuqianhao.support.bluetooth;

// Declare any non-default types here with import statements

import com.yuqianhao.support.bluetooth.IBluetoothStateChange;
import com.yuqianhao.support.bluetooth.IBluetoothListenSocket;

interface IBluetoothController {

    //判断蓝牙是否开启
    boolean isEnabled();
    //静默开启蓝牙
    void enabled();
    //获取本地蓝牙名称
    void getName();

    //获取已经配对的蓝牙设备
    List<BluetoothDevice> getBondedDevices();

    //配对蓝牙设备
    void pair(in BluetoothDevice bluetoothDevice);

    //连接蓝牙
    void connectBluetooth(in BluetoothDevice bluetoothDevice,in String uuid);

    //断开蓝牙连接
    void disconnectBluetooth(in BluetoothDevice bluetoothDevice);

    //开始扫描
    void startScan();

    //结束扫描
    void cancelScan();

    //注册蓝牙状态监听器
    void registerBluetoothChangeListener(in IBluetoothStateChange change);

    //监听蓝牙收到的数据
    void listenSocket(in String name,in String uuid,in IBluetoothListenSocket socket);
    //关闭监听
    void dislistenSocket();

    //发送数据
    void send(in byte[] data);


}
