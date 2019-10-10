package com.jackiepenghe.serialportsample;

import android.graphics.Color;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jackiepenghe.serialportlibrary.OnSerialPortDataChangedListener;
import com.jackiepenghe.serialportlibrary.SerialPortManager;
import com.jackiepenghe.serialportsample.adapter.SerialPortAdapter;
import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.utils.ConversionUtil;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.baselibrary.view.ReSpinner;

import java.util.ArrayList;

public class MainActivity extends BaseAppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
  /*  static {
        System.loadLibrary("native-lib");
    }*/

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

//        // Example of a call to a native method
//        TextView tv = findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
//    }

//    /**
//     * A native method that is implemented by the 'native-lib' native library,
//     * which is packaged with this application.
//     */
//    public native String stringFromJNI();

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String NULL = "NULL";

    private ArrayList<String> serialPortAdapterData = new ArrayList<>();

    private SerialPortAdapter serialPortAdapter = new SerialPortAdapter(serialPortAdapterData);

    private ArrayAdapter<CharSequence> baudRateAdapter;

    private TextView nullSerialPortTv;

    private Button openSerialPortBtn, closeSerialPortBtn;

    private ReSpinner serialPortReSpinner, baudRateReSpinner;

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
            DebugUtil.warnOut(TAG, "serialPortDataReceived cache = " + ConversionUtil.bytesToHexStr(cache));
            DebugUtil.warnOut(TAG, "serialPortDataReceived cacheStr = " + new String(cache));
            SerialPortManager.writeData(cache);
        }
    };
    private int count;

    private int MAX_COUNT = 50;


    @Override
    protected void titleBackClicked() {

    }

    @Override
    protected void doBeforeSetLayout() {

    }

    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void doBeforeInitOthers() {
        hideTitleBar();
    }

    @Override
    protected void initViews() {
        serialPortReSpinner = findViewById(R.id.serial_port_spinner);
        openSerialPortBtn = findViewById(R.id.open_serial_port_btn);
        closeSerialPortBtn = findViewById(R.id.close_serial_port_btn);
        baudRateReSpinner = findViewById(R.id.baud_rate_spinner);
        nullSerialPortTv = findViewById(R.id.null_serial_port);
    }

    @Override
    protected void initViewData() {
        initReSpinnerData();
    }

    @Override
    protected void initOtherData() {

    }

    @Override
    protected void initEvents() {
        openSerialPortBtn.setOnClickListener(onClickListener);
        SerialPortManager.setOnSerialPortDataChangedListener(onSerialPortDataChangedListener);
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
        int size = serialPortAdapterData.size();
        serialPortAdapter.notifyDataSetChanged();
        if (size == 1) {
            String s = serialPortAdapterData.get(0);
            if (!NULL.equals(s)) {
                openSerialPort();
            }
        } else if (size > 3) {
            serialPortReSpinner.setSelection(4);
            baudRateReSpinner.setSelection(13);
            openSerialPort();
        }
    }

    /**
     * 初始化ReSpinner的数据
     */
    private void initReSpinnerData() {
        serialPortReSpinner.setAdapter(serialPortAdapter);
        baudRateAdapter = ArrayAdapter.createFromResource(this, R.array.baud_rate_array, android.R.layout.simple_list_item_1);
        baudRateReSpinner.setAdapter(baudRateAdapter);
    }

    /**
     * 打开串口
     */
    private void openSerialPort() {
        if (SerialPortManager.isOpened()) {
            SerialPortManager.closeSerialPort();
        }

        String serialPort = serialPortAdapterData.get(serialPortReSpinner.getSelectedItemPosition());
        if (NULL.equals(serialPort)) {
            ToastUtil.toastL(this, R.string.serial_not_supported);
            return;
        }
        CharSequence baudRateAdaterItem = baudRateAdapter.getItem(baudRateReSpinner.getSelectedItemPosition());
        if (baudRateAdaterItem == null) {
            ToastUtil.toastL(this, R.string.baud_rate_not_supported);
            return;
        }
        int baudRate;
        try {
            baudRate = Integer.valueOf(baudRateAdaterItem.toString());
        } catch (NumberFormatException e) {
            ToastUtil.toastL(this, R.string.baud_rate_not_supported);
            return;
        }
        DebugUtil.warnOut(TAG, "baudRate = " + baudRate);
        boolean open = SerialPortManager.openSerialPort(serialPort, baudRate);
        if (open) {
            DebugUtil.warnOut(TAG, "open serial port succeed");
        } else {
            DebugUtil.warnOut(TAG, "open serial port failed");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (count < MAX_COUNT){
                    SerialPortManager.writeData("测试数据：" + count);
                    count++;
                    SystemClock.sleep(1000);
                }
            }
        }).start();
    }

    private void closeSerialPort() {
        SerialPortManager.closeSerialPort();
    }
}
