package com.usac.brayan.mensajeriaarquitectura;


import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MensajesManager {

    public static void convertJsonToMensaje(String json,LinkedList<ChatMessage> salida) throws JSONException {
        JSONObject jsonObj = new JSONObject(json);
        JSONArray jsonArray = jsonObj.getJSONArray("arreglo");
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject temp = jsonArray.getJSONObject(i);
                ChatMessage temporal = new ChatMessage(temp.getInt("visibilidad"),temp.getString("seccion"),temp.getString("curso"),temp.getString("mensaje"),temp.getString("catedratico"),temp.getString("fecha"));
                salida.addLast(temporal);
            }
        }

    }

    public static LinkedList<ChatMessage> convertJsonToMensaje(String json) throws JSONException {
        LinkedList<ChatMessage> salida = new LinkedList<>();
        JSONObject jsonObj = new JSONObject(json);
        JSONArray jsonArray = jsonObj.getJSONArray("arreglo");
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject temp = jsonArray.getJSONObject(i);
                ChatMessage temporal = new ChatMessage(temp.getInt("visibilidad"),temp.getString("seccion"),temp.getString("curso"),temp.getString("mensaje"),temp.getString("catedratico"),temp.getString("fecha"));
                salida.addLast(temporal);
            }
        }
        return salida;
    }

    public static LinkedList<Publicacion> convertJsonToPublications(String json) throws JSONException {
        LinkedList<Publicacion> salida = new LinkedList<>();
        JSONObject jsonObj = new JSONObject(json);
        JSONArray jsonArray = jsonObj.getJSONArray("publicacion");
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject temp = jsonArray.getJSONObject(i);
                Publicacion temporal = new Publicacion(temp.getString("contenido").replace("$32","<br>"),realPara(temp.getString("para")),temp.getString("fecha"),temp.getInt("idPublicacion"));
                salida.addLast(temporal);
            }
        }
        return salida;
    }


    private static String realPara(String para){
        if(para.equals("2")){
            return "Maestros";
        }else if(para.equals("1")){
            return "Alumnos";
        }else{
            return "Todos";
        }
    }

    public static LinkedList<ChatMessage> convertJsonToMensajeWithNoDate(String json) throws JSONException {
        LinkedList<ChatMessage> salida = new LinkedList<>();
        JSONObject jsonObj = new JSONObject(json);
        JSONArray jsonArray = jsonObj.getJSONArray("arreglo");
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject temp = jsonArray.getJSONObject(i);
                ChatMessage temporal = new ChatMessage(temp.getInt("visibilidad"),temp.getString("seccion"),temp.getString("curso"),temp.getString("mensaje"),temp.getString("catedratico"));
                salida.addLast(temporal);
            }
        }
        return salida;
    }


    public static LinkedList<Curso> convertJsonToCursos(String json) throws JSONException {
        LinkedList<Curso> items = new LinkedList<>();
        JSONObject jsonObj = new JSONObject(json);
        JSONArray jsonArray = jsonObj.getJSONArray("arreglo");
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject temp = jsonArray.getJSONObject(i);
                Curso temporal = new Curso(temp.getString("curso"),temp.getString("seccion"),temp.getString("catedratico"));
                items.add(temporal);
            }
        }
        return items;
    }
}
