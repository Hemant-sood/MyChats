package com.example.mychats.Fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.VerifiedInputEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mychats.ProfileIntroduction;
import com.example.mychats.R;
import com.example.mychats.UserModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.Api;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import de.hdodenhof.circleimageview.CircleImageView;


public class Find extends Fragment {



    private View view;
    private RecyclerView recyclerView;

    private DatabaseReference mDatabaseReference;
    private Query query;
    private FirebaseRecyclerAdapter< UserModel, Holder> firebaseRecyclerAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_find, container, false);

        init();

        return view;
    }

    private void init() {

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");


        recyclerView = view.findViewById(R.id.recyclerView__find);


        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


    }


    @Override
    public void onStart() {
        super.onStart();


        Query query = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseRecyclerOptions<UserModel> options = new FirebaseRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();

        FirebaseRecyclerAdapter<UserModel, Holder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserModel, Holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Holder holder, final int i, @NonNull UserModel userModel) {

                if( !TextUtils.isEmpty(userModel.getProfileLink()) )
                    holder.setImage(userModel.getProfileLink());

                holder.setName(userModel.getName());

                holder.setStatus(userModel.getStatus());


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntroduction = new Intent(view.getContext(), ProfileIntroduction.class );
                        String user_ID = getRef(i).getKey();
                        profileIntroduction.putExtra("user_ID", user_ID);
                        startActivity(profileIntroduction);
                    }
                });

            }

            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_for_recyclerview_find, parent, false);
                return new Holder(view);
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
            TextView userName = mView.findViewById(R.id.userName);
            userName.setText(name);
        }

        public void setStatus(String status) {
            TextView userStatus = mView.findViewById(R.id.userStatus);
            userStatus.setText(status);
        }

        public void setImage(String profileLink) {
            CircleImageView circleImageView = mView.findViewById(R.id.userImage);
            Picasso.get().load(profileLink).placeholder(R.drawable.profile_pic_default).into(circleImageView);
        }
    }


}