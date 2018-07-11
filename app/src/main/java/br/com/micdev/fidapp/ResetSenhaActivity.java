package br.com.micdev.fidapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static br.com.micdev.fidapp.Config.KEY_EMAIL;
import static br.com.micdev.fidapp.MeusTicketsActivity.isNetworkAvailable;

public class ResetSenhaActivity extends AppCompatActivity {

    EditText editTextEmailReset;
    Button buttonReset;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_senha);
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
            editTextEmailReset = findViewById(R.id.editTextEmailReset);
            buttonReset = findViewById(R.id.buttonReset);
        }
    }
    public void mudaCena(View view){
        resetSenha();
    }
    public void resetSenha(){
        final String email = editTextEmailReset.getText().toString().trim();

        loading = ProgressDialog.show(this,"Espere...","Alterando senha...",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.RESET_SENHA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_EMAIL, email);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
