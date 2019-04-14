package com.yuqianhao.support.bluetooth;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * 连接蓝牙服务的辅助类，通过重写{@link #onConnected(IBluetoothController)}方法可以获取到一个{@link IBluetoothController}
 * 的实例对象，该类继承{@link ServiceConnection}，可以使用{@link android.content.Context#bindService(Intent, ServiceConnection, int)}
 * 绑定该对象的实例进行和{@link BluetoothService}绑定。
 * */
public abstract class BluetoothServiceConnected implements ServiceConnection {
    @Override
    public final void onServiceConnected(ComponentName name, IBinder service) {
        onConnected(IBluetoothController.Stub.asInterface(service));
    }

    @Override
    public final void onServiceDisconnected(ComponentName name) {
        onDisconnected();
    }

    /**
     * 绑定成功
     * @param IBluetoothController 绑定成功的IBluetoothController实例
     * */
    protected abstract void onConnected(IBluetoothController IBluetoothController);

    protected void onDisconnected(){

    }
}
