package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.socialmediaapp.Adater.CommentAdapter;
import com.example.socialmediaapp.Model.Comment;
import com.example.socialmediaapp.Utils.Utils;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {
    private ImageView send_Comment ;
    private CircleImageView img_UserComment ;
    private EditText edt_WriteComment ;
    private RecyclerView rec_Comments ;
    CommentAdapter adapter ;
    ArrayList<Comment> list ;

    String postId , publisherId ;
    Utils utils;

    FirebaseUser user ;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        utils = new Utils();

        Toolbar toolBar_Comment = findViewById(R.id.toolBar_Comment);
        send_Comment = (ImageView) findViewById(R.id.send_Comment);
        img_UserComment = findViewById(R.id.img_UserComment);
        edt_WriteComment = findViewById(R.id.edt_WriteComment);
        rec_Comments = findViewById(R.id.rec_Comments);
        rec_Comments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rec_Comments.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        adapter = new CommentAdapter(this , list);
        rec_Comments.setAdapter(adapter);

        setSupportActionBar(toolBar_Comment);
        getSupportActionBar().setTitle("Bình Luận");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolBar_Comment.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });





        postId = getIntent().getStringExtra("postId");
        publisherId = getIntent().getStringExtra("publisherId");

        send_Comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_WriteComment.getText().toString().equals("")){
                    edt_WriteComment.setError("Viết bình luận!");
                }else {
                    addComment();
                }
            }
        });

        edt_WriteComment.setOnClickListener(view ->{
            checkKeyBoard();
        });


        loadImgUserComment();
        readComments();

    }

    //When key board appear push up recycleview above keyBoard
    private void checkKeyBoard(){
        final  View view = findViewById(R.id.rel_ChatUser);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();

                view.getWindowVisibleDisplayFrame(rect);

                int h = view.getRootView().getHeight() -rect.height();
                if(h >0.25*view.getRootView().getHeight()){
                    if(list.size() >0){
                        rec_Comments.scrollToPosition(list.size()-1);
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });
    }

//Add comment
    private void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        Date date = new Date();

        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
        final  String srtDate = format.format(date);

        HashMap<String  , Object> map = new HashMap<>();
        map.put("comment" , ""+edt_WriteComment.getText().toString());
        map.put("publisher" , ""+user.getUid());
        map.put("postBy" , ""+publisherId);
        map.put("timeCmt" , ""+ srtDate);

        addNotification();

      reference.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
              if(task.isSuccessful()){
                  edt_WriteComment.setText("");
                  utils.hideKeyBoard(CommentActivity.this);
              }
          }
      });

    }

    //
    private void loadImgUserComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                   final String img_Profile = ""+snapshot.child("img_Profile").getValue();

                   try {
                       Picasso.get().load(img_Profile).into(img_UserComment);
                   }catch (Exception e ){
                       e.printStackTrace();
                   }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Comment comment = ds.getValue(Comment.class);
                    list.add(comment);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //Add notification

    private void addNotification(){


        if(user.getUid().equals(publisherId)){

        }else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notification").child(publisherId);

            Date date = new Date();

            SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
            final  String strDate = format.format(date);
            String timeStamp = ""+System.currentTimeMillis();
            HashMap<String , Object> map = new HashMap<>();
            map.put("noId" , timeStamp);
            map.put("uId" ,user.getUid());
            map.put("id" , postId);
            map.put("text" , "Đã bình luận : " + edt_WriteComment.getText().toString());
            map.put("isPost" ,"true");
            map.put("time", strDate);

            reference.child(timeStamp).setValue(map);
        }

    }


}