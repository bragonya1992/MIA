package com.usac.brayan.mensajeriaarquitectura;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Brayan on 26/12/2016.
 */
public class AdaptadorDeCurso extends RecyclerView.Adapter<AdaptadorDeCurso.CursoViewHolder> {

    private List<Curso> items;
    static List<Curso> mContactFilter;
    Filtro filter;
    private Context mContext;
    public static class CursoViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public TextView nombre;
        public TextView seccion;
        public TextView catedratico;
        public Button button_asignacion;
        private View view;

        public CursoViewHolder(View v) {
            super(v);
            nombre = (TextView) v.findViewById(R.id.nombreCursoCard);
            seccion = (TextView) v.findViewById(R.id.seccionCard);
            catedratico = (TextView) v.findViewById(R.id.catedraticoCard);
            button_asignacion = (Button) v.findViewById(R.id.button_asignacion);
        }

        /*@Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem myActionItem = menu.add(mContactFilter.get(getAdapterPosition()).nombre+","+mContactFilter.get(getAdapterPosition()).seccion);
            menu.setHeaderTitle("¿Está seguro que desea asignarse este curso?");
            menu.setHeaderIcon(R.drawable.ic_course);
            myActionItem.setOnMenuItemClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Log.d("Voy a asignar el curso "+mContactFilter.get(getAdapterPosition()).nombre,"ASIGNAOR");
            ServicioNotificacionesFARUSAC.sc.enviarAsignacionCurso(mContactFilter.get(getAdapterPosition()).nombre,mContactFilter.get(getAdapterPosition()).seccion);
            return true;
        }*/
    }

    public AdaptadorDeCurso(List<Curso> items,Context mContext) {

        this.items = items;
        this.mContactFilter=items;
        filter=new Filtro(this.items,this);
        this.mContext=mContext;
    }

    public void add(Curso nuevo){
        this.items.add(nuevo);
    }

    @Override
    public int getItemCount() {
        return mContactFilter.size();
    }

    @Override
    public CursoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card, viewGroup, false);
        return new CursoViewHolder(v);
    }
    public void setList(List<Curso> list) {
        this.mContactFilter = list;
    }
    //call when you want to filter
    public void filterList(String text) {
        filter.filter(text);
    }

    @Override
    public void onBindViewHolder(CursoViewHolder viewHolder, int i) {
        viewHolder.nombre.setText(mContactFilter.get(i).nombre);
        viewHolder.seccion.setText("Seccion:"+String.valueOf(mContactFilter.get(i).seccion));
        viewHolder.catedratico.setText("Catedrático: "+ String.valueOf(mContactFilter.get(i).catedratico));
        final String name=mContactFilter.get(i).nombre;
        final String section =String.valueOf(mContactFilter.get(i).seccion);
        viewHolder.button_asignacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = ((Activity)mContext).getFragmentManager().beginTransaction();
                DialogAsignacion newFragment = DialogAsignacion.newInstance(name,section);
                newFragment.show(ft, "dialog");
            }
        });
    }
}
