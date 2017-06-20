package com.usac.brayan.mensajeriaarquitectura;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class AsignacionDeCursos extends AppCompatActivity {
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private SearchView buscador;
    public static AdaptadorDeCurso adaptador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignacion_de_cursos);
        // Inicializar Animes
        List items = new ArrayList();
        buscador = (SearchView) findViewById(R.id.searchView);

//        items.add(new Curso("Materiales", "A","Jose Samayoa"));
//        items.add(new Curso("Mercadotecnia", "A","Juan Urrutia"));
//        items.add(new Curso("Mercadotecnia", "N","Mayra Arias"));
//        items.add(new Curso("Matematica 1", "N","Joaquin Conde"));
//        items.add(new Curso("Matematica 2", "P","Lucas Zambrano"));

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adaptador = new AdaptadorDeCurso(items,this);
        adapter=adaptador;
        recycler.setAdapter(adapter);
        buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adaptador.filterList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adaptador.filterList(newText);
                return true;
            }
        });
        buscador.setQueryHint("Busca tu curso aqui");

        ServicioNotificacionesFARUSAC.sc.pedirListadoCurso();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsmenu, menu);

        // Associate searchable configuration with the SearchView
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =(SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setIconifiedByDefault(false);
//        searchView.setQueryHint("Busca tu curso aqui");
//        searchView.setX(0);
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }
}
