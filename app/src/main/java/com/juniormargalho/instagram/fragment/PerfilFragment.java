package com.juniormargalho.instagram.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juniormargalho.instagram.R;
import com.juniormargalho.instagram.activity.EditarPerfilActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {
    private ProgressBar progressBarPerfil;
    private CircleImageView imagePerfil;
    public GridView gridViewPerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private Button buttonAcaoPerfil;

    public PerfilFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //configuracoes componentes
        gridViewPerfil = view.findViewById(R.id.gridViewPerfil);
        progressBarPerfil = view.findViewById(R.id.progressBarPerfil);
        imagePerfil = view.findViewById(R.id.imageEditarPerfil);
        textPublicacoes = view.findViewById(R.id.textPublicacoes);
        textSeguidores = view.findViewById(R.id.textSeguidores);
        textSeguindo = view.findViewById(R.id.textSeguindo);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);

        //abrir edicao perfil
        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

}
