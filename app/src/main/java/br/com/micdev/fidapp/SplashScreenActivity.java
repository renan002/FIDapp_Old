package br.com.micdev.fidapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static br.com.micdev.fidapp.MeusTicketsActivity.isNetworkAvailable;


public class SplashScreenActivity extends AppCompatActivity {

    public static String userNome;
    public static String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Splash().execute();
    }
    private class Splash extends AsyncTask<String, Integer, String> {

        Splash() {
        }

        @Override
        protected void onPreExecute() {
            if (!isNetworkAvailable(SplashScreenActivity.this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
                builder.setTitle("Internet").setMessage(R.string.tem_internet_msg);
                builder.setNeutralButton(R.string.fechar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.setCancelable(false);
                builder.show();
            } else {

                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setMax(100);
                progressBar.setProgress(0);
            }

        }

        @Override
        protected synchronized String doInBackground(String... arg0) {

            try {
                getData();
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



            return "Foi";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setMax(100);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected synchronized void onPostExecute(String result) {

            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }
        private synchronized void getData() {

            SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String userEmail = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, null);


                String url = Config.NOME_URL + userEmail;

                StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setProgress(50);
                        showJSON(response);
                    }

                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {


                            }
                        });
                RequestQueue requestQueue = Volley.newRequestQueue(SplashScreenActivity.this);
                requestQueue.add(stringRequest);




        }

        private synchronized void showJSON(String response) {

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
                JSONObject collegeData = result.getJSONObject(0);
                userNome = collegeData.getString(Config.KEY_NOME);
                userID = collegeData.getString(Config.KEY_ID);


                SharedPreferences sharedPreferences = SplashScreenActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Config.KEY_NOME, userNome);
                editor.putString(Config.KEY_ID, userID);
                editor.apply();

                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setMax(100);
                progressBar.setProgress(100);



                notify();


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        }
    }

