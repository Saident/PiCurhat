package com.example.mindly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Button btLogin;
    EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        btLogin = findViewById(R.id.bt_login);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Email dan Password tidak boleh kosong",Toast.LENGTH_SHORT).show();
                } else if(email.equals("test")){
                    if(password.equals("test")){
                        Toast.makeText(LoginActivity.this,"Berhasil Log In", Toast.LENGTH_SHORT).show();
                        Intent x = new Intent(LoginActivity.this,HomeActivity.class);
                        x.putExtra("email_key", email);
                        x.putExtra("pass_key", password);
                        startActivity(x);
                    }else Toast.makeText(LoginActivity.this,"Email atau Password Salah",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(LoginActivity.this,"Email atau Password Salah",Toast.LENGTH_SHORT).show();
            }
        });
    }
}