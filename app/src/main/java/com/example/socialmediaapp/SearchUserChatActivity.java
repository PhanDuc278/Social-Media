package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.socialmediaapp.Adater.UserContactAdapter;
import com.example.socialmediaapp.Model.User;
import com.example.socialmediaapp.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SearchUserChatActivity extends AppCompatActivity {
    private  EditText edt_SearchUserChat ;
    private ImageView clear_TextSeachChat , back_SearchUser;
    private RecyclerView rec_ResultUserChat ;
    List<User> userList ;
    UserContactAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_chat);

        edt_SearchUserChat = findViewById(R.id.edt_SearchUserChat);
        clear_TextSeachChat = findViewById(R.id.clear_TextSeachChat);
        rec_ResultUserChat = findViewById(R.id.rec_ResultUserChat);
        back_SearchUser = findViewById(R.id.back_SearchUser);
        rec_ResultUserChat.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rec_ResultUserChat.setLayoutManager(manager);


        back_SearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        clear_TextSeachChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_SearchUserChat.setText("");
            }
        });

        edt_SearchUserChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                if(s.length() == 0){
                    clear_TextSeachChat.setVisibility(View.GONE);
                    laodUserSusggest();
                }else {
                    adapter.getFilter().filter(s);
                    clear_TextSeachChat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        laodUserSusggest();
    }

    private void laodUserSusggest() {
        userList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    User model = ds.getValue(User.class);
                    if(!model.getuId().equals(Utils.uid())){
                        userList.add(model);
                        adapter = new UserContactAdapter(SearchUserChatActivity.this , userList);
                        rec_ResultUserChat.setAdapter(adapter);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(Utils.uid());
        HashMap<String , Object> map = new HashMap<>();
        map.put("status" , status);
        reference.updateChildren(map);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(Utils.uid());
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

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(Utils.uid());
        HashMap<String , Object> map = new HashMap<>();
        map.put("calculatorTimeOff" , strDate);
        reference.updateChildren(map);
    }
}