package com.usac.brayan.mensajeriaarquitectura;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SubMenu;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;


import com.onesignal.OneSignal;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static android.text.Html.FROM_HTML_MODE_LEGACY;
import static com.usac.brayan.mensajeriaarquitectura.R.layout.left;

public class principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NavigationView.OnCreateContextMenuListener, DialogConfirmacion.responseDialogConfirm {
    public static final int NOTIFICATION_ID=1;
    public static NavigationView navigationView;
    public static LinkedList<Curso> listaCursos= new LinkedList<>();
    public static Menu nvMenu;
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
    private static int pagination=0;
    private static ProgressBar circular_progress_bar;
    private static RelativeLayout content_fallback;
    private static LinearLayout content_principal;
    private static int getKey;
    public static boolean mIsInForegroundMode=false;
    public static Handler UIHandler = new Handler(Looper.getMainLooper());
    static Context ct;
    private static boolean loading = true;
    private CheckBox chkAlumnos;
    private CheckBox chkMaestros;
    private EditText content_publication;
    private EditText title_publication;
    private TextView name_info;
    public static int steps=0;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    public static boolean isTutorial=false;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        notificationsNumber=(TextView) findViewById(R.id.textOne);
        circular_progress_bar = (ProgressBar) findViewById(R.id.circular_progress_bar);
        content_circle = (RelativeLayout) findViewById(R.id.content_circle);
        name_info = (TextView) findViewById(R.id.name_info);
        content_fallback = (RelativeLayout) findViewById(R.id.content_fallback);
        content_principal = (LinearLayout) findViewById(R.id.content) ;
        writer=(LinearLayout) findViewById(R.id.writer);
        NavigationView nv=(NavigationView) findViewById(R.id.nav_view);
        nvMenu =nv.getMenu();
        //mapearCursos(nvMenu);
        toolbar.setTitle("Noticias");
        setSupportActionBar(toolbar);

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
        View header=navigationView.getHeaderView(0);
        name_info = (TextView) header.findViewById(R.id.name_info);
        name_info.setText(Autenticacion.sm.getName()/*"Brayan"*/);
        name_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                getKey++;
                if(getKey==5){
                    OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                        @Override
                        public void idsAvailable(String userId, String registrationId) {
                            getKey=0;
                            SessionManager sm = new SessionManager(view.getContext());
                            if(sm.getToken().equals(userId)) {
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("KeyChain", userId);
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(view.getContext(), "Copy keychain to the clipboard!", Toast.LENGTH_SHORT).show();
                            }else{
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("KeyChain", "the keychain is not the same at the token");
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(view.getContext(), "Wrong keychain!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                }
            }
        });
        chkAlumnos = (CheckBox) findViewById(R.id.chkAlumnos);
        chkMaestros = (CheckBox) findViewById(R.id.chkMaestros);
        content_publication = (EditText) findViewById(R.id.content_publication);
        title_publication = (EditText) findViewById(R.id.title_publication);
        IntentAlumnos = new Intent(this, MensajesAlumnos.class);
        IntentMaestros = new Intent(this, MensajesMaestros.class);
        if((publications_list.size()==0 && pagination!=0) || (publications_list.size()!=0 && publications_list.size()!=10 && pagination==0) || (publications_list.size()==0 && pagination==0) ) {
            showLoader();
        }
        ServicioNotificacionesFARUSAC.newInstance(this, new SocketIOSubscriber(){
            @Override
            public void onNext(Object o) {
                /**
                 *
                 **/
                super.onNext(o);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                showFallback();
                hideLoader();

            }
        });
        if(ServicioNotificacionesFARUSAC.sm==null){
            ServicioNotificacionesFARUSAC.sm = new SessionManager(this);
        }

        if(ServicioNotificacionesFARUSAC.sm.getRole()==2) {
            steps=2;
            ServicioNotificacionesFARUSAC.sc.pedirCursosMaestro();
        }else{
            steps=1;
            ServicioNotificacionesFARUSAC.sc.pedirCursosAlumno();
        }
        iniciarAdapter();

        final Activity mAc = this;
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                if(!ServicioNotificacionesFARUSAC.sm.getToken().equals(userId)){
                    ServicioNotificacionesFARUSAC.sc.registrarse(userId);

                    //so.close();
                }

            }
        });

        boolean speeching=getIntent().getBooleanExtra("speech",false);
        isTutorial=speeching;
        if(speeching){
            t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        t1.setLanguage(new Locale("es", "MEX"));
                        t1.setPitch(0);
                        t1.setSpeechRate(0.95f);
                        t1.speak("Bienvenido a mía "+ServicioNotificacionesFARUSAC.sm.getName()+" si deseas ver el tutorial, presiona aceptar, un gusto", TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });

            Log.d("speech","entry");
        }else{
            Log.d("speech","no entry");
        }



    }

    private static void showLoader(){
        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                if(circular_progress_bar!=null)
                circular_progress_bar.setVisibility(View.VISIBLE);
            }
        });
    }

    private static void showFallback(){

        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                if(content_fallback!=null)
                content_fallback.setVisibility(View.VISIBLE);
                if(content_principal!=null)
                content_principal.setVisibility(View.GONE);
            }
        });
    }

    private static void hideLoader(){
        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                if(circular_progress_bar!=null)
                circular_progress_bar.setVisibility(View.GONE);
            }
        });
    }

    private static void hideFallback(){
        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                if(content_fallback!=null)
                content_fallback.setVisibility(View.GONE);
                if(content_principal!=null)
                content_principal.setVisibility(View.VISIBLE);
            }
        });
    }


    public void sendToServerPublication(View v){
        String contenido = content_publication.getText().toString();
        String titulo = title_publication.getText().toString();
        int para =para();
        if(para==-1){
            Toast.makeText(this,"Por favor seleccione a quien desea enviar la publicacion",Toast.LENGTH_LONG).show();
        }else{
            ServicioNotificacionesFARUSAC.sc.publicar(para, StringEscapeUtils.escapeJava(contenido.replaceAll("[\\n\\r]+","\\$32").replace("\"","$33").replace("\"","$33").replace("$34","\'")),titulo);
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


    public static void reconnect(){
        hideFallback();
        showLoader();
        if(ServicioNotificacionesFARUSAC.sc!=null){

                ServicioNotificacionesFARUSAC.sc.connect("reconnect", new SocketIOSubscriber(){
                    @Override
                    public void onNext(Object o) {
                        /**
                         *
                         **/
                        super.onNext(o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        showFallback();
                        hideLoader();
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        if(publications_list.isEmpty()){
                            ServicioNotificacionesFARUSAC.sc.getPublicaciones(ServicioNotificacionesFARUSAC.sm.getRole(),pagination);
                        }else{
                            hideFallback();
                            hideLoader();
                        }
                        if(listaCursos.size()==0){
                            if(ServicioNotificacionesFARUSAC.sm.getRole()==2) {
                                ServicioNotificacionesFARUSAC.sc.pedirCursosMaestro();
                            }else{
                                ServicioNotificacionesFARUSAC.sc.pedirCursosAlumno();
                            }
                        }
                    }
                });
            ServicioNotificacionesFARUSAC.sc.escucharNotificaciones();
        }else{
            ServicioNotificacionesFARUSAC.newInstance(ct, new SocketIOSubscriber(){
                @Override
                public void onNext(Object o) {
                    super.onNext(o);
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    showFallback();
                    hideLoader();
                }

                @Override
                public void onComplete() {
                    super.onComplete();
                    if(publications_list.isEmpty()){
                        ServicioNotificacionesFARUSAC.sc.getPublicaciones(ServicioNotificacionesFARUSAC.sm.getRole(),pagination);
                    }else{
                        hideFallback();
                        hideLoader();
                    }
                    if(listaCursos.size()==0){
                        if(ServicioNotificacionesFARUSAC.sm.getRole()==2) {
                            ServicioNotificacionesFARUSAC.sc.pedirCursosMaestro();
                        }else{
                            ServicioNotificacionesFARUSAC.sc.pedirCursosAlumno();
                        }
                    }

                }
            });
        }

    }

    public void reloaded(View v){
        reconnect();
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
        recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

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
                            showLoader();
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
        hideLoader();
        hideFallback();
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
        hideLoader();
        hideFallback();
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
        //nvMenu.add("Cursos Asignados").setIcon(R.drawable.ic_group_white_24dp).setTitle("Cursos Asignados").setEnabled(false);
        SubMenu sub=nvMenu.addSubMenu("Cursos Asignados").setIcon(R.drawable.ic_group_white_24dp);
        mensajes_totales=0;
        for(int i=0;i<listaCursos.size();i++) {
            MenuItem m=sub.add(listaCursos.get(i).nombre+" - "+listaCursos.get(i).seccion);// Agregar elemento al menu deslizable
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
        if(isTutorial) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            // Create and show the dialog.
            DialogConfirmacion newFragment = new DialogConfirmacion();
            newFragment.setListener(this);
            newFragment.show(ft, "dialogTutorial");
        }
        Log.d("pagination",pagination+"");
        Log.d("pagination lista size",publications_list.size()+"");
        if((publications_list.size()==0 && pagination!=0) || (publications_list.size()!=0 && publications_list.size()!=10 && pagination==0) || (publications_list.size()==0 && pagination==0) ) {
            pagination = 0;
            try {
                publications_list.clear();
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (Autenticacion.sm.getRole() == 2) {
                ServicioNotificacionesFARUSAC.sc.pedirCursosMaestro();
                ServicioNotificacionesFARUSAC.sc.getPublicaciones(ServicioNotificacionesFARUSAC.sm.getRole(), pagination);
            } else {
                ServicioNotificacionesFARUSAC.sc.pedirCursosAlumno();
                ServicioNotificacionesFARUSAC.sc.getPublicaciones(ServicioNotificacionesFARUSAC.sm.getRole(), pagination);
            }
            showLoader();
        }
        if(principal.listaCursos!=null) {
            if (principal.listaCursos.size()==0) {
                if (Autenticacion.sm.getRole() == 2) {
                    ServicioNotificacionesFARUSAC.sc.pedirCursosMaestro();
                } else {
                    ServicioNotificacionesFARUSAC.sc.pedirCursosAlumno();
                }
            }
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

            //stopService(new Intent(this,ServicioNotificacionesFARUSAC.class));
            ServicioNotificacionesFARUSAC.sc.deleteSesion();

            //this.finish();
            //System.exit(0);
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

    @Override
    public void onCreateContextMenu (ContextMenu menu,
                              View v,
                              ContextMenu.ContextMenuInfo menuInfo){

    }

    @Override
    public void onConfirm() {
        final Toolbar tb = (Toolbar) this.findViewById(R.id.toolbar);
        final Activity ac =this;
        new MaterialTapTargetPrompt.Builder(principal.this)
                .setTarget(tb.getChildAt(1))
                .setPrimaryText("Cursos")
                .setSecondaryText("¡Cuando te asignes cursos aparecerán si das un tap aquí! \n ¡También aparecerá un icono cuando tengas notificaciones nuevas en tus cursos!")
                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener()
                {

                    @Override
                    public void onHidePrompt(MotionEvent event, boolean tappedTarget)
                    {
                        //TODO: Store in SharedPrefs so you don't show this prompt again.
                    }

                    @Override
                    public void onHidePromptComplete()
                    {
                        new MaterialTapTargetPrompt.Builder(principal.this)
                                .setTarget(tb.getChildAt(2))
                                .setPrimaryText("Opciones")
                                .setSecondaryText("-En este menu puedes cerrar sesión \n -Si eres alumno, también puedes asignarte cursos")
                                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener()
                                {
                                    @Override
                                    public void onHidePrompt(MotionEvent event, boolean tappedTarget)
                                    {
                                        //TODO: Store in SharedPrefs so you don't show this prompt again.
                                    }

                                    @Override
                                    public void onHidePromptComplete()
                                    {
                                        View rootView = ac.getWindow().getDecorView().findViewById(android.R.id.content);
                                        final Snackbar snack = Snackbar.make(rootView, "Presiona el reloj en una noticia y podrás agendarla o con un tap largo copiarás su contenido", Snackbar.LENGTH_INDEFINITE);
                                        View snackview = snack.getView();
                                        TextView snacktext = (TextView) snackview.findViewById(android.support.design.R.id.snackbar_text);
                                        snacktext.setMaxLines(5);
                                        snack.show();
                                                //.setActionTextColor(Color.CYAN)
                                        Thread thread = new Thread() {
                                            @Override
                                            public void run() {
                                                try {
                                                        sleep(5000);
                                                        steps=steps-1;
                                                    if (steps==0) {
                                                        isTutorial=false;
                                                    }
                                                        snack.dismiss();
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };

                                        thread.start();
                                    }
                                })
                                .show();
                    }
                })
                .show();
    }
}
