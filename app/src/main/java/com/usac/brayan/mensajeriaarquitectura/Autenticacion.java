package com.usac.brayan.mensajeriaarquitectura;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Autenticacion extends AppCompatActivity {
    Button btnIng;
    EditText txtCarne;
    EditText txtPass;
    Spinner sp;
    public static SessionManager sm;
    public static SharedPreferences.Editor editor;
    public static Context mContext;
    public static SocketIO so;
    public static Activity actividad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnIng = (Button) findViewById(R.id.btnIngresar);
        txtCarne = (EditText) findViewById(R.id.txtCarne);
        txtPass=(EditText)findViewById(R.id.txtPass);
        sp=(Spinner) findViewById(R.id.spinner);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        sm = new SessionManager(this);
        if(sm.isLoggedIn()){
            this.startService(new Intent(this,ServicioNotificacionesFARUSAC.class));
            this.startActivity(new Intent(this, principal.class));
            this.finish();
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

    }

    public static void entrar(String nombre, int carne, int role){
        Autenticacion.sm.createLoginSession(nombre,role,carne);
        so.close();
        mContext.startActivity(new Intent(mContext, principal.class));
        mContext.startService(new Intent(mContext,ServicioNotificacionesFARUSAC.class));
        final Activity activity= actividad;
        activity.finish();
    }

    public static void noEntrar(String mensaje){
        Toast.makeText(mContext,mensaje,Toast.LENGTH_LONG).show();
        so.close();
    }

}
