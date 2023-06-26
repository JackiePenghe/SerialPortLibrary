# SerialPortSample

这是一个安卓关于串口使用的库

this is a library to use serial port for android!

由于个人原因，此仓库已迁移到gitee

[传送门quick jump](https://gitee.com/sscl/SerialPortLibrary)

# 配置(Configure)

```xml
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

```xml
	dependencies {
	        implementation 'com.github.JackiePenghe:SerialPortLibrary:version'
	}
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

如果接收的数据过长，会导致串口接收时分成多条数据。

解决方案：设置串口读取延时。

默认值为100毫秒。
 
If the received data is too long, it will cause the serial port to receive more than one data.

Solution: Set serial read delay.

The default read delay value is 100 ms;

```java
//设置读取数据的延时，单位：毫秒
//set read data delay time. Unit:ms
SerialPortManager.setReadDataDelay(readDelay);
```

### 0.0.2版本新增（0.0.2 version）

MultipleSerialPortManager类。这个类支持同时打开多个串口，并单独对已打开的串口进行操作。用法与SerialPortManager完全一致

Add a new class MultipleSerialPortManager.That class can be open and operation multiple serial port.

### 0.0.3版本修改（0.0.3 version）
使用MultipleSerialPortManager类打开串口时，需要传递一个回调。方便单独监听.

when use MultipleSerialPortManager class to open your serialport,need to set callback!

### 1.0.0版本修改（1.0.0 version）
第一版本正式发布

The first release version
