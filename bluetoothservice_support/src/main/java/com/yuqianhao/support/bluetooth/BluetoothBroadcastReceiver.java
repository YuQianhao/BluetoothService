package com.yuqianhao.support.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    private IBluetoothStateChange bluetoothStateChange;

    public BluetoothBroadcastReceiver(IBluetoothStateChange bluetoothStateChange){
        this.bluetoothStateChange=bluetoothStateChange;
    }

    public void setBluetoothStateChange(IBluetoothStateChange iBluetoothStateChange){
        this.bluetoothStateChange=iBluetoothStateChange;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            String action=intent.getAction();
            if(bluetoothStateChange==null){
                return;
            }
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothStateChange.onScanningBluetoothDevice(device);
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                bluetoothStateChange.onScanningComplete();
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                BluetoothDevice bluetoothDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (bluetoothDevice.getBondState()){
                    case BluetoothDevice.BOND_BONDED:
                        bluetoothStateChange.onPairSuccess(bluetoothDevice);
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        bluetoothStateChange.onPairing(bluetoothDevice);
                        break;
                    case BluetoothDevice.BOND_NONE:
                        bluetoothStateChange.onPairFailure(bluetoothDevice);
                        break;
                }
            }
        }catch (RemoteException r){
            r.printStackTrace();
        }
    }
}
