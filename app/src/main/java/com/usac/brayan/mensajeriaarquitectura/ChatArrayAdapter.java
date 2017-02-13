package com.usac.brayan.mensajeriaarquitectura;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;
    private LinkedList<String> pila = new LinkedList<>();
    private int bold1=-1;
    private int bold2=-1;
    private int underline1=-1;
    private int underline2=-1;

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
        chatText.setText(Html.fromHtml(convertion(chatMessageObj.getMessage())));
        //chatText.setText(Html.fromHtml("this is <u>underlined</u> text and <b>This text has a color</b>"));
        return row;
    }

    public String convertion(String cadena){
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

    public void asignacionBold(int pos){
        if(bold1==-1){
            bold1=pos;
        }else{
            bold2=pos;
            changeBoldSymbol(bold1,bold2);
            bold1=-1;
            bold2=-1;
        }
    }

    public void asignacionUnderline(int pos){
        if(underline1==-1){
            underline1=pos;
        }else{
            underline2=pos;
            changeUnderlineSymbol(underline1,underline2);
            underline1=-1;
            underline2=-1;
        }
    }

    public  void changeBoldSymbol(int pos1, int pos2){
        pila.set(pos1,"<b>");
        pila.set(pos2,"</b>");
    }

    public  void changeUnderlineSymbol(int pos1, int pos2){
        pila.set(pos1,"<u>");
        pila.set(pos2,"</u>");
    }

    public String generateString(){
        String salida="";
        while (!pila.isEmpty()){
            salida+=pila.pop();
        }
        return salida;
    }
}