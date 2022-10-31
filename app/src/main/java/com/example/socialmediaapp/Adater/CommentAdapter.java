package com.example.socialmediaapp.Adater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.Model.Comment;
import com.example.socialmediaapp.Model.User;
import com.example.socialmediaapp.ProfileUserActivity;
import com.example.socialmediaapp.R;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context ;
    ArrayList<Comment> list ;

    FirebaseUser user ;


    public CommentAdapter(Context context, ArrayList<Comment> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = list.get(position);

        holder.txt_CommentItem.setText(comment.getComment());
        String timeAgo = caculator(comment.getTimeCmt());
        holder.time_ItemComment.setText(timeAgo);

        userCmtInfo(holder.txt_NameCommentItem , holder.img_UserItemComment , comment.getPublisher());

        holder.img_UserItemComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("SHARE" , Context.MODE_PRIVATE).edit();
                String uId = comment.getPublisher();
                //Check if current user show profile fragment
                if(uId.equals(user.getUid())){
                    Toast.makeText(context , "Đây là bạn" , Toast.LENGTH_SHORT).show();
                }else {
                    editor.putString("uId" , uId);
                    editor.apply();

                    context.startActivity(new Intent(context , ProfileUserActivity.class));
                }

            }
        });


    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView img_UserItemComment ;
        private TextView txt_CommentItem , time_ItemComment , txt_NameCommentItem ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_UserItemComment = itemView.findViewById(R.id.img_UserItemComment);
            txt_CommentItem = itemView.findViewById(R.id.txt_CommentItem);
            time_ItemComment = itemView.findViewById(R.id.time_ItemComment);
            txt_NameCommentItem = itemView.findViewById(R.id.txt_NameCommentItem);
        }
    }

    private void userCmtInfo(TextView txt_name , CircleImageView img_Profile , String uId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user1 = snapshot.getValue(User.class);
                Picasso.get().load(user1.getImg_Profile()).into(img_Profile);
                txt_name.setText(user1.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String caculator(String timeCmt) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
        try {
            long time = sdf.parse(timeCmt).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
