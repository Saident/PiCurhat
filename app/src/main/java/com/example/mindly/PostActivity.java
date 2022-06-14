package com.example.mindly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mindly.databinding.ActivityPostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class PostActivity extends AppCompatActivity {

    Button cancel, post;
    EditText etPost;

    ImageView userProf;

    DatabaseReference database;

    FirebaseUser user;
    private String userID, fullname;
    private int childCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getUserName();

        userProf = findViewById(R.id.iv_avatar);
        cancel = findViewById(R.id.bt_cancel);
        post = findViewById(R.id.bt_post);
        etPost = findViewById(R.id.et_post);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postText, username, UID;
                postText = etPost.getText().toString().trim();
                username = fullname;

                HomeActivity homeActivity = new HomeActivity();

                Post posted = new Post(postText, username, username);
                FirebaseDatabase.getInstance().getReference("PostData")
                        .child("Post" + new Date().getTime())
                        .setValue(posted).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(PostActivity.this, "Post Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PostActivity.this, HomeActivity.class));
                        }else{
                            Toast.makeText(PostActivity.this, "Post Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void getUserName(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        database.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null){
                    String fullnames = user.fullname;
                    fullname = fullnames;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostActivity.this, "Something wrong happened!", Toast.LENGTH_SHORT);
            }
        });
    }


}