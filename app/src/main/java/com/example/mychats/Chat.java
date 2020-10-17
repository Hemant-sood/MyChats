package com.example.mychats;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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


        Log.d("Id", MakeNodeIDForMessages.setOneToOneChat("abc", "zbd"));

        init();

        updateAppBarDetails();

        sendDataButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    private void sendMessage() {
        String message = sendDataText.getText().toString();
        sendDataText.setText("");
        if(  TextUtils.isEmpty(message) ) return;

        sendRingTone = MediaPlayer.create(getApplicationContext(), R.raw.notification);
        sendRingTone.start();

        //Establishing the nodeId for one to one chat
        nodeIdForMessage = MakeNodeIDForMessages.setOneToOneChat(from_User_ID, to_User_Id);

        //Establishing connection the nodeId
        mNodeIdForMessageReference = FirebaseDatabase.getInstance().getReference("Chats").child(nodeIdForMessage).push();



        HashMap messageData = new HashMap();

        messageData.put("messageText", message);
        messageData.put("SenderID", from_User_ID);
        messageData.put("ReceiverId", to_User_Id);

        long millis = new Date().getTime();
        messageData.put("Timestamp", millis);



        mNodeIdForMessageReference.setValue(messageData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sendRingTone.release();
                sendRingTone = null;
            }
        });


    }

    private void updateAppBarDetails() {

        to_User_Id = getIntent().getStringExtra("UserProfileLink");

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

        from_User_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();    // getting current logged in user OR we can say user who send tbe message

        mUserWhomToChatDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");



    }
}