package com.usac.brayan.mensajeriaarquitectura;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.text.Html.FROM_HTML_MODE_LEGACY;
import static com.usac.brayan.mensajeriaarquitectura.R.layout.left;

public class principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        public static final int NOTIFICATION_ID=1;
        public static NavigationView navigationView;
        public static LinkedList<Curso> listaCursos= new LinkedList<>();
        public static Menu nvMenu;
        public static TextView tx;
        public static TextView notificationsNumber;
        public static RelativeLayout content_circle;
        public static List publications_list = new ArrayList();
        public static LinearLayout writer;
        public  Intent IntentAlumnos;
        public  Intent IntentMaestros;
        private static RecyclerView recycler;
        private static RecyclerView.Adapter adapter;
        private WrapContentLinearLayoutManager lManager;
        private static int mensajes_totales=0;
        private int pagination=0;
        public static boolean mIsInForegroundMode=false;
        static Context ct;
    private static boolean loading = true;
        private CheckBox chkAlumnos;
        private CheckBox chkMaestros;
        private EditText content_publication;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tx = (TextView) findViewById(R.id.textView2);
        notificationsNumber=(TextView) findViewById(R.id.textOne);
        content_circle = (RelativeLayout) findViewById(R.id.content_circle);
        writer=(LinearLayout) findViewById(R.id.writer);
        if(ServicioNotificacionesFARUSAC.sm.getRole()==2) {
            toolbar.setTitle("Modo para docentes"); // titulo de la ventana
            ServicioNotificacionesFARUSAC.sc.pedirCursosMaestro();
        }else{
            toolbar.setTitle("Modo para estudiantes"); // titulo de la ventana
            ServicioNotificacionesFARUSAC.sc.pedirCursosAlumno();
        }
        ServicioNotificacionesFARUSAC.sc.cancelNotification();
        NavigationView nv=(NavigationView) findViewById(R.id.nav_view);
        nvMenu =nv.getMenu();
        //mapearCursos(nvMenu);
        setSupportActionBar(toolbar);
        tx.setText(Html.fromHtml("Bienvenido a MIA  <b><u>"+Autenticacion.sm.getName()+"</u></b>, estas son tus noticias")); // for 24 api and more

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServicioNotificacionesFARUSAC.sc.authPublication();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        ct=this;
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        chkAlumnos = (CheckBox) findViewById(R.id.chkAlumnos);
        chkMaestros = (CheckBox) findViewById(R.id.chkMaestros);
        content_publication = (EditText) findViewById(R.id.content_publication);
        IntentAlumnos = new Intent(this, MensajesAlumnos.class);
        IntentMaestros = new Intent(this, MensajesMaestros.class);
        iniciarAdapter();
    }


    public void sendToServerPublication(View v){
        String contenido = content_publication.getText().toString();
        int para =para();
        if(para==-1){
            Toast.makeText(this,"Por favor seleccione a quien desea enviar la publicacion",Toast.LENGTH_LONG).show();
        }else{
            ServicioNotificacionesFARUSAC.sc.publicar(para,contenido.replaceAll("[\\n\\r]+","\\$32"));
            writer.setVisibility(View.GONE);
            content_publication.setText("");
        }
    }

    private int para(){
        if(chkAlumnos.isChecked() && !chkMaestros.isChecked()){
            return 1;
        }else if(!chkAlumnos.isChecked() && chkMaestros.isChecked()){
            return 2;
        }else if(chkAlumnos.isChecked() && chkMaestros.isChecked()){
            return 0;
        }else{
            return -1;
        }
    }


    public static void AsignarCursos(String cadena) throws JSONException {
        principal.listaCursos.clear();
        JSONObject jsonObj = new JSONObject(cadena);
        JSONArray jsonArray = jsonObj.getJSONArray("cursos");
        for(int i=0;i<jsonArray.length();i++){
            JSONObject temp =jsonArray.getJSONObject(i);
            String nombre= temp.getString("nombre");
            String seccion = temp.getString("seccion");
            String catedratico = temp.getString("catedratico");
            Curso nuevo;
            if(temp.has("contador")) {
                nuevo = new Curso(nombre, seccion, catedratico,temp.getInt("contador"));
            }else{
                nuevo = new Curso(nombre, seccion, catedratico,0);
            }
            principal.listaCursos.addLast(nuevo);
        }
        principal.mapearCursos();

    }


    public void iniciarAdapter(){

/*
        items.add(new Publicacion("Este es un mensaje desde el servidor de FARUSAC","Todos","12/12/2012"));
        items.add(new Publicacion("Aqui enviaremos mensajes de forma general","Todos","12/12/2012"));
        items.add(new Publicacion("Tambien podemos enviar mensajes a un grupo en especifico, por ejemplo a los maestros o alumnos segun sea el caso","Maestros","12/12/2012"));
        items.add(new Publicacion("Tambien podemos enviar mensajes a un grupo en especifico, por ejemplo a los maestros o alumnos segun sea el caso","Alumnos","12/12/2012"));
        items.add(new Publicacion("Bienvenidos a Mensajeria FARUSAC, espero sea de su agrado","TODOS","12/12/2012"));
*/

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.recicladorPublicaciones);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new WrapContentLinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new AdaptadorPublicacion(publications_list);
        recycler.setAdapter(adapter);
        final Activity miActivity = this;
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = lManager.getChildCount();
                    totalItemCount = lManager.getItemCount();
                    pastVisiblesItems = lManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            pagination=pagination+1;
                            ServicioNotificacionesFARUSAC.sc.getPublicaciones(ServicioNotificacionesFARUSAC.sm.getRole(),pagination);
                            //Do pagination.. i.e. fetch new data
                        }
                    }
                }
            }
        });
        //ServicioNotificacionesFARUSAC.sc.getPublicaciones(ServicioNotificacionesFARUSAC.sm.getRole(),pagination);
    }

    public static void addPublications(List<Publicacion> newMessages){
        if(adapter!=null) {
            publications_list.addAll(newMessages);
            adapter.notifyDataSetChanged();
            loading = true;
            if (newMessages.size() > 0) {
                if (newMessages.get(0).idPublicacion > ServicioNotificacionesFARUSAC.sm.getLastPublicationRegister()) {
                    ServicioNotificacionesFARUSAC.sm.setLastPublicationRegister(newMessages.get(0).idPublicacion);
                }
            }
        }
    }

    public static void addPublicationFirst(List<Publicacion> newMessages){
        if(adapter!=null) {
            publications_list.add(0, newMessages.get(0));
            adapter.notifyDataSetChanged();
            loading = true;
            if (newMessages.size() > 0) {
                if (newMessages.get(0).idPublicacion > ServicioNotificacionesFARUSAC.sm.getLastPublicationRegister()) {
                    ServicioNotificacionesFARUSAC.sm.setLastPublicationRegister(newMessages.get(0).idPublicacion);
                }
            }
        }
    }

    public static Curso buscarCurso(String nombre, String seccion){
        for(int i=0;i<listaCursos.size();i++){
            if(listaCursos.get(i).nombre.equals(nombre) && listaCursos.get(i).seccion.equals(seccion)){
                return listaCursos.get(i);
            }
        }
        return  null;
    }




    public void addIcon(MenuItem menu, int i,Context ct){
        TextView tv = new TextView(ct);
        tv.setText(""+i);
        tv.setBackgroundColor(Color.RED);
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundResource(R.drawable.badge_circle);
        tv.setTypeface(null, Typeface.BOLD);
        //tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;

        tv.setLayoutParams(params);
        tv.setTextSize(12);
        menu.setActionView(tv).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    public static void mapearCursos(){
        nvMenu.clear();
        mensajes_totales=0;
        for(int i=0;i<listaCursos.size();i++) {
            MenuItem m=nvMenu.add(listaCursos.get(i).nombre+" - "+listaCursos.get(i).seccion);// Agregar elemento al menu deslizable
            int n=listaCursos.get(i).contador;
            if(n>0) {
                principal p = new principal();
                mensajes_totales+=n;
                p.addIcon(m, n, ct);
            }
        }
        if(mensajes_totales>0){
            content_circle.setVisibility(View.VISIBLE);
            notificationsNumber.setText(""+mensajes_totales);
        }else{
            content_circle.setVisibility(View.GONE);
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Opciones de tres puntos
        if(Autenticacion.sm.getRole()!=2){
            menu.add("Listado de cursos");
        }
        getMenuInflater().inflate(R.menu.principal, menu);
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
        pagination=0;
        publications_list.clear();
        if(Autenticacion.sm.getRole()==2){
            ServicioNotificacionesFARUSAC.sc.pedirCursosMaestro();
            ServicioNotificacionesFARUSAC.sc.getPublicaciones(ServicioNotificacionesFARUSAC.sm.getRole(),pagination);
        }else{
            ServicioNotificacionesFARUSAC.sc.pedirCursosAlumno();
            ServicioNotificacionesFARUSAC.sc.getPublicaciones(ServicioNotificacionesFARUSAC.sm.getRole(),pagination);
        }
        mIsInForegroundMode = true;
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
        } else if (nombre.equals("Listado de cursos")) {
            Intent myIntent = new Intent(this, AsignacionDeCursos.class);
            startActivityForResult(myIntent, 0);
            this.finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //cuando presiona un item, para poder manipularlo
        int id = item.getItemId();
        String nombre=item.getTitle().toString();
        String[] nomSec= nombre.split("-");
        String nombreB=nomSec[0];
        String seccionB=nomSec[1];
        for(int i=0;i<listaCursos.size();i++){

            if(nombreB.equals(listaCursos.get(i).nombre+" ") && seccionB.equals(" "+listaCursos.get(i).seccion)){
                if(Autenticacion.sm.getRole()==2) {
                    MensajesMaestros.actualCurso = listaCursos.get(i);
                    Intent myIntent = new Intent(this, MensajesMaestros.class);
                    startActivityForResult(myIntent, 0);
                }else{
                    MensajesAlumnos.actualCurso = listaCursos.get(i);
                    Intent myIntent = new Intent(this, MensajesAlumnos.class);
                    //myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(myIntent, 0);
                }
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
