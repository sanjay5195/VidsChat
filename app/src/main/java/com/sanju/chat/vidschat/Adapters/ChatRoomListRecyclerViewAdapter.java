package com.sanju.chat.vidschat.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sanju.chat.vidschat.Activities.ChatActivity;
import com.sanju.chat.vidschat.Models.Chat;
import com.sanju.chat.vidschat.Models.User;
import com.sanju.chat.vidschat.R;
import com.sanju.chat.vidschat.Utiles.AppController;

import java.util.List;

/**
 * Created by angad on 25/11/16.
 */

public class ChatRoomListRecyclerViewAdapter extends RecyclerView.Adapter<ChatRoomListRecyclerViewAdapter.MyViewHolder>{

    Context context;
    public LayoutInflater inflater;
    public ImageLoader imageLoader;
    public Dialog dialog;
    List<Chat> userList;


    DatabaseReference databaseReference;


    public ChatRoomListRecyclerViewAdapter(Context context, List<Chat> userList){

        this.context = context;
        this.userList = userList;
        this.inflater = LayoutInflater.from(this.context);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        imageLoader = AppController.getInstance().getImageLoader();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txt_view_name;
        TextView txt_view_msg;
        NetworkImageView ntwk_img_view;

        public MyViewHolder(View itemView) {
            super(itemView);

            ntwk_img_view = (NetworkImageView) itemView.findViewById(R.id.ntwk_img_view);
            txt_view_name = (TextView) itemView.findViewById(R.id.txt_view_name);
            txt_view_msg = (TextView) itemView.findViewById(R.id.txt_view_msg);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int pos = getAdapterPosition();

            Chat chat = userList.get(pos);

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("otherUserId", chat.author);
            intent.putExtra("name", chat.name);
            context.startActivity(intent);

            //Toast.makeText(context, chat.name, Toast.LENGTH_LONG ).show();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        if (!userList.get(position).photoUrl.equals("")){
            holder.ntwk_img_view.setImageUrl(userList.get(position).photoUrl, imageLoader);
        }else {
            holder.ntwk_img_view.setDefaultImageResId(R.drawable.default_male);
        }

        holder.txt_view_name.setText(userList.get(position).name);
        holder.txt_view_msg.setText(userList.get(position).message);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }



}
