package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.socialmediaapp.Adater.ChatAdapter;
import com.example.socialmediaapp.Model.Chat;
import com.example.socialmediaapp.Model.User;
import com.example.socialmediaapp.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserActivity extends AppCompatActivity {
    String uId ;
    private ImageView back_ChatDetail , audio_Call , video_Call , send_Message  , add_TypeMessage;
    private CircleImageView img_UserChatDetail ;
    private EditText edt_Message ;
    private TextView nameUserChatDetail , user_Status , txt_UserOff;
    private LottieAnimationView animationView ;
    String lastChat , isSeen , myName , myImage;
    long timeLast ;
    FirebaseUser fuser  ;
    DatabaseReference reference ;

    private ChatAdapter chatAdapter ;
    private List<Chat> chats ;
    RecyclerView rec_ChatUser2 ;
    Chat chat ;
    Utils utils  ;

    ValueEventListener seen;
    SharedPreferences sharedPreferences ;

    @Override
    protected void onStart() {
        super.onStart();
        checkKeyBoard();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user);

        utils = new Utils();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        uId = getIntent().getStringExtra("uId");
        sharedPreferences = ChatUserActivity.this.getSharedPreferences("SHARE" , Context.MODE_PRIVATE);
        myImage = sharedPreferences.getString("myName" , "none");
        myImage = sharedPreferences.getString("myImage" , "none");

        //Init UI views
        back_ChatDetail = findViewById(R.id.back_ChatDetail);
        audio_Call = findViewById(R.id.audio_Call);
        video_Call = findViewById(R.id.video_Call);
        send_Message = findViewById(R.id.send_Message);
        edt_Message = findViewById(R.id.edt_Message);
        img_UserChatDetail = findViewById(R.id.img_UserChatDetail);
        nameUserChatDetail = findViewById(R.id.nameUserChatDetail);
        user_Status = findViewById(R.id.user_Status);
        txt_UserOff = findViewById(R.id.txt_UserOff);
        add_TypeMessage = findViewById(R.id.add_TypeMessage);
        animationView = findViewById(R.id.animation_Typing);




        //Init Rec chat
        rec_ChatUser2 = findViewById(R.id.rec_ChatUser2);
        rec_ChatUser2.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rec_ChatUser2.setLayoutManager(linearLayoutManager);
        chats = new ArrayList<>();


        back_ChatDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        audio_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatUserActivity.this , "Chức năng chưa có!" , Toast.LENGTH_SHORT).show();
            }
        });

        video_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatUserActivity.this , "Chức năng chưa có!" , Toast.LENGTH_SHORT).show();
            }
        });

        add_TypeMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheetTypeMessage();
            }
        });



        send_Message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edt_Message.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    edt_Message.setError("Nhập tin nhắn!");
                }else {
                    sendMessage(message);
                    utils.hideKeyBoard(ChatUserActivity.this);
                    getToken(message , uId , myImage);
                }

            }
        });

        edt_Message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkKeyBoard();
            }
        });

        edt_Message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                if(s.length() == 0){
                    send_Message.setVisibility(View.GONE);
                    updateTyping("false");
                }else {
                    send_Message.setVisibility(View.VISIBLE);
                    updateTyping(uId);
                }
            }



            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        img_UserChatDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("SHARE" ,MODE_PRIVATE).edit();
                editor.putString("uId" , uId);
                editor.apply();
                startActivity(new Intent(ChatUserActivity.this , ProfileUserActivity.class));
                finish();

            }
        });

        nameUserChatDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("SHARE" ,MODE_PRIVATE).edit();
                editor.putString("uId" , uId);
                editor.apply();
                startActivity(new Intent(ChatUserActivity.this , ProfileUserActivity.class));
                finish();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(uId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                //Check status User

                if(user.getStatus().equals("online")){
                    user_Status.setVisibility(View.VISIBLE);
                    txt_UserOff.setVisibility(View.GONE);
                    user_Status.setText("Đang hoạt động");

                }if(user.getStatus().equals("offline")){
                    user_Status.setVisibility(View.GONE);
                    txt_UserOff.setVisibility(View.VISIBLE);
                    String timeOff = calculator(user.getCalculatorTimeOff());
                    txt_UserOff.setText("Hoạt động " + timeOff);

                }

                nameUserChatDetail.setText(user.getName());

                try {
                    Glide.with(getApplicationContext()).load(user.getImg_Profile()).into(img_UserChatDetail);
                }catch (Exception e){
                    e.printStackTrace();
                }

                readChat(fuser.getUid() , uId , user.getImg_Profile());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seenMessage(uId);
        checkTyping(uId);

    }

    private void openBottomSheetTypeMessage() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_type_chat , null);

        BottomSheetDialog sheetDialog = new BottomSheetDialog(ChatUserActivity.this);
        sheetDialog.setContentView(view);
        sheetDialog.show();
    }

    private void updateTyping(String typing) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        HashMap<String , Object> map = new HashMap<>();
        map.put("typing" , typing);
        reference.updateChildren(map);
    }

    private void checkTyping(String hisId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(hisId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String typing = snapshot.child("typing").getValue().toString();
                    if(typing.equals(fuser.getUid())){
                        animationView.setVisibility(View.VISIBLE);
                        animationView.playAnimation();
                    }else {
                        animationView.cancelAnimation();
                        animationView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Check if recipent seen message
    private void seenMessage(String userId){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seen = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if(chat.getRecipient().equals(fuser.getUid()) && chat.getSenderId().equals(userId) ){
                        HashMap<String , Object> map = new HashMap<>();
                        map.put("isseen" , "true");
                        ds.getRef().updateChildren(map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private String calculator(String calculatorTimeOff) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
        try {
            long time = sdf.parse(calculatorTimeOff).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    private void sendMessage(String message) {
        String id = ""+System.currentTimeMillis();

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        HashMap<String , Object> map = new HashMap<>();
        map.put("id" , id);
        map.put("senderId" , fuser.getUid());
        map.put("message" ,message);
        map.put("recipient" , uId);
        map.put("timeSend" , System.currentTimeMillis());
        map.put("isseen" , "false");
        map.put("emoji" , "");

        reference.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                edt_Message.setText("");
            }
        });

        lastChat();

    }

    public void readChat(String myId , String uId , String img_Profile){


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if(chat.getRecipient().equals(myId) && chat.getSenderId().equals(uId) ||
                            chat.getRecipient().equals(uId) && chat.getSenderId().equals(myId)){
                        chats.add(chat);
                    }
                    chatAdapter = new ChatAdapter(ChatUserActivity.this , chats , img_Profile);
                    rec_ChatUser2.setAdapter(chatAdapter);
                    rec_ChatUser2.scrollToPosition(chats.size() -1);
                }

                lastChat();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Get last message to fill in chat List
    private void lastChat(){
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("ChatList").child(fuser.getUid());
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("ChatList").child(uId);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    chat = ds.getValue(Chat.class);
                    if (chat.getRecipient().equals(fuser.getUid()) && chat.getSenderId().equals(uId) ||
                            chat.getRecipient().equals(uId) && chat.getSenderId().equals(fuser.getUid())){
                        lastChat = chat.getMessage();
                        isSeen = chat.getIsseen();
                        timeLast = chat.getTimeSend();
                    }
                    //Check if last chat by You is Sender
                    if(chat.getSenderId().equals(fuser.getUid())){
                        HashMap<String , Object> map = new HashMap<>();
                        map.put("id" , uId);
                        map.put("lastChat" , "Bạn : " + lastChat);
                        map.put("timeLast" , timeLast);
                        map.put("isSeen" , "");
                        reference1.child(uId).updateChildren(map);

                        HashMap<String , Object> map1 = new HashMap<>();
                        map1.put("id" , fuser.getUid());
                        map1.put("lastChat" , lastChat);
                        map1.put("timeLast" , timeLast);
                        map1.put("isSeen" , isSeen);
                        reference2.child(fuser.getUid()).updateChildren(map1);

                    }else {
                        HashMap<String , Object> map = new HashMap<>();
                        map.put("id" , uId);
                        map.put("lastChat" , lastChat);
                        map.put("timeLast" , timeLast);
                        map.put("isSeen" , isSeen);
                        reference1.child(uId).updateChildren(map);

                        HashMap<String , Object> map1 = new HashMap<>();
                        map1.put("id" , fuser.getUid());
                        map1.put("lastChat" , "Bạn : " + lastChat);
                        map1.put("timeLast" , timeLast);
                        map1.put("isSeen" , "");
                        reference2.child(fuser.getUid()).updateChildren(map1);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                    if(chats.size() >0){
                        rec_ChatUser2.scrollToPosition(chats.size()-1);
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });
    }


    private void getToken(String message , String uId , String image){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(uId);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();


                JSONObject to = new JSONObject();
                JSONObject data = new JSONObject();
                try {
                    data.put("title", myName);
                    data.put("message", message);
                    data.put("hisID", fuser.getUid());
                    data.put("hisImage", image);


                    to.put("to", token);
                    to.put("data", data);

                    sendNotification(to);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(JSONObject to) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, com.example.socialmediaapp.Service.NOTIFICATION, to, response -> {
            Log.d("notification", "sendNotification: " + response);
        }, error -> {
            Log.d("notification", "sendNotification: " + error);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("Authorization", "key=" + Service.SERVICE_KEY);
                map.put("Content-Type", "application/json");
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }


    private void status(String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        HashMap<String , Object> map = new HashMap<>();
        map.put("status" , status);
        reference.updateChildren(map);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        HashMap<String , Object> map = new HashMap<>();
        map.put("calculatorTimeOff" , "");
        reference.updateChildren(map);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        reference.removeEventListener(seen);
        Date date = new Date();

        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
        final  String strDate = format.format(date);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        HashMap<String , Object> map = new HashMap<>();
        map.put("calculatorTimeOff" , strDate);
        reference.updateChildren(map);
    }

}