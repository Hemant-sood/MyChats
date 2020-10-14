package com.example.mychats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.UiAutomation;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychats.Fragments.Find;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReceivedRequest extends AppCompatActivity {

    private Toolbar toolbar;
    private String currentUser_ID;
    private String receivedPath = "Received";
    private DatabaseReference mReceivedPathDatabaseReference;
    private DatabaseReference mUsersReference;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_request);
        init();


    }


    @Override
    protected void onStart() {
        super.onStart();



        Query query = mReceivedPathDatabaseReference;
        FirebaseRecyclerOptions<ReceivedRequestModel> options = new FirebaseRecyclerOptions.Builder<ReceivedRequestModel>()
                .setQuery(query, ReceivedRequestModel.class)
                .build();

        FirebaseRecyclerAdapter<ReceivedRequestModel, ReceivedRequest.Holder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ReceivedRequestModel, ReceivedRequest.Holder>(options) {
            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_for_recyclerview_receivedrequest, parent, false);
                return new ReceivedRequest.Holder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final Holder holder, int i, @NonNull ReceivedRequestModel receivedRequestModel) {
                String link = receivedRequestModel.getName();
                mUsersReference.child(link).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        UserModel userModel =  snapshot.getValue(UserModel.class);

                        Log.d("Name", userModel.getProfileLink());

                        if( !TextUtils.isEmpty(userModel.getProfileLink()) )
                            holder.setImage(userModel.getProfileLink());

                        holder.setName(userModel.getName());
                        holder.setStatus(userModel.getStatus());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        };


        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);




    }


    class Holder extends RecyclerView.ViewHolder {

        View mView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setName(String name) {
            TextView userName = mView.findViewById(R.id.receivedName);
            userName.setText(name);
        }

        public void setStatus(String status) {
            TextView userStatus = mView.findViewById(R.id.receivedStatus);
            userStatus.setText(status);
        }

        public void setImage(String profileLink) {
            CircleImageView circleImageView = mView.findViewById(R.id.receivedImage);
            Picasso.get().load(profileLink).placeholder(R.drawable.profile_pic_default).into(circleImageView);
        }
    }

    private void init() {

        //For toolbar
        toolbar = findViewById(R.id.toolbar_received_request);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerviewForRequest);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //get Current logged in user
        currentUser_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mUsersReference = FirebaseDatabase.getInstance().getReference("Users");

        mReceivedPathDatabaseReference = FirebaseDatabase.getInstance().getReference("FriendRequest").child(receivedPath).child(currentUser_ID);


    }


}