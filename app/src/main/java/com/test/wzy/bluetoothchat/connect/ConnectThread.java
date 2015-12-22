package com.test.wzy.bluetoothchat.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.test.wzy.bluetoothchat.Constant;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by wzy85 on 2015/12/18.
 */
public class ConnectThread extends Thread {

    private BluetoothAdapter mAdapter;
    private BluetoothDevice mDevice;
    private BluetoothSocket mmSocket;
    private Handler handler;
    private ConnectedThread mConnectedThread;

    private UUID myUUID = UUID.fromString(Constant.CONNECTTION_UUID);

    public ConnectThread(BluetoothAdapter mAdapter, BluetoothDevice mDevice, Handler handler) {
        this.mAdapter = mAdapter;
        this.mDevice = mDevice;
        this.handler = handler;

        try {
            mmSocket = mDevice.createRfcommSocketToServiceRecord(myUUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        mAdapter.cancelDiscovery();

        try {
            mmSocket.connect();
        } catch (Exception connectException) {
            handler.sendMessage(handler.obtainMessage(Constant.MSG_ERROR, connectException));
            try {
                mmSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        ManagerSocket(mmSocket);
    }

    public void sendData(byte[] data) {
        mConnectedThread.write(data);
    }

    private void ManagerSocket(BluetoothSocket mmSocket) {
        handler.sendEmptyMessage(Constant.MSG_CONNECTED_TO_SERVER);
        mConnectedThread = new ConnectedThread(mmSocket,  handler);
        mConnectedThread.start();
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}
