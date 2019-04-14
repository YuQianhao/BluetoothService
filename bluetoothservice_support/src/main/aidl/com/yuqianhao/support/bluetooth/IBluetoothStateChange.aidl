// IBluetoothStateChange.aidl
package com.yuqianhao.support.bluetooth;


//这个接口定义了蓝牙从扫描到配对的所有状态改变的监听回调方法
interface IBluetoothStateChange {

    //蓝牙扫描开始
    void onScanningStart();

    //蓝牙扫描结束
    void onScanningComplete();

    //扫描到蓝牙的通知
    void onScanningBluetoothDevice(in BluetoothDevice bluetoothDevice);

    //已经配对成功的蓝牙
    void onBondedDevice(in List<BluetoothDevice> bluetoothDevice);

    //配对成功
    void onPairSuccess(in BluetoothDevice bluetoothDevice);

    //正在配对中
    void onPairing(in BluetoothDevice bluetoothDevice);

    //配对失败
    void onPairFailure(in BluetoothDevice bluetoothDevice);

    //连接成功
    void onConnectSuccess();
    //连接失败
    void onConnectFailure(in String msg);
    //连接关闭
    void onConnectClose();

    //监听成功
    void onListenSuccess();
    //监听失败
    void onListenFailure(in String msg);
    //监听关闭
    void onListenClose();

    //蓝牙发生异常
    void onBluetoothError(in int errno);

}
