package com.example.socialmediaapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.socialmediaapp.Adater.UserContactAdapter;
import com.example.socialmediaapp.Model.User;
import com.example.socialmediaapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UserContactFragment extends Fragment {
    private RecyclerView rec_UserChat1 ;

    private UserContactAdapter adapter ;
    private ArrayList<User> list ;
    FirebaseUser fuser;

    public UserContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_chat, container, false);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        rec_UserChat1 = view.findViewById(R.id.rec_UserChat1);
        rec_UserChat1.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rec_UserChat1.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        adapter = new UserContactAdapter(getContext(), list);
        rec_UserChat1.setAdapter(adapter);
        loadUser();
        return view ;
    }



    private void loadUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    User user1 = ds.getValue(User.class);
                    if(!user1.getuId().equals(fuser.getUid())){
                        list.add(user1);
                    }
                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}