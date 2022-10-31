package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.socialmediaapp.Adater.PostAdapter;
import com.example.socialmediaapp.Model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class PostDetailActivity extends AppCompatActivity {

    String postId ;
    private RecyclerView rec_ImageView ;
    private PostAdapter postAdapter ;
    private ArrayList<Post> posts ;

    private Toolbar toolbar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        SharedPreferences preferences = PostDetailActivity.this.getSharedPreferences("SHARE" , Context.MODE_PRIVATE);
        postId = preferences.getString("postId" , "none");


        toolbar = findViewById(R.id.toolBar_DetailPost);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chi tiết");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rec_ImageView = findViewById(R.id.rec_ImageView);
        rec_ImageView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rec_ImageView.setLayoutManager(linearLayoutManager);

        posts = new ArrayList<>();
        postAdapter = new PostAdapter(this, posts);
        rec_ImageView.setAdapter(postAdapter);

        readPost();

    }

    private void readPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                Post post = snapshot.getValue(Post.class);
                posts.add(post);

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}