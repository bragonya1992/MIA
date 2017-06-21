package com.usac.brayan.mensajeriaarquitectura;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by brayan on 21/06/17.
 */

public class DialogConfirmacion extends DialogFragment {
    responseDialogConfirm listener;

    public void setListener(responseDialogConfirm listener){
        this.listener=listener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        builder.setMessage("¿Desea ver el tutorial?")
                .setTitle("Confirmación")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()  {
                    public void onClick(DialogInterface dialog, int id) {
                        if(listener!=null){
                            listener.onConfirm();
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    public interface responseDialogConfirm{
        void onConfirm();
    }
}