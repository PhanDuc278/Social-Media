package com.example.socialmediaapp.Adater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.Model.User;
import com.example.socialmediaapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserChatOnlineAdapter extends RecyclerView.Adapter<UserChatOnlineAdapter.ViewHolder> {
    private Context context ;
    List<User> list ;

    public UserChatOnlineAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UserChatOnlineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_online , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserChatOnlineAdapter.ViewHolder holder, int position) {
        User user = list.get(position);
        holder.txt_NameUserOnline.setText(user.getName());
        holder.img_Online.setVisibility(View.VISIBLE);
        Picasso.get().load(user.getImg_Profile()).into(holder.img_UserOnline);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView img_UserOnline , img_Online;
        private TextView txt_NameUserOnline ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_UserOnline = itemView.findViewById(R.id.img_UserOnline);
            img_Online = itemView.findViewById(R.id.img_Online);
            txt_NameUserOnline = itemView.findViewById(R.id.txt_NameUserOnline);
        }
    }
}
