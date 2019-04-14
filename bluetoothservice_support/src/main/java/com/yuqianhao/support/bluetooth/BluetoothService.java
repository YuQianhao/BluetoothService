package com.yuqianhao.support.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class BluetoothService extends Service {


    private IBluetoothController.Stub bluetoothService=new IBluetoothController.Stub() {
        @Override
        public boolean isEnabled() throws RemoteException {
            return bluetoothAdapter.isEnabled();
        }

        @Override
        public void enabled() throws RemoteException {
            bluetoothAdapter.enable();
        }

        @Override
        public void getName() throws RemoteException {
            bluetoothAdapter.getName();
        }

        @Override
        public List<BluetoothDevice> getBondedDevices() throws RemoteException {
            return getBluetoothBondedDevices();
        }

        @Override
        public void pair(BluetoothDevice bluetoothDevice) throws RemoteException {
            bluetoothDevice.createBond();
        }

        @Override
        public void connectBluetooth(BluetoothDevice bluetoothDevice, String uuid) throws RemoteException {
            if(socketThread==null){
                socketThread=new BlueSocketThread(bluetoothStateChange,bluetoothDevice,uuid);
                socketThread.start();
            }
        }

        @Override
        public void disconnectBluetooth(BluetoothDevice bluetoothDevice) throws RemoteException {
            if(socketThread!=null){
                socketThread.close();
            }
        }

        @Override
        public void startScan() throws RemoteException {
            if(bluetoothStateChange!=null){
                bluetoothStateChange.onScanningStart();
                bluetoothStateChange.onBondedDevice(getBluetoothBondedDevices());
            }
            bluetoothAdapter.startDiscovery();
        }

        @Override
        public void cancelScan() throws RemoteException {
            bluetoothAdapter.cancelDiscovery();
        }

        @Override
        public void registerBluetoothChangeListener(IBluetoothStateChange change) throws RemoteException {
            bluetoothStateChange=change;
            bluetoothBroadcastReceiver.setBluetoothStateChange(bluetoothStateChange);
        }

        @Override
        public void listenSocket(String name, String uuid, IBluetoothListenSocket socket) throws RemoteException {
            if(blueAcceptThread ==null){
                blueAcceptThread =new BlueAcceptThread(bluetoothAdapter,bluetoothStateChange,socket,name,uuid);
                blueAcceptThread.start();
            }
        }

        @Override
        public void dislistenSocket() throws RemoteException {
            if(blueAcceptThread !=null){
                blueAcceptThread.cancel();
            }
        }

        @Override
        public void send(byte[] data) throws RemoteException {
            if(socketThread!=null){
                socketThread.write(data);
            }
        }

    };

    private IBluetoothStateChange bluetoothStateChange;
    private BluetoothBroadcastReceiver bluetoothBroadcastReceiver;
    private BluetoothAdapter bluetoothAdapter;
    private BlueSocketThread socketThread;
    private BlueAcceptThread blueAcceptThread;

    @Override
    public void onCreate() {
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter.isEnabled()){
            bluetoothAdapter.enable();
        }
        bluetoothBroadcastReceiver=new BluetoothBroadcastReceiver(bluetoothStateChange);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(bluetoothBroadcastReceiver,intentFilter);
    }

    private List<BluetoothDevice> getBluetoothBondedDevices(){
        Set<BluetoothDevice> deviceSet=bluetoothAdapter.getBondedDevices();
        List<BluetoothDevice> deviceList=new ArrayList<>();
        for(BluetoothDevice device:deviceSet){
            deviceList.add(device);
        }
        return deviceList;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(bluetoothBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return bluetoothService;
    }
}
