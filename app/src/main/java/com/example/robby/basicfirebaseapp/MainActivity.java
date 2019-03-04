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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.robby.basicfirebaseapp.model.User;
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

    private Button findButton;
    private Button followersButton;
    private Button followingButton;
    private Button postButton;
    private Button newsFeedButton;

    private TextView nameTextView;
    private TextView emailTextView;
    //private ListView postListView;
    private GridView gridView;
    private PostAdapter adapter;

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

        findButton = findViewById(R.id.btnFind);
        followersButton = findViewById(R.id.btnFollowers);
        followingButton = findViewById(R.id.btnFollowing);
        postButton = findViewById(R.id.btnPost);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        //postListView = findViewById(R.id.mainPostListView);
        newsFeedButton = findViewById(R.id.newsFeedButton);
        gridView = findViewById(R.id.postGridView);

        // retrieves an instance of FirebaseDatabase and references the location to write to
        mDatabase = FirebaseDatabase.getInstance().getReference();

        postList = new ArrayList<>();
        adapter = new PostAdapter(MainActivity.this, R.layout.post_list_item, postList);
        //postListView.setAdapter(adapter);
        gridView.setNumColumns(3);
        gridView.setAdapter(adapter);

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        if(authUser != null){
            // authUser signed in
            authUid = authUser.getUid();
            // set up UI elements
            updateUI();
            attachPostListeners();

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
        followersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FollowerActivity.class);
                startActivity(intent);
            }
        });

        // following
        followingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FollowingActivity.class);
                startActivity(intent);
            }
        });

        // find people
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FindActivity.class);
                startActivity(intent);
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
                startActivityForResult(intent, RC_POST);
            }
        });

        // newsfeed
        newsFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewsFeedActivity.class);
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
                authUid = authUser.getUid();
                // add user to database
                User user = new User(authUser.getDisplayName(), authUser.getEmail());
                mDatabase.child("users").child(authUser.getUid()).setValue(user);
                // update UI elements
                updateUI();
                attachPostListeners();

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
        // TODO: postList out of order?
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

        mDatabase.child("followers").child(authUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followersButton.setText(dataSnapshot.getChildrenCount() + " followers");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("following").child(authUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingButton.setText(dataSnapshot.getChildrenCount() + " following");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    private void attachPostListeners(){
        // get posts
        mDatabase.child("posts").child(authUid).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        collectPosts((Map<String, Object>) dataSnapshot.getValue());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // handle error
                    }
                }
        );

        // post click
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map.Entry<String, Object> item = (Map.Entry) gridView.getItemAtPosition(position);
                String pid = item.getKey();
                // start new PostActivity and pass pid with Intent
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                intent.putExtra("PID", pid);
                intent.putExtra("UID", authUid);
                startActivity(intent);
            }
        });
    }

}
