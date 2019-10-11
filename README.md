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

//当收到串口数据时，会触发回调。
//callback will be triggered while received data.

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
  boolean open = SerialPortManager.openSerialPort(serialPortPath, baudRate);
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

## 向串口写数据（write data to serial port）

```java
boolean succeed = SerialPortManager.writeData(data);

if (!succeed){
     ToastUtil.toastL(this, R.string.write_data_failed);
}else{
    ToastUtil.toastL(this, R.string.write_data_succeed);
}
```

## 注意（notice）

如果接收的数据过长，导致串口接收变成条数据时，可以设置串口延时，这样就不会被强制分成几次触发。默认值为100毫秒。
 
If the received data is too long, causing the serial port to receive a piece of data, the serial delay can be set, so that it will not be forced to divide into several triggered.The default read delay value is 100 ms;

```java
//设置读取数据的延时，单位：毫秒
//set read data delay time. Unit:ms
SerialPortManager.setReadDataDelay(readDelay);
```
 
