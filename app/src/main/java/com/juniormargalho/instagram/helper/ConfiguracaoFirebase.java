package com.juniormargalho.instagram.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {
    private static DatabaseReference referenciaDatabase;
    private static FirebaseAuth instanciaAutenticacao;
    private static StorageReference referenciaStorage;

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

    public static StorageReference getReferenciaStorage(){
        if(referenciaStorage == null){
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenciaStorage;
    }

}
