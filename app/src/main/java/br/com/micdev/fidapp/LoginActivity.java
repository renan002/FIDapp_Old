package br.com.micdev.fidapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import static br.com.micdev.fidapp.SplashScreenActivity.userID;
import static br.com.micdev.fidapp.SplashScreenActivity.userNome;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    protected EditText editTextEmail;
    private EditText editTextSenha;
    private ProgressDialog loading;
    public static String userNome10;
    public static String userID10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

            editTextEmail = findViewById(R.id.editTextEmail);
            editTextSenha = findViewById(R.id.editTextSenha);

            Button buttonLogin = findViewById(R.id.buttonLogar);

            buttonLogin.setOnClickListener(this);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        boolean loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

        if(loggedIn){
            Intent intent = new Intent(LoginActivity.this, MeusTicketsActivity.class);
            startActivity(intent);
        }
    }


    private void login(){

        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextSenha.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.equalsIgnoreCase(Config.LOGIN_SUCCESS)){


                            SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                            editor.putString(Config.EMAIL_SHARED_PREF, email);

                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, MeusTicketsActivity.class);
                            startActivity(intent);
                            loading.dismiss();

                        }else{
                            loading.dismiss();
                            Toast.makeText(LoginActivity.this, "Usuário ou senha inválidos", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(LoginActivity.this, "Por favor, conecte-se à Internet", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Config.KEY_EMAIL, email);
                params.put(Config.KEY_PASSWORD, password);


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {

        getData();
    }

    private void getData() {
        userID = null;
        userNome = null;
        editTextEmail = findViewById(R.id.editTextEmail);
        final String email = editTextEmail.getText().toString().trim();
        String url = Config.NOME_URL + email;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON(response);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);

        try {
            loading = ProgressDialog.show(this, "Aguarde....","Fazendo login...",false,false);
            Thread.sleep(3000);
            login();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void showJSON(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);
            userNome10 = collegeData.getString(Config.KEY_NOME);
            userID10 = collegeData.getString(Config.KEY_ID);

            SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Config.KEY_NOME, userNome10);
            editor.putString(Config.KEY_ID, userID10);
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void abrirCadastro(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    public void abrirReset(View view) {
        Intent intent = new Intent(this, ResetSenhaActivity.class);
        startActivity(intent);
    }
}
