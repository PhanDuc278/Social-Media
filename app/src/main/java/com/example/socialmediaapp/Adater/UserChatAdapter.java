package com.example.socialmediaapp.Adater;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.ChatUserActivity;
import com.example.socialmediaapp.Model.ChatList;
import com.example.socialmediaapp.Model.User;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder>  {
    final Context context ;
    List<ChatList>  list ;

    private Utils utils ;

    public UserChatAdapter(Context context, List<ChatList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UserChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_chat , parent , false);
        utils = new Utils();
        return new ViewHolder(view);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull UserChatAdapter.ViewHolder holder, int position) {
        ChatList model = list.get(position);
        holder.last_Chat.setText(model.getLastChat());

        holder.time_LastChat.setText(Utils.getTimeAgo(model.timeLast));

        if(model.getIsSeen().equals("true") || model.getIsSeen().equals("")){
            holder.check_seen.setVisibility(View.GONE);
        }else {
            holder.check_seen.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , ChatUserActivity.class);
                intent.putExtra("uId" ,model.getId());
                context.startActivity(intent);
            }
        });

        loadUserInfor(model.getId() , holder.name_UserChat , holder.img_Online1, holder.img_UserChat1 , holder.txt_Off);

    }

    private void loadUserInfor(String id, TextView name_userChat,
                               ImageView img_online,
                               CircleImageView img_userChat1,
                               TextView txt_off) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                name_userChat.setText(user.getName());

                if(user.getStatus().equals("online")){
                    img_online.setVisibility(View.VISIBLE);
                    txt_off.setVisibility(View.GONE);
                }else {
                    img_online.setVisibility(View.GONE);
                    txt_off.setVisibility(View.VISIBLE);
                    txt_off.setText(utils.calculatorTime(user.getCalculatorTimeOff()));
                }

                Picasso.get().load(user.getImg_Profile()).into(img_userChat1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView img_UserChat1 ;
        private final ImageView img_Online1 , check_seen;
        private final TextView name_UserChat , last_Chat , time_LastChat , txt_Off;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_UserChat1 = itemView.findViewById(R.id.img_UserChat1);
            img_Online1 = itemView.findViewById(R.id.img_Online1);
            check_seen = itemView.findViewById(R.id.check_seen);
            name_UserChat = itemView.findViewById(R.id.name_UserChat);
            last_Chat = itemView.findViewById(R.id.last_Chat);
            time_LastChat = itemView.findViewById(R.id.time_LastChat);
            txt_Off = itemView.findViewById(R.id.txt_Off);
        }
    }
}
