package com.usac.brayan.mensajeriaarquitectura;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Brayan on 19/06/2017.
 */

    public class DialogAsignacion extends DialogFragment {
    String nombre;
    String seccion;
    Button cancelar;
    Button aceptar;
    TextView texto;
    // this method create view for your Dialog


    public static DialogAsignacion newInstance(String nombre, String seccion) {
        DialogAsignacion f = new DialogAsignacion();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("nombre", nombre);
        args.putString("seccion",seccion);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout with recycler view
        View v = inflater.inflate(R.layout.dialog_asignacion, container, false);
        getDialog().setTitle("Confirmación");
        nombre = getArguments().getString("nombre");
        seccion = getArguments().getString("seccion");
        texto = (TextView) v.findViewById(R.id.texto);
        cancelar = (Button) v.findViewById(R.id.btn_cancelar);
        aceptar = (Button) v.findViewById(R.id.btn_aceptar);
        texto.setText(texto.getText().toString().replace("_curso",nombre).replace("_seccion",seccion));
        final DialogFragment myDialog = this;
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServicioNotificacionesFARUSAC.sc.enviarAsignacionCurso(nombre,seccion);
                myDialog.dismiss();
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        //get your recycler view and populate it.
        return v;
    }
}
