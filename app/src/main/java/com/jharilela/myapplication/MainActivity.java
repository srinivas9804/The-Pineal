package com.jharilela.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    static BluetoothAdapter mBluetoothAdapter;
    static BluetoothSocket mSocket;
    static BluetoothDevice Pi;
    static int ctr=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        System.out.println("Number of paired devices: "+ pairedDevices.size());
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                if(deviceHardwareAddress.equals("B8:27:EB:74:5C:7C")){
                    Pi = device;
                }
            }
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button mSetup = (Button) findViewById(R.id.setup);
        final Button mCheck = (Button) findViewById(R.id.check_pins);
        mSetup.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Context context = getApplicationContext();
                CharSequence text="";
                if(MainActivity.Pi == null){
                    text = "Error! Not paired with the Pi";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    text = "Connecting to the Pi. Please Wait!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    ConnectThread obj = new ConnectThread(MainActivity.Pi);
                    obj.start();
                }
            }
        });
        mCheck.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Context context = getApplicationContext();
                CharSequence text="";
                if(ctr==0){
                    text = "Connect to the Pi first";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else{
                    if(mSocket==null)
                        System.out.println("Empty");
                    ConnectedThread1 obj = new ConnectedThread1(mSocket);
                    byte arr[] = "check".getBytes();
                    obj.write(arr);
                    obj.run();
                }

            }
        });
    }
    public static void setCtr(int num){
        ctr=num;
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                UUID uuid = UUID.fromString("7be1fcb3-5776-42fb-91fd-2ee7b5bbb86d");
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e("Tag", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("Tag", "Could not close the client socket", closeException);
                }
                return;
            }
            System.out.println("Socket created");
            mSocket = mmSocket;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Context context = getApplicationContext();
                    CharSequence text="";
                    text = "Pi Connected! You can now check Pins.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    MainActivity.setCtr(1);
                }
            });
            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("Tag", "Could not close the client socket", e);
            }
        }
    }
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler mHandler; // handler that gets info from Bluetooth service
    void startActivity1(String str){
        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
        intent.putExtra("pins",str);
        startActivity(intent);
    }

    private class ConnectedThread1 extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread1(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String str="";
                        try {
                             str = new String(mmBuffer, "UTF-8");
                        }
                        catch(Exception e) {
                            System.out.println("lol got emm");
                        }
                        System.out.println(str);
                        int num = str.charAt(0)-'0';
                        if(num==1){
                            if(str.charAt(1)=='0')
                                num=10;
                        }
                        String temp="";
                        int i = 2;
                        int start=2;
                        if(num==10)
                            start=3;
                        for(i=start;i<start+num;i++) {
                            temp += str.charAt(i);
                            if(str.charAt(i)=='1'&& str.charAt(i+1)=='0')
                                temp+='0';
                        }
                        startActivity1(temp);
                    }
                });
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                System.out.println("In the function "+bytes.toString());
                mmOutStream.write(bytes);
                // Share the sent message with the UI activity.
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.

            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Context context = getApplicationContext();
                    CharSequence text="";
                    text = "Command successfully sent!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }


}
