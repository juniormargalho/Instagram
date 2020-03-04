package com.juniormargalho.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.juniormargalho.instagram.R;
import com.juniormargalho.instagram.adapter.AdapterMiniaturas;
import com.juniormargalho.instagram.helper.ConfiguracaoFirebase;
import com.juniormargalho.instagram.helper.RecyclerItemClickListener;
import com.juniormargalho.instagram.helper.UsuarioFirebase;
import com.juniormargalho.instagram.model.Postagem;
import com.juniormargalho.instagram.model.Usuario;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageFotoEscolhida;
    private Bitmap imagem, imagemFiltro;
    private List<ThumbnailItem> listaFiltros;
    private RecyclerView recyclerFiltros;
    private AdapterMiniaturas adapterMiniaturas;
    private String idUsuarioLogado;
    private TextInputEditText textDescricaoFiltro;
    private DatabaseReference usuariosRef, usuarioLogadoRef;
    private Usuario usuarioLogado;
    private ProgressBar progressBar;
    private boolean estaCarregando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        //configuracoes iniciais
        listaFiltros = new ArrayList<>();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        usuariosRef = ConfiguracaoFirebase.getReferenciaDatabase().child("usuarios");

        //Inicializar componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        recyclerFiltros = findViewById(R.id.recyclerFiltros);
        textDescricaoFiltro = findViewById(R.id.textDescricaoFiltro);
        progressBar = findViewById(R.id.progressFiltro);

        recuperarDadosUsuarioLogado();

        //configuracoes toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //recupera a imagem escolhida pelo usuario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            byte[] dadosImagem = bundle.getByteArray("fotoEscolhida");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
            imageFotoEscolhida.setImageBitmap(imagem);
            imagemFiltro = imagem.copy(imagem.getConfig(), true);

            //configuracao recycler view
            adapterMiniaturas = new AdapterMiniaturas(listaFiltros, getApplicationContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerFiltros.setLayoutManager(layoutManager);
            recyclerFiltros.setAdapter(adapterMiniaturas);

            //evento de clique do recyclerview
            recyclerFiltros.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerFiltros,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            ThumbnailItem item = listaFiltros.get(position);
                            imagemFiltro = imagem.copy(imagem.getConfig(), true);
                            Filter filtro = item.filter;
                            imageFotoEscolhida.setImageBitmap(filtro.processFilter(imagemFiltro));
                        }
                        @Override
                        public void onLongItemClick(View view, int position) {
                        }
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        }
                    }));

            recuperarFiltros();
        }
    }

    private void carregando(boolean estado){
        if(estado){
            estaCarregando = true;
            progressBar.setVisibility(View.VISIBLE);
        }else {
            estaCarregando = false;
            progressBar.setVisibility(View.GONE);
        }
    }

    private void recuperarDadosUsuarioLogado(){
        carregando(true);

        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioLogado = dataSnapshot.getValue(Usuario.class);
                carregando(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void recuperarFiltros() {
        //limpar lista
        ThumbnailsManager.clearThumbs();
        listaFiltros.clear();

        //filtro normal
        ThumbnailItem item = new ThumbnailItem();
        item.image = imagem;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb(item);

        //listar todos os filtros
        List<Filter> filtros = FilterPack.getFilterPack(getApplicationContext());
        for (Filter filtro : filtros) {
            ThumbnailItem itemFiltro = new ThumbnailItem();
            itemFiltro.image = imagem;
            itemFiltro.filter = filtro;
            itemFiltro.filterName = filtro.getName();

            ThumbnailsManager.addThumb(itemFiltro);
        }
        listaFiltros.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));
        adapterMiniaturas.notifyDataSetChanged();
    }

    private void publicarPostagem(){
        if(estaCarregando){
            Toast.makeText(getApplicationContext(), "Carregando dados, aguarde!", Toast.LENGTH_SHORT).show();
        }else{
            final Postagem postagem = new Postagem();
            postagem.setIdUsuario(idUsuarioLogado);
            postagem.setDescricao(textDescricaoFiltro.getText().toString());

            //recuperar dados da imagem para o firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imagemFiltro.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] dadosImagem = baos.toByteArray();

            //salvar imagem no storage
            StorageReference storageRef = ConfiguracaoFirebase.getReferenciaStorage();
            final StorageReference imagemRef = storageRef.child("imagens").child("postagens").child(postagem.getId() + ".jpeg");

            UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FiltroActivity.this, "Erro ao salvar imagem!", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //salvando local da foto no database
                    imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            postagem.setCaminhoFoto(uri.toString());

                            if(postagem.salvar()){
                                //atualiza quantidade de postagens
                                int qtdPostagem = usuarioLogado.getPostagens() + 1;
                                usuarioLogado.setPostagens(qtdPostagem);
                                usuarioLogado.atualizarQtdPostagem();

                                Toast.makeText(FiltroActivity.this, "Sucesso ao salvar postagem!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_salvar_postagem:
                publicarPostagem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
