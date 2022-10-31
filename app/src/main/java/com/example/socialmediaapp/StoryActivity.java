package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialmediaapp.Model.Story;
import com.example.socialmediaapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter = 0;
    long pressTime = 0L;
    long limit = 5000L;

    private CircleImageView img_UserStory;
    private TextView name_UserStory, time_StoryPost , number_UserView;
    private ImageView img_StoryView, close_Story, delete_Story ;
    StoriesProgressView stories;
    LinearLayout lia_UserView ;
    RelativeLayout rel_Emoji ;


    List<String> images;
    List<String> storieds;
    List<String> times;
    String uId;
    String time;

    FirebaseUser user ;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    stories.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    stories.resume();
                    return limit < now -pressTime;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        user = FirebaseAuth.getInstance().getCurrentUser();

        uId = getIntent().getStringExtra("uId");


        img_UserStory = findViewById(R.id.img_UserStory);
        name_UserStory = findViewById(R.id.name_UserStory);
        img_StoryView = findViewById(R.id.img_StoryView);
        stories = findViewById(R.id.stories);
        close_Story = findViewById(R.id.close_Story);
        time_StoryPost = findViewById(R.id.time_StoryPost);
        delete_Story = findViewById(R.id.delete_Story);
        lia_UserView = findViewById(R.id.lia_UserView);
        rel_Emoji = findViewById(R.id.rel_Emoji);
        number_UserView = findViewById(R.id.number_UserView);


        lia_UserView.setVisibility(View.GONE);
        rel_Emoji.setVisibility(View.VISIBLE);
        delete_Story.setVisibility(View.GONE);

        if(uId.equals(user.getUid())){
            lia_UserView.setVisibility(View.VISIBLE);
            rel_Emoji.setVisibility(View.GONE);
            delete_Story.setVisibility(View.VISIBLE);
        }


        close_Story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        View reverse = findViewById(R.id.reverse);
        View next = findViewById(R.id.next);
        View pause = findViewById(R.id.pause);

        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stories.resume();
            }
        });

        reverse.setOnTouchListener(onTouchListener);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stories.skip();
            }
        });

        next.setOnTouchListener(onTouchListener);

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stories.pause();
            }
        });




        lia_UserView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoryActivity.this , FollowersActivity.class);
                intent.putExtra("id" , uId);
                intent.putExtra("storyId" , storieds.get(counter));
                intent.putExtra("title" , "views");
                startActivity(intent);
            }
        });

        delete_Story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stories.pause();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StoryActivity.this);
                alertDialogBuilder.setMessage("Xoá tin!");
                alertDialogBuilder.setPositiveButton("Xóa",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(user.getUid()).child(storieds.get(counter));
                                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(StoryActivity.this , "Đã xóa!" , Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                            }
                        });

                alertDialogBuilder.setNegativeButton("Bỏ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stories.resume();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        getStories(uId);
        userInfo(uId);

    }



    @Override
    public void onNext() {

        addView(storieds.get(counter));
        countSeen(storieds.get(counter));

        Glide.with(getApplicationContext()).load(images.get(++counter)).into(img_StoryView);
        time = caculator(times.get(counter++));
        time_StoryPost.setText(time);



    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) {
            return;
        }
        countSeen(storieds.get(counter));
        time = caculator(times.get(--counter));
        time_StoryPost.setText(time);
        Glide.with(getApplicationContext()).load(images.get(--counter)).into(img_StoryView);


    }




    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stories.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stories.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        stories.resume();
    }


    //Get Story
    private void getStories(String uId) {
        images = new ArrayList<>();
        storieds = new ArrayList<>();
        times = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(uId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                images.clear();
                storieds.clear();
                times.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Story story = ds.getValue(Story.class);
                    long timeCurrent = System.currentTimeMillis();
                    if (timeCurrent > story.getTimeStart() && timeCurrent < story.getTimeEnd()) {
                        images.add(story.getImg_Story());
                        storieds.add(story.getStoryId());
                        times.add(story.getPostTime());
                    }
                }
                stories.setStoriesCount(images.size());
                stories.setStoryDuration(5000L);
                stories.setStoriesListener(StoryActivity.this);
                stories.startStories(counter);

                Glide.with(getApplicationContext()).load(images.get(counter)).into(img_StoryView);
                time = caculator(times.get(counter));
                time_StoryPost.setText(time);

                addView(storieds.get(counter));
                countSeen(storieds.get(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String caculator(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
        try {
            long time = sdf.parse(s).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void userInfo(String uId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getImg_Profile()).into(img_UserStory);
                name_UserStory.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addView(String storyId){
        FirebaseDatabase.getInstance().getReference("Story").child(uId).child(storyId).child("views").child(user.getUid()).setValue(true);
    }

    private void countSeen(String  storyId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(uId).child(storyId).child("views");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                number_UserView.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}