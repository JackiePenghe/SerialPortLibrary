package com.jackiepenghe.serialportlibrary;

import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DebugUtils;
import android.util.Log;

import androidx.annotation.NonNull;

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
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r);
        }
    };

    private static final Handler HANDLER = new Handler();

    private static int serialPortCacheDataSize = 1024;

    private static int readDataDelay = 100;

    private static OnSerialPortDataChangedListener onSerialPortDataChangedListener;

    private static InputStream inputStream;

    private static OutputStream outputStream;

    private static Thread receiveDataThread;

    private static SerialPortFinder serialPortFinder = SerialPortFinder.getInstance();

    private static SerialPort serialPort;

    public static void setOnSerialPortDataChangedListener(OnSerialPortDataChangedListener onSerialPortDataChangedListener) {
        SerialPortManager.onSerialPortDataChangedListener = onSerialPortDataChangedListener;
    }

    public static void setReadDataDelay(int readDataDelay) {
        SerialPortManager.readDataDelay = readDataDelay;
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
        } catch (IOException | SecurityException e) {
            closeSerialPort();
            return false;
        }
    }

    public static void closeSerialPort() {
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
        if (receiveDataThread != null) {
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

    public static void setSerialPortCacheDataSize(int size){
        SerialPortManager.serialPortCacheDataSize = size;
    }

    public static boolean isOpened() {
        return serialPort != null;
    }

    public static boolean writeData(String data) {
        return writeData(data, Charset.forName("GBK"));
    }

    public static boolean writeData(String data, Charset charset) {
        if (outputStream == null) {
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
        if (outputStream == null) {
            return false;
        }
        try {
            outputStream.write(data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static void startReceiveDataThread() {
        if (receiveDataThread != null && receiveDataThread.isAlive()) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                byte[] buffer = new byte[serialPortCacheDataSize];
                while (true) {
                    if (buffer.length != serialPortCacheDataSize){
                        buffer = new byte[serialPortCacheDataSize];
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    if (inputStream == null) {
                        break;
                    }
                    SystemClock.sleep(readDataDelay);
                    int available;
                    final int size;
                    try {
                        available = inputStream.available();
                        size = inputStream.read(buffer,0,available);
                    } catch (Exception e) {
                        continue;
                    }
                    if (size == 0) {
                        continue;
                    }
                    Log.w("tag","available = " + available);
                    Log.w("tag","size = " + size);
                    final byte[] finalBuffer = buffer;
                    HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            if (onSerialPortDataChangedListener != null) {
                                onSerialPortDataChangedListener.serialPortDataReceived(finalBuffer, size);
                            }
                        }
                    });
                    SystemClock.sleep(50);
                }
            }
        };
        receiveDataThread = THREAD_FACTORY.newThread(runnable);
        receiveDataThread.start();
    }

    private SerialPortManager() {
    }
}
