package com.example.socialmediaapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediaapp.Adater.PostAdapter;
import com.example.socialmediaapp.Adater.StoryAdapter;
import com.example.socialmediaapp.AddStoryActivity;
import com.example.socialmediaapp.ChatActivity;
import com.example.socialmediaapp.Model.Chat;
import com.example.socialmediaapp.Model.Post;
import com.example.socialmediaapp.Model.Story;
import com.example.socialmediaapp.Model.User;
import com.example.socialmediaapp.PostActivity;
import com.example.socialmediaapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private ImageView  img_Messesge;
    private ProgressBar progress_HomePost ;
    private TextView count_Chat ;

    private RecyclerView rec_Stories , rec_Posts ;
    FirebaseAuth auth ;
    FirebaseUser user ;

    //Init rec Stories
    ArrayList<Story> stories ;
    StoryAdapter adapter ;

    //Init rec Posts
    ArrayList<Post> posts;
    PostAdapter postAdapter ;

    //Id user
    private List<String> followingList ;
    public static int count = 0 ;


    public HomeFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        img_Messesge = view.findViewById(R.id.img_Messesge);
        progress_HomePost = view.findViewById(R.id.progress_HomePost);
        count_Chat = view.findViewById(R.id.count_Chat);
        count_Chat.setVisibility(View.GONE);

        //Init UI Stories Views
        rec_Stories = view.findViewById(R.id.rec_Stories);
        rec_Stories.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity() , LinearLayoutManager.HORIZONTAL , false);
        rec_Stories.setLayoutManager(linearLayoutManager1);
        stories = new ArrayList<>();
        adapter = new StoryAdapter(getActivity() , stories);
        rec_Stories.setAdapter(adapter);

        //Init rec Post
        rec_Posts = view.findViewById(R.id.rec_Posts);
        rec_Posts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rec_Posts.setLayoutManager(linearLayoutManager);
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getActivity() , posts);
        rec_Posts.setAdapter(postAdapter);

        checkFollowing();

        img_Messesge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity() , ChatActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


    //check user Following (If following show post and story else not)

    private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(user.getUid()).child("following");
                reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    followingList.add(ds.getKey());
                }
                readPost();
                readStory();
                progress_HomePost.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Read data Post User Following
    private void readPost() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Post post = ds.getValue(Post.class);
                    for (String id : followingList){
                        if(post.getPost_By().equals(id)){
                            posts.add(post);
                        }
                    }
                    if(post.getPost_By().equals(user.getUid())){
                        posts.add(post);
                    }
                }
                Collections.shuffle(posts);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timeCurrent = System.currentTimeMillis();
                stories.clear();
                stories.add(new Story("" , "" , user.getUid() , "", 0 ,0 ));
                for (String id:followingList){
                    int countStory = 0 ;
                    Story story = null ;
                    for (DataSnapshot ds:snapshot.child(id).getChildren()){
                        story = ds.getValue(Story.class);
                        if(timeCurrent > story.getTimeStart() && timeCurrent < story.getTimeEnd()){
                            countStory++;
                        }
                    }
                    if(countStory>0){
                        stories.add(story);

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
