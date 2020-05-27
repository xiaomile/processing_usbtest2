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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

import android.os.Bundle; 
import processing.core.PApplet;

//import cn.wch.ch34xuartdriver.*;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException;
import java.util.Set;

public class processing_usbtest2 extends PApplet {


//import android.os.Message;


boolean isOpen;

private int retval;
private MyActivity myactivity;


public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);

try{
    myactivity = new MyActivity(this);
}
catch(Exception e){
e.printStackTrace();
}/*

  
  */
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
        myactivity.usbService.write(val_write);
    }
    catch(Exception e){
        System.out.println("Exception:"+e.getMessage());
    }
    old_s = s1;}
    old_time=new_time;
}
}
/*
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
          length = myactivity.usbService.read;
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
*/
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













    public class MyActivity extends AppCompatActivity {


        private UsbService usbService;
        private TextView display;
        private EditText editText;
        private MyHandler mHandler;
        private PApplet parent;
        private Context context;
        public MyActivity(PApplet parent){
            this.parent = parent;
            this.context = parent.getActivity();

        }
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
        /*
         * Notifications from UsbService will be received here.
         */
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
/*
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mHandler = new MyHandler(this);

            display = (TextView) findViewById(R.id.textView1);
            editText = (EditText) findViewById(R.id.editText1);
            Button sendButton = (Button) findViewById(R.id.buttonSend);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editText.getText().toString().equals("")) {
                        String data = editText.getText().toString();
                        if (usbService != null) { // if UsbService was correctly binded, Send data
                            usbService.write(data.getBytes());
                        }
                    }
                }
            });
        }
*/
        @Override
        public void onResume() {
            super.onResume();
            setFilters();  // Start listening notifications from UsbService
            startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
        }

        @Override
        public void onPause() {
            super.onPause();
            unregisterReceiver(mUsbReceiver);
            unbindService(usbConnection);
        }

        private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
            if (!UsbService.SERVICE_CONNECTED) {
                Intent startService = new Intent(this, service);
                if (extras != null && !extras.isEmpty()) {
                    Set<String> keys = extras.keySet();
                    for (String key : keys) {
                        String extra = extras.getString(key);
                        startService.putExtra(key, extra);
                    }
                }
                startService(startService);
            }
            Intent bindingIntent = new Intent(this, service);
            bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }

        private void setFilters() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
            filter.addAction(UsbService.ACTION_NO_USB);
            filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
            filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
            filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
            registerReceiver(mUsbReceiver, filter);
        }

        /*
         * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
         */
        private class MyHandler extends Handler {
            private final WeakReference<MainActivity> mActivity;

            public MyHandler(MainActivity activity) {
                mActivity = new WeakReference<>(activity);
            }

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UsbService.MESSAGE_FROM_SERIAL_PORT:
                        String data = (String) msg.obj;
                        //mActivity.get().display.append(data);
                        break;
                    case UsbService.CTS_CHANGE:
                        Toast.makeText(mActivity.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
                        break;
                    case UsbService.DSR_CHANGE:
                        Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    }

  public void settings() {  fullScreen();  smooth(); }
}


