package com.example.socialmediaapp.Adater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.Model.Post;
import com.example.socialmediaapp.PostDetailActivity;
import com.example.socialmediaapp.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Post> posts ;

    public MyPostAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public MyPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_profile , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);

        Picasso.get().load(post.getImg_Post()).into(holder.img_PostItem);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor  = context.getSharedPreferences("SHARE" , Context.MODE_PRIVATE).edit();
                editor.putString("postId" , post.getId());
                editor.apply();
                context.startActivity(new Intent(context , PostDetailActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView img_PostItem ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_PostItem = itemView.findViewById(R.id.img_PostItem);
        }
    }
}
