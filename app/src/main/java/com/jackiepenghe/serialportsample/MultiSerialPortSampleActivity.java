package com.jackiepenghe.serialportsample;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.jackiepenghe.serialportlibrary.MultipleSerialPortManager;
import com.jackiepenghe.serialportlibrary.OnSerialPortDataChangedListener;
import com.sscl.baselibrary.activity.BaseAppCompatActivity;
import com.sscl.baselibrary.utils.ConversionUtil;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.ToastUtil;

import java.util.logging.Handler;

/**
 * @author jackie
 */
public class MultiSerialPortSampleActivity extends BaseAppCompatActivity {

    private static final String TAG = MultiSerialPortSampleActivity.class.getSimpleName();

    /**
     * 标题栏的返回按钮被按下的时候回调此方法
     */
    @Override
    protected void titleBackClicked() {
        onBackPressed();
    }

    /**
     * 在设置布局之前需要进行的操作
     */
    @Override
    protected void doBeforeSetLayout() {

    }

    /**
     * 设置布局
     *
     * @return 布局id
     */
    @Override
    protected int setLayout() {
        return R.layout.activity_multi_serial_port_sample;
    }

    /**
     * 在设置布局之后，进行其他操作之前，所需要初始化的数据
     */
    @Override
    protected void doBeforeInitOthers() {

    }

    /**
     * 初始化布局控件
     */
    @Override
    protected void initViews() {

    }

    /**
     * 初始化控件数据
     */
    @Override
    protected void initViewData() {

    }

    /**
     * 初始化其他数据
     */
    @Override
    protected void initOtherData() {

    }

    /**
     * 初始化事件
     */
    @Override
    protected void initEvents() {

    }

    /**
     * 在最后进行的操作
     */
    @Override
    protected void doAfterAll() {
        MultipleSerialPortManager.setSerialPortCacheDataSize(40);
        boolean b = MultipleSerialPortManager.openSerialPort("/dev/ttyS1", 115200, new OnSerialPortDataChangedListener() {
            @Override
            public void serialPortDataReceived(byte[] data, int size) {
                DebugUtil.warnOut(TAG, "serialPortDataReceived ttyS1");
                byte[] validData = new byte[size];
                System.arraycopy(data, 0, validData, 0, size);
                DebugUtil.warnOut(TAG,"data = " + ConversionUtil.byteArrayToHexStr(validData));
                String receivedData = new String(validData);
                DebugUtil.warnOut(TAG,"data = " + receivedData);
                ToastUtil.toast(MultiSerialPortSampleActivity.this, receivedData, 300);
            }
        });
        if (b) {
            DebugUtil.warnOut(TAG, "ttyS1 opened");
        }
        b = MultipleSerialPortManager.openSerialPort("/dev/ttyS3", 115200, new OnSerialPortDataChangedListener() {
            @Override
            public void serialPortDataReceived(byte[] data, int size) {
                DebugUtil.warnOut(TAG, "serialPortDataReceived ttyS3");
                byte[] validData = new byte[size];
                System.arraycopy(data, 0, validData, 0, size);
                String receivedData = new String(validData);
                ToastUtil.toast(MultiSerialPortSampleActivity.this, receivedData, 300);
            }
        });
        if (b) {
            DebugUtil.warnOut(TAG, "ttyS3 opened");
        }
        b = MultipleSerialPortManager.openSerialPort("/dev/ttyS4", 115200, new OnSerialPortDataChangedListener() {
            @Override
            public void serialPortDataReceived(byte[] data, int size) {
                DebugUtil.warnOut(TAG, "serialPortDataReceived ttyS4");
                byte[] validData = new byte[size];
                System.arraycopy(data, 0, validData, 0, size);
                String receivedData = new String(validData);
                ToastUtil.toast(MultiSerialPortSampleActivity.this, receivedData, 300);
            }
        });
        if (b) {
            DebugUtil.warnOut(TAG, "ttyS4 opened");
        }
    }

    private void writeS1Data() {
        DebugUtil.warnOut(TAG, "writeS1Data");
        MultipleSerialPortManager.writeData("/dev/ttyS1", "test data");
    }

    private void writeS3Data() {
        DebugUtil.warnOut(TAG, "writeS3Data");
        MultipleSerialPortManager.writeData("/dev/ttyS3", "test data");
    }

    /**
     * 设置菜单
     *
     * @param menu 菜单
     * @return 只是重写 public boolean onCreateOptionsMenu(Menu menu)
     */
    @Override
    protected boolean createOptionsMenu(@NonNull Menu menu) {
        return false;
    }

    /**
     * 设置菜单监听
     *
     * @param item 菜单的item
     * @return true表示处理了监听事件
     */
    @Override
    protected boolean optionsItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    protected void onDestroy() {
        MultipleSerialPortManager.closeAll();
        super.onDestroy();
    }

    public void write1(View view) {
        writeS1Data();
    }

    public void write3(View view) {
        writeS3Data();
    }
}