package com.example.mychats.Fragments;

import android.graphics.Path;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mychats.Adapters.RecentChatUsersAdapter;
import com.example.mychats.ModelClasses.UserModel;
import com.example.mychats.R;
import com.example.mychats.ModelClasses.RecentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.internal.cache.DiskLruCache;

public class MyChats extends Fragment {


    private View view;
    private RecyclerView mRecyclerView;
    private RecentChatUsersAdapter mRecentChatUsersAdapter;
    private List<String> listOfUsersLink;
    private Map<String , Integer> map;
    private String currentUser_Id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_chats, container, false);

        init();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getRecentUsers();
    }

    private void getRecentUsers() {

        currentUser_Id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("RecentUsersChat").child(currentUser_Id).orderByChild("time" ).limitToLast(20) .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                listOfUsersLink.add(0, snapshot.getKey());
                mRecentChatUsersAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull final DataSnapshot snapshot, @Nullable final String previousChildName) {

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



    }

    private void init() {

        mRecyclerView = view.findViewById(R.id.recycler_view_for_my_chat);
        listOfUsersLink = new ArrayList<>();
        mRecentChatUsersAdapter = new RecentChatUsersAdapter(listOfUsersLink, view.getContext());
        mRecyclerView.setAdapter(mRecentChatUsersAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());

        mRecyclerView.setLayoutManager(linearLayoutManager);
        //map = new HashMap<>();

        getRecentUsers();

    }
}