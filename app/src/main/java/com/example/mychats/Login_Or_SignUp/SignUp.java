package com.example.mychats.Login_Or_SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mychats.MainActivity;
import com.example.mychats.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.jar.Attributes;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private EditText signupPassword, signupEmail, signupName;
    private Button  signUpButton;
    private String email, password, name;
    private ProgressDialog mProgressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();


        init(); // For initialization

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromEditText();  // get data from input fields
                if( !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(name) ) {

                    mProgressDialog.setTitle("Sign Up progress..");
                    mProgressDialog.setMessage("One moment please...");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    signUp(view);

//                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(main);
//                    finish();
                }
                else{
                    Snackbar.make(view, "All fields are mandatory", Snackbar.LENGTH_LONG).show();
                }
            }
        });



    }

    private void getDataFromEditText() {
        email = signupEmail.getText().toString();
        password = signupPassword.getText().toString();
        name = signupName.getText().toString();
    }


    public void init(){
        signupEmail = findViewById(R.id.signup_email_id);
        signupPassword = findViewById(R.id.signup_password);
        signupName = findViewById(R.id.signup_name);
        signUpButton = findViewById(R.id.signup_button);

        mProgressDialog = new ProgressDialog(this);

        toolbar = findViewById(R.id.toolbar_signup);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    public void signUp(final View view){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    createUserInRealTimeDB();
                }
                else{
                    mProgressDialog.hide();
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });
    }

    private void createUserInRealTimeDB() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();

        Log.d("User id ", uid);

        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        HashMap<String, String> map= new HashMap<>();

        map.put("Name", name);
        map.put("Status", "");
        map.put("ProfileLink", "");

        mRef.setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Account created Success",Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(loginActivity);
                            finish();
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
}