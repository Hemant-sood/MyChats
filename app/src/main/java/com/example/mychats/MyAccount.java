package com.example.mychats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class MyAccount extends AppCompatActivity {


    private Toolbar toolbar;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private TextView currName, currStatus;
    private EditText name, status;
    private ImageView saveImage, saveName, saveStatus, removeImage;
    private CircleImageView myImage;
    private Uri imageUri = null;
    private String downloadUri = "";

    private StorageReference mStorageRef;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        init();

        updateStatusAndName();
        updateProfileImage();

        saveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNameIntoDatabase(); // save new Name into Firebase Realtime Database
            }
        });


        saveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveStatusIntoDatabase();  // save new Status into Firebase Realtime Database
            }
        });

        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageFromGalary();
            }
        });


        removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(MyAccount.this)
                        .setTitle("Remove Profile Image")
                        .setMessage("Do you really want to remove Profile Image ?")
                        .setIcon(R.drawable.remove)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                removeImageLinkFromRealTimeDatabase();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });


    }

    private void removeImageLinkFromRealTimeDatabase() {

        mRef.child("ProfileLink").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Profile photo removed....", Toast.LENGTH_SHORT).show();
                Picasso.get().load(R.drawable.profile_pic_default).into(myImage);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }




    private void updateProfileImage() {

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                downloadUri = snapshot.child("ProfileLink").getValue().toString();
                if( ! TextUtils.isEmpty( downloadUri ) ) {
                    Picasso.get().load(downloadUri).placeholder(R.drawable.profile_pic_default).into(myImage) ;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void pickImageFromGalary() {

        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }


    private void saveNameIntoDatabase(){
        String newName = name.getText().toString().trim();
        if( ! TextUtils.isEmpty(newName) ) {
            mRef.child("Name").setValue(newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        name.setText("");
                        Toast.makeText(getApplicationContext(), "Update Success", Toast.LENGTH_LONG).show();
                        updateStatusAndName();
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
            Toast.makeText(getApplicationContext(), "Can't be empty", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveStatusIntoDatabase(){
        String newStatus = status.getText().toString().trim();
        if( ! TextUtils.isEmpty(newStatus) ) {
            mRef.child("Status").setValue(newStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        status.setText("");
                        Toast.makeText(getApplicationContext(), "Update Success", Toast.LENGTH_LONG).show();
                        updateStatusAndName();
                    }
                    else{
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void updateStatusAndName() {

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
//                myImage.setImageURI(imageUri);
                makeCustomProgressDialog("Uploading image");
                try {
                    uploadImageIntoDatabase();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadImageIntoDatabase() throws IOException {

        File file = new File(imageUri.getPath());

        Bitmap bitmap =  Compressor.getDefault(this).compressToBitmap(file);


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        mStorageRef.putBytes(byteArray)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String photoStringLink = uri.toString();
                                mRef.child("ProfileLink").setValue(photoStringLink)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Uploaded Success", Toast.LENGTH_LONG).show();
                                                updateProfileImage();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }
                        });

                    }
                });
    }

    private void init() {

        myImage = findViewById(R.id.profile_pic);
        name = findViewById(R.id.yourName);
        status = findViewById(R.id.yourStatus);
        saveImage = findViewById(R.id.saveImageId);
        saveName = findViewById(R.id.saveNameID);
        saveStatus = findViewById(R.id.saveStatusId);
        currName = findViewById(R.id.currentName);
        currStatus = findViewById(R.id.currentStatus);
        removeImage = findViewById(R.id.removeProfile);

        mProgressDialog = new ProgressDialog(this);

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