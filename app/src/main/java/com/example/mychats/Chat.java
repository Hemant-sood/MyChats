package com.example.mychats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychats.Adapters.ChatSendAndReceiverAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseReference mUserWhomToChatDatabaseRef, mNodeIdForMessageReference;
    private CircleImageView userWhomToChatImage;
    private TextView userWhomToChatName;
    private EditText sendDataText;
    private ImageView sendDataButton;
    private String nodeIdForMessage, from_User_ID, to_User_Id ;
    private MediaPlayer sendRingTone;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<ChatModel, Chat.Holder> firebaseRecyclerAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ChatSendAndReceiverAdapter mChatSendAndReceiverAdapter;
    List<ChatModel> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();

        updateAppBarDetails();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMessages();
            }
        });

        sendDataButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseDatabase.getInstance().getReference("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if( snapshot.hasChild(nodeIdForMessage) ) {
                    loadMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void loadMessages() {


        FirebaseDatabase.getInstance().getReference("Chats").child(nodeIdForMessage).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatModel chatModel = snapshot.getValue(ChatModel.class);
                list.add(chatModel);
                mChatSendAndReceiverAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(list.size()-1);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        mSwipeRefreshLayout.setRefreshing(false);

    }


    static class Holder extends RecyclerView.ViewHolder {

        View mView;
        private TextView message, timestamp ;
        private CardView cardView;

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
            mView = itemView;
            message = mView.findViewById(R.id.messageId);
            timestamp = mView.findViewById(R.id.timestamp);
            cardView = mView.findViewById(R.id.cardViewForMessageItem);

        }

    }



    private void sendMessage() {

        String message = sendDataText.getText().toString();
        sendDataText.setText("");

        if(  TextUtils.isEmpty(message) ) return;

        sendRingTone = MediaPlayer.create(getApplicationContext(), R.raw.notification);
        sendRingTone.start();


        HashMap messageData = new HashMap();

        messageData.put("messageText", message);
        messageData.put("SenderID", from_User_ID);
        messageData.put("ReceiverId", to_User_Id);

        long millis = new Date().getTime();
        messageData.put("Timestamp", millis);



        mNodeIdForMessageReference.push().setValue(messageData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(sendRingTone != null) {
                    sendRingTone.release();
                    sendRingTone = null;
                }

                mChatSendAndReceiverAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(list.size()-1);

            }
        });


    }



    private void updateAppBarDetails() {

        mUserWhomToChatDatabaseRef.child(to_User_Id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);

                userWhomToChatName.setText(userModel.getName());

                if( !TextUtils.isEmpty( userModel.getProfileLink()) )
                    Picasso.get().load(userModel.getProfileLink()).into(userWhomToChatImage);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }




    private void init() {

        toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userWhomToChatImage = findViewById(R.id.userWhomToChatImage);
        userWhomToChatName = findViewById(R.id.userWhomToChatName);

        sendDataText = findViewById(R.id.sendTextData);
        sendDataButton = findViewById(R.id.sendImageButton);

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);




        //Get the userId to whom we want to send the messages using previous activity through Intent
        to_User_Id = getIntent().getStringExtra("UserProfileLink");

        // getting current logged in user OR we can say user who send tbe message
        from_User_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Set Reference to whom we want to chat
        mUserWhomToChatDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");

        //Establishing the nodeId for one to one chat
        nodeIdForMessage = MakeNodeIDForMessages.setOneToOneChat(from_User_ID, to_User_Id);

        //Establishing connection the nodeId
        mNodeIdForMessageReference = FirebaseDatabase.getInstance().getReference("Chats").child(nodeIdForMessage);


        //For RecyclerView
        recyclerView = findViewById(R.id.recyclerview_for_chat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        mChatSendAndReceiverAdapter = new ChatSendAndReceiverAdapter(getApplicationContext(), list, from_User_ID);
        recyclerView.setAdapter(mChatSendAndReceiverAdapter);






    }

}