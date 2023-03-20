package com.example.deneme1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //standard bağlantı değeri
//    static final UUID mUUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    //Layoutun ana activity ile bağdaştırılması
    Switch onOff;
    BluetoothAdapter myBluetooth;
    Switch toogleSwitch;

    BluetoothSocket mmSocket = null;
    BluetoothDevice mmDevice; //myBluetooth.getRemoteDevice("98:DA:60:04:B1:AA");


    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    boolean connectionState = false; //başlangıçta bağlı değiliz
    byte BtAdapterSayac = 0;
    //EditText edtTxt;
    String sGelenVeri;
    String sPlusGidenVeri;
    String sMinusGidenVeri;
    boolean isChecked = false;
    //TextView MtxtVwState;
    TextView totalPoints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        mmDevice = myBluetooth.getRemoteDevice("98:DA:60:04:B1:AA");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        BluetoothSocket socket = null; // Use the default UUID for SPP
        try {
            socket = mmDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (IOException e) {
            Log.e("Bluetooth socket","Bluetooth ya bulunamadı ya da gerekli izinler verilmedi.");
        }
        try {
            socket.connect();
        } catch (IOException e) {
            Log.e("Bluetooth Socket","IO Exception aldı.");
        }

        try {
            mmOutputStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.e("Output stream","io exception");
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        System.out.println(myBluetooth.getBondedDevices());
        toogleSwitch = (Switch) findViewById(R.id.on_off_switch); //bluetooth açık kapamak için switch
        Button plusRed = (Button) findViewById(R.id.plusRed);
        Button plusGreen = (Button) findViewById(R.id.plusGreen);
        Button minusRed = (Button) findViewById(R.id.minusRed);
        Button minusGreen = (Button) findViewById(R.id.minusGreen);
        totalPoints = (TextView)    findViewById(R.id.totalPoints);
        //On/off switchi çalıştırıldığında
        toogleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {    //switch değiştirilince durum değiştiriliyor.
                Log.v("Switch State=", "" + isChecked);
                isChecked = true;
            }
        });
        if (!isChecked) {
            try {
                System.out.println("bt açılıyor...");
                openBT();
                System.out.println("bt bulunuyor...");
                findBT();
                isChecked = true;
            } catch (IOException data) {
                data.printStackTrace();
            }
        } else {
            try {
                closeBT();
                isChecked = false;
            } catch (IOException data) {
                data.printStackTrace();
            }
        }

        plusRed.setOnClickListener(this);
        plusGreen.setOnClickListener(this);
        minusGreen.setOnClickListener(this);
        minusRed.setOnClickListener(this);


        getData(totalPoints);


    }

    private void getData(TextView totalPoints) {


    }

    private void SendData(String data) throws IOException {
        try{
            System.out.println("Bağlantı durumu" + connectionState);
            if(!connectionState){//Bluetooth bağlantısı aktif ise veri gönderebiliriz.
                mmOutputStream.write(data.getBytes()); //get bytes stringi byte arrayine çevirir

            }
        } catch (Exception ignored) {
        }
    }


    //ON CREATE BITIS YERI
    void openBT() throws IOException {
        //Bluetooth u açıyoruz.

        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard //SerialPortService I
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();/*Bluetooth üzerinden gelen verileri yakalamak için bir listener oluşturuyoruz.*/
        } catch (Exception ignored) {
        }

    }


    void findBT() {
        try {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            if (myBluetooth == null) {
                System.out.println("Bluetooth adaptörü bulunamadı");
            }

            if (BtAdapterSayac == 0) {
                if (!myBluetooth.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivityForResult(enableBluetooth, 0);
                    BtAdapterSayac = 1;
                }
            }
            Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (("HC-06").equals(device.getName().toString())) {//Eşleşmiş cihazlarda HC-06 adında cihaz varsa bağlantıyı aktişleştiriyoruz. Burada HC-05 yerine bağlanmasını istediğiniz Bluetooth adını yazabilirsiniz.
                        mmDevice = device;
                        connectionState = true;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void closeBT() throws IOException {
        try {
            if (myBluetooth.isEnabled()) {
                stopWorker = true;
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                myBluetooth.disable();
                mmOutputStream.close();
                mmInputStream.close();
                mmSocket.close();
            }
            else {
            }
        } catch(Exception ignored){
        }
    }


    void beginListenForData() {
        try {
            final Handler handler = new Handler();
            final byte delimiter = 10; //This is the ASCII code for a newline character

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        final byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        final String data = new String(readBuffer, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                sGelenVeri = data.toString();
                                                sGelenVeri = sGelenVeri.substring(0, 3);
                                                //MtxtVwState.setText(sGelenVeri);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.plusGreen:
                //+2 puan yeşil olacak şekilde
                sPlusGidenVeri="PG";   //Arduinoya yeşil rengin rgb kodunu ileticek.
                System.out.println("Değeri PG aldım");
                try {
                    SendData(sPlusGidenVeri);
                } catch (IOException e) {
                    Log.e("Veri Gönderme Hatası","Veriyi gönderemiyor PG");
                }
                break;
            case R.id.plusRed:
                //+2 puan kırmızı
                sPlusGidenVeri="PR";
                System.out.println("Değeri PR aldım");
                try {
                    SendData(sPlusGidenVeri);
                } catch (IOException e) {
                    Log.e("Veri Gönderme Hatası","Veriyi gönderemiyor PR");
                }
                break;
            case R.id.minusRed:
                //-1 puan kırmızı
                sMinusGidenVeri="MR";
                System.out.println("Değeri MR aldım");
                try {
                    SendData(sMinusGidenVeri);
                } catch (IOException e) {
                    Log.e("Veri Gönderme Hatası","Veriyi gönderemiyor MR");
                }
                break;
            case R.id.minusGreen:
                //-1 puan yeşil
                sMinusGidenVeri="MG";
                System.out.println("Değeri MG aldım");
                try {
                    SendData(sMinusGidenVeri);
                } catch (IOException e) {
                    Log.e("Veri Gönderme Hatası","Veriyi gönderemiyor MG");
                }
                break;
        }
    }

}





/*package com.example.deneme1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public abstract class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //standard bağlantı değeri
    private final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    //Layoutun ana activity ile bağdaştırılması
    Switch onOff;
    BluetoothAdapter myBluetooth;
    Switch toogleSwitch;

    BluetoothSocket mmSocket = null;
    BluetoothDevice mmDevice; //


    private OutputStream outputStream;
    InputStream inputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    boolean connectionState = false; //başlangıçta bağlı değiliz
    byte BtAdapterSayac = 0;
    //EditText edtTxt;
    String sGelenVeri;
    String sPlusGidenVeri;
    String sMinusGidenVeri;
    boolean isChecked = false;
    //TextView MtxtVwState;
    TextView totalPoints;
    TextView connectionStatusTextView;
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        mmDevice = myBluetooth.getRemoteDevice("98:DA:60:04:B1:AA");
        connectToDevice();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        System.out.println(myBluetooth.getBondedDevices());
        toogleSwitch = (Switch) findViewById(R.id.on_off_switch); //bluetooth açık kapamak için switch
        Button plusRed = (Button) findViewById(R.id.plusRed);
        Button plusGreen = (Button) findViewById(R.id.plusGreen);
        Button minusRed = (Button) findViewById(R.id.minusRed);
        Button minusGreen = (Button) findViewById(R.id.minusGreen);
        totalPoints = (TextView) findViewById(R.id.totalPoints);

        plusRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendString("Plus Red");
            }
        });
        plusGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendString("Plus Green");
            }
        });
        minusRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendString("Minus Red");
            }
        });
        minusGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendString("Minus Green");
            }
        });


        //On/off switchi çalıştırıldığında
        toogleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {    //switch değiştirilince durum değiştiriliyor.
                Log.v("Switch State=", "" + isChecked);
                isChecked = true;
            }
        });
        if (isChecked) {
            try {
                openBT();
                findBT();
                isChecked = true;
            } catch (IOException data) {
                data.printStackTrace();
            }
        } else {
            try {
                closeBT();
                isChecked = false;
            } catch (IOException data) {
                data.printStackTrace();
            }
        }


        try {
            sendString(sPlusGidenVeri, sMinusGidenVeri);
        } catch (IOException e) {

        }
        // getData(totalPoints);


    }

    private void connectToDevice() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                return;
            }
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();

            connectionStatusTextView.setText("Connected");

            outputStream = mmSocket.getOutputStream();
            inputStream = mmSocket.getInputStream();

            beginListenForData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendString(String data) {
        byte[] bytes = data.getBytes();

        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getData(TextView totalPoints) {
        //Platformdan gelecek verinin ekrana yansıtılması işlemi
    }

    private void SendData(String plus,String minus) throws IOException {
        try{
            if(connectionState){//Bluetooth bağlantısı aktif ise veri gönderebiliriz.
                mmOutputStream.write(plus.getBytes()); //get bytes stringi byte arrayine çevirir
                //bu sayede
            }
        } catch (Exception ignored) {
        }
    }


    //ON CREATE BITIS YERI
    void openBT() throws IOException {
        //Bluetooth u açıyoruz.

        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard //SerialPortService I
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            outputStream = mmSocket.getOutputStream();
            inputStream = mmSocket.getInputStream();
            beginListenForData();/*Bluetooth üzerinden gelen verileri yakalamak için bir listener oluşturuyoruz.
        } catch (Exception ignored) {
        }

    }


    void findBT() {
        try {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            if (myBluetooth == null) {
                System.out.println("Bluetooth adaptörü bulunamadı");
            }

            if (BtAdapterSayac == 0) {
                if (!myBluetooth.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivityForResult(enableBluetooth, 0);
                    BtAdapterSayac = 1;
                }
            }
            Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (("HC-06").equals(device.getName().toString())) {//Eşleşmiş cihazlarda HC-06 adında cihaz varsa bağlantıyı aktişleştiriyoruz. Burada HC-06 yerine bağlanmasını istediğiniz Bluetooth adını yazabilirsiniz.
                        mmDevice = device;
                        connectionState = true;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void closeBT() throws IOException {
        try {
            if (myBluetooth.isEnabled()) {
                stopWorker = true;
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                myBluetooth.disable();
                outputStream.close();
                inputStream.close();
                mmSocket.close();
            }
            else {
            }
        } catch(Exception ignored){
        }
    }


 /*   void beginListenForData() {
        try {
            final Handler handler = new Handler();
            final byte delimiter = 10; //This is the ASCII code for a newline character

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = inputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                inputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        final byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        final String data = new String(readBuffer, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                sGelenVeri = data.toString();
                                                sGelenVeri = sGelenVeri.substring(0, 3);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        } catch (Exception ignored) {
        }
    }


 private void beginListenForData() {
     final Handler handler = new Handler() {
         @Override
         public void handleMessage(Message msg) {
             if (msg.what == 0) {
                 String readMessage = (String) msg.obj;
             }
         }
     };
     Thread thread = new Thread(new Runnable() {
         @Override
         public void run() {
             byte[] buffer = new byte[1024];
             int bytes;

             while (true) {
                 try {
                     bytes = inputStream.read(buffer);
                     String readMessage = new String(buffer, 0, bytes);
                     handler.obtainMessage(0, bytes, -1, readMessage).sendToTarget();
                 } catch (IOException e) {
                     break;
                 }
             }
         }
     });
 }
}*/











