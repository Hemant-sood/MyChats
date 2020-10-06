
package com.example.mychats.Login_Or_SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mychats.MainActivity;
import com.example.mychats.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText loginPassword, loginEmail;
    private Button loginButton, signUpButton;
    private String email, password;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init(); // For initialization

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromEditText();  // get data from input fields
                if( !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) ) {
                    mProgressDialog.setTitle("Login progress..");
                    mProgressDialog.setMessage("One moment please...");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    login(view);
                }
                else{
                    Snackbar.make(view, "Both fields are mandatory", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(signUpIntent);

            }
        });

    }

    public void getDataFromEditText() {
        email = loginEmail.getText().toString();
        password = loginPassword.getText().toString();
    }

    public void init(){

        mAuth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.login_email_id);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button1);
        signUpButton = findViewById(R.id.signup_button1);

        mProgressDialog = new ProgressDialog(this);

    }

    public void login(final View view){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login Success",Toast.LENGTH_LONG).show();
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(main);
                    finish();
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

}