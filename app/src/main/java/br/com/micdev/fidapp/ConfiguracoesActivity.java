package br.com.micdev.fidapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static br.com.micdev.fidapp.Config.KEY_ID;
import static br.com.micdev.fidapp.Config.POLITICA_URL;
import static br.com.micdev.fidapp.MeusTicketsActivity.isNetworkAvailable;
import static br.com.micdev.fidapp.SplashScreenActivity.userID;

public class ConfiguracoesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog loading;
    public static final String EXCLUIR_CONTA_URL = "https://micdev.000webhostapp.com/excluirConta.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
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


            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            toggle.syncState();

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String userEmail = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Error");
            String userNome = sharedPreferences.getString(Config.KEY_NOME, "Error");

            navigationView.setNavigationItemSelectedListener(this);

            View header = navigationView.getHeaderView(0);
            TextView textViewEmailMenuConfig = header.findViewById(R.id.textViewEmailMenuConfig);
            textViewEmailMenuConfig.setText(userEmail);
            TextView textViewNomeMenuConfig = header.findViewById(R.id.textViewNomeMenuConfig);
            textViewNomeMenuConfig.setText(userNome);
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
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void desligarNoticacoes(View view){
        Toast.makeText(getApplicationContext(),
        "Ainda não funcional",
        Toast.LENGTH_LONG).show();
    }

    public void alterarDados(View view){
        Intent i = new Intent(this, AlterarDadosActivity.class);
        startActivity(i);
    }

    public void excluirConta(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Excluir conta:").setMessage("Deseja mesmo excluir sua conta?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loading = ProgressDialog.show(ConfiguracoesActivity.this,"Espere...","Deletando conta...",false,false);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, EXCLUIR_CONTA_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                loading.dismiss();
                                Toast.makeText(ConfiguracoesActivity.this, response, Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(ConfiguracoesActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put(KEY_ID, userID);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(ConfiguracoesActivity.this);
                requestQueue.add(stringRequest);
                SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);


                editor.putString(Config.EMAIL_SHARED_PREF, "");

                editor.putString(Config.NOME_SHARED_PREF, "");

                editor.putString(Config.KEY_NOME, "");

                editor.putString(KEY_ID, "");

                editor.putString(Config.ID_SHARED_PREF, "");

                editor.putString(Config.KEY_CIDADE, "");

                editor.putString(Config.KEY_ESTADO, "");

                editor.putString(Config.KEY_IDADE, "");

                editor.apply();

                Intent intent = new Intent(ConfiguracoesActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        builder.show();
    }

    public void mostrarPolitica(View view){
        Uri uri = Uri.parse(POLITICA_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    private void logout(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Tem certeza que deseja deslogar?");
        alertDialogBuilder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);


                        editor.putString(Config.EMAIL_SHARED_PREF, "");

                        editor.putString(Config.NOME_SHARED_PREF, "");

                        editor.putString(Config.KEY_NOME, "");

                        editor.putString(KEY_ID, "");

                        editor.putString(Config.ID_SHARED_PREF, "");

                        editor.putString(Config.KEY_CIDADE, "");

                        editor.putString(Config.KEY_ESTADO, "");

                        editor.putString(Config.KEY_IDADE, "");

                        editor.apply();

                        Intent intent = new Intent(ConfiguracoesActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("Não",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    public void deslogar(View view){
        logout();
    }
}
