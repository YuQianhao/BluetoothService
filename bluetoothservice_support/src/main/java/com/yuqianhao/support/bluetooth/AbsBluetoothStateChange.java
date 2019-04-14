package com.yuqianhao.support.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.RemoteException;

import java.util.List;

/**
 * 实现了{@link IBluetoothStateChange} 接口，拥有蓝牙扫描服务{@link BluetoothService}所有的状态回调方法，
 * 创建该类的子类，并调用{@link IBluetoothController#registerBluetoothChangeListener(IBluetoothStateChange)}
 * 方法进行蓝牙状态的监听。
 * */
public class AbsBluetoothStateChange extends IBluetoothStateChange.Stub{
    @Override
    public void onScanningStart() throws RemoteException {

    }

    @Override
    public void onScanningComplete() throws RemoteException {

    }

    @Override
    public void onScanningBluetoothDevice(BluetoothDevice bluetoothDevice) throws RemoteException {

    }

    @Override
    public void onBondedDevice(List<BluetoothDevice> bluetoothDevice) throws RemoteException {

    }

    @Override
    public void onPairSuccess(BluetoothDevice bluetoothDevice) throws RemoteException {

    }

    @Override
    public void onPairing(BluetoothDevice bluetoothDevice) throws RemoteException {

    }

    @Override
    public void onPairFailure(BluetoothDevice bluetoothDevice) throws RemoteException {

    }

    @Override
    public void onConnectSuccess() throws RemoteException {

    }

    @Override
    public void onConnectFailure(String msg) throws RemoteException {

    }

    @Override
    public void onConnectClose() throws RemoteException {

    }

    @Override
    public void onListenSuccess() throws RemoteException {

    }

    @Override
    public void onListenFailure(String msg) throws RemoteException {

    }

    @Override
    public void onListenClose() throws RemoteException {

    }

    @Override
    public void onBluetoothError(int errno) throws RemoteException {

    }
}
