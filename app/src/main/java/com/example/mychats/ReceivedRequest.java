package com.example.mychats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ReceivedRequest extends AppCompatActivity {

    private Toolbar toolbar;
    private String currentUser_ID;
    private String receivedPath = "Received";
    private DatabaseReference mReceivedPathDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_request);
        init();
        findALlReceivedRequest();


    }

    private void findALlReceivedRequest() {
        mReceivedPathDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for( DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d("All", currentUser_ID);
                    String s = (String) dataSnapshot.getKey();

                    Log.d("All Req", s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {

        //For toolbar
        toolbar = findViewById(R.id.toolbar_received_request);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get Current logged in user
        currentUser_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mReceivedPathDatabaseReference = FirebaseDatabase.getInstance().getReference("FriendRequest").child(receivedPath).child(currentUser_ID);


    }


}