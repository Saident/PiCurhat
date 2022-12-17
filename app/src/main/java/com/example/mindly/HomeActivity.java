package com.example.mindly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.model.Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView rv_post;
    DatabaseReference database;
    PostAdapter postAdapter;
    ArrayList<Post> listPost;

    ImageView btAdd;
    Button btDel;

    FirebaseUser user;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        initialize();
        getRecView();
        getUserName();

    }

    public void initialize(){
        btAdd = findViewById(R.id.bt_add);
        btAdd.setOnClickListener(this);
    }

    private void getRecView(){
        rv_post = findViewById(R.id.recview_post);
        database = FirebaseDatabase.getInstance().getReference("PostData");
        rv_post.setHasFixedSize(true);
        rv_post.setLayoutManager(new LinearLayoutManager(this));

        listPost = new ArrayList<>();
        postAdapter = new PostAdapter(this,listPost);
        rv_post.setAdapter(postAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPost.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue((Post.class));
                        listPost.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
                final TextView tv_name = (TextView) findViewById(R.id.tv_name);
                User user = snapshot.getValue(User.class);
                if (user != null){
                    String fullname = user.fullname;
                    tv_name.setText("Hi, " + fullname + "!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Something wrong happened!", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_add:
                startActivity(new Intent(this, PostActivity.class));
                break;
        }
    }

    public void onBackPressed() {
        return;
    }
}

