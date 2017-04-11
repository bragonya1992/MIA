package com.usac.brayan.mensajeriaarquitectura;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
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

public class MensajesAlumnos extends AppCompatActivity {
        private static ChatArrayAdapter chatArrayAdapter;
        private ListView listView;
        private static EditText chatText;
        private Button buttonSend;
        private static boolean side = false;
        public static Context context;
        public static Curso actualCurso;
        public static boolean mIsInForegroundMode=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_alumnos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(actualCurso.nombre+" - "+actualCurso.seccion);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.msgviewA);
        setLista();
        chatArrayAdapter = actualCurso.mensajes;
        listView.setAdapter(chatArrayAdapter);


//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();


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
                        ServicioNotificacionesFARUSAC.sc.pedirTopAlumno(totalItemCount,totalItemCount+10,actualCurso.nombre,actualCurso.seccion);
                    }else{
                        contador++;
                    }
                }
            }
        });


        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);
        context=this;
        //principal.mapearCursos(navigationView.getMenu());
    }

    public static boolean sendChatMessage(ChatMessage cm) {
        chatArrayAdapter.add(cm);
        chatText.setText("");
        return true;
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
        getMenuInflater().inflate(R.menu.principal_alumnos, menu);
        return true;
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
            ServicioNotificacionesFARUSAC.sc.pedirMensajesAlumno(actualCurso.nombre, actualCurso.seccion);
        }
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

}
