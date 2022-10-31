package com.example.socialmediaapp.Adater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.ChatUserActivity;
import com.example.socialmediaapp.Fillter.SearchUserContact;
import com.example.socialmediaapp.Model.User;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserContactAdapter extends RecyclerView.Adapter<UserContactAdapter.ViewHolder>  implements Filterable {

    private final Context context ;
    public List<User> list  , filterList;
    Utils utils ;
    SearchUserContact userContact ;

    public UserContactAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
        this.filterList = list ;
    }

    @NonNull
    @Override
    public UserContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_contact , parent , false);
        utils = new Utils() ;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserContactAdapter.ViewHolder holder, int position) {
        User user = list.get(position);
        holder.name_UserChatContact.setText(user.getName());
        Picasso.get().load(user.getImg_Profile()).into(holder.img_UserChatContact);

        if(user.getStatus().equals("online")){
            holder.img_Online.setVisibility(View.VISIBLE);
            holder.txt_Off.setVisibility(View.GONE);
        }else {
            holder.img_Online.setVisibility(View.GONE);
            holder.txt_Off.setVisibility(View.VISIBLE);
            holder.txt_Off.setText(utils.calculatorTime(user.getCalculatorTimeOff()));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , ChatUserActivity.class);
                intent.putExtra("uId" , user.getuId());
                intent.putExtra("img_Profile" , user.getImg_Profile());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        if(userContact== null){
            userContact = new SearchUserContact(this , (ArrayList<User>) filterList);
        }
        return userContact ;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView img_UserChatContact ;
        private final ImageView img_Online ;
        private final TextView txt_Off;
        private final TextView name_UserChatContact;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_UserChatContact = itemView.findViewById(R.id.img_UserChatContact);
            img_Online = itemView.findViewById(R.id.img_Online);
            txt_Off = itemView.findViewById(R.id.txt_Off);
            name_UserChatContact = itemView.findViewById(R.id.name_UserChatContact);

        }
    }
}
