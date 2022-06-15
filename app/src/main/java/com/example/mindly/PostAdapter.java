package com.example.mindly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    Context context;

    ArrayList<Post> postList;

    public PostAdapter(Context context, ArrayList<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.post_item,parent,false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.text_post.setText(post.getTextPost());
        holder.username.setText(post.getUsername());
        holder.atUsername.setText(post.getUserID());
        Glide.with(context).load(postList.get(position).getImages()).into(holder.images);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        TextView text_post, username, atUsername;
        ImageView images;
        Button del;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            text_post = itemView.findViewById(R.id.tv_text_post);
            username = itemView.findViewById(R.id.tv_username);
            atUsername = itemView.findViewById(R.id.tv_userID);
            images = itemView.findViewById(R.id.iv_image_post);
            del = itemView.findViewById(R.id.bt_delete);
            del.setOnClickListener(view -> {
                FirebaseDatabase.getInstance().getReference().child("PostData").child("Post" + text_post.getText().toString()).removeValue();
            });

        }

    }

}
