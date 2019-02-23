package com.example.robby.basicfirebaseapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    private Button btnFind;
    private Button btnFollowers;
    private Button btnFollowing;
    private Button btnPost;
    private TextView nameTextView;
    private TextView emailTextView;
    private ListView postListView;
    private EntryAdapter adapter;

    private List<AuthUI.IdpConfig> providers;
    private DatabaseReference mDatabase;
    private FirebaseUser authUser;
    private String authUid;
    private ArrayList<Map.Entry> postList;

    private static final int RC_SIGN_IN = 69;
    private static final int RC_EDIT = 70;
    private static final int RC_POST = 71;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFind = findViewById(R.id.btnFind);
        btnFollowers = findViewById(R.id.btnFollowers);
        btnFollowing = findViewById(R.id.btnFollowing);
        btnPost = findViewById(R.id.btnPost);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        postListView = findViewById(R.id.mainPostListView);

        // retrieves an instance of FirebaseDatabase and references the location to write to
        mDatabase = FirebaseDatabase.getInstance().getReference();

        postList = new ArrayList<>();
        adapter = new EntryAdapter(MainActivity.this, R.layout.map_list_item, postList, "title");
        postListView.setAdapter(adapter);

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        if(authUser != null){
            // authUser signed in
            authUid = authUser.getUid();
            // set up UI elements
            nameTextView.setText(authUser.getDisplayName());
            emailTextView.setText(authUser.getEmail());
            // get posts
            mDatabase.child("posts").child(authUid).addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            collectPosts((Map<String, Object>) dataSnapshot.getValue());
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "onDataChange: posts");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // handle error
                        }
                    }
            );
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

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
                startActivityForResult(intent, RC_POST);
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
            updateUI();

        } else if(requestCode == RC_POST){
            if(resultCode == RESULT_OK){
                Log.d(TAG, "onActivityResult: rc_post");
            }
        }
    }

    private void collectPosts(Map<String,Object> posts) {
        // TODO: posts out of order?
        if(posts != null) {
            postList.clear();
            for (Map.Entry<String, Object> item : posts.entrySet()) {
                postList.add(item);
            }
        }
    }

    private void updateUI(){
        nameTextView.setText(authUser.getDisplayName());
        emailTextView.setText(authUser.getEmail());
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
                startActivityForResult(intent, RC_EDIT);

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
