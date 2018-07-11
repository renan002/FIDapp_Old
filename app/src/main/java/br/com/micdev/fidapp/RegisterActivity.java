package br.com.micdev.fidapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import static br.com.micdev.fidapp.MeusTicketsActivity.isNetworkAvailable;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String REGISTER_URL = "https://micdev.000webhostapp.com/registrar.php";

    public static final String KEY_NOME = "nome";
    public static final String KEY_IDADE = "idade";
    public static final String KEY_ESTADO = "estado";
    public static final String KEY_CIDADE = "cidade";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_SENHA = "senha";

    private ProgressDialog loading;

    private EditText editTextNome;
    private EditText editTextIdade;
    private EditText editTextEstado;
    private EditText editTextCidade;
    private EditText editTextEmail;
    private EditText editTextSenha;
    private EditText editTextConfirmarSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (!isNetworkAvailable(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Internet").setMessage("É necessário uma conexão de internet ativa para atulizar o FID");
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

            editTextNome = findViewById(R.id.editTextNome);
            editTextIdade = findViewById(R.id.editTextIdade);
            editTextEstado = findViewById(R.id.editTextEstado);
            editTextCidade = findViewById(R.id.editTextCidade);
            editTextEmail = findViewById(R.id.editTextEmail);
            editTextSenha = findViewById(R.id.editTextSenha);
            editTextConfirmarSenha = findViewById(R.id.editTextConfirmarSenha);


            Button buttonCadastrar = findViewById(R.id.buttonCadastrar);

            buttonCadastrar.setOnClickListener(this);
        }
    }

    private void registerUser() throws JSONException {

        final String nome = editTextNome.getText().toString().trim();
        final String idade = editTextIdade.getText().toString().trim();
        final String estado = editTextEstado.getText().toString().toUpperCase().trim();
        final String cidade = editTextCidade.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String senha = editTextSenha.getText().toString().trim();
        final String confirmaSenha = editTextConfirmarSenha.getText().toString().trim();

        loading = ProgressDialog.show(this,"Espere...","Registrando...",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_NOME, nome);
                params.put(KEY_IDADE, idade);
                params.put(KEY_ESTADO, estado);
                params.put(KEY_CIDADE, cidade);
                params.put(KEY_EMAIL, email);
                params.put(KEY_SENHA, senha);
                return params;
            }
        };
        if (senha.equals(confirmaSenha)) {
            if(nome.isEmpty() || idade.isEmpty() || estado.isEmpty() || cidade.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()) {
                loading.dismiss();
                Toast.makeText(RegisterActivity.this,"Todos os campos devem ser preenchidos",Toast.LENGTH_LONG).show();
            }else{
                if(isValidEmail(email)) {
                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    requestQueue.add(stringRequest);
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    loading.dismiss();
                    Toast.makeText(RegisterActivity.this,"O campo email deve ser um email válido",Toast.LENGTH_LONG).show();
                }
            }
        } else{
            loading.dismiss();
            Toast.makeText(RegisterActivity.this,"Senhas devem ser iguais",Toast.LENGTH_LONG).show();
        }

    }

    public static boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



    @Override
    public void onClick(View v) {
        try {
            registerUser();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
