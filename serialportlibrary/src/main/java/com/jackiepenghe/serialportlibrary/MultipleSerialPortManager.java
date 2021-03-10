package com.jackiepenghe.serialportlibrary;

import android.os.SystemClock;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.jackiepenghe.serialportlibrary.Constants.HANDLER;
import static com.jackiepenghe.serialportlibrary.Constants.THREAD_FACTORY;

/**
 * 多串口同时使用时的管理工具
 *
 * @author pengh
 */
public class MultipleSerialPortManager {

    private static int serialPortCacheDataSize = 1024;

    private static int readDataDelay = 100;

    private static HashMap<String, InputStream> inputStreams = new HashMap<>();

    private static HashMap<String, OutputStream> outputStreams = new HashMap<>();

    private static HashMap<String, Thread> receiveDataThreads = new HashMap<>();

    private static SerialPortFinder serialPortFinder = SerialPortFinder.getInstance();

    private static HashMap<String, SerialPort> serialPorts = new HashMap<>();

    private static HashMap<String, OnSerialPortDataChangedListener> onSerialPortDataChangedListeners = new HashMap<>();

    public static void setReadDataDelay(int readDataDelay) {
        MultipleSerialPortManager.readDataDelay = readDataDelay;
    }

    public static String[] getAllDevices() {
        return serialPortFinder.getAllDevices();
    }

    public static String[] getAllDevicesPath() {
        return serialPortFinder.getAllDevicesPath();
    }

    public static boolean openSerialPort(String serialPortPath, int baudrate, @Nullable OnSerialPortDataChangedListener onSerialPortDataChangedListener) {
        if (serialPorts.containsKey(serialPortPath)) {
            return false;
        }
        try {
            SerialPort serialPort = new SerialPort(new File(serialPortPath), baudrate, 0);
            InputStream inputStream = serialPort.getInputStream();
            inputStreams.put(serialPortPath, inputStream);
            OutputStream outputStream = serialPort.getOutputStream();
            outputStreams.put(serialPortPath, outputStream);
            startReceiveDataThread(serialPortPath);
            onSerialPortDataChangedListeners.put(serialPortPath, onSerialPortDataChangedListener);
            return true;
        } catch (IOException | SecurityException e) {
            closeSerialPort(serialPortPath);
            return false;
        }
    }

    public static void closeSerialPort(String serialPortPath) {
        closeSerialPort(serialPortPath, true);
    }

    public static void closeAll() {
        if (serialPorts.isEmpty()) {
            return;
        }
        Set<Map.Entry<String, SerialPort>> entries = serialPorts.entrySet();
        for (Map.Entry<String, SerialPort> next : entries) {
            String key = next.getKey();
            closeSerialPort(key, false);
        }

        serialPorts.clear();
        inputStreams.clear();
        outputStreams.clear();
        receiveDataThreads.clear();
    }

    private static void closeSerialPort(String serialPortPath, boolean needRemove) {
        if (!serialPorts.containsKey(serialPortPath)) {
            return;
        }
        SerialPort serialPort = serialPorts.get(serialPortPath);
        if (serialPort != null) {
            serialPort.close();
            if (needRemove) {
                serialPorts.remove(serialPortPath);
            }
        }
        InputStream inputStream = inputStreams.get(serialPortPath);
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (needRemove) {
                inputStreams.remove(serialPortPath);
            }
        }
        OutputStream outputStream = outputStreams.get(serialPortPath);
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (needRemove) {
                outputStreams.remove(serialPortPath);
            }
        }
        if (needRemove) {
            receiveDataThreads.remove(serialPortPath);
        }

    }

    public static void setSerialPortCacheDataSize(int size) {
        MultipleSerialPortManager.serialPortCacheDataSize = size;
    }

    public static boolean isOpened(String serialPortPath) {
        return serialPorts.containsKey(serialPortPath);
    }

    public static boolean writeData(String serialPortPath, String data) {
        return writeData(serialPortPath, data, Charset.forName("GBK"));
    }

    public static boolean writeData(String serialPortPath, String data, Charset charset) {
        if (!outputStreams.containsKey(serialPortPath)) {
            return false;
        }
        OutputStream outputStream = outputStreams.get(serialPortPath);
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

    public static boolean writeData(String serialPortPath, byte[] data) {
        if (!outputStreams.containsKey(serialPortPath)) {
            return false;
        }
        OutputStream outputStream = outputStreams.get(serialPortPath);
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

    private static void startReceiveDataThread(final String serialPortPath) {
        if (receiveDataThreads.containsKey(serialPortPath)) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[serialPortCacheDataSize];
                while (true) {
                    if (buffer.length != serialPortCacheDataSize) {
                        buffer = new byte[serialPortCacheDataSize];
                    }
                    Arrays.fill(buffer, (byte) 0);
                    if (Thread.currentThread().isInterrupted()) {
                        receiveDataThreads.remove(serialPortPath);
                        break;
                    }
                    if (inputStreams.isEmpty()) {
                        receiveDataThreads.remove(serialPortPath);
                        break;
                    }
                    InputStream inputStream = inputStreams.get(serialPortPath);
                    if (inputStream == null) {
                        receiveDataThreads.remove(serialPortPath);
                        break;
                    }
                    SystemClock.sleep(readDataDelay);
                    int available;
                    final int size;
                    try {
                        available = inputStream.available();
                        size = inputStream.read(buffer, 0, available);
                    } catch (IOException e) {
                        continue;
                    }
                    if (size == 0) {
                        continue;
                    }
                    final byte[] finalBuffer = new byte[size];
                    System.arraycopy(buffer, 0, finalBuffer, 0, size);

                    HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            OnSerialPortDataChangedListener onSerialPortDataChangedListener = onSerialPortDataChangedListeners.get(serialPortPath);
                            if (onSerialPortDataChangedListener != null) {
                                onSerialPortDataChangedListener.serialPortDataReceived(finalBuffer, size);
                            }
                        }
                    });
                }
            }
        };
        Thread receiveDataThread = THREAD_FACTORY.newThread(runnable);
        receiveDataThread.start();
        receiveDataThreads.put(serialPortPath, receiveDataThread);
    }

    private MultipleSerialPortManager() {
    }
}
