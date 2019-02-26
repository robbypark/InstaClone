package com.example.robby.basicfirebaseapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class NewsFeedActivity extends AppCompatActivity {

    private FirebaseUser authUser;
    private String authUid;
    private DatabaseReference mDatabase;

    private ArrayList<Post> postList;
    private ArrayList<String> followingUids;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private PostAdapter mAdapter;

    private ValueEventListener postListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        authUid = authUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        postList = new ArrayList<>();
        followingUids = new ArrayList<>();

        recyclerView = findViewById(R.id.newsFeedRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(mAdapter);


        mDatabase.child("following/" + authUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("NewsFeedActivity", "following onDataChange");
                if(dataSnapshot.getValue() != null){
                    if(postListener != null){
                        mDatabase.child("posts").removeEventListener(postListener);
                    }

                    collectFollowing((Map<String, Object>) dataSnapshot.getValue());
                    Log.d("NewsFeedActivity", "following updated");

                    mDatabase.child("posts").addValueEventListener(postListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("NewsFeedActivity", "post onDataChange");
                if(dataSnapshot.getValue() != null){
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        // if childSnapshot is a follower
                        if(followingUids.contains(childSnapshot.getKey())){
                            // get all of childSnapshots postList
                            for(DataSnapshot childChildSnapshot : childSnapshot.getChildren()){
                                Post post = childChildSnapshot.getValue(Post.class);
                                postList.add(post);
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

    }

    private void collectFollowing(Map<String,Object> users) {
        for (Map.Entry<String, Object> item : users.entrySet()){
            followingUids.clear();
            //Get user map
            String uid = item.getKey();
            //followerList.add(uid);
            followingUids.add(uid);
        }
    }

}