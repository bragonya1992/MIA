package com.usac.brayan.mensajeriaarquitectura;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Brayan on 12/02/2017.
 */
public class AdaptadorPublicacion extends RecyclerView.Adapter<AdaptadorPublicacion.PublicacionViewHolder> {
    private List<Publicacion> items;

    public static class PublicacionViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public TextView contenido;
        public TextView fecha;
        public TextView para;

        public PublicacionViewHolder(View v) {
            super(v);
            contenido = (TextView) v.findViewById(R.id.contenidoPublicacion);
            fecha = (TextView) v.findViewById(R.id.fechaPublicacion);
            para = (TextView) v.findViewById(R.id.paraPublicacion);
        }
    }

    public AdaptadorPublicacion(List<Publicacion> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public PublicacionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardforpublications, viewGroup, false);
        return new PublicacionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PublicacionViewHolder viewHolder, int i) {
        viewHolder.contenido.setText(items.get(i).getContenido());
        viewHolder.fecha.setText(items.get(i).getFecha());
        viewHolder.para.setText("Para:"+String.valueOf(items.get(i).getPara()));
    }
}