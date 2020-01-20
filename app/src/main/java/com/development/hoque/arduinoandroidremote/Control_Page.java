package com.development.hoque.arduinoandroidremote;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class Control_Page extends AppCompatActivity {

    //Must Have items for Bluetooth Connection=
    String address = null;
    DisplayMetrics dm;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //End of List

    Button b_send,config1,config2,config3,b_on,b_off,b_forward,b_backward,b_left,b_right;
    TextView value;
    EditText terminal;
    SeekBar seek1, seek2;
    String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control__page);

        Intent new_int = getIntent();
        address = new_int.getStringExtra(Bluetooth_List_Page.EXTRA_ADDRESS);
        new ConnectBT().execute();

        initiateWidgets();

        b_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminalMethod();
            }
        });
        config1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configure_Button();
            }
        });
    }

    void initiateWidgets()
    {
        b_send = (Button)findViewById(R.id.b_Send);
        config1 = (Button)findViewById(R.id.b_Configure);
        config2 = (Button)findViewById(R.id.b_Seekbar1);
        config3 = (Button)findViewById(R.id.b_Seekbar2);
        b_on = (Button)findViewById(R.id.b_ON);
        b_off = (Button)findViewById(R.id.b_OFF);
        b_forward = (Button)findViewById(R.id.b_Forward);
        b_backward = (Button)findViewById(R.id.b_Backward);
        b_left = (Button)findViewById(R.id.b_Left);
        b_right = (Button)findViewById(R.id.b_Right);
        value = (TextView)findViewById(R.id.text_lastVal);
        terminal = (EditText)findViewById(R.id.editText);
        seek1 = (SeekBar)findViewById(R.id.seekBar1);
        seek2 = (SeekBar)findViewById(R.id.seekBar2);
    }

    void terminalMethod()
    {
        data = terminal.getText().toString();
        value.setText(data);
        SendChar(data);
    }

    void configure_Button()
    {
        setContentView(R.layout.configure_page);
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*1),(int)(height*1));
        SettingsPageButton();

    }

    EditText onText, ofText, foText, baText, leText, riText;
    void SettingsPageButton()
    {
        Button save = (Button)findViewById(R.id.button_save_Settings);

        onText = (EditText)findViewById(R.id.edit_on);
        ofText = (EditText)findViewById(R.id.edit_off);
        foText = (EditText)findViewById(R.id.edit_forward);
        baText = (EditText)findViewById(R.id.edit_backward);
        leText = (EditText)findViewById(R.id.edit_left);
        riText = (EditText)findViewById(R.id.edit_right);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText_ConfigurePage();
            }
        });

        getText_ConfigurePage();
    }

    void getText_ConfigurePage()
    {
        SharedPreferences sharedPreferences = Control_Page.this.getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
        String text = sharedPreferences.getString(getString(R.string.BUTTON_ON), "on");
        onText .setText(text);
        text = sharedPreferences.getString(getString(R.string.BUTTON_OFF), "off");
        ofText .setText(text);
        text = sharedPreferences.getString(getString(R.string.FORWARD), "f");
        foText .setText(text);
        text = sharedPreferences.getString(getString(R.string.BACKWARD), "b");
        baText.setText(text);
        text = sharedPreferences.getString(getString(R.string.LEFT), "l");
        leText.setText(text);
        text = sharedPreferences.getString(getString(R.string.RIGHT), "r");
        riText.setText(text);
    }

    void setText_ConfigurePage()
    {
        SharedPreferences sharedPreferences = Control_Page.this.getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try
        {
            editor.putString(getString(R.string.BUTTON_ON), onText.getText().toString());
            editor.putString(getString(R.string.BUTTON_OFF), ofText.getText().toString());
            editor.putString(getString(R.string.FORWARD), foText.getText().toString());
            editor.putString(getString(R.string.BACKWARD), baText.getText().toString());
            editor.putString(getString(R.string.LEFT), leText.getText().toString());
            editor.putString(getString(R.string.RIGHT), riText.getText().toString());
            editor.commit();
            finish();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"No data entered in one of the fields",Toast.LENGTH_LONG).show();
        }
    }



    private void SendChar(String str)
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(str.toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error, Can not send data!");
            }
        }
    }
    private void Disconnect()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.close();
            }
            catch (IOException e)
            { msg("Error, Can not disconnect bluetooth!");}
        }
        finish();

    }
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(Control_Page.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {

                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Please try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
