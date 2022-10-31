package com.example.socialmediaapp.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediaapp.Adater.MyPostAdapter;
import com.example.socialmediaapp.EditProfileActivity;
import com.example.socialmediaapp.FollowersActivity;
import com.example.socialmediaapp.LoginActivity;
import com.example.socialmediaapp.Model.Post;
import com.example.socialmediaapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private TextView name_ToolbarProfile, name_UserProfile, count_Post, count_Follower, count_Following, edit_Profile , txt_Following1 , txt_Follower1;
    private ImageView img_TabPost, img_TabSave, img_SettingProfile, back_Profile;
    private CircleImageView img_Profile;
    private RecyclerView rel_TabPost, rel_TabSave;

    private List<String> mySaves;

    private MyPostAdapter postAdapter ;
    ArrayList<Post> posts ;

    private MyPostAdapter saveAdapter ;
    ArrayList<Post> saves ;

    FirebaseAuth auth;
    FirebaseUser user;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();



        name_ToolbarProfile = view.findViewById(R.id.name_ToolbarProfile);
        name_UserProfile = view.findViewById(R.id.name_UserProfile);
        count_Post = view.findViewById(R.id.count_Post);
        count_Follower = view.findViewById(R.id.count_Follower);
        count_Following = view.findViewById(R.id.count_Following);
        edit_Profile = view.findViewById(R.id.edit_Profile);
        img_TabPost = view.findViewById(R.id.img_TabPost);
        img_TabSave = view.findViewById(R.id.img_TabSave);
        img_Profile = view.findViewById(R.id.img_Profile);
        img_SettingProfile = view.findViewById(R.id.img_SettingProfile);
        back_Profile = view.findViewById(R.id.back_Profile);
        txt_Following1 = view.findViewById(R.id.txt_Following1);
        txt_Follower1 = view.findViewById(R.id.txt_Follower1);


        //init Rel post
        rel_TabPost = view.findViewById(R.id.rel_TabPost);
        rel_TabPost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getActivity(), 3);
        posts = new ArrayList<>();
        postAdapter = new MyPostAdapter(getActivity() , posts);
        rel_TabPost.setAdapter(postAdapter);
        rel_TabPost.setLayoutManager(linearLayoutManager);

        //init Rel Save
        rel_TabSave = view.findViewById(R.id.rel_TabSave);
        rel_TabSave.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new GridLayoutManager(getActivity() , 3);
        rel_TabSave.setLayoutManager(linearLayoutManager1);
        saves = new ArrayList<>();
        saveAdapter = new MyPostAdapter(getActivity() , saves);
        rel_TabSave.setAdapter(saveAdapter);

        img_TabPost.setBackgroundResource(R.drawable.bg_rel);
        img_TabSave.setBackgroundResource(R.drawable.bg_rel1);


        loadMyPost();

        img_SettingProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomsheet();
            }
        });

        img_TabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rel_TabSave.setVisibility(View.VISIBLE);
                rel_TabPost.setVisibility(View.GONE);

                img_TabPost.setBackgroundResource(R.drawable.bg_rel1);
                img_TabSave.setBackgroundResource(R.drawable.bg_rel);

                mySaves();
            }
        });

        img_TabPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rel_TabSave.setVisibility(View.GONE);
                rel_TabPost.setVisibility(View.VISIBLE);

                img_TabPost.setBackgroundResource(R.drawable.bg_rel);
                img_TabSave.setBackgroundResource(R.drawable.bg_rel1);
                loadMyPost();
            }
        });

        edit_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity() , EditProfileActivity.class);
                intent.putExtra("uId" , user.getUid());
                startActivity(intent);
            }
        });

        txt_Follower1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity() , FollowersActivity.class);
                intent.putExtra("id" , user.getUid());
                intent.putExtra("title" , "Người theo dõi");
                startActivity(intent);
            }
        });

        txt_Following1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity() , FollowersActivity.class);
                intent.putExtra("id" , user.getUid());
                intent.putExtra("title" , "Đang theo dõi");
                startActivity(intent);
            }
        });

        currentUserInfo();
        countFollowing();
        countFollower();
        countPost();




        return view;
    }



    private void currentUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    name_UserProfile.setText(""+snapshot.child("name").getValue());
                    name_ToolbarProfile.setText(""+snapshot.child("name").getValue());

                    String img_Profile1 = ""+snapshot.child("img_Profile").getValue();
                    Picasso.get().load(img_Profile1).into(img_Profile);
                }
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
                int i = 0 ;
                for (DataSnapshot ds:snapshot.getChildren()){
                    Post post = ds.getValue(Post.class);
                    if(post.getPost_By().equals(user.getUid())){
                        i++;
                    }
                }
                count_Post.setText(""+i);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void countFollowing(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count_Following.setText(""+ snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countFollower(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid()).child("follower");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count_Follower.setText(""+ snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showBottomsheet() {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet_logout, null);
        sheetDialog.setContentView(view);
        sheetDialog.show();
        TextView logout = view.findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Bạn có muốn đăng xuất không!");
                alertDialogBuilder.setPositiveButton("Có",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                auth.signOut();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sheetDialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    private void loadMyPost() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Post post = ds.getValue(Post.class);
                    if(post.getPost_By().equals(user.getUid())){
                        posts.add(post);
                    }
                }
                Collections.reverse(posts);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity() , ""+error.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mySaves(){
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Save").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    mySaves.add(ds.getKey());
                }
                loadSave();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadSave(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                saves.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Post post = ds.getValue(Post.class);
                    for (String id:mySaves){
                        if(post.getId().equals(id)){
                            saves.add(post);
                        }
                    }
                }
                saveAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}