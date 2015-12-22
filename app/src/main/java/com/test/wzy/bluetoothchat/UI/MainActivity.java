package com.test.wzy.bluetoothchat.UI;

import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.wzy.bluetoothchat.Constant;
import com.test.wzy.bluetoothchat.R;
import com.test.wzy.bluetoothchat.controller.BluetoothController;
import com.test.wzy.bluetoothchat.controller.ChatController;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 0;
    private BluetoothController mController = new BluetoothController();

    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private List<BluetoothDevice> mBondedDeviceList = new ArrayList<>();

    private ListView mListview;
    private DeviceListAdapter mAdapter;
    private Toast mToast;

    private Button mSendBt;
    private EditText mInputBox;
    private TextView mChatContent;
    private RelativeLayout chatPanel;

    private StringBuilder mChatText = new StringBuilder();

    private MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initUI();
        BluetoothConfigMode();

        registerBlueToothReceiver();
        mController.turnOnBlueTooth(this, REQUEST_CODE);
    }

    private void initUI() {

        handler = new MyHandler();
        mAdapter = new DeviceListAdapter(mDeviceList, this);
        mListview = (ListView) findViewById(R.id.device_list);
        mSendBt = (Button) findViewById(R.id.bt_send);
        mSendBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                String ext = "[" + df.format(new Date()) + "] " + mController.getAdapter().getName() + "\n" + mInputBox.getText().toString();
                ChatController.getInstance().sendMessage(ext);

                mChatText.append(ext + "\n");
                mChatContent.setText(mChatText);
                mInputBox.setText("");
            }
        });
        chatPanel = (RelativeLayout) findViewById(R.id.chat_panel);

        mInputBox = (EditText) findViewById(R.id.chat_edit);
        mChatContent = (TextView) findViewById(R.id.chat_content);
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(bindDeviceClick);

    }

    private void registerBlueToothReceiver() {
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(mReceiver, intentFilter);


    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                setProgressBarIndeterminateVisibility(true);
                //初始化数据列表
                mDeviceList.clear();
                mAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //找到一个，添加一个
                mDeviceList.add(device);
                mAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0);
                if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    setProgressBarIndeterminateVisibility(true);
                } else {
                    setProgressBarIndeterminateVisibility(false);
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (remoteDevice == null) {
                    showToast("no device");
                    return;
                }
                int status = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0);
                if (status == BluetoothDevice.BOND_BONDED) {
                    showToast("Bonded " + remoteDevice.getName());
                } else if (status == BluetoothDevice.BOND_BONDING) {
                    showToast("Bonding " + remoteDevice.getName());
                } else if (status == BluetoothDevice.BOND_NONE) {
                    showToast("Not bond " + remoteDevice.getName());
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode != RESULT_OK)
                finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.enable_visiblity) {
            mController.enableVisibly(this);
        } else if (id == R.id.find_device) {

            mAdapter.refresh(mDeviceList);
            mController.findDevice();
            mListview.setOnItemClickListener(bindDeviceClick);
            BluetoothConfigMode();

        }
        if (id == R.id.bonded_device) {
            mBondedDeviceList = mController.getBondedDeviceList();
            mAdapter.refresh(mBondedDeviceList);
            mListview.setOnItemClickListener(connectDeviceClick);
            BluetoothConfigMode();
        }
        if (id == R.id.listening) {
            ChatController.getInstance().acceptFriend(mController.getAdapter(), handler);
        }
        if (id == R.id.stop_listening) {
            ChatController.getInstance().stopChat();
        }
        if (id == R.id.disconnect) {
            BluetoothConfigMode();
            mChatContent.setText("");
        }


        return super.onOptionsItemSelected(item);
    }

    private void showToast(String text) {

        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }

    private void ChatMode() {
        mListview.setVisibility(View.GONE);
        chatPanel.setVisibility(View.VISIBLE);

    }

    private void BluetoothConfigMode() {
        chatPanel.setVisibility(View.GONE);
        mListview.setVisibility(View.VISIBLE);
    }


    private AdapterView.OnItemClickListener bindDeviceClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = mDeviceList.get(position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                device.createBond();
            }
        }
    };

    private AdapterView.OnItemClickListener connectDeviceClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = mBondedDeviceList.get(position);
            ChatController.getInstance().chatWithFriend(mController.getAdapter(), device, handler);
        }
    };

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CONNECTED_TO_SERVER:
                    showToast("Connection Success");
                    ChatMode();
                    break;
                case Constant.MSG_GOT_A_CLINET:
                    showToast("Got a client");
                    ChatMode();
                    break;
                case Constant.MSG_FINISH_LISTENING:
                    mController.getAdapter().cancelDiscovery();
                    break;
                case Constant.MSG_START_LISTENING:
                    showToast("Waiting for Client");
                    break;
                case Constant.MSG_GOT_DATA:
                    String ext = ChatController.getInstance().decodeMessage((byte[]) msg.obj);
                    mChatText.append(ext + "\n");
                    mChatContent.setText(mChatText);
                    break;

            }

        }
    }
}
