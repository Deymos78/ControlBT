package com.example.controlbt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class DispositivosBT extends AppCompatActivity {

    //1)
    //Depuracion de LOGCAT
    private static final String TAG = "DispositivosBT";//<<< PARTE A MODIFICAR>>> Cambiar por el nombre de la clase que se este utilizando
    //Declaracion de ListView
    ListView IdLista;
    //String que se enviara a la actividad principal, mainActivity
    public static String EXTRA_DEVICE_ADDRESS="device address";

    //Declaracion de campos para comtrolar el Bluetooth
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos_bt);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //-------------------------------------------
        //COMPROBAMOS QUE EL DISPOSITIVO TENGA BLUETOOTH, Y QUE ESTE ENCENDIDO
        VerificarEstadoBT();

        //Inicializa el array que contendra la lista de los dispositivos  bluetooth vinculados
        mPairedDevicesArrayAdapter = new ArrayAdapter(this, R.layout.nombre_dispositivos); // <<<PARTE A MODIFICAR>>>
        //Presenta los dispositivos vinculados en el ListView
        //Recuperamos a traves del id el elemento xml en java, para poder manipularlo
        IdLista = (ListView) findViewById(R.id.IdLista);
        //Colocamos un adaptador dentro de la lista, el cual contendra los dispositivos que esten previamente emparejados con el dispositivo
        //en el que estemos corriendo la aplicacion
        IdLista.setAdapter(mPairedDevicesArrayAdapter);
        //A cada elemento que se encuentre en la lista se le asocia un evento
        IdLista.setOnItemClickListener(mDeviceClickListener);
        //obtiene el adaptador local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        //------------------- EN CASO DE ERROR -------------------------------------
        //SI OBTIENES UN ERROR EN LA LINEA (BluetoothDevice device : pairedDevices)
        //CAMBIA LA SIGUIENTE LINEA POR
        //Set <BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        //------------------------------------------------------------------------------


        //Obtiene un conjunto de dispositivos actualmente emparejados  y agrega a 'pairedDevices'
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        //Adiciona un dispositivo previo emparejado al array
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){//EN CASO DE ERROR LEER LA ANTERIOR EXPLICACION
                //Para cada dispositivo vinculado con el movil, obtendremos de el el nombre y la direccion
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    //Configura un (on-click) para cada elemento de la lista
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            //Obtener la direccion MAC del dispositivo, que son los ultimos 17 caracteres en
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            //Realiza un intent para iniciar la siguiente actividad
            //mientras toma un EXTRA_DEVICE_ADDRESS que es la direccion MAC
            Intent i = new Intent(DispositivosBT.this, UserInterfaz.class);//<<< PARTE A MODIFICAR>>>
            i.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(i);
        }
    };

    private void VerificarEstadoBT(){
        //Comprueba que el dispositivo tiene Bluetooth y que esta encendido
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter==null){//Si el dispositivo no tiene Bluetooth
            Toast.makeText(getBaseContext(), "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT);
        }else{//SI EL DISPOSITIVO TIENE BLUETOOTH
            if(mBtAdapter.isEnabled()){//Si el Bluetooth esta activado
                Log.d(TAG, "...Bluetooth Activado...");
            }else{
                //Solicita al usuario que activa el Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }
}
