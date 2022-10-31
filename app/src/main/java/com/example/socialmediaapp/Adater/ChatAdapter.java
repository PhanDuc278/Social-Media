package com.example.socialmediaapp.Adater;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.Model.Chat;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static final int CHAT_LEFT = 0 ;
    public static final int CHAT_RIGHT = 1 ;
    FirebaseUser user ;

    private final Context context ;
    private final List<Chat> list ;
    private final String img_Profile ;
    Dialog dialog;


    public ChatAdapter(Context context, List<Chat> list, String img_Profile) {
        this.context = context;
        this.list = list;
        this.img_Profile = img_Profile;
    }


    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(viewType == CHAT_RIGHT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right , parent ,false);
            return new ChatAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent ,false);
            return new ChatAdapter.ViewHolder(view);
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        Chat chat  = list.get(position);
        holder.message.setText(chat.getMessage());
        if(img_Profile.equals("default")){
            holder.img_RecipientChat.setImageResource(R.drawable.tao1);
        }else {
            Picasso.get().load(img_Profile).into(holder.img_RecipientChat);
        }

        //Check if last message seen or not seen
        if(position == list.size()-1){
            if(chat.getIsseen().equals("true")){
                holder.txt_Seen.setText("Đã xem");
            }else {
                holder.txt_Seen.setText("Đã gửi");
            }
        }else {
            holder.txt_Seen.setVisibility(View.GONE);
        }

        holder.time_Send.setText(Utils.getTimeAgo(chat.getTimeSend()));
        holder.time_Send.setVisibility(View.GONE);
        switch (chat.getEmoji()) {
            case "":
                holder.img_Emoji.setVisibility(View.GONE);
                break;
            case "like":
                holder.img_Emoji.setVisibility(View.VISIBLE);
                holder.img_Emoji.setImageResource(R.drawable.like);
                break;
            case "love":
                holder.img_Emoji.setVisibility(View.VISIBLE);
                holder.img_Emoji.setImageResource(R.drawable.heart1);
                break;
            case "haha":
                holder.img_Emoji.setVisibility(View.VISIBLE);
                holder.img_Emoji.setImageResource(R.drawable.haha);
                break;
            case "sad":
                holder.img_Emoji.setVisibility(View.VISIBLE);
                holder.img_Emoji.setImageResource(R.drawable.sad);
                break;
            case "care":
                holder.img_Emoji.setVisibility(View.VISIBLE);
                holder.img_Emoji.setImageResource(R.drawable.care1);
                break;
            default:
                holder.img_Emoji.setVisibility(View.VISIBLE);
                holder.img_Emoji.setImageResource(R.drawable.angry1);
                break;
        }

        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.time_Send.getVisibility() == View.GONE){
                    holder.time_Send.setVisibility(View.VISIBLE);
                }else {
                    holder.time_Send.setVisibility(View.GONE);
                }

            }
        });

        //Check if it's a sender's message, show dialog
        if(user.getUid().equals(chat.getRecipient())){
            holder.message.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showDialog(chat.getId() , chat);
                    return false;
                }
            });
        }

        //Thu hoi message
        if (user.getUid().equals(chat.getSenderId())){
            holder.message.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage("Xóa");
                    alertDialogBuilder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(chat.getId());
                                    reference.removeValue();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return false;
                }
            });
        }

    }

    private void showDialog(String id, Chat chat) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_emoji);
        ImageView like , love, haha ,angry ,care ,sad ;
        TextView delete_Emoji ;
        like = dialog.findViewById(R.id.like);
        love = dialog.findViewById(R.id.love);
        haha = dialog.findViewById(R.id.haha);
        angry = dialog.findViewById(R.id.angry);
        care = dialog.findViewById(R.id.care);
        sad = dialog.findViewById(R.id.sad);
        delete_Emoji = dialog.findViewById(R.id.delete_Emoji);


        delete_Emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(id);
                HashMap<String , Object> map = new HashMap<>();
                map.put("emoji" , "");
                reference.updateChildren(map);
                dialog.dismiss();
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(id);
                HashMap<String , Object> map = new HashMap<>();
                map.put("emoji" , "like");
                reference.updateChildren(map);
                dialog.dismiss();
            }
        });
        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(id);
                HashMap<String , Object> map = new HashMap<>();
                map.put("emoji" , "love");
                reference.updateChildren(map);
                dialog.dismiss();
            }
        });
        haha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(id);
                HashMap<String , Object> map = new HashMap<>();
                map.put("emoji" , "haha");
                reference.updateChildren(map);
                dialog.dismiss();
            }
        });
        angry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(id);
                HashMap<String , Object> map = new HashMap<>();
                map.put("emoji" , "angry");
                reference.updateChildren(map);
                dialog.dismiss();
            }
        });
        care.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(id);
                HashMap<String , Object> map = new HashMap<>();
                map.put("emoji" , "care");
                reference.updateChildren(map);
                dialog.dismiss();
            }
        });
        sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(id);
                HashMap<String , Object> map = new HashMap<>();
                map.put("emoji" , "sad");
                reference.updateChildren(map);
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView img_RecipientChat ;
        private TextView message  , time_Send , txt_Seen;
        private ImageView img_Emoji ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_RecipientChat = itemView.findViewById(R.id.img_RecipientChat);
            message = itemView.findViewById(R.id.message);
            time_Send = itemView.findViewById(R.id.time_Send);
            txt_Seen = itemView.findViewById(R.id.txt_Seen);
            img_Emoji = itemView.findViewById(R.id.img_Emoji);
        }
    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(list.get(position).getSenderId().equals(user.getUid())){
            return CHAT_RIGHT ;
        }else {
            return CHAT_LEFT ;
        }
    }


}
