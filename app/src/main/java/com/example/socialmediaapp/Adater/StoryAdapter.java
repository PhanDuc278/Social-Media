package com.example.socialmediaapp.Adater;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediaapp.AddStoryActivity;
import com.example.socialmediaapp.LoginActivity;
import com.example.socialmediaapp.Model.Story;
import com.example.socialmediaapp.Model.User;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.StoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    Context context ;
    ArrayList<Story> stories ;
    FirebaseUser mUser ;

    public StoryAdapter(Context context, ArrayList<Story> stories) {
        this.context = context;
        this.stories = stories;
    }

    @NonNull
    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(viewType==0){
            //Image story == null

            View view = LayoutInflater.from(context).inflate(R.layout.item_add_story , parent , false);
            return new StoryAdapter.ViewHolder(view);
        }else {
            //Image story exsist

            View view = LayoutInflater.from(context).inflate(R.layout.item_story , parent , false);
            return new  StoryAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull StoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Story story = stories.get(position);
       DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(story.getuId());
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               User user = snapshot.getValue(User.class);
               Picasso.get().load(user.getImg_Profile()).into(holder.img_Story);
               if(position!=0){
                   Picasso.get().load(user.getImg_Profile()).into(holder.story_photo_seen);
                   holder.name_UserStory.setText(user.getName());
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

        if(holder.getAdapterPosition() !=0){
            seenStory(holder , story.getuId());
        }

        if(holder.getAdapterPosition() == 0){
            myStory(holder.addStory_Text , holder.add_Story , false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.getAdapterPosition() == 0){
                    myStory(holder.addStory_Text , holder.add_Story , true);
                }else {
                    Intent intent = new Intent(context , StoryActivity.class);
                    intent.putExtra("uId" , story.getuId());
                    context.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView story_photo_seen , img_Story  , add_Story;
        private TextView addStory_Text , name_UserStory ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            add_Story = itemView.findViewById(R.id.add_Story);
            img_Story = itemView.findViewById(R.id.img_Story);
            addStory_Text = itemView.findViewById(R.id.addStory_Text);
            name_UserStory = itemView.findViewById(R.id.name_UserStory);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return 0 ;
        }
        return 1;
    }


    //View myStory
    private void myStory(TextView view , ImageView imageView , boolean click){
        FirebaseUser  user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(user.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0 ;
                long timeCurrent = System.currentTimeMillis();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Story story = ds.getValue(Story.class);
                    //Check if story <24h show story
                    if(timeCurrent > story.getTimeStart() && timeCurrent < story.getTimeEnd()){
                        count++ ;
                    }
                }

                if(click){
                        if(count>0){
                            //if Current user posted story , when click img_Story will show tow chose 1:View story , 2:Add new story

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setMessage("Chọn!");
                            alertDialogBuilder.setPositiveButton("Xem tin",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Intent intent = new Intent(context , StoryActivity.class);
                                            intent.putExtra("uId" , user.getUid());
                                            context.startActivity(intent);

                                        }
                                    });

                            alertDialogBuilder.setNegativeButton("Thêm vào tin", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(context , AddStoryActivity.class);
                                    context.startActivity(intent);
                                    dialog.dismiss();

                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }else {
                            Intent intent = new Intent(context , AddStoryActivity.class);
                            context.startActivity(intent);
                        }
                }
                else {
                    if(count>0){
                        view.setText("Xem");
                        imageView.setVisibility(View.GONE);
                    }else {
                        view.setText("Thêm tin");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenStory(ViewHolder holder , String uId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(uId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0 ;
                for (DataSnapshot ds:snapshot.getChildren()){
                    if(!ds.child("views").child(mUser.getUid()).exists()
                            && System.currentTimeMillis() < ds.getValue(Story.class).getTimeEnd()){
                        i++;
                    }
                }
                if(i>0){
                    holder.img_Story.setVisibility(View.VISIBLE);
                    holder.story_photo_seen.setVisibility(View.GONE);
                }else {
                    holder.img_Story.setVisibility(View.GONE);
                    holder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
