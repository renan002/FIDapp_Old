package br.com.micdev.fidapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static br.com.micdev.fidapp.Config.EVENTOS_ATIVOS_URL;
import static br.com.micdev.fidapp.Config.EVENTOS_REVOGADOS_URL;
import static br.com.micdev.fidapp.Config.EVENTOS_UTILIZADOS_URL;
import static br.com.micdev.fidapp.Config.EVENTOS_VENCIDOS_URL;
import static br.com.micdev.fidapp.SplashScreenActivity.userID;
import static br.com.micdev.fidapp.SplashScreenActivity.userNome;
import static br.com.micdev.fidapp.LoginActivity.userID10;
import static br.com.micdev.fidapp.LoginActivity.userNome10;

public class MeusTicketsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    ExpandableListAdapter listAdapter;
    ExpandableListView listaTickets;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    int conti=0;

    String nomeEvento;

    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Splash(this).execute();


    }

    private class Splash extends AsyncTask<Object, Integer, Object> {


        private Activity activity;

        Splash(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Aguarde");
            progressDialog.setMessage("Seus eventos estão sendo carregados...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();
            setContentView(R.layout.activity_meus_tickets);
            listaTickets = findViewById(R.id.listaTickets);
            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

            listDataHeader.add("Tickets Ativos");
            listDataHeader.add("Tickets Utilizados");
            listDataHeader.add("Tickets Vencidos");
            listDataHeader.add("Tickets Revogados");
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recreate();
                }
            });
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(MeusTicketsActivity.this);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    MeusTicketsActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            toggle.syncState();
            navigationView.setNavigationItemSelectedListener(MeusTicketsActivity.this);
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            if(userID==null || userID.isEmpty()) {
                getEventosAtivos(userID10);
                getEventosUtilizados(userID10);
                getEventosVencidos(userID10);
                getEventosRevogados(userID10);

            }else{
                getEventosAtivos(userID);
                getEventosUtilizados(userID);
                getEventosVencidos(userID);
                getEventosRevogados(userID);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Object result) {

            if (!isNetworkAvailable(MeusTicketsActivity.this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MeusTicketsActivity.this);
                builder.setTitle("Internet").setMessage(R.string.tem_internet_msg);
                builder.setNeutralButton(R.string.recarregar, new DialogInterface.OnClickListener() {
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
            } else {



                SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                String userEmail = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");

                NavigationView navigationView = findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(MeusTicketsActivity.this);

                View header = navigationView.getHeaderView(0);
                TextView textViewEmailMenu = header.findViewById(R.id.textViewEmailMenu1);
                textViewEmailMenu.setText(userEmail);
                TextView textViewNomeMenu = header.findViewById(R.id.textViewNomeMenu1);
                if (userNome==null || userNome.isEmpty()) {
                    textViewNomeMenu.setText(userNome10);
                }else{
                    textViewNomeMenu.setText(userNome);
                }
                listAdapter = new ExpandableListAdapterUse(MeusTicketsActivity.this, listDataHeader, listDataChild);
                listaTickets.setAdapter(listAdapter);
                listaTickets.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v,
                                                int groupPosition, long id) {
                        return false;
                    }
                });

                listaTickets.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                    @Override
                    public void onGroupExpand(int groupPosition) {
                    }
                });


                listaTickets.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                    @Override
                    public void onGroupCollapse(int groupPosition) {

                    }
                });

                listaTickets.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v,
                                                int groupPosition, int childPosition, long id) {

                        String nomeEvento = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                        String nomeGrupo = listDataHeader.get(groupPosition);


                        Intent intent = new Intent(MeusTicketsActivity.this, TicketInfoActivity.class);
                        intent.putExtra("nome do evento", nomeEvento);
                        intent.putExtra("nome do grupo", nomeGrupo);
                        if (nomeEvento.equals("Nenhum evento cadastrado")) {

                        } else {
                            startActivity(intent);
                        }

                        return false;
                    }
                });

            }
        }



    }

        @Override
        public void onBackPressed () {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "Tchau :)", Toast.LENGTH_SHORT).show();
                finishAffinity();

            }
        }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected();
    }

    public void getEventosRevogados(String userID2) {
        String url2 = EVENTOS_REVOGADOS_URL + userID2;


        StringRequest stringRequest = new StringRequest(url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String respostaRevogados) {

                showJSONEventoRevogados(respostaRevogados);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSONEventoRevogados(String respostaRevogados) {
        String[] nomeEvento2;
        nomeEvento2 = new String[50];
        try {

            for (int i = 0; i < 50; i++) {

                JSONObject jsonObject = new JSONObject(respostaRevogados);
                JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);

                for (int x = 0; x < 50; x++) {
                    JSONObject collegeData = result.getJSONObject(x);
                    nomeEvento = collegeData.getString(Config.KEY_NOME_EVENTO);
                    nomeEvento2[x] = nomeEvento;
                    trocaNomeEventoRevogados(nomeEvento2);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            trocaNomeEventoRevogados(nomeEvento2);
        }
    }

    public void trocaNomeEventoRevogados(String[] nomeEvento5) {

        List<String> revogados = new ArrayList<>();

        for (int i = 0; i < 50; i++) {

            if (nomeEvento5[i] == null || nomeEvento5[i].equals("")) {

            } else {
                revogados.add(nomeEvento5[i]);
            }
        }
        if (revogados.isEmpty()) {
            revogados.add("Nenhum evento cadastrado");
        }
        conti+=25;
        progressDialog.setProgress(conti);
        listDataChild.put(listDataHeader.get(3), revogados);
        progressDialog.dismiss();

    }

    public void getEventosVencidos(String userID2) {
        String url2 = EVENTOS_VENCIDOS_URL + userID2;


        StringRequest stringRequest = new StringRequest(url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String respostaVencidos) {

                showJSONEventoVencidos(respostaVencidos);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSONEventoVencidos(String respostaVencidos) {
        String[] nomeEvento2;
        nomeEvento2 = new String[50];
        try {

            for (int i = 0; i < 50; i++) {

                JSONObject jsonObject = new JSONObject(respostaVencidos);
                JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);

                for (int x = 0; x < 50; x++) {
                    JSONObject collegeData = result.getJSONObject(x);
                    nomeEvento = collegeData.getString(Config.KEY_NOME_EVENTO);
                    nomeEvento2[x] = nomeEvento;
                    trocaNomeEventoVencidos(nomeEvento2);
                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
            trocaNomeEventoVencidos(nomeEvento2);
        }
    }

    public void trocaNomeEventoVencidos(String[] nomeEvento4) {

        List<String> vencidos = new ArrayList<>();

        for (int i = 0; i < 50; i++) {

            if (nomeEvento4[i] == null || nomeEvento4[i] == "") {

            } else {
                vencidos.add(nomeEvento4[i]);
            }
        }
        if (vencidos.isEmpty()) {
            vencidos.add("Nenhum evento cadastrado");
        }

        conti+=25;
        progressDialog.setProgress(conti);
        listDataChild.put(listDataHeader.get(2), vencidos);
    }


    public void getEventosUtilizados(String userID2) {
        String url2 = EVENTOS_UTILIZADOS_URL + userID2;


        StringRequest stringRequest = new StringRequest(url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String respostaUtilizados) {

                showJSONEventoUtilizados(respostaUtilizados);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSONEventoUtilizados(String respostaUtilizados) {
        String[] nomeEvento2;
        nomeEvento2 = new String[50];
        try {
            for (int i = 0; i < 50; i++) {

                JSONObject jsonObject = new JSONObject(respostaUtilizados);
                JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);

                for (int x = 0; x < 50; x++) {
                    JSONObject collegeData = result.getJSONObject(x);
                    nomeEvento = collegeData.getString(Config.KEY_NOME_EVENTO);
                    nomeEvento2[x] = nomeEvento;
                    trocaNomeEventoUtilizados(nomeEvento2);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            trocaNomeEventoUtilizados(nomeEvento2);
        }
    }

    public void trocaNomeEventoUtilizados(String[] nomeEvento3) {

        List<String> utilizados = new ArrayList<>();


        for (int i = 0; i < 50; i++) {

            if (nomeEvento3[i] == null || nomeEvento3[i] == "") {

            } else {
                utilizados.add(nomeEvento3[i]);
            }
        }
        if (utilizados.isEmpty()) {
            utilizados.add("Nenhum evento cadastrado");
        }

        conti+=25;
        progressDialog.setProgress(conti);
        listDataChild.put(listDataHeader.get(1), utilizados);

    }

    public void getEventosAtivos(String userID1) {

        String url2 = EVENTOS_ATIVOS_URL + userID1;


        StringRequest stringRequest = new StringRequest(url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String respostaAtivos) {

                showJSONEventoAtivo(respostaAtivos);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void showJSONEventoAtivo(String respostaAtivos) {
        String[] nomeEvento2;
        nomeEvento2 = new String[50];
        try {

            for (int i = 0; i < 50; i++) {

                JSONObject jsonObject = new JSONObject(respostaAtivos);
                JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);

                for (int x = 0; x < 50; x++) {
                    JSONObject collegeData = result.getJSONObject(x);
                    nomeEvento = collegeData.getString(Config.KEY_NOME_EVENTO);
                    nomeEvento2[x] = nomeEvento;
                    trocaNomeEventoAtivo(nomeEvento2);
                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
            trocaNomeEventoAtivo(nomeEvento2);
        }


    }

    public void trocaNomeEventoAtivo(String[] nomeEvento2) {

        List<String> ativos = new ArrayList<>();

        for (int i = 0; i < 50; i++) {

            if (nomeEvento2[i] == null || nomeEvento2[i] == "") {

            } else {
                ativos.add(nomeEvento2[i]);
            }
        }
        if (ativos.isEmpty()) {
            ativos.add("Nenhum evento cadastrado");
        }

        conti+=25;
        progressDialog.setProgress(conti);
        listDataChild.put(listDataHeader.get(0), ativos);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

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

}

