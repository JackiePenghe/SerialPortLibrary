package com.jackiepenghe.serialportlibrary;

import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
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

    private static OnMultiSerialPortDataChangedListener onMultiSerialPortDataChangedListener;

    private static HashMap<String, InputStream> inputStreams = new HashMap<>();

    private static HashMap<String, OutputStream> outputStreams = new HashMap<>();

    private static Thread receiveDataThread;

    private static SerialPortFinder serialPortFinder = SerialPortFinder.getInstance();

    private static HashMap<String, SerialPort> serialPorts = new HashMap<>();

    public static void setOnMultiSerialPortDataChangedListener(OnMultiSerialPortDataChangedListener onMultiSerialPortDataChangedListener) {
        MultipleSerialPortManager.onMultiSerialPortDataChangedListener = onMultiSerialPortDataChangedListener;
    }

    public static void setReadDataDelay(int readDataDelay) {
        MultipleSerialPortManager.readDataDelay = readDataDelay;
    }

    public static String[] getAllDevices() {
        return serialPortFinder.getAllDevices();
    }

    public static String[] getAllDevicesPath() {
        return serialPortFinder.getAllDevicesPath();
    }

    public static boolean openSerialPort(String serialPortPath, int baudrate) {
        if (serialPorts.containsKey(serialPortPath)) {
            return false;
        }
        try {
            SerialPort serialPort = new SerialPort(new File(serialPortPath), baudrate, 0);
            InputStream inputStream = serialPort.getInputStream();
            inputStreams.put(serialPortPath, inputStream);
            OutputStream outputStream = serialPort.getOutputStream();
            outputStreams.put(serialPortPath, outputStream);
            startReceiveDataThread();
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
        Iterator<Map.Entry<String, SerialPort>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SerialPort> next = iterator.next();
            String key = next.getKey();
            closeSerialPort(key, false);
        }
        serialPorts.clear();
        inputStreams.clear();
        outputStreams.clear();
        if (receiveDataThread != null) {
            receiveDataThread.interrupt();
            receiveDataThread = null;
        }
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
            if (serialPorts.isEmpty()) {
                if (receiveDataThread != null) {
                    receiveDataThread.interrupt();
                    receiveDataThread = null;
                }
            }
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
                    if (buffer.length != serialPortCacheDataSize) {
                        buffer = new byte[serialPortCacheDataSize];
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    if (inputStreams.isEmpty()) {
                        break;
                    }
                    SystemClock.sleep(readDataDelay);
                    Set<Map.Entry<String, InputStream>> entries = inputStreams.entrySet();
                    for (Map.Entry<String, InputStream> entry : entries) {
                        int available;
                        final int size;
                        final String key = entry.getKey();
                        InputStream value = entry.getValue();
                        try {
                            available = value.available();
                            size = value.read(buffer, 0, available);
                        } catch (IOException e) {
                            e.printStackTrace();
                            continue;
                        }
                        if (size == 0) {
                            continue;
                        }
                        final byte[] finalBuffer = buffer;
                        HANDLER.post(new Runnable() {
                            @Override
                            public void run() {
                                if (onMultiSerialPortDataChangedListener != null) {
                                    onMultiSerialPortDataChangedListener.serialPortDataReceived(key, finalBuffer, size);
                                }
                            }
                        });
                    }
                    SystemClock.sleep(50);
                }
            }
        };
        receiveDataThread = THREAD_FACTORY.newThread(runnable);
        receiveDataThread.start();
    }

    private MultipleSerialPortManager() {
    }
}
