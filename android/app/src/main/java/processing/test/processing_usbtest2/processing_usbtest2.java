package processing.test.processing_usbtest2;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
//import cn.wch.ch34xuartdriver.CH34xUARTDriver;
import android.hardware.usb.UsbManager; 
import android.content.Context; 
import android.content.DialogInterface; 
import android.app.Dialog; 
import android.app.AlertDialog;
import android.os.IBinder;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.usb.UsbManager; 
import android.hardware.usb.UsbDevice; 
import android.hardware.usb.UsbDeviceConnection; 
import android.content.BroadcastReceiver; 
import android.content.IntentFilter; 
import android.app.PendingIntent; 
import android.app.Activity; 
import cn.wch.ch34xuartdriver.CH34xUARTDriver; 
import android.os.Bundle; 
import processing.core.PApplet;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
//import cn.wch.ch34xuartdriver.*;

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class processing_usbtest2 extends PApplet {










//import android.os.Message;


boolean isOpen;

private int retval;
private MyActivity myactivity;


public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);

try{
myactivity = new MyActivity(this);
myactivity.openUsbDevice();
  if(!myactivity.UsbFeatureSupported()){
    new AlertDialog.Builder(this.getActivity())
    .setTitle("notice")
    .setMessage("Your cell phone does not support USB HOST,Please change and try again!")
    .setPositiveButton("exit",new DialogInterface.OnClickListener(){
      @Override
      public void onClick(DialogInterface arg0,int arg1){System.exit(0);}
    }).create();
  }
  //System.out.print("isopen");
  if(!isOpen){
    //retval = myactivity.usbdriver.ResumeUsbList();
    retval=0;
    switch(retval){
    case -1:
      Toast.makeText(this.getActivity(),"open devices failed",Toast.LENGTH_SHORT).show();
      myactivity.usbdriver.close();
      break;
    case 0:
      //if(!myactivity.usbdriver.UartInit()){
      //  Toast.makeText(this.getActivity(),"device init failed",Toast.LENGTH_SHORT).show();
      //  return;
      //}
      Toast.makeText(this.getActivity(),"open devices successed",Toast.LENGTH_SHORT).show();
      isOpen = true;
      //if (myactivity.usbdriver.SetConfig(9600, (byte)8, (byte)1, (byte)0,(byte)0)) {
      //    Toast.makeText(this.getActivity(), "Serial config successed!",
      //        Toast.LENGTH_SHORT).show();
      //  } else {
      //    Toast.makeText(this.getActivity(), "Serial config failed!",
      //        Toast.LENGTH_SHORT).show();
      //  }
      myactivity.usbdriver.setBaudRate(9600);
      new readThread().start();
      break;
    default:
      AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
      builder.setTitle("no permission"+" "+str(retval)+"!");
      builder.setMessage("Confirm to exit?");
      builder.setPositiveButton("confirm",new DialogInterface.OnClickListener(){
      @Override
      public void onClick(DialogInterface arg0,int arg1){System.exit(0);}
      });
      builder.setNegativeButton("cancel",new DialogInterface.OnClickListener(){
      @Override
      public void onClick(DialogInterface arg0,int arg1){}
      });
      builder.show();
    }
  }
  else{
    myactivity.usbdriver.close();
    isOpen = false;
  }
}
catch(Exception e){
e.printStackTrace();
}/*

  
  */
}
@Override
    public void onDestroy() {
        myactivity.context.unregisterReceiver(myactivity.mUsbPermissionActionReceiver);
        super.onDestroy();
}
public void onActivityResult(int requestCode, int resultCode, Intent data) {
//bt.onActivityResult(requestCode, resultCode, data);
}
byte[] val_read  =new byte[1]; 
byte[] val_write =new byte[1];  
String read_recv = "";

int y=640;
byte s1;
int new_time;
int old_time=0;
byte old_s=0;
public void setup() {
  System.out.print("fullscreen");
  
  
  ellipseMode(RADIUS);
  textSize(50);
  strokeWeight(5);
  //bt.start();
  //klist = new KetaiList(this, bt.getPairedDeviceNames());
  //System.out.println("run here!1");
}

