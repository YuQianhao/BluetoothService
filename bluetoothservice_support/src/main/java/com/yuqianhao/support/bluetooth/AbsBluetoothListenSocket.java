package com.yuqianhao.support.bluetooth;

import android.os.RemoteException;

public abstract class AbsBluetoothListenSocket extends IBluetoothListenSocket.Stub{
    @Override
    public abstract void onReceiveDataArray(byte[] data) throws RemoteException ;
}
