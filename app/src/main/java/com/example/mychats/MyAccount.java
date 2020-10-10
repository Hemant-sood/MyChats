package com.example.mychats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
    private Uri imageUri = null;
    private String downloadedUrl = "";
    private ImageView cameraImg;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        init();
        getUserDetails();   // For fetching user Details (name, status, image)

        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDetails(); imageUri = null;
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                myImage.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void init() {

        name = findViewById(R.id.profle_name);
        myImage = findViewById(R.id.profile_pic);
        status = findViewById(R.id.profile_status);
        saveChanges = findViewById(R.id.profile_save_button);
        cameraImg = findViewById(R.id.camrea_icon);

        toolbar = findViewById(R.id.toolbar_my_account);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser =  mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Users").child(uid);

        mStorageRef = FirebaseStorage.getInstance().getReference().child("UsersProfile").child(uid);

    }



    private void getUserDetails() {

        mRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                name.setText(snapshot.child("Name").getValue().toString());
                status.setText(snapshot.child("Status").getValue().toString());

                if( snapshot.child("ProfileLink").exists()) {
                    downloadedUrl = snapshot.child("ProfileLink").getValue().toString();
                    Picasso.get().load(downloadedUrl).into(myImage);
                }
                else
                    Picasso.get().load(R.drawable.profile_pic_default).into(myImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.toException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getImage() {

        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }



    private void saveDetails() {

        String newName = name.getText().toString().trim();
        String newStatus = status.getText().toString().trim();

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


        if( imageUri != null) {
            mStorageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                     @Override
                     public void onSuccess(Uri uri) {
                         mRef.child("ProfileLink").setValue(uri.toString());
                     }
                 });
                }
            });
        }

        getUserDetails();

    }



}