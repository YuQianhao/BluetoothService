package com.yuqianhao.support.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.UUID;

public class BlueSocketThread extends Thread{

    private BluetoothDevice bluetoothDevice;
    private UUID uuid;
    private BluetoothSocket bluetoothSocket;
    private IBluetoothStateChange bluetoothStateChange;
    private BufferedOutputStream bufferedOutputStream;
    private Handler handler;

    public BlueSocketThread(IBluetoothStateChange bluetoothStateChange, BluetoothDevice bluetoothDevice, String uuid){
        this.bluetoothDevice=bluetoothDevice;
        this.uuid=UUID.fromString(uuid);
        this.bluetoothStateChange=bluetoothStateChange;
        try {
            bluetoothSocket=bluetoothDevice.createRfcommSocketToServiceRecord(this.uuid);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if(bluetoothStateChange==null){
                    bluetoothStateChange.onConnectFailure(e.getMessage());
                }
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
    }

    public synchronized void write(byte[] data){
        Message message=handler.obtainMessage();
        message.obj=data;
        handler.sendMessage(message);
    }

    public synchronized void close(){
        handler=null;
        try {
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            if(bluetoothStateChange!=null){
                try {
                    bluetoothStateChange.onConnectFailure(e.getMessage());
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void run() {
        try {
            Looper.prepare();
            bluetoothSocket.connect();
            bufferedOutputStream=new BufferedOutputStream(bluetoothSocket.getOutputStream());
            handler=new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    byte[] data= (byte[]) msg.obj;
                    try {
                        bufferedOutputStream.write(data);
                        bufferedOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            if(bluetoothStateChange!=null){
                bluetoothStateChange.onConnectSuccess();
            }
            Looper.loop();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if(bluetoothStateChange==null){
                    bluetoothStateChange.onConnectFailure(e.getMessage());
                }
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
