# BluetoothService：经典蓝牙服务（双向）

#### 蓝牙服务管理类，通过这些辅助的服务可以方便快捷的使用蓝牙发送数据和接收数据，这套框架仅适用于**经典蓝牙**，属于双向蓝牙，能够同时发送数据和接收数据。

![](https://github.com/YuQianhao/BluetoothService/blob/master/a.jpg)

#### 一、依赖

![](https://jitpack.io/v/YuQianhao/BluetoothService.svg)

#### 1、Gradle

```xml
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
```

```xml
dependencies {
    implementation 'com.github.YuQianhao:BluetoothService:1.0.0'
}
```

#### 2、Maven

```xml
<repositories>
    <repository>
    <id>jitpack.io</id>
	<url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
	 <groupId>com.github.YuQianhao</groupId>
	 <artifactId>BluetoothService</artifactId>
	 <version>1.0.0</version>
</dependency>
```

#### 二、使用方式

##### 1、权限申请

想要使用蓝牙服务首先需要申请和蓝牙相关的权限，例如在AndroidManifest清单文件里声明需要使用的权限：

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="你的包名">
    
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

</manifest>
```

其中

```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```

蓝牙权限和蓝牙管理权限只需要在AndroidManifest文件中声明即可，如果API在Android6.0+的情况下，需要声明

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```

权限并在运行时动态的申请权限，通过使用**ContextCompat.checkSelfPermission()**方法来检查权限，如果未获得权限就使用**ActivityCompat.requestPermissions**来申请权限，最终申请结果会在Activity的**onRequestPermissionsResult**方法中获取出来。

##### 2、服务声明

​	因为蓝牙辅助是通过服务的形式提供，所以需要在AndroidManifest清单文件中声明这个服务：

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="你的包名">
    
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <service android:name="com.yuqianhao.support.bluetooth.BluetoothService"
            android:process=":bluetooth"/>

    </application>
    
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

</manifest>
```

要注意的是，声明服务的时候启动了一个子进程去创建和执行这个Service，因为**蓝牙发送数据和接收数据都各占一个子线程，而且还是常驻线程，如果不启动子进程去维护这两个线程name会消耗主进程的线程资源，所有在这里使用了子进程去创建维护这个服务**。

##### 3、服务绑定

​	BluetoothService使用服务提供蓝牙相关的操作，所以如果想要在Activity中使用蓝牙，需要将Service和Activity绑定到一起，和普通的服务绑定没有区别，例如：

```java
public class BluetoothActivity extends Activity{
    
    private IBluetoothController iBluetoothController;
    
    BluetoothServiceConnected serviceConnection=new BluetoothServiceConnected() {
        @Override
        protected void onConnected(IBluetoothController iBluetoothController) {
            bluetoothController=iBluetoothController;
            print("[BluetoothService]服务绑定成功");
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        bindService(new Intent(this,BluetoothService.class),serviceConnection,BIND_AUTO_CREATE);
    }
}
```

在这里使用正常的**bindService**的方式绑定**BluetoothService**，其中**BluetoothServiceConnected**继承自**ServiceConnected**，所以可以直接在**bindService**中使用，通过实现**BluetoothServiceConnected**的子类后重载**onConnected(IBluetoothController iBluetoothController)**方法，当服务绑定成功之后，这个方法会被回调并将**IBluetoothController**实例传过来。

* IBluetoothController接口使用来控制BluetoothService和操作蓝牙设备的，定义如下：

```java
interface IBluetoothController {
    //判断蓝牙是否开启
    boolean isEnabled();
    //静默开启蓝牙
    void enabled();
    //获取本地蓝牙名称
    void getName();
    //获取已经配对的蓝牙设备
    List<BluetoothDevice> getBondedDevices();
    //配对蓝牙设备
    void pair(in BluetoothDevice bluetoothDevice);
    //连接蓝牙
    void connectBluetooth(in BluetoothDevice bluetoothDevice,in String uuid);
    //断开蓝牙连接
    void disconnectBluetooth(in BluetoothDevice bluetoothDevice);
    //开始扫描
    void startScan();
    //结束扫描
    void cancelScan();
    //注册蓝牙状态监听器
    void registerBluetoothChangeListener(in IBluetoothStateChange change);
    //监听蓝牙收到的数据
    void listenSocket(in String name,in String uuid,in IBluetoothListenSocket socket);
    //关闭监听
    void dislistenSocket();
    //发送数据
    void send(in byte[] data);
}
```

##### 4、注册回调接口

​	所有关于蓝牙状态改变的，例如扫描到蓝牙设备，配对成功，或者连接蓝牙成功都通过一个叫**IBluetoothStateChange**的回调接口回传给Activity，当服务绑定成功之后可以调用方法**registerBluetoothChangeListener**将这个回调接口绑定在BluetoothService中，例如：

```java
private IBluetoothStateChange bluetoothStateChange=new IBluetoothStateChange(){...};

BluetoothServiceConnected serviceConnection=new BluetoothServiceConnected() {
        @Override
        protected void onConnected(IBluetoothController iBluetoothController) {
            print("[BluetoothService]服务绑定成功");
            //注册状态改变监听器
            iBluetoothController.registerBluetoothChangeListener(bluetoothStateChange);
        }
 };
```

* IBluetoothStateChange的定义如下：

```java
//这个接口定义了蓝牙从扫描到配对的所有状态改变的监听回调方法
interface IBluetoothStateChange {
    //蓝牙扫描开始
    void onScanningStart();
    //蓝牙扫描结束
    void onScanningComplete();
    //扫描到蓝牙的通知
    void onScanningBluetoothDevice(in BluetoothDevice bluetoothDevice);
    //已经配对成功的蓝牙
    void onBondedDevice(in List<BluetoothDevice> bluetoothDevice);
    //配对成功
    void onPairSuccess(in BluetoothDevice bluetoothDevice);
    //正在配对中
    void onPairing(in BluetoothDevice bluetoothDevice);
    //配对失败
    void onPairFailure(in BluetoothDevice bluetoothDevice);
    //连接成功
    void onConnectSuccess();
    //连接失败
    void onConnectFailure(in String msg);
    //连接关闭
    void onConnectClose();
    //监听成功
    void onListenSuccess();
    //监听失败
    void onListenFailure(in String msg);
    //监听关闭
    void onListenClose();
    //蓝牙发生异常
    void onBluetoothError(in int errno);
}
```

当然，并不是让你把这些方法全部重写了，如果去 **new  IBluetoothStateChange()**那么一定意味着你需要去重写这个接口中的所有方法，但是为了使代码整洁，定义了一个名叫**AbsBluetoothStateChange**的类实现了这个接口，这就意味着在任何需要**IBluetoothStateChange**接口的地方都可以使用**AbsBluetoothStateChange**来代替，而且只需要重写对自己有用的方法即可，例如：

```java
private IBluetoothStateChange bluetoothStateChange=new AbsBluetoothStateChange(){
	@Override
    public void onScanningStart(){
        //扫描蓝牙开始
    }
};

BluetoothServiceConnected serviceConnection=new BluetoothServiceConnected() {
    @Override 
    protected void onConnected(IBluetoothController iBluetoothController) {
        print("[BluetoothService]服务绑定成功");
        //注册状态改变监听器
        iBluetoothController.registerBluetoothChangeListener(bluetoothStateChange);
    }
 };
```

##### 5、蓝牙是否开启

​	在开始蓝牙业务之前需要判断一下蓝牙是否开启，可以通过**IBluetoothController**的**isEnabled**方法来判断蓝牙是否开启， 如果为true则开启，否则需要主动开启蓝牙，有两种方式：

①使用静默开启：

```java
iBluetoothController.enabled();
```

②显示的开启：

```java
Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
```

##### 6、蓝牙扫描

​	在连接蓝牙之前，需要进行扫描蓝牙，可以使用**IBluetoothController**的 **startScan**方法开启扫描，使用**cancelScan**来关闭扫描，当调用这个方法的时候，Service开始搜索蓝牙，如果搜索到蓝牙设备，就会通过回调接口**IBluetoothStateChange**回传结果，其中和扫描蓝牙相关的回调方法如下：

```java
//蓝牙扫描开始
void onScanningStart();
//蓝牙扫描结束
void onScanningComplete();
//扫描到蓝牙的通知
void onScanningBluetoothDevice(in BluetoothDevice bluetoothDevice);
//已经配对成功的蓝牙
void onBondedDevice(in List<BluetoothDevice> bluetoothDevice);
```

当开始扫描蓝牙的时候会回调**onScanningStart**方法，当扫描到蓝牙设备的时候会将扫描到的蓝牙设备通过**onScanningBluetoothDevice**方法的参数回传道客户端，值得注意的是，**每扫描到一个设备就会回调一次，假如有多个设备，这个方法就会被调用多次**，当扫描结束的时候会回调**onScanningComplete**方法，当开始扫描的时候，BluetoothService会立即将已经配对成功的蓝牙设备列表通过**onBondedDevice**方法回传。

* 如果想要立即获取已经配对成功的蓝牙列表，可以通过**IBluetoothController**的**getBondedDevices**的方法的到一个**List< BluetoothDevice >**列表，这个列表代表了已经配对成功的蓝牙设备列表。

##### 7、蓝牙配对

​	当扫描到蓝牙设备之后需要进行设备的配对，可以使用**IBluetoothController**的**pair**方法进行蓝牙配对，是否配对成功以及配对状态也是通过回调接口**IBluetoothStateChange**回传结果，其中和配对相关的回调如下：

```java
//配对成功
void onPairSuccess(in BluetoothDevice bluetoothDevice);
//正在配对中
void onPairing(in BluetoothDevice bluetoothDevice);
//配对失败
void onPairFailure(in BluetoothDevice bluetoothDevice);
```

不管是配对成功还是正在配对，还是配对失败，都会回传当时状态下的蓝牙设备，例如：

```java
void onScanningBluetoothDevice(in BluetoothDevice bluetoothDevice){
    //扫描到蓝牙设备
    if(bluetoothDevice.getName().equals("TestBluetoothDevice")){
        //停止扫描
        iBluetoothController.cancelScan();
        //配对这个设备
        iBluetoothController.pair(bluetoothDevice);
    }
}
...省略代码...
//配对成功
void onPairSuccess(in BluetoothDevice bluetoothDevice){
    //意味着bluetoothDevice这个设备配对成功
}
```

##### 8、连接蓝牙，断开连接/监听蓝牙，断开监听

​	如果需要向目标蓝牙发送数据，就需要连接到目标蓝牙，这是在客户端的方式，如果是在服务端就需要监听蓝牙，监听蓝牙端口对方发送过来的数据。

​	相关API：

```java
//连接蓝牙
void connectBluetooth(in BluetoothDevice bluetoothDevice,in String uuid);
//断开蓝牙连接
void disconnectBluetooth(in BluetoothDevice bluetoothDevice);
//监听蓝牙收到的数据
void listenSocket(in String name,in String uuid,in IBluetoothListenSocket socket);
//关闭监听
void dislistenSocket();
```

​	如果本机app既想发送数据也想接收数据，那么在连接蓝牙的同时在监听即可，例如：

```java
//配对成功
void onPairSuccess(in BluetoothDevice bluetoothDevice){
    iBluetoothController.connectBluetooth(bluetoothDevice,"UUID");
    iBluetoothController.listenSocket("TestName","UUID",bluetoothListenSocket);
}
```

* 方法**connectBluetooth**用来连接目标设备，参数1代表目标设备的实例，参数2代表通信的UUID。

* 方法**listenSocket**用来监听某个UUID指定的通信管道，参数1代表监听的名称，参数2代表要坚挺的UUID，参数3是**IBluetoothListenSocket**，这个类是监听目标蓝牙发送过来的数据的监听器类，只有一个方法：

```java
interface IBluetoothListenSocket {
	//接收到的数据
    void onReceiveDataArray(in byte[] data);
}
```

实现一个**IBluetoothListenSocket**的实例，并重写**onReceiveDataArray**方法可以监听到对方发送过来的数据，例如：

```java
IBluetoothListenSocket bluetoothListenSocket=new IBluetoothListenSocket.Stub(){
    @Override 
    public void onReceiveDataArray(byte[] data){
        System.out.println("接收到的数据："+Arrays.toString(data));
    }
};

void onPairSuccess(in BluetoothDevice bluetoothDevice){
    iBluetoothController.connectBluetooth(bluetoothDevice,"UUID");
    iBluetoothController.listenSocket("TestName","UUID",bluetoothListenSocket);
}
```

任何需要**IBluetoothListenSocket**实例的位置都可以使用**AbsBluetoothListenSocket**类的实例，AbsBluetoothListenSocket直接继承自**IBluetoothListenSocket.Stub**，所以可以这样写：

```java
IBluetoothListenSocket bluetoothListenSocket=new AbsBluetoothListenSocket(){
    @Override 
    public void onReceiveDataArray(byte[] data){
        System.out.println("接收到的数据："+Arrays.toString(data));
    }
};
```

其中和连接蓝牙/监听蓝牙相关的回调函数如下：

```java
//连接成功
void onConnectSuccess();
//连接失败
void onConnectFailure(in String msg);
//连接关闭
void onConnectClose();
//监听成功
void onListenSuccess();
//监听失败
void onListenFailure(in String msg);
//监听关闭
void onListenClose();
```

当连接成功方法**onConnectSuccess**被调用时，即可发送数据。

##### 9、发送数据

当蓝牙连接成功时，可以向目标蓝牙发送数据，调用**IBluetoothController**的**sendData**发送数据，例如：

```java
void onConnectSuccess(){
    //连接成功
    iBluetoothController.sendData("Hello World!".getBytes());
}
```

相关API：

```java
void send(in byte[] data);
```

# END



​	

​	当服务绑定成功并拿到IBluetoothController接口的实例之后，可以调用**startScan**方法开始扫描周围的蓝牙设备，