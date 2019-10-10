package com.jackiepenghe.serialportlibrary;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.ThreadFactory;

/**
 * 串口管理工具
 *
 * @author pengh
 */
public class SerialPortManager {

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r);
        }
    };

    private static final Handler HANDLER = new Handler();

    private static OnSerialPortDataChangedListener onSerialPortDataChangedListener;

    private static InputStream inputStream;

    private static OutputStream outputStream;

    private static Thread receiveDataThread;

    private static SerialPortFinder serialPortFinder = SerialPortFinder.getInstance();

    private static SerialPort serialPort;

    public static void setOnSerialPortDataChangedListener(OnSerialPortDataChangedListener onSerialPortDataChangedListener) {
        SerialPortManager.onSerialPortDataChangedListener = onSerialPortDataChangedListener;
    }

    public static String[] getAllDevices() {
        return serialPortFinder.getAllDevices();
    }

    public static String[] getAllDevicesPath() {
        return serialPortFinder.getAllDevicesPath();
    }

    public static boolean openSerialPort(String serialPortPath, int baudrate) {
        closeSerialPort();
        try {
            serialPort = new SerialPort(new File(serialPortPath), baudrate, 0);
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            startReceiveDataThread();
            return true;
        } catch (IOException e) {
            serialPort.close();
            serialPort = null;
            return false;
        }
    }

    public static void closeSerialPort() {
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
        if (receiveDataThread != null){
            receiveDataThread.interrupt();
            receiveDataThread = null;
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = null;
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }
    }

    public static boolean isOpened() {
        return serialPort != null;
    }

    private static void startReceiveDataThread() {
        if (receiveDataThread != null && receiveDataThread.isAlive()) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                final byte[] buffer = new byte[1024];
                while (true) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    if (inputStream == null) {
                        break;
                    }
                    final int size;
                    try {
                        size = inputStream.read(buffer);
                    } catch (IOException e) {
                        continue;
                    }
                    if (size == 0){
                        continue;
                    }
                    HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            if (onSerialPortDataChangedListener != null) {
                                onSerialPortDataChangedListener.serialPortDataReceived(buffer, size);
                            }
                        }
                    });
                }
            }
        };
        receiveDataThread = THREAD_FACTORY.newThread(runnable);
        receiveDataThread.start();
    }

    private SerialPortManager() {
    }

    public static boolean writeData(String data) {
      return writeData(data,Charset.forName("GBK"));
    }

    public static boolean writeData(String data, Charset charset){
        if (outputStream == null){
            return false;
        }
        try {
            outputStream.write(data.getBytes(charset));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean writeData(byte[] data) {
        if (outputStream == null){
            return false;
        }
        try {
            outputStream.write(data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
