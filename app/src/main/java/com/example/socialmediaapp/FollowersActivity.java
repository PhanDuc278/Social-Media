package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.example.socialmediaapp.Adater.SearchUserAdapter;
import com.example.socialmediaapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    String id , title  , storyId;
    List<String> idList;
    private Toolbar toolbar ;
    private RecyclerView rec_Follower;

    ArrayList<User> list;
    SearchUserAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        storyId = intent.getStringExtra("storyId");

        toolbar = findViewById(R.id.tool_BarFollower);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rec_Follower = findViewById(R.id.rec_Follower);
        rec_Follower.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rec_Follower.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        adapter = new SearchUserAdapter(this , list);
        rec_Follower.setAdapter(adapter);

        idList = new ArrayList<>();

        switch (title){
            case "Like":
                getLikes();
                break;
            case "Người theo dõi":
                getFollower();
                break;
            case "Đang theo dõi":
                getFollowing();
                break;
            case "views":
                getStoryView();
                break;
            default:
                return;

        }
    }

    private void getStoryView(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(id).child(storyId).child("views");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    idList.add(ds.getKey());
                }
                showUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(id).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    idList.add(ds.getKey());
                }
                showUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollower() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(id).child("follower");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    idList.add(ds.getKey());
                }
                showUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLikes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    idList.add(ds.getKey());
                }
                showUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    for (String id:idList){
                        if(user.getuId().equals(id)){
                            list.add(user);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}