package com.example.bluetoothcontrolappv2;

import static android.Manifest.permission.BLUETOOTH_CONNECT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /*    BluetoothManager bluetoothManager=getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter=bluetoothManager.getAdapter();
        if(bluetoothAdapter ==null) {
            /* Device won't support bluetooth */
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ACCESS_LOCATION = 2;


    private static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_SCAN
    };
    private static final String[] CALL_PERMISSIONS = {Manifest.permission.CALL_PHONE};

    ListView listView;
    TextView statusTextView;
    Button searchButton;
    BluetoothAdapter myBluetoothAdapter;
    ArrayList<String> bluetoothDevices = new ArrayList<>();

    ArrayList<String> addresses = new ArrayList<>();

    ArrayAdapter arrayAdapter;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.i("Action", action);

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {    //Eğer aram bitmiş ise yazıyı yazdır enable'ı true yap
                statusTextView.setText("Finished..");
                searchButton.setEnabled(true);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //Kullanıcıdan bluetooth kullanma iznini almadıysak onu almak için
                if (ActivityCompat.checkSelfPermission(context, BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{BLUETOOTH_CONNECT}, 1);

                    return;
                }
                String name = device.getName(); // Bağlanılan cihazın adı
                String address = device.getAddress();   //Bağlanılan cihazın adresi
                String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));  //Rssi alınan sinyal gücücnü belirtir.Birimi dBm
                //Log.i("Device found","Name "+ name + "Adress:" + address + "RSSI" +rssi);
                if (!addresses.contains(address)) {
                    addresses.add(address);
                    String deviceString;
                    if (name == null) {     //Eğer cihaza bağlı değilsek
                        deviceString = address + "-RSSI" + rssi + "dBm";
                    } else {
                        deviceString = name + "-RSSI" + rssi + "dBm";
                    }
                    /*    if (!bluetoothDevices.contains(deviceString)) {
                        bluetoothDevices.add(deviceString);
                    }*/
                    bluetoothDevices.add(deviceString);
                    arrayAdapter.notifyDataSetChanged();
                }


            }

        }
    };

    public void searchClicked(View view) {  //onClick
        statusTextView.setText("Searching..");
        searchButton.setEnabled(false);
        bluetoothDevices.clear();
        addresses.clear();
        /* if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.});
            ActivityCompat.requestPermissions(MainActivity.device, new String[]{BLUETOOTH_SCAN}, 2);
            return;
        }*/
        myBluetoothAdapter.startDiscovery();
        System.out.println("Line 111");
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        statusTextView = findViewById(R.id.statusTextView);
        searchButton = findViewById(R.id.searchButton);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, bluetoothDevices);
        listView.setAdapter(arrayAdapter);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);

    }
/*
    private void requestBluetoothPermissions() {
        // Check if Bluetooth is supported on this device
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            return;
        }

        // Check if Bluetooth is enabled on this device
        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled, request to enable it
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            // Bluetooth is already enabled, request location permissions
            requestLocationPermissions();
        }
    }
    // Request location permissions
    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Location permission has not been granted yet, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_LOCATION);
        } else {
            // Location permission has already been granted, start scanning for Bluetooth devices
            startBluetoothScan();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ACCESS_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Location permission has been granted, start scanning for Bluetooth devices
                    startBluetoothScan();
                } else {
                    // Location permission has been denied, handle the situation accordingly
                    // ...
                    Toast.makeText(this, "Can't start the program if the permissions are not granted.", Toast.LENGTH_SHORT).show();
                }
                break;
            // Handle other permission requests if necessary
            // ...
        }
    }

    // Start scanning for Bluetooth devices
    private void startBluetoothScan() {
        // Use BluetoothAdapter to start scanning for devices
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // ...
    }

*/

    private void verifyPermissions(){
        Log.d("MainActivity","verifyPermissions: Checking Permissions.");

        int permissionBt = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH);
        int permissionBtSCAN = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN);
        int permissionBtADMIN = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN);
        int permissionBtCONNECT = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT);

        if(permissionBt != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,CALL_PERMISSIONS,1);
        }


    }




}