package co.macrosystem.soportemacrosystem;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    // Método que queremos ejecutar en el servicio web
    private static final String Metodo = "ConsultarUsuarios";
    private static final String MetodoLogin = "ConsultarUsuario";
    // Namespace definido en el servicio web
    private static final String namespace = "http://webservices/";
    // namespace + metodo
    private static final String accionSoap = "http://webservices//ConsultarUsuarios";
    // Fichero de definicion del servcio web
    private static final String url = "http://192.168.1.6:8090/WSUsuario/WSGestionUsuario?wsdl";
    private static final String url2 = "http://186.0.93.194:8090/WSUsuario/WSGestionUsuario?wsdl";

    private boolean boolres=false;
    private ListView lista;
    ArrayAdapter<Login> adapter;
    Button btnBuscarUsuarios ;
    Button btnValidarUsuario;
    EditText clave;
    EditText user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lista=(ListView) findViewById(R.id.lstUsuarios);
        btnBuscarUsuarios = (Button) findViewById(R.id.btnConsumir);
        btnValidarUsuario = (Button) findViewById(R.id.btnConsumirValidar);
        btnBuscarUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OcultarTeclado(view);
                consumir(view, "listar");
                limpiar(view);
            }
        });
        btnValidarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OcultarTeclado(view);
                consumir(view, "login");
            }
        });
    }

    public void OcultarTeclado(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public  void limpiar(View view){
        user = (EditText) findViewById(R.id.txtUsuario);
        clave = (EditText) findViewById(R.id.txtClave);
        user.getText().clear();
        clave.getText().clear();
    }

    public  void consumir(View view, String funcion){
        if (funcion.equals("listar")){
            new consumirAsyc().execute();
        }else if (funcion.equals("login")){
            new consumirAsycLogin().execute();
        }


    }

    public boolean invoceWS(){
        boolean res=false;
        try {
            SoapObject request = new SoapObject(namespace, Metodo);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope. VER11);
            //sobre.dotNet = true;
            sobre.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(url);
            transporte.call(accionSoap, sobre);    // Llamada
            Vector<?> responseVector=null;
            SoapObject soapObject=null;
            List<Login> listaEst= new ArrayList<>();
            if(sobre.getResponse() instanceof Vector)
                responseVector = (Vector<?>) sobre.getResponse();//almacenar en vector
            else
                soapObject=(SoapObject)sobre.getResponse();
            if(responseVector!=null){
                int count=responseVector.size();
                for (int i = 0; i <count; ++i) { //Cada registro encontrado
                    SoapObject test=(SoapObject)responseVector.get(i);
                    listaEst.add(leerSoap(test));
                }
            }else{
                if(soapObject!=null)
                    listaEst.add(leerSoap(soapObject));
            }

            adapter = new ArrayAdapter<Login>(this, android.R.layout.simple_list_item_1, listaEst);
            res=true;
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }

        return res;
    }

    public boolean invoceWSLogin(){
        boolean res=false;
        try {
            EditText user = (EditText) findViewById(R.id.txtUsuario);
            EditText clave = (EditText) findViewById(R.id.txtClave);
            String us = user.getText().toString();
            String pass = clave.getText().toString();
            SoapObject request = new SoapObject(namespace, MetodoLogin);
            request.addProperty("usuario", us);
            request.addProperty("clave", pass);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope. VER11);
            //sobre.dotNet = true;
            sobre.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(url);
            transporte.call(accionSoap, sobre);    // Llamada
            Vector<?> responseVector=null;
            SoapObject soapObject=null;
            List<Login> listaUser= new ArrayList<>();
            if(sobre.getResponse() instanceof Vector)
                responseVector = (Vector<?>) sobre.getResponse();//almacenar en vector
            else
                soapObject=(SoapObject)sobre.getResponse();
            if(responseVector!=null){
                int count=responseVector.size();
                for (int i = 0; i <count; ++i) { //Cada registro encontrado
                    SoapObject test=(SoapObject)responseVector.get(i);
                    listaUser.add(leerSoap(test));
                }
            }else{
                if(soapObject!=null)
                    listaUser.add(leerSoap(soapObject));
            }

            adapter = new ArrayAdapter<Login>(this, android.R.layout.simple_list_item_1, listaUser);
            res=true;
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }

        return res;
    }

    private Login leerSoap(SoapObject soapObj){
        String usuario = soapObj.getProperty("usuario").toString();
        String clave = soapObj.getProperty("clave").toString();
        String nomape = soapObj.getProperty("nomApe").toString();
        Login user = new Login();
        user.setUsuario(usuario);
        user.setClave(clave);
        user.setNomApe(nomape);
        return user;
    }

    private class consumirAsyc extends  android.os.AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            boolres=invoceWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(boolres){
                lista.setAdapter(adapter);
                System.out.println("Actualizado..");
            }
            Toast.makeText(getApplicationContext(), "Finalizada..", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Descargando..", Toast.LENGTH_SHORT).show();
        }

    }

    private class consumirAsycLogin extends android.os.AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            boolres=invoceWSLogin();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(boolres){
                lista.setAdapter(adapter);
                System.out.println("Actualizado..");
            }
            Toast.makeText(getApplicationContext(), "Finalizada..", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Descargando..", Toast.LENGTH_SHORT).show();
        }

    }

}
