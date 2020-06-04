package com.jackiepenghe.serialportlibrary;

/**
 * 串口数据有变化的监听
 *
 * @author pengh
 */
public interface OnMultiSerialPortDataChangedListener {

    /**
     * 接收到串口数据了
     *
     * @param serialPortPath 串口路径
     * @param data           串口数据
     * @param size           数据长度
     */
    void serialPortDataReceived(String serialPortPath, byte[] data, int size);
}
