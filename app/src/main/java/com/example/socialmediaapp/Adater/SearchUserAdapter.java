package com.example.socialmediaapp.Adater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.Fillter.SearchUser;
import com.example.socialmediaapp.Model.User;
import com.example.socialmediaapp.ProfileUserActivity;
import com.example.socialmediaapp.R;
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

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> implements Filterable {
    private Context context;
    public ArrayList<User> arrayList , filterList;
    private SearchUser searchUser ;

    public SearchUserAdapter(Context context, ArrayList<User> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.filterList = arrayList ;
    }

    final int max = 10;

    private FirebaseAuth auth ;
    FirebaseUser muser ;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        auth = FirebaseAuth.getInstance();
        muser = auth.getCurrentUser();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_search , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user =arrayList.get(position);

        holder.search_follow.setVisibility(View.VISIBLE);

        holder.name_UserSearch.setText("" + user.getName());

        try {
            Picasso.get().load(user.getImg_Profile()).into(holder.img_ProfileUserSearch);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (user.getuId().equals(auth.getCurrentUser().getUid())){
            holder.search_follow.setVisibility(View.GONE);
        }

        following(""+user.getuId() , holder.search_follow);





//Click item view to view detail profile User
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("SHARE" , Context.MODE_PRIVATE).edit();
                editor.putString("uId" , user.getuId());
                editor.apply();

                context.startActivity(new Intent(context , ProfileUserActivity.class));
            }
        });

        //BTN Follow

        holder.search_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.search_follow.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference("Follow")
                            .child(muser.getUid())
                            .child("following")
                            .child(user.getuId())
                            .setValue(true);


                    FirebaseDatabase.getInstance().getReference("Follow")
                            .child(user.getuId())
                            .child("follower")
                            .child(muser.getUid())
                            .setValue(true);
                    addNotification(user.getuId());

                }else if(holder.search_follow.getText().toString().equals("Following")){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow")
                            .child(muser.getUid())
                            .child("following")
                            .child(user.getuId())
                            .removeValue();

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow")
                            .child(user.getuId())
                            .child("follower")
                            .child(muser.getUid())
                            .removeValue();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(arrayList.size()>max){
            return max ;
        }else {
            return arrayList.size();
        }
    }

    @Override
    public Filter getFilter() {
        if (searchUser==null){
            searchUser = new SearchUser(this, filterList);
        }
        return searchUser ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView img_ProfileUserSearch ;
        private TextView name_UserSearch ;
        private TextView search_follow ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_ProfileUserSearch = itemView.findViewById(R.id.img_ProfileUserSearch);
            name_UserSearch = itemView.findViewById(R.id.name_UserSearch);
            search_follow = itemView.findViewById(R.id.search_follow);
        }
    }

    private void following(final String uId , TextView btn_Follow){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(muser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(uId).exists()){
                        btn_Follow.setText("Following");
                    }else {
                        btn_Follow.setText("Follow");
                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Add notification

    private void addNotification(String uId ){
        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notification").child(uId);
        Date date = new Date();

        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
        final  String strDate = format.format(date);
        String timeStamp = ""+System.currentTimeMillis();

        HashMap<String , Object> map = new HashMap<>();
        map.put("noId" , timeStamp);
        map.put("uId" ,user.getUid());
        map.put("id" , "");
        map.put("text" , "Đã theo dõi bạn!");
        map.put("isPost" ,"false");
        map.put("time" , strDate);

        reference.child(timeStamp).setValue(map);
    }


}
