package com.usac.brayan.mensajeriaarquitectura;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static android.widget.Toast.LENGTH_SHORT;

public class MensajesMaestros extends AppCompatActivity {
        private static ChatArrayAdapter chatArrayAdapter;
        private ListView listView;
        private static EditText chatText;
        private Button buttonSend;
        private static boolean side = false;
        public static Curso actualCurso;
        public static Context context;
        public static boolean mIsInForegroundMode=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mensajesmaestros);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(actualCurso.nombre+" - "+ actualCurso.seccion);
        setSupportActionBar(toolbar);
        buttonSend = (Button) findViewById(R.id.send);
        listView = (ListView) findViewById(R.id.msgview);
        setLista();
        chatArrayAdapter = actualCurso.mensajes;
        Log.d("FUERA VER MENSAJES","Adaptados a la lista "+actualCurso.mensajes.getCount());
        Log.d("FUERA VER MENSAJES","CharArrayAdapter "+chatArrayAdapter.getCount());
        listView.setAdapter(chatArrayAdapter);
        //chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);
        chatText = (EditText) findViewById(R.id.msg);
        //chatText.setText(Html.fromHtml("this is <u>underlined</u> text and <b>This text has a color</b>"));
//        chatText.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                Toast.makeText(getApplicationContext(),chatText.getText().toString(),Toast.LENGTH_SHORT).show();
//
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//
//                    return sendChatMessage();
//                }
//                return false;
//            }
//
//        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                    ServicioNotificacionesFARUSAC.sc.enviarMensaje(actualCurso.nombre,actualCurso.seccion, chatText.getText().toString().replaceAll("[\\n\\r]+","\\$32"));
                sendChatMessage(new ChatMessage(2,actualCurso.nombre,actualCurso.seccion,chatText.getText().toString().replaceAll("[\\n\\r]+","\\<br\\>"),"","Hace pocos momentos"));

            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }
            int contador=0;
            int peticiones=0;
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("FIRST: ",""+firstVisibleItem);
                if(firstVisibleItem==0){
                    if(contador==10) {
                        Toast.makeText(getApplicationContext(), "Estoy en el tope "+peticiones, Toast.LENGTH_SHORT).show();
                        peticiones++;
                        Log.d("PETICIONES:",""+peticiones);
                        contador=0;
                        ServicioNotificacionesFARUSAC.sc.pedirTopMaestro(totalItemCount,totalItemCount+10,actualCurso.nombre,actualCurso.seccion);
                    }else{
                        contador++;
                    }
                }
            }
        });



        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
           //     this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        //toggle.syncState();
        context=this;

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //navigationView.setNavigationItemSelectedListener(this);
        //principal.mapearCursos(navigationView.getMenu());
    }

    public void setLista(){
        actualCurso.setLista(getApplicationContext(),R.layout.right);
        actualCurso.mensajes.clear();
        Log.d("CONTADOR DE Messages",""+actualCurso.mensajes.getCount());
        int nCola= actualCurso.cola.size();
        while (!actualCurso.cola.isEmpty()) {
            chatArrayAdapter.add(actualCurso.cola.getFirst());
            actualCurso.cola.removeFirst();
        }
        if(nCola<11) {
            ServicioNotificacionesFARUSAC.sc.pedirMensajesMaestro(actualCurso.nombre, actualCurso.seccion);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsInForegroundMode = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsInForegroundMode = true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mensajes_maestros, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String nombre =(String)item.getTitle();
        //manipular las acciones del menu de los tres puntos
        //noinspection SimplifiableIfStatement
        if (nombre.equals("Cerrar Sesion")) {
            Autenticacion.sm.logoutUser();
            stopService(new Intent(this,ServicioNotificacionesFARUSAC.class));
            this.finish();
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static boolean sendChatMessage(ChatMessage cm) {
        chatArrayAdapter.add(cm);
        chatText.setText("");
        //side = !side;
        return true;
    }
}
