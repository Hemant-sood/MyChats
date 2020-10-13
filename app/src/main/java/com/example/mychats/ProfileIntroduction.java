package com.example.mychats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileIntroduction extends AppCompatActivity {


    private Toolbar toolbar;
    private DatabaseReference mRef;
    private TextView userName, userStatus;
    private CircleImageView userImage;
    private String userProfile_ID;
    private Button requestButton;
    private TextView isReqSent;
    private DatabaseReference mFriendRequestDatabaseReference;
    private String currentUser_ID;
    private String sentPath = "Sent" , receivedPath = "Received" ;
    private boolean  isFriend =  false ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_introduction);

        init();

        getUserDetailsFromRealTimeDatabase();

        requestButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {

                // If current user is already a friend of UserProfile then setButton text respectively
                if ( isFriend) {
                    cancelRequest();
                }
                else {
                    sendRequest();
                }
                isFriend = !isFriend;
                checkIFUserIsAlreadyAFriend();

            }
        });



    }

    private void checkIFUserIsAlreadyAFriend() {


        mFriendRequestDatabaseReference.child(sentPath).child(currentUser_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if( snapshot.hasChild(userProfile_ID) ) {
                    isFriend = true;
                    Log.d("Found", "Fond");
                    requestButton.setText("Cancel Request");
                    isReqSent.setText("Request sent successfully");
                }
                else {
                    requestButton.setText("Send Request");
                    isReqSent.setText("To make Friend Send a Request");
                }
                requestButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void cancelRequest() {

        mFriendRequestDatabaseReference.child(sentPath).child(currentUser_ID).child(userProfile_ID) .removeValue();

        mFriendRequestDatabaseReference.child(receivedPath).child(userProfile_ID).child(currentUser_ID) .removeValue();

    }

    private void sendRequest() {

        mFriendRequestDatabaseReference.child(sentPath).child(currentUser_ID).child(userProfile_ID).setValue("");

        mFriendRequestDatabaseReference.child(receivedPath).child(userProfile_ID).child(currentUser_ID).setValue("");


    }

    private void getUserDetailsFromRealTimeDatabase() {

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName.setText(snapshot.child("Name").getValue().toString());
                userStatus.setText(snapshot.child("Status").getValue().toString());

                String url = snapshot.child("ProfileLink").getValue().toString();
                if( ! TextUtils.isEmpty( url ) )
                    Picasso.get().load( url ).into(userImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void init() {

        //For toolbar
        toolbar = findViewById(R.id.toolbar_profile_introduction);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Linking
        userImage = findViewById(R.id.selectedUserImage);
        userName = findViewById(R.id.selectedUserName);
        userStatus = findViewById(R.id.selectedUserStatus);
        requestButton = findViewById(R.id.sendFriendRequest);
        requestButton.setVisibility(View.INVISIBLE);
        isReqSent = findViewById(R.id.isRequestSent);



        //get user_ID Data from Intent
        userProfile_ID = getIntent().getStringExtra("user_ID");

        //To get the current user id who is logged in
        currentUser_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // get Reference of database reference
        mRef = FirebaseDatabase.getInstance().getReference("Users").child(userProfile_ID);
        mFriendRequestDatabaseReference = FirebaseDatabase.getInstance().getReference("FriendRequest");



    }


    @Override
    protected void onStart() {
        super.onStart();
        checkIFUserIsAlreadyAFriend();
    }


}