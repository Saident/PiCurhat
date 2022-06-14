package com.example.mindly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    Button cancel, post;
    EditText etPost;

    ImageView userProf;
    ImageView getImages;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference database = db.getReference().child("PostData");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    FirebaseUser user;

    private String userID, fullname, media=null;
    private String id = "";
    private ProgressDialog progressDialog;
    private int a = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getUserName();

        progressDialog = new ProgressDialog(PostActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Menyimpan...");

        userProf = findViewById(R.id.iv_avatar);
        cancel = findViewById(R.id.bt_cancel);
        post = findViewById(R.id.bt_post);
        etPost = findViewById(R.id.et_post);
        getImages = findViewById(R.id.ib_addpic);

        getImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                a =+ 1;
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (a == 0){
                    String postText, username;
                    postText = etPost.getText().toString().trim();
                    username = fullname;

                    Post posted = new Post(postText, username, "@" + username, media);
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
                }else{
                    upload();
                }
            }
        });
    }

    private void selectImage(){
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog. Builder builder = new AlertDialog.Builder(PostActivity.this);
        builder.setTitle (getString(R.string.app_name));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, (dialog , item)-> {
            if (items[item].equals("Take Photo")) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 10);
            } else if (items[item].equals("Choose from Library")) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image "), 20);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20 && resultCode == RESULT_OK && data != null){
            final Uri path = data.getData();
            Thread thread = new Thread(()-> {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(path);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream) ;
                    getImages.post(() -> {
                        getImages.setImageBitmap(bitmap);
                    });
                } catch (IOException e){
                    e.printStackTrace();
                }
            });
            thread.start();
        }

        if (requestCode == 10 && resultCode == RESULT_OK) {
            final Bundle extras = data.getExtras();
            Thread thread = new Thread(() -> {
                Bitmap bitmap = (Bitmap) extras.get("data");
                getImages.post(() -> {
                    getImages.setImageBitmap(bitmap);
                });
            });
            thread.start();
        }
    }

    private void upload() {
        progressDialog.show();

        getImages.setDrawingCacheEnabled(true);
        getImages.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) getImages.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();

        //UPLOAD
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("images").child("IMG"+new Date().getTime()+".jpeg");
        UploadTask uploadTask = reference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata()!=null) {
                    if (taskSnapshot.getMetadata().getReference()!=null){
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.getResult()!=null) {
                                    saveData(task.getResult().toString());
                                } else {
                                    Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveData(String media) {
        String postText, username;
        postText = etPost.getText().toString().trim();
        username = fullname;
        this.media = media;

        Post posted = new Post(postText, username, "@" + username, media);
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