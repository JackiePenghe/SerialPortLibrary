package com.jackiepenghe.serialportsample;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.jackiepenghe.serialportlibrary.MultipleSerialPortManager;
import com.jackiepenghe.serialportlibrary.OnMultiSerialPortDataChangedListener;
import com.sscl.baselibrary.activity.BaseAppCompatActivity;

/**
 * @author jackie
 */
public class MultiSerialPortSampleActivity extends BaseAppCompatActivity {

    /**
     * 标题栏的返回按钮被按下的时候回调此方法
     */
    @Override
    protected void titleBackClicked() {

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
        String[] allDevicesPath = MultipleSerialPortManager.getAllDevicesPath();
        boolean b = MultipleSerialPortManager.openSerialPort(allDevicesPath[0], 115200);
        MultipleSerialPortManager.setOnMultiSerialPortDataChangedListener(new OnMultiSerialPortDataChangedListener() {
            @Override
            public void serialPortDataReceived(String serialPortPath, byte[] data, int size) {

            }
        });
        MultipleSerialPortManager.writeData(allDevicesPath[0],"test data");

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
}