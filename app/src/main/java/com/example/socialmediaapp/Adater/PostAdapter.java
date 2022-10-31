package com.example.socialmediaapp.Adater;

import android.content.Context;
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

import com.example.socialmediaapp.CommentActivity;
import com.example.socialmediaapp.FollowersActivity;
import com.example.socialmediaapp.Fragments.ProfileFragment;
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
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context context;
    ArrayList<Post> list ;

    public FirebaseAuth auth ;
    public FirebaseUser user ;

    public PostAdapter(Context context, ArrayList<Post> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Post post = list.get(position);
        Picasso.get().load(post.getImg_Post()).into(holder.img_UserPost);
        String timeAgo = caculator(post.getPost_Time());
        holder.time_Post.setText(timeAgo);

        userPostInfo(holder.img_ProfileUserPost , holder.name_UserPost , holder.publisher , post.getPost_By());
        isLike(post.getId() , holder.heart);
        isSave(post.getId() , holder.save);
        countLikes(holder.count_Like, post.getId());
        countComment(post.getId() , holder.view_Comments);
        checkCurrentUserPost(post.getId()  , holder.save);

        if (post.getDescription().equals("")){
            holder.descrition.setVisibility(View.GONE);
        }else {
            holder.descrition.setVisibility(View.VISIBLE);
            holder.descrition.setText(post.getDescription());
        }

//Click like
        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.heart.getTag().equals("Like")){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Likes")
                            .child(post.getId()).child(user.getUid()).setValue(true);
                    addNotification(post.getPost_By() , post.getId());
                }else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Likes")
                            .child(post.getId()).child(user.getUid()).removeValue();
                }

            }
        });

        holder.img_UserPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("SHARE" , Context.MODE_PRIVATE).edit();
                editor.putString("postId" , post.getId());
                editor.apply();

                context.startActivity(new Intent(context , PostDetailActivity.class));
            }
        });

        //Show detail profile User (If current User open Fragment Profile else Open ProfileUser Activity )
        holder.img_ProfileUserPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("SHARE" , Context.MODE_PRIVATE).edit();

                String uId = post.getPost_By();
                if (uId.equals(user.getUid())) {
                    ((FragmentActivity)context).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
                }else {
                    editor.putString("uId" , uId);
                    editor.apply();
                    context.startActivity(new Intent(context , ProfileUserActivity.class));

                }

            }
        });

//Show detail profile User (If current User open Fragment Profile else Open ProfileUser Activity )
        holder.name_UserPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("SHARE" , Context.MODE_PRIVATE).edit();

                String uId = post.getPost_By();
                if (uId.equals(user.getUid())) {
                    ((FragmentActivity)context).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
                }else {
                    editor.putString("uId" , uId);
                    editor.apply();
                    context.startActivity(new Intent(context , ProfileUserActivity.class));
                }

            }
        });




        //Click image Save
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.save.getTag().equals("Save")){
                    FirebaseDatabase.getInstance().getReference().child("Save").child(user.getUid()).child(post.getId()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Save").child(user.getUid()).child(post.getId()).removeValue();
                }
            }
        });


        //Click comment

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context , CommentActivity.class);
                intent.putExtra("postId" , post.getId());
                intent.putExtra("publisherId" , post.getPost_By());
                context.startActivity(intent);
            }
        });

        holder.view_Comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context , CommentActivity.class);
                intent.putExtra("postId" , post.getId());
                intent.putExtra("publisherId" , post.getPost_By());
                context.startActivity(intent);
            }
        });

        holder.count_Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(context , FollowersActivity.class);
                intent.putExtra("id" , post.getId());
                intent.putExtra("title" , "Like");
                context.startActivity(intent);
            }
        });

    }

//Set time ago
    private String caculator(String post_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
        try {
            long time = sdf.parse(post_time).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
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
        private CircleImageView img_ProfileUserPost ;
        private TextView name_UserPost  , time_Post , count_Like , descrition , publisher , view_Comments;
        private ImageView more_Post , heart , comment , save ;
        private RoundedImageView img_UserPost ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_ProfileUserPost = itemView.findViewById(R.id.img_ProfileUserPost);
            name_UserPost = itemView.findViewById(R.id.name_UserPost);
            time_Post = itemView.findViewById(R.id.time_Post);
            time_Post = itemView.findViewById(R.id.time_Post);
            count_Like = itemView.findViewById(R.id.count_Like);
            descrition = itemView.findViewById(R.id.descrition);
            publisher = itemView.findViewById(R.id.publisher);
            view_Comments = itemView.findViewById(R.id.view_Comments);
            heart = itemView.findViewById(R.id.heart);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            img_UserPost = itemView.findViewById(R.id.img_UserPost);


        }
    }

    //Load info User Post
    private void userPostInfo(CircleImageView img_ProfileUserPost  , TextView name_UserPost  ,  TextView publisher , final String uId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    Picasso.get().load(user.getImg_Profile()).into(img_ProfileUserPost);
                    name_UserPost.setText(user.getName());
                    publisher.setText(user.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Click like or not like
    private void isLike(String id , ImageView imageView){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(user.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_click_heart);
                    imageView.setTag("Liked");
                }else {
                    imageView.setImageResource(R.drawable.heart);
                    imageView.setTag("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Check save post
    private void isSave(String postId , ImageView imageView ){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Save").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if(snapshot.child(postId).exists()){
                    imageView.setImageResource(R.drawable.ic_saved);
                    imageView.setTag("Saved");

                }else {
                    imageView.setImageResource(R.drawable.ic_save1);
                    imageView.setTag("Save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Count like fill in textView like
    private void countLikes(final TextView likes , String id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getChildrenCount() == 0){
                    likes.setText("Hãy là người đầu tiên like !");
                }else {
                    likes.setText(snapshot.getChildrenCount() +" likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countComment(String postId , TextView txt_comment){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                    if(snapshot.getChildrenCount()== 0){
                        txt_comment.setText("Chưa có bình luận !");
                    }else {
                        txt_comment.setText("Xem " + snapshot.getChildrenCount() + " bình luận!");
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Check if current user will hide img_Save
    private void checkCurrentUserPost( String post_Id , ImageView img_Save){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(post_Id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    if(ds.child("post_By").equals(user.getUid())){
                        img_Save.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Add notification

    private void addNotification(String uId , String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notification").child(uId);

        if(uId.equals(user.getUid())){

        }else {
            Date date = new Date();

            SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
            final  String strDate = format.format(date);

            String  timeStamp = ""+System.currentTimeMillis();
            HashMap<String , Object> map = new HashMap<>();
            map.put("noId" , timeStamp);
            map.put("uId" ,user.getUid());
            map.put("id" , postId);
            map.put("text" , "Đã yêu thích bài viết của bạn!");
            map.put("isPost" ,"true");
            map.put("time" , strDate);

            reference.child(timeStamp).setValue(map);

        }

    }
}
