package com.example.mychats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccount extends AppCompatActivity {


    private Toolbar toolbar;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private EditText name, status;
    private Button saveChanges;
    private CircleImageView myImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        init();
        getUserDetails();   // For fetching user Details (name, status, image)

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDetails();
            }
        });



    }

    private void saveDetails() {

        String newName = name.getText().toString();
        String newStatus = status.getText().toString();

        HashMap<String, String> map = new HashMap<>();
        map.put("Name", newName);
        map.put("Status", newStatus);

        mRef.setValue(map)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Changes updated...", Toast.LENGTH_SHORT).show();
                }
            });

        getUserDetails();

    }

    private void getUserDetails() {

        mRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                name.setText(snapshot.child("Name").getValue().toString());
                status.setText(snapshot.child("Status").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.toException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void init() {

        name = findViewById(R.id.profle_name);
        myImage = findViewById(R.id.profile_pic);
        status = findViewById(R.id.profile_status);
        saveChanges = findViewById(R.id.profile_save_button);

        toolbar = findViewById(R.id.toolbar_my_account);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser =  mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Users/"+uid);

    }
}