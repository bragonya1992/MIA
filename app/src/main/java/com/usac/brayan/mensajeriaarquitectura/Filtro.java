package com.usac.brayan.mensajeriaarquitectura;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brayan on 27/12/2016.
 */
public class Filtro extends Filter {
    private List<Curso> contactList;
    private List<Curso> filteredContactList;
    private AdaptadorDeCurso adapter;


    public Filtro(List<Curso> contactList, AdaptadorDeCurso adapter){
        this.adapter=adapter;
        this.contactList=contactList;
        this.filteredContactList = new ArrayList<>();
    }
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredContactList.clear();
        final FilterResults results = new FilterResults();

        //here you need to add proper items do filteredContactList
        for (final Curso item : contactList) {
            if (item.nombre.toLowerCase().trim().contains(constraint)) {
                filteredContactList.add(item);
            }
        }

        results.values = filteredContactList;
        results.count = filteredContactList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setList(filteredContactList);
        adapter.notifyDataSetChanged();
    }
}
