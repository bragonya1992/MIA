package com.usac.brayan.mensajeriaarquitectura;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.tooltip.OnClickListener;
import com.tooltip.Tooltip;

public class Autenticacion extends AppCompatActivity {
    static Button btnIng;
    static EditText txtCarne;
    static EditText txtPass;
    Spinner sp;
    public static SessionManager sm;
    public static SharedPreferences.Editor editor;
    public static Context mContext;
    public static SocketIO so;
    public static Activity actividad;
    static ProgressBar wait;
    ImageView chartImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacion);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnIng = (Button) findViewById(R.id.btnIngresar);
        txtCarne = (EditText) findViewById(R.id.txtCarne);
        txtPass=(EditText)findViewById(R.id.txtPass);
        sp=(Spinner) findViewById(R.id.spinner);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        wait =(ProgressBar) findViewById(R.id.pbHeaderProgress);
        sm = new SessionManager(this);
        if(sm.isLoggedIn()){
            ServicioNotificacionesFARUSAC.newInstance(this);
            this.startActivity(new Intent(this, principal.class));
            this.finish();
        }else{
            Tooltip tooltip = new Tooltip.Builder(fab).setGravity(Gravity.TOP)
                    .setText("¡Registrate aquí!")
                    .show();
            tooltip.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(@NonNull Tooltip tooltip) {
                    tooltip.dismiss();
                }
            });
        }

        final Activity mActivity= this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(new Intent(mActivity,Registro.class));

            }
        });
        mContext=this;
        actividad=this;
    }

    public void onClick(View view)  {
        so = new SocketIO(mContext);
        so.esperarRespuesta();
        so.solicitarAutenticacion(txtCarne.getText().toString(),sp.getSelectedItem().toString(),txtPass.getText().toString());
        view.setEnabled(false);
        wait.setVisibility(View.VISIBLE);
        txtCarne.setEnabled(false);
        txtPass.setEnabled(false);
    }

    public static void entrar(String nombre, String carne, int role){
        Autenticacion.sm.createLoginSession(nombre,role,carne);
        so.close();
        mContext.startActivity(new Intent(mContext, principal.class));
        final Activity activity= actividad;
        ServicioNotificacionesFARUSAC.newInstance(activity);
        sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken(),activity);
        activity.finish();
        btnIng.setEnabled(true);
        txtCarne.setEnabled(true);
        txtPass.setEnabled(true);
    }

    public static void noEntrar(){
        so.close();
        btnIng.setEnabled(true);
        txtCarne.setEnabled(true);
        txtPass.setEnabled(true);
        //setProgressBarIndeterminateVisibility(false);
        wait.setVisibility(View.GONE);
    }

    private static void sendRegistrationToServer(String token,Activity a) {
        // Add custom implementation, as needed.
        ServicioNotificacionesFARUSAC.newInstance(a);
        if(token!=null) {
            ServicioNotificacionesFARUSAC.sc.registrarse(token);
        }else{
            Toast.makeText(a,"Lamentablemente aún no tienes token para recibir notificaciones en MIA, las notificaciones te llegarán hasta que un token sea generado por tu dispositivo :(",Toast.LENGTH_LONG).show();
        }
    }

}
