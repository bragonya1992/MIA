package com.usac.brayan.mensajeriaarquitectura;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.media.Image;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class MensajesMaestros extends AppCompatActivity {
        private static ChatArrayAdapter chatArrayAdapter;
        private ListView listView;
        private static EditText chatText;
        private FloatingActionButton buttonSend;
        private static boolean side = false;
        public static Curso actualCurso;
        public static Context context;
        public static boolean mIsInForegroundMode=false;
    private ImageButton reads;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mensajesmaestros);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(actualCurso!=null) {
            toolbar.setTitle(actualCurso.nombre + " - " + actualCurso.seccion);
        }
        setSupportActionBar(toolbar);
        buttonSend = (FloatingActionButton) findViewById(R.id.send);
        listView = (ListView) findViewById(R.id.msgview);
        setLista();
        chatArrayAdapter = actualCurso.mensajes;
        listView.setAdapter(chatArrayAdapter);
        reads = (ImageButton) findViewById(R.id.reads);
        listView.setAdapter(chatArrayAdapter);
        chatText = (EditText) findViewById(R.id.msg);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                    ServicioNotificacionesFARUSAC.sc.enviarMensaje(actualCurso.nombre,actualCurso.seccion, StringEscapeUtils.escapeJava(chatText.getText().toString().replaceAll("[\\n\\r]+","\\$32").replace("\"","$33").replace("$34","\'")));
                sendChatMessage(new ChatMessage(2,actualCurso.nombre,actualCurso.seccion,chatText.getText().toString().replaceAll("[\\n\\r]+","\\<br\\>").replace("\"","$33").replace("$34","\'"),"","Hace pocos momentos"));

            }
        });

        reads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ServicioNotificacionesFARUSAC.sc.getAlumnos(actualCurso.nombre,actualCurso.seccion);

            }
        });
        registerReceiver(recieverForEstudiantes, new IntentFilter("recieverForEstudiantes"));
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

    private final BroadcastReceiver recieverForEstudiantes = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Estudiante> response = (ArrayList<Estudiante>) intent.getSerializableExtra("listaAlumnos");
            // DialogFragment.show() will take care of adding the fragment
            // in a transaction.  We also want to remove any currently showing
            // dialog, so make our own transaction and take care of that here.
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            // Create and show the dialog.
            MyDialogFragment newFragment = MyDialogFragment.newInstance(response);
            newFragment.show(ft, "dialog");
        }
    };

    public void setLista(){
        actualCurso.setLista(this,R.layout.right);
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
            //stopService(new Intent(this,ServicioNotificacionesFARUSAC.class));
            ServicioNotificacionesFARUSAC.sc.deleteSesion();
            //this.finish();
            //System.exit(0);
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
