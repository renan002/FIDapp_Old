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
import android.webkit.WebSettings;
import android.webkit.WebView;
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

import static br.com.micdev.fidapp.Config.KEY_EVENTO_ID;
import static br.com.micdev.fidapp.Config.KEY_ID;
import static br.com.micdev.fidapp.LoginActivity.userID10;
import static br.com.micdev.fidapp.MeusTicketsActivity.isNetworkAvailable;
import static br.com.micdev.fidapp.SplashScreenActivity.userID;

public class TicketInfoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog loading2;
    private ProgressDialog loading3;
    String evento_id;
    String evento_vence_dia;
    String evento_local;
    String evento_data;
    String evento_descricao;

    public static final String DELETAR_EVENTO_URL = "https://micdev.000webhostapp.com/eventorevoga.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

            WebView webViewQR = findViewById(R.id.webViewQRCode);

            WebSettings ws = webViewQR.getSettings();

            ws.setJavaScriptEnabled(false);
            ws.setSupportZoom(false);

            Intent intent = getIntent();
            String nomeEvento = intent.getStringExtra("nome do evento");


            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            toggle.syncState();

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            TextView textViewNomeEvento = findViewById(R.id.textViewNomeEvento);

            getEventoInfo(nomeEvento);

            textViewNomeEvento.setText(nomeEvento);

            SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String userEmail = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");
            String userNome = sharedPreferences.getString(Config.KEY_NOME, "Error");

            navigationView.setNavigationItemSelectedListener(this);

            View header = navigationView.getHeaderView(0);
            TextView textViewEmailMenuInfo = header.findViewById(R.id.textViewEmailMenuInfo);
            TextView textViewNomeMenuInfo = header.findViewById(R.id.textViewNomeMenuInfo);
            textViewEmailMenuInfo.setText(userEmail);
            textViewNomeMenuInfo.setText(userNome);
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

    public void getEventoInfo(String nomeEvento){
        loading3 = ProgressDialog.show(TicketInfoActivity.this, "Espere...", "Carregando informações do Ticket...", false, false);
        String url2 = Config.EVENTOS_INFO_URL+nomeEvento;

        StringRequest stringRequest1 = new StringRequest(url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String respostaInfo) {
                showJSONInfo(respostaInfo);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                loading3.dismiss();
                Toast.makeText(TicketInfoActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest1);
    }

    private void showJSONInfo(String respostaInfo){

        try {
            JSONObject jsonObject = new JSONObject(respostaInfo);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);
            evento_id = collegeData.getString(KEY_EVENTO_ID);
            evento_descricao = collegeData.getString(Config.KEY_EVENTO_DESCRICAO);
            evento_data = collegeData.getString(Config.KEY_EVENTO_DATA);
            evento_local = collegeData.getString(Config.KEY_EVENTO_LOCAL);
            evento_vence_dia = collegeData.getString(Config.KEY_VENCE_DIA);

            SharedPreferences sharedPreferences = TicketInfoActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Config.KEY_EVENTO_DESCRICAO, evento_descricao);
            editor.putString(KEY_EVENTO_ID, evento_id);
            editor.putString(Config.KEY_EVENTO_DATA, evento_data);
            editor.putString(Config.KEY_EVENTO_LOCAL, evento_local);
            editor.putString(Config.KEY_VENCE_DIA, evento_vence_dia);
            editor.apply();

            TextView textViewDescricaoEvento = findViewById(R.id.textViewDescricaoEvento);
            TextView textViewDataEvento = findViewById(R.id.textViewDataEvento);
            TextView textViewLocalEvento = findViewById(R.id.textViewLocalEvento);
            TextView textViewVencimentoEvento = findViewById(R.id.textViewVencimentoEvento);

            textViewDescricaoEvento.setText("Descrição: "+evento_descricao);
            textViewDataEvento.setText("Data: "+evento_data);
            textViewLocalEvento.setText("Local: "+evento_local);
            textViewVencimentoEvento.setText("Ticket vence dia "+evento_vence_dia);

            WebView webViewQR = findViewById(R.id.webViewQRCode);

            if(userID==null || userID.isEmpty()) {
                String urlQR = "http://chart.apis.google.com/chart?cht=qr&chl="+ userID10 + " SEPARADOR " + evento_id +"&chs=210x208" ;
                webViewQR.loadUrl(urlQR);

            }else{
                String urlQR = "http://chart.apis.google.com/chart?cht=qr&chl="+ userID + " SEPARADOR " + evento_id +"&chs=210x208" ;
                webViewQR.loadUrl(urlQR);
            }

            loading3.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
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
    public void revogarTicket(View view) {

        Intent intent = getIntent();
        final String nomeGrupo = intent.getStringExtra("nome do grupo");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Deseja revogar seu Ticket?")
                .setTitle("Revogar ticket:");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if ( nomeGrupo.equals("Tickets Ativos")) {
                    loading2 = ProgressDialog.show(TicketInfoActivity.this, "Espere...", "Revogando o evento...", false, false);
                    StringRequest stringRequest5 = new StringRequest(Request.Method.POST, DELETAR_EVENTO_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response1) {
                                    loading2.dismiss();
                                    Toast.makeText(TicketInfoActivity.this, response1, Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(TicketInfoActivity.this, MeusTicketsActivity.class);
                                    startActivity(i);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading2.dismiss();
                            Toast.makeText(TicketInfoActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put(KEY_EVENTO_ID, evento_id);
                            params.put(KEY_ID, userID);
                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(TicketInfoActivity.this);
                    requestQueue.add(stringRequest5);
                }else{
                    Toast.makeText(TicketInfoActivity.this, "Não é possível revogar um ticket não ativo!",Toast.LENGTH_LONG).show();
                }
            }

        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(),
                        "Ação cancelada",
                        Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog msgConfirmacao = builder.create();
        msgConfirmacao.show();
    }
}
