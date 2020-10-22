package com.example.mychats.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychats.ModelClasses.ChatModel;
import com.example.mychats.R;

import java.util.Date;
import java.util.List;

public class ChatSendAndReceiverAdapter extends RecyclerView.Adapter<ChatSendAndReceiverAdapter.Holder> {

    Context context;
    List<ChatModel> list;
    String currentUser_ID;


    public ChatSendAndReceiverAdapter(Context context, List<ChatModel> list, String currentUser_ID) {
        this.context = context;
        this.list = list;
        this.currentUser_ID = currentUser_ID;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_for_recyclerview_message_item, parent, false);
        return new Holder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        ChatModel chatModel = list.get(position);

        String sender_ID = chatModel.getSenderId();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);



        if( sender_ID.equals(currentUser_ID) ) {

            params.gravity = Gravity.END;
            params.setMarginEnd(40);

            holder.linearLayoutForMessageItem.setLayoutParams(params);
            holder.linearLayoutForBackgroundColor.setBackgroundColor(Color.WHITE);


            holder.timestamp.setTextColor(Color.BLACK);
            holder.message.setTextColor(Color.BLACK);

        }
        else {

            params.gravity = Gravity.START;
            params.setMarginStart(40);

            holder.linearLayoutForMessageItem.setLayoutParams(params);
            holder.linearLayoutForBackgroundColor.setBackgroundColor(Color.BLACK);


            holder.timestamp.setTextColor(Color.WHITE);
            holder.message.setTextColor(Color.WHITE);


        }

        holder.setText(chatModel.getMessageText());
        holder.setTime(chatModel.getTimestamp());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class Holder extends RecyclerView.ViewHolder {

        private TextView message, timestamp ;
        private LinearLayout linearLayoutForMessageItem;
        private LinearLayout linearLayoutForBackgroundColor;

        public void setText(String name) {
            message.setText(name);
        }

        public void setTime(long time) {
            Date date = new Date(time);
            String s = ""+date.getHours() +":"+date.getMinutes();
            timestamp.setText(s);
        }

        public Holder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageId);
            timestamp = itemView.findViewById(R.id.timestamp);
            linearLayoutForBackgroundColor = itemView.findViewById(R.id.backgroundForSenderAndReceiver);
            linearLayoutForMessageItem = itemView.findViewById(R.id.linearLayoutForMessageItem);

        }
    }



}
