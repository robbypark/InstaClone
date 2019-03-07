package com.example.robby.basicfirebaseapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class FollowingActivity extends AppCompatActivity {

    private FirebaseUser authUser;
    private String authUid;
    private DatabaseReference mDatabase;
    private ArrayList<String> uidList;
    private ArrayList<Map.Entry> followingList;
    private EntryAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);

        listView = findViewById(R.id.follower_list);
        uidList = new ArrayList<>();
        followingList = new ArrayList<>();

        adapter = new EntryAdapter(FollowingActivity.this, R.layout.map_list_item, followingList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map.Entry<String, Object> item = (Map.Entry) listView.getItemAtPosition(position);
                String uid = item.getKey();
                // start new UserActivity and pass uid with Intent
                Intent intent = new Intent(FollowingActivity.this, UserActivity.class);
                intent.putExtra("UID", uid);
                startActivity(intent);
                finish();
            }
        });


        authUser = FirebaseAuth.getInstance().getCurrentUser();
        authUid = authUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("following/" + authUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    uidList.clear();
                    followingList.clear();
                    collectFollowers((Map<String, Object>) dataSnapshot.getValue());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void collectFollowers(Map<String,Object> users) {
        for (Map.Entry<String, Object> item : users.entrySet()){
            String uid = item.getKey();
            uidList.add(uid);
        }
        addUsersToList();
    }

    private void addUsersToList(){
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                collectUsers(map);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void collectUsers(Map<String,Object> users) {
        for (Map.Entry<String, Object> item : users.entrySet()){
            if(uidList.contains(item.getKey())){
                followingList.add(item);
            }
        }
    }

}
