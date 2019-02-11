package com.example.robby.basicfirebaseapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    private DatabaseReference mDatabase;
    private Button btnFind;
    private Button btnFollowers;
    private Button btnFollowing;
    private TextView nameTextView;
    private TextView emailTextView;

    private List<AuthUI.IdpConfig> providers;
    private static final int RC_SIGN_IN = 69;
    private static final int RC_EDIT = 70;


    private FirebaseUser authUser;
    private String authUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFind = findViewById(R.id.btnFind);
        btnFollowers = findViewById(R.id.btnFollowers);
        btnFollowing = findViewById(R.id.btnFollowing);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        if(authUser != null){
            // authUser signed in
            authUid = authUser.getUid();
            // set up UI elements
            nameTextView.setText(authUser.getDisplayName());
            emailTextView.setText(authUser.getEmail());
        } else {
            // set up firebase auth
            providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build()
            );

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }

        // retrieves an instance of FirebaseDatabase and references the location to write to
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // followers
        btnFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FollowerActivity.class);
                startActivity(intent);
            }
        });

        // following
        btnFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FollowingActivity.class);
                startActivity(intent);
            }
        });

        // find people
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FindActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // handle firebase login
        if(requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK){
                // sign in succeeded
                authUser = FirebaseAuth.getInstance().getCurrentUser();
                // add user to database
                User user = new User(authUser.getDisplayName(), authUser.getEmail());
                mDatabase.child("users").child(authUser.getUid()).setValue(user);
                // update UI elements
                nameTextView.setText(authUser.getDisplayName());
                emailTextView.setText(authUser.getEmail());
            } else {
                // sign in failed
                // TODO handle failed sign in
            }
        } else if(requestCode == RC_EDIT){
            // update UI
            nameTextView.setText(authUser.getDisplayName());
            emailTextView.setText(authUser.getEmail());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                signOut();
                return true;
            case R.id.edit_profile:
                // launch edit activity
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(intent,
                        RC_EDIT);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // sign out
                        finishAffinity();
                    }
                });
    }




}
