package com.juniormargalho.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.juniormargalho.instagram.R;
import com.juniormargalho.instagram.helper.UsuarioFirebase;
import com.juniormargalho.instagram.model.Comentario;
import com.juniormargalho.instagram.model.Usuario;

public class ComentariosActivity extends AppCompatActivity {
    private EditText editComentario;
    private String idPostagem;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        //inicializar componentes
        usuario = UsuarioFirebase.getDadosUsuarioLogado();

        //configuracoes iniciais
        editComentario = findViewById(R.id.editComentario);

        //configuracoes toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Comentários");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //recupera id da postagem
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            idPostagem = bundle.getString("idPostagem");
        }

    }

    public void salvarComentario(View view){
        String textoComentario = editComentario.getText().toString();

        if(textoComentario != null && !textoComentario.equals("")){

            Comentario comentario = new Comentario();
            comentario.setIdPostagem(idPostagem);
            comentario.setIdUsuario(usuario.getId());
            comentario.setNomeUsuario(usuario.getNome());
            comentario.setCaminhoFoto(usuario.getCaminhoFoto());
            comentario.setComentario(textoComentario);

            if(comentario.salvar()){
                Toast.makeText(this, "Comentário salvo com sucesso!", Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(this, "Insira um comentário antes de salvar!", Toast.LENGTH_SHORT).show();
        }

        //limpar comentario digitado
        editComentario.setText("");

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
