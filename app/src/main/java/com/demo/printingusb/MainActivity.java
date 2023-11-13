package com.demo.printingusb;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private OutputStream btOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Check if Bluetooth is enabled
        if (!btAdapter.isEnabled()) {
            // Request user to enable Bluetooth
            Toast.makeText(getApplicationContext(), "Please enable Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Connect to the Bluetooth printer
        connectToPrinter();

        // Button to trigger printing
        Button printButton = findViewById(R.id.printButton);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is where printing happens, use escape codes for proper control like line feed, cutter etc.
                printData("Hello, GooPRT PT-210!\n\n\n");
                printData("\n\n\n\n\n\n");
            }
        });
    }

    private void connectToPrinter() {
        // Replace "YOUR_PRINTER_MAC_ADDRESS" with the actual MAC address of your GooPRT PT-210
        //String printerAddress = "86-67-7A-CE-0F-27";

        // Get the Bluetooth device with hardcoded mac address
        BluetoothDevice printerDevice = btAdapter.getRemoteDevice("86:67:7A:CE:0F:27");

        // Create and connect the Bluetooth socket
        try {
            btSocket = printerDevice.createRfcommSocketToServiceRecord(MY_UUID);
            btSocket.connect();
            btOutputStream = btSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printData(String data) {
        try {
            // Send data to the printer
            btOutputStream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Failed to print", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the Bluetooth socket when the activity is destroyed
        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}