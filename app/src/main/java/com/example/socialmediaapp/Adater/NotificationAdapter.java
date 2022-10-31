package com.example.socialmediaapp.Adater;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.Fragments.ProfileFragment;
import com.example.socialmediaapp.LoginActivity;
import com.example.socialmediaapp.Model.Notification;
import com.example.socialmediaapp.Model.Post;
import com.example.socialmediaapp.Model.User;
import com.example.socialmediaapp.PostDetailActivity;
import com.example.socialmediaapp.ProfileUserActivity;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context ;
    private ArrayList<Notification> list ;

    FirebaseUser user;

    public NotificationAdapter(Context context, ArrayList<Notification> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification , parent , false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        final Notification notification = list.get(position);

        holder.txt_Notification.setText(notification.getText());
        String timeAgo = caculator(notification.getTime());
        holder.time_Notification.setText(timeAgo);
        getUserInfo( holder.img_UserNotification, holder.name_UserNotification , notification.getuId());

        if(notification.getIsPost().equals("true")){
            holder.img_PostNotification.setVisibility(View.VISIBLE);
            getImagePost(holder.img_PostNotification , notification.getId());
        }else {
            holder.img_PostNotification.setVisibility(View.GONE);
        }

        //View User
        holder.img_UserNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    SharedPreferences.Editor editor = context.getSharedPreferences("SHARE" , Context.MODE_PRIVATE).edit();
                    editor.putString("uId" , notification.getuId());
                    editor.apply();

                context.startActivity(new Intent(context , ProfileUserActivity.class));

            }
        });

//View detail Posts
        holder.img_PostNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("SHARE" , Context.MODE_PRIVATE).edit();
                editor.putString("postId", notification.getId());
                editor.apply();
                context.startActivity(new Intent(context , PostDetailActivity.class));
            }
        });

        //Remove notification
        holder.delete_Notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Xoá thông báo!");
                alertDialogBuilder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                FirebaseDatabase.getInstance().getReference()
                                        .child("Notification")
                                        .child(user.getUid())
                                        .child(notification.getNoId())
                                        .removeValue();
                            }
                        });

                alertDialogBuilder.setNegativeButton("Bỏ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialogBuilder.setCancelable(true);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    private String caculator(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
        try {
            long times = sdf.parse(time).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(times, now, DateUtils.MINUTE_IN_MILLIS);
            return ago+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_UserNotification ;
        private TextView name_UserNotification , time_Notification , txt_Notification , delete_Notification;
        private RoundedImageView img_PostNotification ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_UserNotification = itemView.findViewById(R.id.img_UserNotification);
            name_UserNotification = itemView.findViewById(R.id.name_UserNotification);
            time_Notification = itemView.findViewById(R.id.time_Notification);
            txt_Notification = itemView.findViewById(R.id.txt_Notification);
            delete_Notification = itemView.findViewById(R.id.delete_Notification);
            img_PostNotification = itemView.findViewById(R.id.img_PostNotification);
        }
    }

    private void getUserInfo(final ImageView imageView , final TextView name , String uId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getImg_Profile()).into(imageView);
                name.setText(user.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getImagePost(final ImageView  imageView , String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                Picasso.get().load(post.getImg_Post()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
