package com.jackiepenghe.serialportsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jackiepenghe.serialportlibrary.OnSerialPortDataChangedListener;
import com.jackiepenghe.serialportlibrary.SerialPortManager;
import com.jackiepenghe.serialportsample.adapter.ReceivedDataRecyclerViewAdapter;
import com.jackiepenghe.serialportsample.adapter.SendDataRecyclerViewAdapter;
import com.jackiepenghe.serialportsample.adapter.SerialPortAdapter;
import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.textwatcher.HexTextAutoAddEmptyCharInputWatcher;
import com.sscl.baselibrary.utils.ConversionUtil;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.DefaultItemDecoration;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.baselibrary.widget.ReSpinner;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SerialPortSampleActivity extends BaseAppCompatActivity {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINESE);

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String NULL = "NULL";

    private ArrayList<String> serialPortAdapterData = new ArrayList<>();

    private SerialPortAdapter serialPortAdapter = new SerialPortAdapter(serialPortAdapterData);

    private ArrayList<String> receivedData = new ArrayList<>();

    private ArrayList<String> sendData = new ArrayList<>();

    private DefaultItemDecoration defaultItemDecoration = DefaultItemDecoration.newLine(Color.GRAY);

    private ReceivedDataRecyclerViewAdapter receivedDataRecyclerViewAdapter = new ReceivedDataRecyclerViewAdapter(receivedData);

    private SendDataRecyclerViewAdapter sendDataRecyclerViewAdapter = new SendDataRecyclerViewAdapter(sendData);

    private ArrayAdapter<CharSequence> baudRateAdapter;

    private TextView nullSerialPortTv;

    private Button openSerialPortBtn, closeSerialPortBtn;

    private ReSpinner serialPortReSpinner, baudRateReSpinner, encodingRespiner;

    private RecyclerView sendDataRecyclerView, receivedDataRecyclerView;

    private EditText commandEt;

    private Button sendBtn;

    private CheckBox hexCb, timeStampCb;

    private HexTextAutoAddEmptyCharInputWatcher hexTextAutoAddEmptyCharInputWatcher;

    /**
     * 点击事件的监听
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.open_serial_port_btn:
                    openSerialPort();
                    break;
                case R.id.close_serial_port_btn:
                    closeSerialPort();
                    break;
                case R.id.send_cmd_btn:
                    sendData();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 串口监听
     */
    private OnSerialPortDataChangedListener onSerialPortDataChangedListener = new OnSerialPortDataChangedListener() {
        @Override
        public void serialPortDataReceived(byte[] data, int size) {
            byte[] cache = new byte[size];
            System.arraycopy(data, 0, cache, 0, size);
            DebugUtil.warnOut(TAG, "cache = " + ConversionUtil.byteArrayToHexStr(cache));
            DebugUtil.warnOut(TAG, "cache = " + new String(cache));
            String result = "";
            if (timeStampCb.isChecked()) {
                result += getTimeStamp() + "\n-------------\n";
            }

            if (hexCb.isChecked()) {
                result += ConversionUtil.byteArrayToHexStr(cache);
            } else {
                String encoding = encodingRespiner.getSelectedItem().toString();
                result += new String(cache, Charset.forName(encoding));
            }
            receivedData.add(result);
            receivedDataRecyclerViewAdapter.notifyItemInserted(receivedData.size() - 1);
            receivedDataRecyclerView.scrollToPosition(receivedData.size() - 1);
        }
    };
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.show_with_hex) {
                if (hexTextAutoAddEmptyCharInputWatcher == null) {
                    hexTextAutoAddEmptyCharInputWatcher = new HexTextAutoAddEmptyCharInputWatcher(commandEt, Integer.MAX_VALUE);
                }
                String s = commandEt.getText().toString();
                if (isChecked) {
                    commandEt.addTextChangedListener(hexTextAutoAddEmptyCharInputWatcher);
                    String encoding = encodingRespiner.getSelectedItem().toString();
                    byte[] bytes = s.getBytes(Charset.forName(encoding));
                    String hexStr = ConversionUtil.byteArrayToHexStr(bytes);
                    commandEt.setText(hexStr);
                    commandEt.setSelection(hexStr.length());
                } else {
                    commandEt.removeTextChangedListener(hexTextAutoAddEmptyCharInputWatcher);
                    byte[] bytes = ConversionUtil.hexStringToByteArray(s);
                    if (bytes != null) {
                        String encoding = encodingRespiner.getSelectedItem().toString();
                        String str = new String(bytes, Charset.forName(encoding));
                        commandEt.setText(str);
                    }
                }
            }
        }
    };


    @Override
    protected void titleBackClicked() {
        onBackPressed();
    }

    @Override
    protected void doBeforeSetLayout() {

    }

    @Override
    protected int setLayout() {
        return R.layout.activity_serial_port_sample;
    }

    @Override
    protected void doBeforeInitOthers() {
    }

    @Override
    protected void initViews() {
        serialPortReSpinner = findViewById(R.id.serial_port_spinner);
        openSerialPortBtn = findViewById(R.id.open_serial_port_btn);
        closeSerialPortBtn = findViewById(R.id.close_serial_port_btn);
        baudRateReSpinner = findViewById(R.id.baud_rate_spinner);
        nullSerialPortTv = findViewById(R.id.null_serial_port);
        sendDataRecyclerView = findViewById(R.id.send_data_recycler_view);
        receivedDataRecyclerView = findViewById(R.id.received_data_recycler_view);
        commandEt = findViewById(R.id.command_et);
        sendBtn = findViewById(R.id.send_cmd_btn);
        hexCb = findViewById(R.id.show_with_hex);
        timeStampCb = findViewById(R.id.show_time_stamp);
        encodingRespiner = findViewById(R.id.encoding_spinner);
    }

    @Override
    protected void initViewData() {
        initReSpinnerData();
        initRecyclerViewData();
    }

    @Override
    protected void initOtherData() {

    }

    @Override
    protected void initEvents() {
        openSerialPortBtn.setOnClickListener(onClickListener);
        closeSerialPortBtn.setOnClickListener(onClickListener);
        sendBtn.setOnClickListener(onClickListener);
        hexCb.setOnCheckedChangeListener(onCheckedChangeListener);
        SerialPortManager.setOnSerialPortDataChangedListener(onSerialPortDataChangedListener);
        sendDataRecyclerViewAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                sendData.clear();
                sendDataRecyclerViewAdapter.notifyDataSetChanged();
                return true;
            }
        });
        receivedDataRecyclerViewAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                receivedData.clear();
                receivedDataRecyclerViewAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    protected void doAfterAll() {
        getSerialPortInfo();
    }

    @Override
    protected boolean createOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected boolean optionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeSerialPort();
    }

    /**
     * 获取串口信息
     */
    private void getSerialPortInfo() {
        String[] allDevices = SerialPortManager.getAllDevices();
        if (allDevices.length == 0) {
            DebugUtil.warnOut(TAG, "allDevices length == 0");
        }
        for (int i = 0; i < allDevices.length; i++) {
            DebugUtil.warnOut(TAG, "allDevices[" + i + "] = " + allDevices[i]);
        }
        String[] allDevicesPath = SerialPortManager.getAllDevicesPath();
        if (allDevicesPath.length == 0) {
            DebugUtil.warnOut(TAG, "allDevicesPath length == 0");
        }
        for (int i = 0; i < allDevicesPath.length; i++) {
            DebugUtil.warnOut(TAG, "allDevicesPath[" + i + "] = " + allDevicesPath[i]);
            serialPortAdapterData.add(allDevicesPath[i]);
        }
        if (serialPortAdapterData.size() == 0) {
            serialPortAdapterData.add(NULL);
            baudRateReSpinner.setVisibility(View.GONE);
            nullSerialPortTv.setVisibility(View.VISIBLE);
            openSerialPortBtn.setClickable(false);
            openSerialPortBtn.setTextColor(Color.GRAY);
            closeSerialPortBtn.setClickable(false);
            closeSerialPortBtn.setTextColor(Color.GRAY);
        }
        serialPortAdapter.notifyDataSetChanged();
        timeStampCb.setChecked(true);
    }

    /**
     * 初始化ReSpinner的数据
     */
    private void initReSpinnerData() {
        serialPortReSpinner.setAdapter(serialPortAdapter);

        baudRateAdapter = ArrayAdapter.createFromResource(this, R.array.baud_rate_array, android.R.layout.simple_list_item_1);
        baudRateReSpinner.setAdapter(baudRateAdapter);

        ArrayAdapter<CharSequence> encodingAdapter = ArrayAdapter.createFromResource(this, R.array.encoding_array, android.R.layout.simple_list_item_1);
        encodingRespiner.setAdapter(encodingAdapter);
    }

    /**
     * 打开串口
     */
    private void openSerialPort() {
        if (SerialPortManager.isOpened()) {
            ToastUtil.toastLong(this, R.string.serial_port_is_opend);
            return;
        }

        String serialPort = serialPortAdapterData.get(serialPortReSpinner.getSelectedItemPosition());
        if (NULL.equals(serialPort)) {
            ToastUtil.toastLong(this, R.string.serial_not_supported);
            return;
        }
        CharSequence baudRateAdaterItem = baudRateAdapter.getItem(baudRateReSpinner.getSelectedItemPosition());
        if (baudRateAdaterItem == null) {
            ToastUtil.toastLong(this, R.string.baud_rate_not_supported);
            return;
        }
        int baudRate;
        try {
            baudRate = Integer.parseInt(baudRateAdaterItem.toString());
        } catch (NumberFormatException e) {
            ToastUtil.toastLong(this, R.string.baud_rate_not_supported);
            return;
        }
        DebugUtil.warnOut(TAG, "baudRate = " + baudRate);
        boolean open = SerialPortManager.openSerialPort(serialPort, baudRate);
        if (open) {
            ToastUtil.toastLong(this, R.string.serial_port_is_opend);
            DebugUtil.warnOut(TAG, "open serial port succeed");
        } else {
            DebugUtil.warnOut(TAG, "open serial port failed");
            ToastUtil.toastLong(this, R.string.serial_port_open_failed);
        }
    }

    private void closeSerialPort() {
        SerialPortManager.closeSerialPort();
    }

    private void initRecyclerViewData() {
        receivedDataRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        receivedDataRecyclerView.addItemDecoration(defaultItemDecoration);
        receivedDataRecyclerViewAdapter.bindToRecyclerView(receivedDataRecyclerView);

        sendDataRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sendDataRecyclerView.addItemDecoration(defaultItemDecoration);
        sendDataRecyclerViewAdapter.bindToRecyclerView(sendDataRecyclerView);
    }

    private synchronized String getTimeStamp() {
        return SIMPLE_DATE_FORMAT.format(System.currentTimeMillis());
    }

    private void sendData() {
        String data = commandEt.getText().toString();
        if (data.isEmpty()) {
            return;
        }
        String encoding = encodingRespiner.getSelectedItem().toString();
        boolean b;
        if (!hexCb.isChecked()) {
            b = SerialPortManager.writeData(data, Charset.forName(encoding));
        } else {
            byte[] bytes = ConversionUtil.hexStringToByteArray(data);
            if (bytes == null) {
                ToastUtil.toastLong(this, R.string.send_failed);
                return;
            }
            b = SerialPortManager.writeData(bytes);
            data = ConversionUtil.byteArrayToHexStr(bytes);
        }
        if (b) {
            sendData.add(data);
            sendDataRecyclerViewAdapter.notifyItemInserted(sendData.size() - 1);
            sendDataRecyclerView.scrollToPosition(sendData.size() - 1);
        } else {
            ToastUtil.toastLong(this, R.string.send_failed);
        }

    }
}
