package com.usac.brayan.mensajeriaarquitectura;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Brayan on 13/05/2017.
 */
public class MyDialogFragment extends DialogFragment {
    private RecyclerView mRecyclerView;
    private AdaptadorEstudiante adapter;
    ArrayList<Estudiante> list;
    // this method create view for your Dialog


    public static MyDialogFragment newInstance(ArrayList<Estudiante> list) {
        MyDialogFragment f = new MyDialogFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putSerializable("list", list);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout with recycler view
        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
        getDialog().setTitle("¡Tus estudiantes! :)");
        list= (ArrayList<Estudiante>) getArguments().getSerializable("list");
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recicladorForEstudiantes);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //setadapter
        RecyclerView.Adapter adapter;
        if(list!=null) {
            adapter = new AdaptadorEstudiante(list);
        }else{
            list= new ArrayList<>();
            adapter = new AdaptadorEstudiante(list);
        }
        mRecyclerView.setAdapter(adapter);
        //get your recycler view and populate it.
        return v;
    }
}