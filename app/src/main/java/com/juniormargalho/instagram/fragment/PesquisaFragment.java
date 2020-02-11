package com.juniormargalho.instagram.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.juniormargalho.instagram.R;
import com.juniormargalho.instagram.adapter.AdapterPesquisa;
import com.juniormargalho.instagram.helper.ConfiguracaoFirebase;
import com.juniormargalho.instagram.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class PesquisaFragment extends Fragment {
    private SearchView searchViewPesquisa;
    private RecyclerView recyclerPesquisa;
    private List<Usuario> listaUsuarios;
    private DatabaseReference usuariosRef;
    private AdapterPesquisa adapterPesquisa;

    public PesquisaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        //configuracoes iniciais
        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerPesquisa = view.findViewById(R.id.recyclerPesquisa);
        listaUsuarios = new ArrayList<>();
        usuariosRef = ConfiguracaoFirebase.getReferenciaDatabase().child("usuarios");

        //configuracao searchView
        searchViewPesquisa.setQueryHint("Buscar usuários");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toUpperCase();
                pesquisarUsuarios( textoDigitado );
                return true;
            }
        });

        //configuracao recyclerView
        recyclerPesquisa.setHasFixedSize(true);
        recyclerPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterPesquisa = new AdapterPesquisa(listaUsuarios, getActivity());
        recyclerPesquisa.setAdapter(adapterPesquisa);

        return view;
    }

    private void pesquisarUsuarios(String texto){

        //primeiro passo - limpar a lista
        listaUsuarios.clear();

        //pesquisar usuarios caso tenha exista texto na pesquisa
        if( texto.length() >= 2 ){
            Query query = usuariosRef.orderByChild("nome").startAt(texto).endAt(texto + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //necessario tambem limpar a lista
                    listaUsuarios.clear();

                    //percorre o database de usuarios e monta a lista
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        listaUsuarios.add(ds.getValue(Usuario.class));
                    }

                    adapterPesquisa.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}
