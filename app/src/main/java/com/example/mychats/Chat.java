package com.example.mychats;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();

        updateAppBarDetails();

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
                    getAllMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void getAllMessages() {

        Query query = FirebaseDatabase.getInstance().getReference("Chats").child(nodeIdForMessage);
        FirebaseRecyclerOptions<ChatModel> options = new FirebaseRecyclerOptions.Builder<ChatModel>()
                .setQuery(query, ChatModel.class)
                .build();

        FirebaseRecyclerAdapter<ChatModel, Chat.Holder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatModel, Holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final Holder holder, final int i, @NonNull final ChatModel chatModel) {



                getRef(i).child("SenderID").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String senderID =  snapshot.getValue().toString() ;


                        if( senderID.equals(from_User_ID) ) {
                            holder.textView.setBackgroundColor(Color.WHITE);
                            holder.textView.setTextColor(Color.BLACK);


                        }
                        else{
                            holder.textView.setBackgroundColor(Color.BLACK);
                            holder.textView.setTextColor(Color.WHITE);
                        }

                        holder.setText(chatModel.getMessageText());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }

            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_for_recyclerview_message_item, parent, false);
                return new Holder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }


    static class Holder extends RecyclerView.ViewHolder {

        View mView;
        private TextView textView ;

        public void setText(String name) {
            textView.setText(name);
        }

        public Holder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            textView = mView.findViewById(R.id.messageId);
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
                sendRingTone.release();
                sendRingTone = null;
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

        //For RecyclerView
        recyclerView = findViewById(R.id.recyclerview_for_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

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

    }

}