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
import android.widget.TextView;

import com.example.socialmediaapp.Adater.UserChatAdapter;
import com.example.socialmediaapp.Adater.UserChatOnlineAdapter;
import com.example.socialmediaapp.Model.ChatList;
import com.example.socialmediaapp.Model.User;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.SearchUserChatActivity;
import com.example.socialmediaapp.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChatFragment extends Fragment {

    private TextView txt_SearchUser ;
    private RecyclerView rec_UserOnline , rec_UserChat ;
    List<ChatList> chatLists ;
    List<User> userList ;
    UserChatAdapter chatAdapter ;
    UserChatOnlineAdapter chatOnlineAdapter ;

    DatabaseReference reference ;

    public ChatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);
        txt_SearchUser = view.findViewById(R.id.txt_SearchUser);
        rec_UserOnline = view.findViewById(R.id.rec_UserOnline);
        rec_UserChat = view.findViewById(R.id.rec_UserChat);

        rec_UserOnline.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL , false);
        rec_UserOnline.setLayoutManager(manager);

        rec_UserChat.setHasFixedSize(true);
        LinearLayoutManager manager1 = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        rec_UserChat.setLayoutManager(manager1);



        txt_SearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity() , SearchUserChatActivity.class));
            }
        });

        loadUserOnline();
        loadUserChat();
        return view ;
    }

    //Load RecycleView UserOnline
    private void loadUserOnline() {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if(!user.getuId().equals(Utils.uid()) && user.getStatus().equals("online")){
                        userList.add(user);
                        if(userList.size() == 0){
                            rec_UserOnline.setVisibility(View.GONE);
                        }else {
                            rec_UserOnline.setVisibility(View.VISIBLE);
                            chatOnlineAdapter = new UserChatOnlineAdapter(getActivity() , userList);
                            rec_UserOnline.setAdapter(chatOnlineAdapter);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Load Chats
    private void loadUserChat() {
        chatLists = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(Utils.uid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatLists.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    if(ds.exists()){
                        ChatList list = ds.getValue(ChatList.class);
                        chatLists.add(list);
                        Collections.reverse(chatLists);
                    }
                    chatAdapter = new UserChatAdapter(getActivity() , chatLists);
                    rec_UserChat.setAdapter(chatAdapter);
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}