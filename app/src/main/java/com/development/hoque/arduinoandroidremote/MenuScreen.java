package com.development.hoque.arduinoandroidremote;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MenuScreen extends AppCompatActivity {

    Button button_btTurnON;
    Button button_ConnectDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);

        button_btTurnON = (Button)findViewById(R.id.button_bluetoothON);
        button_ConnectDevice = (Button)findViewById(R.id.button_Connect);

        button_btTurnON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Turn_Bluetooth_ON();
            }
        });
        button_ConnectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextPage();
            }
        });
    }

    void Turn_Bluetooth_ON()
    {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter != null)
            btAdapter.enable();
        else
            Toast.makeText(getApplicationContext(),"Bluetooth Adapter Not Found!",Toast.LENGTH_LONG).show();
    }

    void goToNextPage()
    {

    }
}
