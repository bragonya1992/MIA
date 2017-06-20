package com.usac.brayan.mensajeriaarquitectura;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

public class Registro extends AppCompatActivity {
    EditText txtNombre;
    EditText txtPassword;
    EditText txtCodigo;
    EditText txtPasswordConfirm;
    ProgressBar circular_progress_bar;
    public static boolean isError=true;
    Button btnRegistrar;
    Spinner sp;
    public static SocketIO so;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        txtNombre= (EditText) findViewById(R.id.txtNombre);
        txtPassword= (EditText) findViewById(R.id.txtPassword);
        txtCodigo= (EditText) findViewById(R.id.txtCodigo);
        sp = (Spinner) findViewById(R.id.spinner);
        txtPasswordConfirm = (EditText) findViewById(R.id.txtPasswordConfirm);
        circular_progress_bar= (ProgressBar) findViewById(R.id.circular_progress_bar);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        registerReceiver(abcd, new IntentFilter("xyz"));
    }

    public void registrar(View v){
        String p1 =txtPassword.getText().toString();
        String p2 =txtPasswordConfirm.getText().toString();
        final Activity miAc=this;
        if(p1.equals(p2)) {
            if(txtPassword.getText().length()>7) {
                if(txtCodigo.getText().length()== 13) {
                    so = new SocketIO(this, new SocketIOSubscriber(){
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
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    btnRegistrar.setEnabled(true);
                                    btnRegistrar.setEnabled(true);
                                    txtNombre.setEnabled(true);
                                    txtPassword.setEnabled(true);
                                    txtPasswordConfirm.setEnabled(true);
                                    circular_progress_bar.setVisibility(View.GONE);
                                    if(isError)
                                    Toast.makeText(miAc,"Ha ocurrido un error de conexión, por favor vuelva a intentarlo",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                    so.esperarRegistro();
                    so.registrarUsuario(txtCodigo.getText().toString(), txtNombre.getText().toString(), sp.getSelectedItem().toString(), txtPassword.getText().toString());
                    circular_progress_bar.setVisibility(View.VISIBLE);
                    btnRegistrar.setEnabled(false);
                    txtNombre.setEnabled(false);
                    txtPassword.setEnabled(false);
                    txtPasswordConfirm.setEnabled(false);
                }else{
                    Toast.makeText(this,"Su CUI debe tener de 13 digitos",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this,"Su contraseña debe ser mayor a 7 digitos",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this,"Por favor verifique que la confirmación de contraseña sea igual",Toast.LENGTH_LONG).show();
        }
    }

    private final BroadcastReceiver abcd = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean response = intent.getBooleanExtra("estado",false);
            if(response) {
                isError=false;
                finish();
            }else{
                btnRegistrar.setEnabled(true);
                btnRegistrar.setEnabled(true);
                txtNombre.setEnabled(true);
                txtPassword.setEnabled(true);
                txtPasswordConfirm.setEnabled(true);
                circular_progress_bar.setVisibility(View.GONE);
            }
        }
    };
}
