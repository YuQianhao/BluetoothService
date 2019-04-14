package com.yuqianhao.support.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.RemoteException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.UUID;

public class BlueAcceptThread extends Thread{

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket bluetoothServerSocket;
    private IBluetoothStateChange bluetoothStateChange;
    private IBluetoothListenSocket bluetoothListenSocket;
    private boolean reading=true;

    public BlueAcceptThread(BluetoothAdapter bluetoothAdapter, IBluetoothStateChange bluetoothStateChange, IBluetoothListenSocket bluetoothListenSocket, String name, String uuid){
        this.bluetoothAdapter=bluetoothAdapter;
        this.bluetoothListenSocket=bluetoothListenSocket;
        this.bluetoothStateChange=bluetoothStateChange;
        try {
            bluetoothServerSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(name, UUID.fromString(uuid));
        } catch (IOException e) {
            e.printStackTrace();
            if(bluetoothStateChange!=null){
                try {
                    bluetoothStateChange.onListenFailure(e.getMessage());
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void cancel(){
        reading=false;
    }

    @Override
    public void run() {
        try{
            BluetoothSocket bluetoothSocket=bluetoothServerSocket.accept();
            if(bluetoothStateChange!=null){
                bluetoothStateChange.onListenSuccess();
            }
            BufferedInputStream bufferedInputStream=new BufferedInputStream(bluetoothSocket.getInputStream());
            while(reading){
                int count=0;
                while(count==0){
                    count=bufferedInputStream.available();
                }
                byte[] data=new byte[count];
                bufferedInputStream.read(data);
                if(bluetoothListenSocket!=null){
                    bluetoothListenSocket.onReceiveDataArray(data);
                }
            }
            bufferedInputStream.close();
            bluetoothSocket.close();
            bluetoothServerSocket.close();
            if(bluetoothStateChange!=null){
                try {
                    bluetoothStateChange.onListenClose();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(bluetoothStateChange!=null){
                try {
                    bluetoothStateChange.onListenFailure(e.getMessage());
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
