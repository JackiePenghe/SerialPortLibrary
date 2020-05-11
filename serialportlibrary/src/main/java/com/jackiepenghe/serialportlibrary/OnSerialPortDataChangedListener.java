package com.jackiepenghe.serialportlibrary;

/**
 * 串口数据有变化的监听
 *
 * @author pengh
 */
public interface OnSerialPortDataChangedListener {

    /**
     * 接收到串口数据了
     * @param data 串口数据
     * @param size 数据长度
     */
    void serialPortDataReceived(byte[] data, int size);
}
