package com.example.robby.basicfirebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class NewsFeedActivity extends AppCompatActivity {

    private FirebaseUser authUser;
    private String authUid;
    private DatabaseReference mDatabase;

    private ArrayList<Map.Entry> postList;
    private ArrayList<String> followingUids;

    private ListView listView;
    private FeedAdapter adapter;

    private ValueEventListener postListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        listView = findViewById(R.id.feedListView);

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        authUid = authUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        postList = new ArrayList<>();
        followingUids = new ArrayList<>();

        adapter = new FeedAdapter(NewsFeedActivity.this, R.layout.newsfeed_view, postList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Map.Entry entry = (Map.Entry) listView.getItemAtPosition(position);
                // open PostActivity
                String pid = (String) entry.getKey();
                Map singlePost = (Map) entry.getValue();
                String uid = (String) singlePost.get("uid");
                // start new PostActivity and pass pid with Intent
                Intent intent = new Intent(NewsFeedActivity.this, PostActivity.class);
                intent.putExtra("PID", pid);
                intent.putExtra("UID", uid);
                startActivity(intent);
                finish();
            }
        });


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
                            collectPosts((Map<String,Object>) childSnapshot.getValue());

                        }
                    }
                    sortPosts();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

    }

    private void collectPosts(Map<String,Object> posts){
        if(posts != null) {
            for (Map.Entry<String, Object> item : posts.entrySet()) {
                postList.add(item);
            }
        }
    }

    private void sortPosts(){
        Collections.sort(postList, new Comparator<Map.Entry>() {
            @Override
            public int compare(Map.Entry p1, Map.Entry p2) {
                Map map1 = (Map) p1.getValue();
                long p1Time = (long) map1.get("time");
                Map map2 = (Map) p2.getValue();
                long p2Time = (long) map2.get("time");

                if(p1Time > p2Time){
                    return -1;
                } else {
                    return 1;
                }
            }
        });

    }

    private void collectFollowing(Map<String,Object> users) {
        for (Map.Entry<String, Object> item : users.entrySet()){
            //Get user map
            String uid = item.getKey();
            //followerList.add(uid);
            followingUids.add(uid);
        }
    }

}