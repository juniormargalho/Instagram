package com.juniormargalho.instagram.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.juniormargalho.instagram.helper.ConfiguracaoFirebase;
import com.juniormargalho.instagram.helper.UsuarioFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Postagem implements Serializable {
    private String id, idUsuario, descricao, caminhoFoto;

    public Postagem() {

        //gerar id automaticamente para a postagem
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaDatabase();
        DatabaseReference postagemRef = firebaseRef.child("postagens");
        String idPostagem = postagemRef.push().getKey();
        setId(idPostagem);
    }

    public boolean salvar(DataSnapshot seguidoresSnapshot){
        Map objeto = new HashMap();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getReferenciaDatabase();
        String combinacaoId = "/" + getIdUsuario() + "/" + getId();

        //referencia para postagem
        objeto.put("/postagens" + combinacaoId, this);

        //referencia para feed
        for(DataSnapshot seguidores : seguidoresSnapshot.getChildren()){
            String idSeguidor = seguidores.getKey();
            HashMap<String, Object> dadosSeguidor = new HashMap<>();
            dadosSeguidor.put("fotoPostagem", getCaminhoFoto());
            dadosSeguidor.put("descricao", getDescricao());
            dadosSeguidor.put("id", getId());
            dadosSeguidor.put("nomeUsuario", usuarioLogado.getNome());
            dadosSeguidor.put("fotoUsuario", usuarioLogado.getCaminhoFoto());

            String idsAtualizacao = "/" + idSeguidor + "/" + getId();
            objeto.put("/feed" + idsAtualizacao, dadosSeguidor);
        }

        firebaseRef.updateChildren(objeto);
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }
}
