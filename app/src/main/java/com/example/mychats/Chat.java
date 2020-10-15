package com.example.mychats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseReference mUserWhomToChatDatabaseRef;
    private CircleImageView userWhomToChatImage;
    private TextView userWhomToChatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();

        updateAppBarDetails();



    }

    private void updateAppBarDetails() {
        String userWhoChat = getIntent().getStringExtra("UserProfileLink");

        mUserWhomToChatDatabaseRef.child(userWhoChat).addListenerForSingleValueEvent(new ValueEventListener() {
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


        mUserWhomToChatDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
    }
}