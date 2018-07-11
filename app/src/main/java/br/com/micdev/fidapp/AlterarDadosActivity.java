package br.com.micdev.fidapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static br.com.micdev.fidapp.MeusTicketsActivity.isNetworkAvailable;

public class AlterarDadosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static final String VERIFICAR_URL = "https://micdev.000webhostapp.com/verificar.php";

    private EditText editTextNome;
    private EditText editTextIdade;
    private EditText editTextEstado;
    private EditText editTextCidade;
    private EditText editTextSenhaAtual;
    private EditText editTextNovaSenha;
    private EditText editTextConfirmaNovaSenha;
    private ProgressDialog loading;
    String userNome;
    String userIdade;
    String userEstado;
    String userCidade;
    String userID;

    public static final String KEY_NOME = "nome";
    public static final String KEY_IDADE = "idade";
    public static final String KEY_ESTADO = "estado";
    public static final String KEY_CIDADE = "cidade";
    public static final String KEY_SENHA = "nova_senha";
    public static final String KEY_SENHA_ANTIGA = "senha";
    public static final String KEY_ID = "id_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_dados);
        if (!isNetworkAvailable(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Internet").setMessage("É necessário uma conexão de internet ativa para utilizar o FID");
            builder.setNeutralButton("Recarregar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = getIntent();
                    overridePendingTransition(0, 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                }
            });
            builder.setCancelable(false);
            builder.show();
        }else {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getData();

            editTextNome = findViewById(R.id.editTextNome);
            editTextIdade = findViewById(R.id.editTextIdade);
            editTextEstado = findViewById(R.id.editTextEstado);
            editTextCidade = findViewById(R.id.editTextCidade);
            editTextNovaSenha = findViewById(R.id.editTextNovaSenha);
            editTextSenhaAtual = findViewById(R.id.editTextSenhaAtual);
            editTextConfirmaNovaSenha = findViewById(R.id.editTextNovaSenhaConf);

            Button salvar = findViewById(R.id.buttonSalvar);

            SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String userEmail = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");
            String userNome = sharedPreferences.getString(Config.KEY_NOME, "Error");

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View header = navigationView.getHeaderView(0);
            TextView textViewEmailMenuAlterar = header.findViewById(R.id.textViewEmailMenuAlterar);
            textViewEmailMenuAlterar.setText(userEmail);
            TextView textViewNomeMenuAlterar = header.findViewById(R.id.textViewNomeMenuAlterar);
            textViewNomeMenuAlterar.setText(userNome);


            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            toggle.syncState();

            navigationView.setNavigationItemSelectedListener(this);

            salvar.setOnClickListener(this);
        }

    }

    public void getData(){
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString(Config.EMAIL_SHARED_PREF,"");

        loading = ProgressDialog.show(this,"Espere...","Obtendo informações...",false,false);

        String url = Config.DADOS_URL+userEmail;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(AlterarDadosActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void showJSON(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);
            userID = collegeData.getString(Config.KEY_ID);
            userNome = collegeData.getString(Config.KEY_NOME);
            userIdade = collegeData.getString(Config.KEY_IDADE);
            userEstado = collegeData.getString(Config.KEY_ESTADO);
            userCidade = collegeData.getString(Config.KEY_CIDADE);

            SharedPreferences sharedPreferences = AlterarDadosActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Config.KEY_ID, userID);
            editor.putString(Config.KEY_NOME, userNome);
            editor.putString(Config.KEY_IDADE, userIdade);
            editor.putString(Config.KEY_ESTADO, userEstado);
            editor.putString(Config.KEY_CIDADE, userCidade);

            editor.apply();

            editTextNome.setText(userNome);
            editTextIdade.setText(userIdade);
            editTextEstado.setText(userEstado);
            editTextCidade.setText(userCidade);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void changeData() throws JSONException {

        final String nome = editTextNome.getText().toString().trim();
        final String idade = editTextIdade.getText().toString().trim();
        final String estado = editTextEstado.getText().toString().toUpperCase().trim();
        final String cidade = editTextCidade.getText().toString().trim();
        final String senhaAtual = editTextSenhaAtual.getText().toString().trim();
        final String nova_senha = editTextNovaSenha.getText().toString().trim();
        final String confirmaNovaSenha = editTextConfirmaNovaSenha.getText().toString().trim();

        loading = ProgressDialog.show(this,"Espere...","Alterando dados...",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, VERIFICAR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AlterarDadosActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(AlterarDadosActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_SENHA_ANTIGA, senhaAtual);
                params.put(KEY_NOME, nome);
                params.put(KEY_IDADE, idade);
                params.put(KEY_ESTADO, estado);
                params.put(KEY_CIDADE, cidade);
                params.put(KEY_SENHA, nova_senha);
                params.put(KEY_ID, userID);
                return params;
            }
        };
        if (nova_senha.equals(confirmaNovaSenha)) {
            if(nome.isEmpty() || idade.isEmpty() || estado.isEmpty() || cidade.isEmpty() || senhaAtual.isEmpty() || nova_senha.isEmpty() || confirmaNovaSenha.isEmpty()) {
                loading.dismiss();
                Toast.makeText(AlterarDadosActivity.this,"Todos os campos devem ser preenchidos",Toast.LENGTH_LONG).show();
            }else{
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        } else{
            loading.dismiss();
            Toast.makeText(AlterarDadosActivity.this,"Senhas devem ser iguais",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent1 = new Intent(this, MeusTicketsActivity.class);
            startActivity(intent1);

        } else if (id == R.id.nav_add_evento) {
            Intent intent1 = new Intent(this, AddEventoActivity.class);
            startActivity(intent1);

        } else if (id == R.id.nav_contato) {
            Intent intent1 = new Intent(this, ContatoActivity.class);
            startActivity(intent1);
        } else if (id == R.id.nav_configs) {
            Intent intent1 = new Intent(this, ConfiguracoesActivity.class);
            startActivity(intent1);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View v) {
        try {
            changeData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
