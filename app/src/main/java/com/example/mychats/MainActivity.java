package com.example.mychats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mychats.Adapters.FragmentsAndViewPager;
import com.example.mychats.Login_Or_SignUp.LoginActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ViewPager mViewPager;

    private FragmentsAndViewPager mFragmentsAndViewPager;
    private TabLayout mTabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        mTabLayout = findViewById(R.id.tabLayout_main);
        mViewPager = findViewById(R.id.viewPager);
        mFragmentsAndViewPager = new FragmentsAndViewPager(getSupportFragmentManager());
        mViewPager.setAdapter(mFragmentsAndViewPager);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setIcon(R.drawable.chat);
        mTabLayout.getTabAt(1).setIcon(R.drawable.addfriend);


    }





    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if( currentUser == null){

            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu) ;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch ( item.getItemId() ){
            case R.id.about_menu :
                    break;

            case R.id.logout_menu :
                new AlertDialog.Builder(this)
                        .setTitle("Alert")
                        .setMessage("Do you really want to Logout?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                mAuth.signOut();
                                Toast.makeText(getApplicationContext(), "Logout success", Toast.LENGTH_LONG).show();
                                onStart();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();


                    break;
            case R.id.account_menu :  Intent myAccount = new Intent(getApplicationContext(), MyAccount.class);
                                    startActivity(myAccount);
                                    break;
        }

        return true;
    }
}