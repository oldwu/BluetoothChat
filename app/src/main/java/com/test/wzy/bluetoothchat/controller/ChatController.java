package com.test.wzy.bluetoothchat.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.test.wzy.bluetoothchat.connect.AcceptThread;
import com.test.wzy.bluetoothchat.connect.ConnectThread;
import com.test.wzy.bluetoothchat.connect.Protocol;

import java.io.UnsupportedEncodingException;

/**
 * Created by wzy85 on 2015/12/18.
 */
public class ChatController {

    private ConnectThread mConnectThread = null;
    private AcceptThread mAcceptThread = null;



    class ChatProtocol implements Protocol {
        @Override
        public byte[] decodeString(String str) {

            byte[] buffer = null;

            if (str == null)
                return new byte[0];
            try {
                buffer = str.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return new byte[0];
            }

            return buffer;

        }

        @Override
        public String encodeString(byte[] buffer) {
            if (buffer == null)
                return "";
            String str = "";
            try {
                str = new String(buffer, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }

            return str;
        }
    }


    ChatProtocol chatProtocol = new ChatProtocol();

    public void chatWithFriend(BluetoothAdapter mAdapter, BluetoothDevice mDevice, Handler handler) {

        mConnectThread = new ConnectThread(mAdapter, mDevice, handler);
        mConnectThread.start();
    }

    public void acceptFriend(BluetoothAdapter mAdapter, Handler handler) {
        mAcceptThread = new AcceptThread(mAdapter, handler);
        mAcceptThread.start();
    }

    public void sendMessage(String msg) {

        if (mConnectThread != null) {
            mConnectThread.sendData(chatProtocol.decodeString(msg));
        } else if (mAcceptThread != null) {
            mAcceptThread.sendData(chatProtocol.decodeString(msg));
        }
    }

    public String decodeMessage(byte[] data) {
        return chatProtocol.encodeString(data);
    }

    public void stopChat() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
        } else if (mAcceptThread != null) {
            mAcceptThread.cancel();
        }
    }

    private static ChatController chatController = new ChatController();

    public static ChatController getInstance() {
        return chatController;
    }


}
