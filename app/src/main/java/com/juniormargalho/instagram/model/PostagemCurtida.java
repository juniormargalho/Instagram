package com.juniormargalho.instagram.model;

import com.google.firebase.database.DatabaseReference;
import com.juniormargalho.instagram.helper.ConfiguracaoFirebase;

import java.util.HashMap;

public class PostagemCurtida {
    public int qtdCurtidas = 0;
    public Feed feed;
    public Usuario usuario;

    public PostagemCurtida() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaDatabase();

        //objeto usuario
        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nomeUsuario", usuario.getNome());
        dadosUsuario.put("caminhoFoto", usuario.getCaminhoFoto());

        DatabaseReference pCurtidasRef = firebaseRef.child("postagens-curtidas").child(feed.getId()).child(usuario.getId());
        pCurtidasRef.setValue(dadosUsuario);

        atualizarQtd(1);
    }

    public void atualizarQtd(int valor){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaDatabase();
        DatabaseReference pCurtidasRef = firebaseRef.child("postagens-curtidas").child(feed.getId()).child("qtdCurtidas");
        setQtdCurtidas(getQtdCurtidas() + valor);
        pCurtidasRef.setValue(getQtdCurtidas());
    }

    public void remover(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaDatabase();

        DatabaseReference pCurtidasRef = firebaseRef.child("postagens-curtidas").child(feed.getId()).child(usuario.getId());
        pCurtidasRef.removeValue();

        atualizarQtd(-1);
    }

    public int getQtdCurtidas() {
        return qtdCurtidas;
    }

    public void setQtdCurtidas(int qtdCurtidas) {
        this.qtdCurtidas = qtdCurtidas;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
