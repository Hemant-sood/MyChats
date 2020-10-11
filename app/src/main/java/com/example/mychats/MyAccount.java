package com.example.mychats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccount extends AppCompatActivity {


    private Toolbar toolbar;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private TextView currName, currStatus;
    private EditText name, status;
    private Button saveChanges;
    private ImageView saveImage, saveName, saveStatus;
    private CircleImageView myImage;
    private Uri imageUri = null;

    private StorageReference mStorageRef;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        init();

        updateUi();

        saveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = name.getText().toString().trim();
                if( ! TextUtils.isEmpty(newName) ) {
                    mRef.child("Name").setValue(newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                name.setText("");
                                Toast.makeText(getApplicationContext(), "Update Success", Toast.LENGTH_LONG).show();
                                updateUi();
                            }
                            else{
                                task.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Can't be empty", Toast.LENGTH_LONG).show();
                }
            }
        });


        saveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newStatus = status.getText().toString().trim();
                if( ! TextUtils.isEmpty(newStatus) ) {
                    mRef.child("Status").setValue(newStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                status.setText("");
                                Toast.makeText(getApplicationContext(), "Update Success", Toast.LENGTH_LONG).show();
                                updateUi();
                            }
                            else{
                                task.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Can't be empty", Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    private void updateUi() {

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currName.setText( snapshot.child("Name").getValue().toString() );
                currStatus.setText( snapshot.child("Status").getValue().toString() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

        name = findViewById(R.id.yourName);
        status = findViewById(R.id.yourStatus);
        saveImage = findViewById(R.id.saveImageId);
        saveName = findViewById(R.id.saveNameID);
        saveStatus = findViewById(R.id.saveStatusId);
        currName = findViewById(R.id.currentName);
        currStatus = findViewById(R.id.currentStatus);

        mProgressDialog  = new ProgressDialog(this);

        toolbar = findViewById(R.id.toolbar_my_account);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Users").child(uid);

        mStorageRef = FirebaseStorage.getInstance().getReference().child("UsersProfile").child(uid);

    }


    private void makeCustomProgressDialog(String title) {
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage("One moment please");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

    }

}