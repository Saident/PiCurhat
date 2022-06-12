package com.example.mindly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView et_fullname, et_age, et_email, et_password;
    private ImageView logo;
    private Button register;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        et_fullname = (TextView) findViewById(R.id.et_fullname);
        et_age = (TextView) findViewById(R.id.et_age);
        et_email = (TextView) findViewById(R.id.et_email);
        et_password = (TextView) findViewById(R.id.et_password);

        logo = findViewById(R.id.logo);
        logo.setOnClickListener(this);

        register = findViewById(R.id.bt_register);
        register.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.logo:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.bt_register:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String fullname = et_fullname.getText().toString().trim();
        String age = et_age.getText().toString().trim();

        if (fullname.isEmpty()){
            et_fullname.setError("Full name is required");
            et_fullname.requestFocus();
            return;
        }

        if (age.isEmpty()){
            et_age.setError("Age is required");
            et_age.requestFocus();
            return;
        }

        if (email.isEmpty()){
            et_email.setError("Email is required");
            et_email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_email.setError("Please use a valid email");
            et_email.requestFocus();
            return;
        }

       if (password.isEmpty()){
            et_password.setError("Password is required");
            et_password.requestFocus();
            return;
        }

       if(password.length() < 6){
           et_password.setError("Password needs to be 6 characters or longer");
           et_password.requestFocus();
           return;
       }

       progressBar.setVisibility(View.VISIBLE);
       mAuth.createUserWithEmailAndPassword(email, password)
               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()){
                           User user = new User(fullname, age, email);

                           FirebaseDatabase.getInstance().getReference("Users")
                                   .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                   .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()){
                                       Toast.makeText(RegisterActivity.this, "User has been created!", Toast.LENGTH_LONG).show();
                                       progressBar.setVisibility(View.VISIBLE);
                                       startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                   }else{
                                       Toast.makeText(RegisterActivity.this, "Failed to register!", Toast.LENGTH_LONG).show();
                                       progressBar.setVisibility(View.GONE);
                                   }
                               }
                           });
                       }else{
                           Toast.makeText(RegisterActivity.this, "Failed to register!", Toast.LENGTH_LONG).show();
                           progressBar.setVisibility(View.GONE);
                       }
                   }
               });
    }
}