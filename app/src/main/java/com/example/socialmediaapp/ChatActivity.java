package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.socialmediaapp.Fragments.ChatFragment;
import com.example.socialmediaapp.Fragments.UserContactFragment;
import com.example.socialmediaapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.ak1.BubbleTabBar;
import io.ak1.OnBubbleClickListener;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class ChatActivity extends AppCompatActivity {
    private BubbleTabBar bottomNavChat  ;
    private CircleImageView img_UserCurrentChat ;
    private ImageView back_Chat ;




    FirebaseUser user ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        user = FirebaseAuth.getInstance().getCurrentUser();
        img_UserCurrentChat = findViewById(R.id.img_UserCurrentChat);
        back_Chat = findViewById(R.id.back_Chat);



        back_Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Load img_User profile
        loadImagUserProfile();

        bottomNavChat = findViewById(R.id.bottomNavChat);


        FragmentTransaction chat = getSupportFragmentManager().beginTransaction();
        chat.replace(R.id.container_Chat, new ChatFragment());
        chat.commit();


        bottomNavChat.addBubbleListener(new OnBubbleClickListener() {
            @Override
            public void onBubbleClick(int i) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (i) {
                    case R.id.home:
                        transaction.replace(R.id.container_Chat, new ChatFragment());
                        break;
                    case R.id.contact:
                        transaction.replace(R.id.container_Chat, new UserContactFragment());
                        break;
                }
                transaction.commit();
            }
        });
    }

    private void loadImagUserProfile() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user1 = snapshot.getValue(User.class);

                try {
                    Glide.with(getApplicationContext()).load(user1.getImg_Profile()).into(img_UserCurrentChat);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void status(String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        HashMap<String , Object> map = new HashMap<>();
        map.put("status" , status);
        reference.updateChildren(map);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        HashMap<String , Object> map = new HashMap<>();
        map.put("calculatorTimeOff" , "");
        reference.updateChildren(map);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");


        Date date = new Date();

        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
        final  String strDate = format.format(date);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        HashMap<String , Object> map = new HashMap<>();
        map.put("calculatorTimeOff" , strDate);
        reference.updateChildren(map);
    }


}