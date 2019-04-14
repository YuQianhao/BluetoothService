// IBluetoothListenSocket.aidl
package com.yuqianhao.support.bluetooth;

//这个接口可以监听到蓝牙串口获取到的数据
interface IBluetoothListenSocket {

    void onReceiveDataArray(in byte[] data);

}
