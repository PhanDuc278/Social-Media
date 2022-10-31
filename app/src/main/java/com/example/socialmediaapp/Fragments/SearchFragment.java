package com.example.socialmediaapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.socialmediaapp.Adater.SearchUserAdapter;
import com.example.socialmediaapp.MainActivity;
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
import java.util.Collections;


public class SearchFragment extends Fragment {
    private ImageView back_SearchView;
    private EditText search_View;
    private ImageView clear_Text ;

    SearchUserAdapter adapter ;
    ArrayList<User> list;
    RecyclerView rec_SearchUser ;
    FirebaseUser user1 ;
    private Toolbar toolbar;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        user1 = FirebaseAuth.getInstance().getCurrentUser();
        back_SearchView = view.findViewById(R.id.back_SearchView);
        clear_Text = view.findViewById(R.id.clear_Text);
        search_View  = view.findViewById(R.id.search_View);
        rec_SearchUser = view.findViewById(R.id.result_SearchView);

        rec_SearchUser.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL , false));

        list = new ArrayList<>();
        adapter = new SearchUserAdapter(getActivity() , list);
        rec_SearchUser.setAdapter(adapter);


        clear_Text.setVisibility(View.GONE);
        loadUser();

        //Search when input text

        search_View.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    clear_Text.setVisibility(View.VISIBLE);
                if (charSequence.length()==0){
                    clear_Text.setVisibility(View.GONE);
                }

                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//Clear text when click img_Close
        clear_Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_View.setText("");
                clear_Text.setVisibility(View.GONE );
            }
        });

//Back main Activity
        back_SearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }

    private void loadUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    User user = ds.getValue(User.class);

                    if(user.getuId().equals(user1.getUid())){
                        list.remove(user);
                    }else {
                        list.add(user);
                        Collections.shuffle(list);
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