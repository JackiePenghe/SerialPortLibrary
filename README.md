# SerialPortSample

这是一个安卓关于串口使用的库

this is a library to use serial port for android!

# 配置(Configure)

```xml
implementation 'com.sscl.serialportlibrary:x:0.0.1'
```

# 使用（use）

## 获取串口信息(get seriral port information)

```java
    /**
     * 获取串口信息
     */
    private void getSerialPortInfo() {
        String[] allDevices = SerialPortManager.getAllDevices();
        
        String[] allDevicesPath = SerialPortManager.getAllDevicesPath();
       
    }
```

## 设置串口监听（set listener）

```java
 SerialPortManager.setOnSerialPortDataChangedListener(onSerialPortDataChangedListener);
```

## 打开串口（open serial port）

```java
private void openSerialPort() {
  if (SerialPortManager.isOpened()) {
    ToastUtil.toastL(this, R.string.serial_port_is_opend);
    return;
  }
  DebugUtil.warnOut(TAG, "baudRate = " + baudRate);
  boolean open = SerialPortManager.openSerialPort(serialPort, baudRate);
  if (open) {
      ToastUtil.toastL(this, R.string.serial_port_is_opend);
  } else {
      ToastUtil.toastL(this, R.string.serial_port_open_failed);
  }
}
```

## 关闭串口

```java
SerialPortManager.closeSerialPort();
```
