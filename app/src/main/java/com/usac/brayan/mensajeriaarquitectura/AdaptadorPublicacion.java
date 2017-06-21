package com.usac.brayan.mensajeriaarquitectura;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


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
        public TextView titulo;

        public PublicacionViewHolder(View v) {
            super(v);
            contenido = (TextView) v.findViewById(R.id.contenidoPublicacion);
            fecha = (TextView) v.findViewById(R.id.fechaPublicacion);
            para = (TextView) v.findViewById(R.id.paraPublicacion);
            titulo = (TextView) v.findViewById(R.id.post_title);
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Publicacion", contenido.getText());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(view.getContext(), "El contenido se ha copiado al portapapeles!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_INSERT);
                        intent.setData(CalendarContract.Events.CONTENT_URI);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra(CalendarContract.Events.TITLE, titulo.getText()+" : "+contenido.getText());
                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "FARUSAC");
                        intent.putExtra(CalendarContract.Events.DESCRIPTION, contenido.getText());
                        intent.putExtra(CalendarContract.Events.STATUS, contenido.getText());
                        v.getContext().startActivity(intent);
                    }catch (Exception e){e.printStackTrace();}
                }
            });

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
        viewHolder.contenido.setText(Html.fromHtml(ChatArrayAdapter.convertion(items.get(i).getContenido())));
        viewHolder.fecha.setText(items.get(i).getFecha());
        viewHolder.para.setText("Para:"+String.valueOf(items.get(i).getPara()));
        viewHolder.titulo.setText(items.get(i).getTitulo());
        Linkify.addLinks(viewHolder.contenido, Linkify.WEB_URLS);
    }
}