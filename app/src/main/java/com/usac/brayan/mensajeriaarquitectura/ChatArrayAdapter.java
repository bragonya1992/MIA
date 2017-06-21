package com.usac.brayan.mensajeriaarquitectura;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;
    private static LinkedList<String> pila = new LinkedList<>();
    private static int bold1=-1;
    private static int bold2=-1;
    private static int underline1=-1;
    private static int underline2=-1;
    private TextView date;

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public void addFirst(ChatMessage cm){
        chatMessageList.add(0,cm);
        super.notifyDataSetChanged();
    }

    public ChatArrayAdapter(){
        super(null,R.layout.right);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.left==1) {
            row = inflater.inflate(R.layout.right, parent, false);
        }else if(chatMessageObj.left==2){
            row = inflater.inflate(R.layout.pendind, parent, false);
        }else{
            row = inflater.inflate(R.layout.left, parent, false);
        }
        chatText = (TextView) row.findViewById(R.id.msgr);
        chatText.setText(Html.fromHtml(StringEscapeUtils.unescapeJava(convertion(chatMessageObj.getMessage()).replace("$32","<br>").replace("$33","\"").replace("$34","\'"))));
        date = (TextView) row.findViewById(R.id.fecha_msj);
        final String msj =chatMessageObj.getMessage();
        chatText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Publicacion", msj);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(view.getContext(), "El mensaje se ha copiado al portapapeles!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        chatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setData(CalendarContract.Events.CONTENT_URI);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra(CalendarContract.Events.TITLE, msj);
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "FARUSAC");
                    v.getContext().startActivity(intent);
                }catch (Exception e){e.printStackTrace();}
            }
        });

        Linkify.addLinks(chatText, Linkify.WEB_URLS);
        //Linkify.addLinks(chatText, Linkify.WEB_URLS);
        try {
            date.setText(chatMessageObj.getFecha());
        }catch (Exception e){
            e.printStackTrace();
        }
        //chatText.setText(Html.fromHtml("this is <u>underlined</u> text and <b>This text has a color</b>"));
        return row;
    }

    public static String convertion(String cadena){
        int estado=0;
        String tramo="";
        for(int i =0; i < cadena.length(); i++){
            char letra = cadena.charAt(i);
            switch (estado){
                case 0:
                    if(letra=='@'){
                        tramo+=letra;
                        estado=4;
                    }else if (letra=='#'){
                        tramo+=letra;
                        estado=2;
                    }else{
                        tramo+=letra;
                        estado=1;
                    }
                    break;
                case 1:
                    if(letra=='@'){
                        pila.addLast(tramo);
                        tramo="";
                        tramo+=letra;
                        estado=4;
                    }else if (letra=='#'){
                        pila.addLast(tramo);
                        tramo="";
                        tramo+=letra;
                        estado=2;
                    }else{
                        tramo += letra;
                    }
                    break;
                case 2:
                    if(letra=='@'){
                        tramo+=letra;
                        estado=4;
                    }else if (letra=='#'){
                        tramo+=letra;
                        estado=3;
                        i--;
                    }else{
                        tramo+=letra;
                        estado=1;
                    }
                    break;
                case 3:
                    if(letra=='@'){
                        tramo+=letra;
                        estado=4;
                    }else if (letra=='#'){
                        pila.addLast(tramo);
                        asignacionBold(pila.size()-1);
                        tramo="";
                        estado=0;
                    }else{
                        tramo+=letra;
                        estado=1;
                    }
                    break;
                case 4:
                    if(letra=='@'){
                        tramo+=letra;
                        estado=5;
                        i--;
                    }else if (letra=='#'){
                        tramo+=letra;
                        estado=2;
                    }else{
                        tramo+=letra;
                        estado=1;
                    }
                    break;
                case 5:
                    if(letra=='@'){
                        pila.addLast(tramo);
                        asignacionUnderline(pila.size()-1);
                        tramo="";
                        estado=0;
                    }else if (letra=='#'){
                        tramo+=letra;
                        estado=2;
                    }else{
                        tramo+=letra;
                        estado=1;
                    }
                    break;
            }

        }
        if(tramo==""){

        }else if(tramo=="##"){
            pila.addLast(tramo);
            asignacionBold(pila.size()-1);
            tramo="";
        }else if(tramo=="@@"){
            pila.addLast(tramo);
            asignacionUnderline(pila.size()-1);
            tramo="";
        }else{
            pila.addLast(tramo);
            tramo="";
        }
        bold1=-1;
        bold2=-1;
        underline2=-1;
        underline1=-1;
        return generateString();
    }

    public static void asignacionBold(int pos){
        if(bold1==-1){
            bold1=pos;
        }else{
            bold2=pos;
            changeBoldSymbol(bold1,bold2);
            bold1=-1;
            bold2=-1;
        }
    }

    public static void asignacionUnderline(int pos){
        if(underline1==-1){
            underline1=pos;
        }else{
            underline2=pos;
            changeUnderlineSymbol(underline1,underline2);
            underline1=-1;
            underline2=-1;
        }
    }

    public static void changeBoldSymbol(int pos1, int pos2){
        pila.set(pos1,"<b>");
        pila.set(pos2,"</b>");
    }

    public static void changeUnderlineSymbol(int pos1, int pos2){
        pila.set(pos1,"<u>");
        pila.set(pos2,"</u>");
    }

    public static String generateString(){
        String salida="";
        while (!pila.isEmpty()){
            salida+=pila.pop();
        }
        return salida;
    }
}