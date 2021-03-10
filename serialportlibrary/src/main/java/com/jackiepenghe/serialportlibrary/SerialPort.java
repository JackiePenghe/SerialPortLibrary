/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jackiepenghe.serialportlibrary;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 串口类
 *
 * @author pengh
 */
class SerialPort {

    private static final String TAG = SerialPort.class.getSimpleName();

    /**
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    @SuppressWarnings("FieldCanBeLocal")
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
        if (!device.exists()) {
            throw new IOException("target serialport not exists");
        }
        /* Check access permission */
        if (!device.canRead() || !device.canWrite()) {
            if (hasRoot()) {
                Process process = null;
                DataOutputStream os = null;
                boolean result;
                try {
                    String cmd = "chmod 666 " + device.getAbsolutePath();
                    //切换到root帐号
                    process = Runtime.getRuntime().exec("su");
                    os = new DataOutputStream(process.getOutputStream());
                    os.writeBytes(cmd + "\n");
                    os.writeBytes("exit\n");
                    os.flush();
                    process.waitFor();
                    result = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    result = false;
                } finally {
                    try {
                        if (os != null) {
                            os.close();
                        }
                        if (process != null) {
                            process.destroy();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!result) {
                    throw new SecurityException();
                }
                if (!device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            }
        }

        mFd = open(device.getAbsolutePath(), baudrate, flags);
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    InputStream getInputStream() {
        return mFileInputStream;
    }

    OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    /**
     * JNI方法，打开串口
     *
     * @param path     串口路径
     * @param baudrate 波特率
     * @param flags    flag
     * @return FileDescriptor
     */
    private native static FileDescriptor open(String path, int baudrate, int flags);

    /**
     * JNI 方法，关闭串口
     */
    public native void close();

    private boolean hasRoot() {
        Process process = null;
        DataOutputStream os = null;
        boolean result;
        try {
            Log.i("roottest", "try it");
            String cmd = "touch /data/roottest.txt";
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            result = true;
        } catch (Exception e) {
            result = false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    static {
        System.loadLibrary("SerialPort");
    }
}
