package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.socialmediaapp.Fragments.ChatFragment;
import com.example.socialmediaapp.Fragments.HomeFragment;
import com.example.socialmediaapp.Fragments.NotificationFragment;
import com.example.socialmediaapp.Fragments.ProfileFragment;
import com.example.socialmediaapp.Fragments.SearchFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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


public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private Fragment fragment = null;
    FirebaseUser user ;
    private BadgeDrawable badgeDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = FirebaseAuth.getInstance().getCurrentUser();

        bottomNav = findViewById(R.id.bottomNav);

        FragmentTransaction main = getSupportFragmentManager().beginTransaction();
        main.replace(R.id.container, new HomeFragment());
        main.commit();
        countNotification();


    bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            badgeDrawable = bottomNav.getBadge(item.getItemId());
            if(badgeDrawable!=null){
                badgeDrawable.clearNumber();
                badgeDrawable.setVisible(false);
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()){
                case R.id.home:
                   transaction.replace(R.id.container , new HomeFragment());
                    break;
                case R.id.search:
                    transaction.replace(R.id.container , new SearchFragment());
                    break;
                case R.id.add:
                    startActivity(new Intent(MainActivity.this , PostActivity.class));
                    finish();
                    break;
                case R.id.notification:
                    transaction.replace(R.id.container , new NotificationFragment());
                    break;
                case R.id.profile:
                    transaction.replace(R.id.container , new ProfileFragment());
                    break;
            }
            transaction.commit();
            return true;
        }
    });

    }


    private void countNotification(){


        badgeDrawable = bottomNav.getOrCreateBadge(R.id.notification);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notification").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                badgeDrawable.setNumber((int) snapshot.getChildrenCount());
                if(snapshot.getChildrenCount()==0){
                    badgeDrawable.setVisible(false);
                }else {
                    badgeDrawable.setVisible(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    private void status(String status){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
//        HashMap<String , Object> map = new HashMap<>();
//        map.put("status" , status);
//        reference.updateChildren(map);
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        status("online");
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
//        HashMap<String , Object> map = new HashMap<>();
//        map.put("calculatorTimeOff" , "");
//        reference.updateChildren(map);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        status("offline");
//        Date date = new Date();
//
//        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
//        final  String strDate = format.format(date);
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
//        HashMap<String , Object> map = new HashMap<>();
//        map.put("calculatorTimeOff" , strDate);
//        reference.updateChildren(map);
//    }
}