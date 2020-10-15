package com.example.mychats.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.text.TextUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychats.ProfileIntroduction;
import com.example.mychats.R;
import com.example.mychats.ReceivedRequest;
import com.example.mychats.ReceivedRequestModel;
import com.example.mychats.UserModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyNetwork extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private String currentUser_ID;
    private DatabaseReference mFriendListReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_my_network, container, false);

         init();

        return  view;
    }

   public void init(){

        recyclerView = view.findViewById(R.id.recyclerView__my_network);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        //Get current logged in user
        currentUser_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();

       //Set reference to Friend to the Fire
       mFriendListReference = FirebaseDatabase.getInstance().getReference("Users");

   }

    @Override
    public void onStart() {

        super.onStart();

        Query query = FirebaseDatabase.getInstance().getReference("Friends").child(currentUser_ID);
        FirebaseRecyclerOptions<ReceivedRequestModel> options = new FirebaseRecyclerOptions.Builder<ReceivedRequestModel>()
                .setQuery(query, ReceivedRequestModel.class)
                .build();


        FirebaseRecyclerAdapter<ReceivedRequestModel,  Holder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ReceivedRequestModel,  Holder>(options) {

            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_for_recyclerview_my_network, parent, false);
                return new Holder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final Holder holder, int i, @NonNull ReceivedRequestModel receivedRequestModel) {

                String link = receivedRequestModel.getName();

                mFriendListReference .child(link) .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        UserModel userModel =  snapshot.getValue(UserModel.class);


                        if( !TextUtils.isEmpty(userModel.getProfileLink()) )
                            holder.setImage(userModel.getProfileLink());

                        holder.setName(userModel.getName());
                        holder.setStatus(userModel.getStatus());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(holder.mView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        };


        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    static class Holder extends RecyclerView.ViewHolder {

        View mView;
        public Button chat;


        public Holder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            chat = mView.findViewById(R.id.friendChatButton);

        }

        public void setName(String name) {
            TextView userName = mView.findViewById(R.id.friendName);
            userName.setText(name);
        }

        public void setStatus(String status) {
            TextView userStatus = mView.findViewById(R.id.friendStatus);
            userStatus.setText(status);
        }

        public void setImage(String profileLink) {
            CircleImageView circleImageView = mView.findViewById(R.id.friendImage);
            Picasso.get().load(profileLink).placeholder(R.drawable.profile_pic_default).into(circleImageView);
        }

    }

}