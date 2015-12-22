package com.test.wzy.bluetoothchat.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.test.wzy.bluetoothchat.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wzy85 on 2015/12/18.
 */
public class ConnectedThread extends Thread {

    private Handler handler;
    private BluetoothSocket mmSocket;

    private InputStream inputStream;
    private OutputStream outputStream;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {

        this.handler = handler;

        this.mmSocket = socket;

    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] buffer = new byte[1024];
                inputStream = mmSocket.getInputStream();
                if (inputStream.available() > 0) {
                    inputStream.read(buffer);
                    Message msg = new Message();
                    msg.obj = buffer;
                    msg.what = Constant.MSG_GOT_DATA;
                    handler.sendMessage(msg);
                }
            } catch (IOException e) {
                try {
                    mmSocket.close();
                    break;
                } catch (IOException e1) {
                }

            }
        }
    }

    public void write(byte[] buffer) {
        try {
            outputStream = mmSocket.getOutputStream();
            outputStream.write(buffer);

        } catch (IOException e) {
            handler.sendEmptyMessage(Constant.MSG_ERROR);
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
