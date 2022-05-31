package com.example.mindly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    Context context;

    ArrayList<PostModel> postList;

    public PostAdapter(Context context, ArrayList<PostModel> postList) {
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


        PostModel postModel = postList.get(position);
        holder.text_post.setText(postModel.getText_post());
        holder.username.setText(postModel.getUsername());
        holder.at_username.setText(postModel.getUsername());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        TextView text_post, username, at_username;
        ImageView image_post;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            text_post = itemView.findViewById(R.id.tv_text_post);
            username = itemView.findViewById(R.id.tv_username);
            at_username = itemView.findViewById(R.id.tv_username);
            image_post = itemView.findViewById(R.id.iv_image_post);
        }
    }

}
