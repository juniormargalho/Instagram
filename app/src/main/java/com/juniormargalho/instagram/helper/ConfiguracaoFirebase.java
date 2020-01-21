package com.juniormargalho.instagram.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {
    private static DatabaseReference referenciaDatabase;
    private static FirebaseAuth instanciaAutenticacao;

    public static DatabaseReference getReferenciaDatabase(){
        if( referenciaDatabase == null ){
            referenciaDatabase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaDatabase;
    }

    public static FirebaseAuth getInstanciaAutenticacao(){
        if( instanciaAutenticacao == null ){
            instanciaAutenticacao = FirebaseAuth.getInstance();
        }
        return instanciaAutenticacao;
    }

}
