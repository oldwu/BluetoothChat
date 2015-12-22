package com.test.wzy.bluetoothchat.connect;

/**
 * Created by wzy85 on 2015/12/18.
 */
public interface Protocol {

    public byte[] decodeString(String str);

    public String encodeString(byte[] buffer);
}
