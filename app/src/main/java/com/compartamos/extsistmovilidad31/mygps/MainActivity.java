package com.compartamos.extsistmovilidad31.mygps;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener {
    private LocationManager locationManager;
    ProgressDialog progress;

    private boolean sonCoordenadasFinales;

    public void obtenerCoordenadasFinales(View view) {
        TextView et = (TextView) findViewById(R.id.textView);
        String etString = et.getText().toString();
        try {
            Context context = getBaseContext();
            boolean estaConectado = Connectivity.isConnected(context);
            NetworkInfo ni = Connectivity.getNetworkInfo(context);
            et.setText(String.format("%s\nFINAL => CONECTADO: %s\t CONECTIVIDAD: %s\t TIPO DE RED: %s",
                    etString, estaConectado ? "Si" : "No", ni == null ? "No conectado" : ni.getTypeName(),
                    ni == null ? "Desconocido" : ni.getSubtypeName()));
        } catch(Exception ex) {
            et.setText(String.format("%s\nERROR FINAL: %s", etString, ex.getMessage()));
        }
        progress.setTitle("Procesando");
        progress.setMessage("Obteniendo las coordenadas finales");
        progress.show();
        sonCoordenadasFinales = true;
        Chronometer cronos = (Chronometer) findViewById(R.id.chronometer);
        cronos.stop();
        Button button = (Button) findViewById(R.id.btn_detener_geolocalizacion);
        button.setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView et = (TextView) findViewById(R.id.textView);
        try {
            Context context = getBaseContext();
            boolean estaConectado = Connectivity.isConnected(context);
            NetworkInfo ni = Connectivity.getNetworkInfo(context);

            et.setText(String.format("INICIAL => CONECTADO: %s\t CONECTIVIDAD: %s\t TIPO DE RED: %s",
                    estaConectado ? "Si" : "No", ni == null ? "No conectado" : ni.getTypeName(),
                    ni == null ? "Desconocido" : ni.getSubtypeName()));
        } catch(Exception ex) {
            et.setText(String.format("ERROR INICIAL: %s", ex.getMessage()));
        }
        progress = new ProgressDialog(this);
        sonCoordenadasFinales = false;
        Chronometer cronos = (Chronometer) findViewById(R.id.chronometer);
        cronos.start();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            TextView latitud = null;
            TextView longitud = null;

            if(sonCoordenadasFinales) {
                latitud = (TextView) findViewById(R.id.tvw_latitudValorFinal);
                longitud = (TextView) findViewById(R.id.tvw_longitudValorFinal);
            } else {
                latitud = (TextView) findViewById(R.id.tvw_latitudValorInicial);
                longitud = (TextView) findViewById(R.id.tvw_longitudValorInicial);
            }

            if(latitud.getText().equals("") && longitud.getText().equals("")) {
                latitud.setText(String.format("\t%1$,.7f\t", location.getLatitude()));
                longitud.setText(String.format("\t%1$,.7f\t", location.getLongitude()));
            }

            if(sonCoordenadasFinales) {
                locationManager.removeUpdates(this);
                progress.dismiss();
                Toast.makeText(getBaseContext(), "Se termina la geolocalización", Toast.LENGTH_LONG).show();
            }
        } catch(Exception ex) {
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getBaseContext(), "GPS Desactivado", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getBaseContext(), "GPS Activado", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        try {
            String estado = status == 2 ? "AVAILABLE" : (status == 1 ? "TEMPORARILY UNAVAILABLE" : "OUT OF SERVICE");
            String msg = String.format("El proveedor %s se encuentra en el estado %s", provider, estado);
            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        } catch(Exception ex) {
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
