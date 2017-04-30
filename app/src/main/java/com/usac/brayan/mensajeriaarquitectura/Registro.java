package com.usac.brayan.mensajeriaarquitectura;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Registro extends AppCompatActivity {
    EditText txtNombre;
    EditText txtPassword;
    EditText txtCodigo;
    EditText txtPasswordConfirm;
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
        registerReceiver(abcd, new IntentFilter("xyz"));
    }

    public void registrar(View v){
        String p1 =txtPassword.getText().toString();
        String p2 =txtPasswordConfirm.getText().toString();
        if(p1.equals(p2)) {
            if(txtPassword.getText().length()>7) {
                if(txtCodigo.getText().length()== 13) {
                    so = new SocketIO(this);
                    so.esperarRegistro();
                    so.registrarUsuario(txtCodigo.getText().toString(), txtNombre.getText().toString(), sp.getSelectedItem().toString(), txtPassword.getText().toString());
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
            finish();
        }
    };
}
