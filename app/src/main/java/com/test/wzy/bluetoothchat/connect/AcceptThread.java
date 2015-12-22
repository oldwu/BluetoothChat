package com.test.wzy.bluetoothchat.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.test.wzy.bluetoothchat.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by wzy85 on 2015/12/18.
 */
public class AcceptThread extends Thread {

    private BluetoothAdapter mAdapter;
    private Handler handler;
    private BluetoothServerSocket mmServerSocket;

    private OutputStream output;
    private InputStream input;

    private ConnectedThread mConnectedThread;

    private final UUID myUUID = UUID.fromString(Constant.CONNECTTION_UUID);

    /**
     * @param mDevice
     * @param mAdapter
     * @param handler
     */
    public AcceptThread(BluetoothAdapter mAdapter, Handler handler) {
        this.mAdapter = mAdapter;
        this.handler = handler;

        try {
            mmServerSocket = mAdapter.listenUsingRfcommWithServiceRecord("BluetoothChat", myUUID);

        } catch (IOException e) {
        }

    }

    @Override
    public void run() {

        BluetoothSocket socket = null;
        while (true) {
            try {
                handler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                handler.sendEmptyMessage(Constant.MSG_ERROR);
                break;
            }

            if (socket != null) {
                try {
                    managerSocket(socket);
                    mmServerSocket.close();
                    handler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

        }



    }

    private void managerSocket(BluetoothSocket socket) {
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
        }

        handler.sendEmptyMessage(Constant.MSG_GOT_A_CLINET);
        mConnectedThread = new ConnectedThread(socket, handler);
        mConnectedThread.start();
    }

    public void sendData(byte[] data) {
        mConnectedThread.write(data);
    }


    public void cancel() {
        try {
            mmServerSocket.close();
            handler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
        } catch (IOException e) {
            handler.sendEmptyMessage(Constant.MSG_ERROR);
            e.printStackTrace();
        }
    }

}
