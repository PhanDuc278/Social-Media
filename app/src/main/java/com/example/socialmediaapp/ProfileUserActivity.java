package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediaapp.Adater.MyPostAdapter;
import com.example.socialmediaapp.Model.Post;
import com.example.socialmediaapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUserActivity extends AppCompatActivity {
    String profileId ;

    private Toolbar toolBarUser ;
    private CircleImageView img_ProfileUser ;
    private TextView name_ProfileUser , txt_Chat ,count_PostUser , count_FollowerUser , count_FollowingUser , txt_Follow , txt_Following , txt_Follower;
    private ImageView img_TabPostUser ;
    private RecyclerView rel_TabPostUser ;

    private MyPostAdapter postAdapter ;
    private ArrayList<Post> posts ;


    FirebaseUser user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getApplication().getSharedPreferences("SHARE", Context.MODE_PRIVATE);

        profileId = preferences.getString("uId", "none");

        toolBarUser = findViewById(R.id.toolBarUser);
        setSupportActionBar(toolBarUser);
        getSupportActionBar().setTitle("Thông tin cá nhân");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolBarUser.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        img_ProfileUser = findViewById(R.id.img_ProfileUser);
        name_ProfileUser = findViewById(R.id.name_ProfileUser);
        count_PostUser = findViewById(R.id.count_PostUser);
        count_FollowerUser = findViewById(R.id.count_FollowerUser);
        count_FollowingUser = findViewById(R.id.count_FollowingUser);
        txt_Follow = findViewById(R.id.txt_Follow);
        txt_Following = findViewById(R.id.txt_Following);
        txt_Follower = findViewById(R.id.txt_Follower);
        txt_Chat = findViewById(R.id.txt_Chat);
        img_TabPostUser = findViewById(R.id.img_TabPostUser);

        rel_TabPostUser = findViewById(R.id.rel_TabPostUser);
        rel_TabPostUser.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this , 3);
        rel_TabPostUser.setLayoutManager(linearLayoutManager);
        posts = new ArrayList<>();
        postAdapter = new MyPostAdapter(this , posts);
        rel_TabPostUser.setAdapter(postAdapter);

        userInfo();
        checkFollowing();
        countFollower();
        countFollowing();
        countPost();

        loadYourPost();

        txt_Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileUserActivity.this , ChatUserActivity.class);
                intent.putExtra("uId" , profileId);
                startActivity(intent);
            }
        });


        txt_Follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt = txt_Follow.getText().toString();
                if (txt.equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference("Follow")
                            .child(user.getUid())
                            .child("following")
                            .child(profileId)
                            .setValue(true);

                    FirebaseDatabase.getInstance().getReference("Follow")
                            .child(profileId)
                            .child("follower")
                            .child(user.getUid())
                            .setValue(true);

                    addNotification();
                } else if (txt.equals("Following")) {

                    FirebaseDatabase.getInstance().getReference("Follow")
                            .child(user.getUid())
                            .child("following")
                            .child(profileId)
                            .removeValue();

                    FirebaseDatabase.getInstance().getReference("Follow")
                            .child(profileId)
                            .child("follower")
                            .child(user.getUid())
                            .removeValue();

                }
            }
        });

        txt_Following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileUserActivity.this  , FollowersActivity.class);
                intent.putExtra("id" , profileId);
                intent.putExtra("title" , "Đang theo dõi");
                startActivity(intent);
            }
        });

        txt_Follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileUserActivity.this  , FollowersActivity.class);
                intent.putExtra("id" , profileId);
                intent.putExtra("title" , "Người theo dõi");
                startActivity(intent);
            }
        });



    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(!snapshot.exists()){
                   return;
               }
                User user1 = snapshot.getValue(User.class);

                Picasso.get().load(user1.getImg_Profile()).into(img_ProfileUser);
                name_ProfileUser.setText(user1.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()) {
                    txt_Follow.setText("Following");
                } else {
                    txt_Follow.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countFollower(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("follower");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count_FollowerUser.setText(""+ snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countFollowing(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count_FollowingUser.setText(""+ snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i  = 0;
                for (DataSnapshot ds:snapshot.getChildren()){
                    Post post= ds.getValue(Post.class);
                    if(post.getPost_By().equals(profileId)){
                        i++;
                    }
                }

                count_PostUser.setText("" +i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//Load item post
    private void loadYourPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Post post = ds.getValue(Post.class);
                    if(post.getPost_By().equals(profileId)){
                        posts.add(post);
                    }
                }
                Collections.reverse(posts);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileUserActivity.this , ""+error.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notification").child(profileId);
        String timeStamp = ""+System.currentTimeMillis();
        HashMap<String , Object> map = new HashMap<>();
        map.put("noId" , timeStamp);
        map.put("uId" ,user.getUid());
        map.put("id" , "");
        map.put("text" , "Đã bắt đầu theo dõi bạn!");
        map.put("isPost" , "false");

        reference.child(timeStamp).setValue(map);
    }



}