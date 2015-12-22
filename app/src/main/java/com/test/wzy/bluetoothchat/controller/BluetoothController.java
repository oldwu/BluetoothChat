package com.test.wzy.bluetoothchat.controller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzy85 on 2015/12/18.
 */
public class BluetoothController {

    private BluetoothAdapter mAdapter;

    /**
     * 蓝牙适配器类构造函数
     */
    public BluetoothController() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 获取蓝牙适配器（BluetoothAdapter）
     *
     * @return BluetoothAdapter
     */
    public BluetoothAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 打开蓝牙
     *
     * @param activity    调用函数前的activity
     * @param requestCode 响应码
     */
    public void turnOnBlueTooth(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 设置可见性
     *
     * @param context
     */
    public void enableVisibly(Context context) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        context.startActivity(intent);
    }


    /**
     * 查找设备
     */
    public void findDevice() {
        assert (mAdapter != null);
        mAdapter.startDiscovery();
    }


    /**
     * 获取已绑定的蓝牙设备
     *
     * @return  List
     */
    public List<BluetoothDevice> getBondedDeviceList() {
        return new ArrayList<>(mAdapter.getBondedDevices());
    }


}
