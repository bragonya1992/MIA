package com.usac.brayan.mensajeriaarquitectura;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class peticioneshttp extends AsyncTask<String, String, String> {
    public String salida="";
    private Context mContext;
    public String carneCandidato;
    public String entrada;
    public peticioneshttp(Context context,String carne) {
        //set context variables if required
        super();
        this.mContext = context;
        this.carneCandidato=carne;
    }

    public peticioneshttp(){

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {

        String urlString = "http://moonguate.com/"+params[0]+params[1]; // URL to call
        entrada=params[0];
        String resultToDisplay = "";

        InputStream in = null;
        try {

            URL url = new URL(urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            in = new BufferedInputStream(urlConnection.getInputStream());


        } catch (Exception e) {

            System.out.println(e.getMessage());

            return e.getMessage();

        }


        try {
            salida=convertBuffered(in);
        } catch (IOException e) {
            salida=e.toString();
        }
        return resultToDisplay;
    }


    @Override
    protected void onPostExecute(String result) {
        //Update the UI
        if (entrada.equals("saludo.php")) {
            if (salida.equals("true")) {
                ServicioNotificacionesFARUSAC.sm.createLoginSession(carneCandidato,2,2);
                mContext.startActivity(new Intent(mContext, principal.class));
            } else {
                Toast.makeText(mContext, "Tus datos son invalidos", Toast.LENGTH_SHORT).show();
            }
        }else if(entrada.equals("cursos.php")){
            try {
                AsignarCursos(salida);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String convertBuffered(InputStream in) throws IOException {
        byte[] contents = new byte[1024];

        int bytesRead = 0;
        String strFileContents="";
        while((bytesRead = in.read(contents)) != -1) {
            strFileContents += new String(contents, 0, bytesRead);
        }
        return strFileContents;
    }

    public void AsignarCursos(String cadena) throws JSONException {
        JSONObject jsonObj = new JSONObject(cadena);
        JSONArray jsonArray = jsonObj.getJSONArray("cursos");
        for(int i=0;i<jsonArray.length();i++){
            JSONObject temp =jsonArray.getJSONObject(i);
            String nombre= temp.getString("nombre");
            String seccion = temp.getString("seccion");
            principal.listaCursos.addLast(new Curso(nombre,seccion));
        }
        principal.mapearCursos();

    }


}