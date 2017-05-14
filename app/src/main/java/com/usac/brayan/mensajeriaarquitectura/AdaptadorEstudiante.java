package com.usac.brayan.mensajeriaarquitectura;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Brayan on 12/02/2017.
 */
public class AdaptadorEstudiante extends RecyclerView.Adapter<AdaptadorEstudiante.EstudianteViewHolder> {
    private List<Estudiante> items;

    public static class EstudianteViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public TextView nombre;
        public TextView cui;

        public EstudianteViewHolder(View v) {
            super(v);
            nombre = (TextView) v.findViewById(R.id.nombreestudiante);
            cui = (TextView) v.findViewById(R.id.cuiestudiante);
        }
    }

    public AdaptadorEstudiante(List<Estudiante> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public EstudianteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardstudents, viewGroup, false);
        return new EstudianteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EstudianteViewHolder viewHolder, int i) {
        viewHolder.cui.setText(Html.fromHtml(items.get(i).getCui()));
        viewHolder.nombre.setText(items.get(i).getNombre());
    }
}