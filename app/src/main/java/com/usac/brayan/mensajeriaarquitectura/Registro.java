package com.usac.brayan.mensajeriaarquitectura;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class Registro extends AppCompatActivity {
    EditText txtNombre;
    EditText txtPassword;
    EditText txtCodigo;
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
    }

    public void registrar(View v){
        so=new SocketIO(this);
        so.esperarRegistro();
        so.registrarUsuario(txtCodigo.getText().toString(),txtNombre.getText().toString(),sp.getSelectedItem().toString(),txtPassword.getText().toString());
    }
}