public void draw() { 
  background(80);
  fill(255);
  line(360,280,360,1000);
  fill(255,255,0);
  ellipse(360,y,50,50);
  s1=PApplet.parseByte((y-280)*100/(1000-280));
  text(s1,200,y);
  if(mousePressed){
    if(abs(mouseX-360)<=50&&abs(mouseY-y)<=50&&mouseY>=280&&mouseY<=1000){
      y=mouseY;
    }
  }
  //text(read_recv+"!",320,1150);
  text("read String:",300,1150);
  String[] t2 = read_recv.split(" ");
  String t1 = "";
  for(int i = 0;i<t2.length;i++){
    if(t2[i].length()>=2)
  t1+=(char)(Integer.parseInt(t2[i],16));}
  text(t1+"?",320,1210);
  new_time= millis();
  if((new_time-old_time)>100){
  if(s1!=old_s){
    val_write[0] = s1;
    //bt.broadcast(val_write);
    try{
        myactivity.usbdriver.write(val_write,val_write.length);
    }
    catch(Exception e){
        //System.out.println(e.getMessage());

        Toast.makeText(this.myactivity.context,e.getMessage(),Toast.LENGTH_LONG).show();
    }
    old_s = s1;}
    old_time=new_time;
}
}

  private class readThread extends Thread{
  public void run(){
    byte[] buffer = new byte[4096];
    while(true){
      //Message msg = Message.obtain();
      if(!isOpen){
        break;
      }
      int length = 0;
      try{
          length = myactivity.usbdriver.read(buffer,4096);
      }
      catch(Exception e){
          Toast.makeText(myactivity.context,e.getMessage(),Toast.LENGTH_LONG).show();
      }
      if(length>0){
        String recv = toHexString(buffer,length);
        //String recv = new String(buffer,0,length);
        //msg.obj = recv;
        //handler.sendMessage(msg);
        read_recv = recv;
      }
    }
  }
}

private String toHexString(byte[] arg, int length) {
    String result = new String();
    if (arg != null) {
      for (int i = 0; i < length; i++) {
        result = result
            + (Integer.toHexString(
                arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                    : arg[i])
                : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                    : arg[i])) + " ";
      }
      return result;
    }
    return "";
  }
  












public class MyActivity {
  private static final String ACTION_USB_PERMISSION = "cn.wch.wchusbdriver.USB_PERMISSION";
  PApplet parent;
  Context context;
  String read_recv = "";
  //public CH34xUARTDriver usbdriver;
  public UsbSerialDriver usbdriver;
  
  public MyActivity(PApplet parent){
    this.parent = parent;
    this.context = parent.getActivity();
    //usbdriver = new CH34xUARTDriver((UsbManager)context.getSystemService(Context.USB_SERVICE),context,ACTION_USB_PERMISSION);

  }
  
  /**
     *  usb 
     */
  public boolean UsbFeatureSupported()
  {
      boolean bool;
      return bool = this.context.getPackageManager().hasSystemFeature("android.hardware.usb.host");
  }
    private void openUsbDevice(){
        //before open usb device
        //should try to get usb permission
        tryGetUsbPermission();
        /*
        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
          return;
        }

       // Open a connection to the first available driver.
       UsbSerialDriver driver = availableDrivers.get(0);
       UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
       if (connection == null) {
          // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
          return;
       }

       UsbSerialPort port = driver.getPorts().get(0); // Most devices have just one port (port 0)
       port.open(connection);
       port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
         */
    }
    UsbManager mUsbManager;
    

    private void tryGetUsbPermission(){

        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(mUsbPermissionActionReceiver, filter);

        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this.context, 0, new Intent(ACTION_USB_PERMISSION), 0);

        //here do emulation to ask all connected usb device for permission
        for (final UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
            //add some conditional check if necessary
            //if(isWeCaredUsbDevice(usbDevice)){
            if(mUsbManager.hasPermission(usbDevice)){
                //if has already got permission, just goto connect it
                //that means: user has choose yes for your previously popup window asking for grant perssion for this usb device
                //and also choose option: not ask again
                afterGetUsbPermission(usbDevice);
            }else{
                //this line will let android popup window, ask user whether to allow this app to have permission to operate this usb device
                mUsbManager.requestPermission(usbDevice, mPermissionIntent);
            }
            //}
        }
    }


    private void afterGetUsbPermission(UsbDevice usbDevice){
        //call method to set up device communication
        //Toast.makeText(this, String.valueOf("Got permission for usb device: " + usbDevice), Toast.LENGTH_LONG).show();
        //Toast.makeText(this, String.valueOf("Found USB device: VID=" + usbDevice.getVendorId() + " PID=" + usbDevice.getProductId()), Toast.LENGTH_LONG).show();

        doYourOpenUsbDevice(usbDevice);
    }

    private void doYourOpenUsbDevice(UsbDevice usbDevice){
        //now follow line will NOT show: User has not given permission to device UsbDevice
        UsbDeviceConnection connection = mUsbManager.openDevice(usbDevice);
        //System.out.println("doYourOpenUsbDevice");
        Toast.makeText(context, String.valueOf("device opened" + usbDevice), Toast.LENGTH_LONG).show();
        //add your operation code here
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private UsbService usbService;
    private TextView display;
    private EditText editText;
    private MyHandler mHandler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

}

  public void settings() {  fullScreen();  smooth(); }
}


