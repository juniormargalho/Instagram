package com.juniormargalho.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.juniormargalho.instagram.R;
import com.juniormargalho.instagram.helper.ConfiguracaoFirebase;
import com.juniormargalho.instagram.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private ProgressBar progressBar;

    private Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();

        //Cadastrar usuario
        progressBar.setVisibility(View.GONE);
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                if( !textoNome.isEmpty() ){
                    if( !textoEmail.isEmpty() ){
                        if( !textoSenha.isEmpty() ){
                            usuario = new Usuario();
                            usuario.setNome( textoNome );
                            usuario.setEmail( textoEmail );
                            usuario.setSenha( textoSenha );

                            cadastrar( usuario );

                        }else {
                            Toast.makeText(CadastroActivity.this, "Campo senha não preenchido!", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(CadastroActivity.this, "Campo e-mail não preenchido!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(CadastroActivity.this, "Campo nome não preenchido!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void cadastrar( Usuario usuario ){
        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getInstanciaAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful() ){

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(CadastroActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                        }else {

                            progressBar.setVisibility(View.GONE);

                            String erroExcessao = "";
                            try{
                                throw task.getException();
                            }catch ( FirebaseAuthWeakPasswordException e ){
                                erroExcessao = "Digite uma senha maiis forte!";
                            }catch ( FirebaseAuthInvalidCredentialsException e ){
                                erroExcessao = "Digite um e-mail válido!";
                            }catch ( FirebaseAuthUserCollisionException e ){
                                erroExcessao = "Conta já cadastrada!";
                            }catch ( Exception e ){
                                erroExcessao = "Erro ao cadastrar usuário: " + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(CadastroActivity.this, erroExcessao, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void inicializarComponentes(){
        campoNome = findViewById(R.id.editCadastroNome);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        botaoCadastrar = findViewById(R.id.buttonCadastrar);
        progressBar = findViewById(R.id.progressCadastro);
    }
}
