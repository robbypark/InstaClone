package com.example.robby.basicfirebaseapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FindActivity extends AppCompatActivity {

    //private FirebaseUser authUser;
    private DatabaseReference mDatabase;
    private EntryAdapter adapter;
    private FeedAdapter feedAdapter;
    private ArrayList<Map.Entry> entryList;
    private ArrayList<Map.Entry> allTempData;
    private ListView listView;
    private EditText editText;

    private ArrayList<Map.Entry> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        listView = findViewById(R.id.find_list);
        editText = findViewById(R.id.key);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        postList = new ArrayList<>();
        entryList = new ArrayList<>();
        allTempData = new ArrayList<>();

        adapter = new EntryAdapter(FindActivity.this, R.layout.map_list_item, entryList);

        feedAdapter = new FeedAdapter(FindActivity.this, R.layout.map_list_item, postList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listView.getAdapter() == adapter) {
                    // handle users
                    Map.Entry<String, Object> item = (Map.Entry) listView.getItemAtPosition(position);
                    String uid = item.getKey();
                    // start new UserActivity and pass uid with Intent
                    Intent intent = new Intent(FindActivity.this, UserActivity.class);
                    intent.putExtra("UID", uid);
                    startActivity(intent);
                } else if(listView.getAdapter() == feedAdapter) {
                    // handle posts
                    Map.Entry entry = (Map.Entry) listView.getItemAtPosition(position);
                    // open PostActivity
                    String pid = (String) entry.getKey();
                    Map singlePost = (Map) entry.getValue();
                    String uid = (String) singlePost.get("uid");
                    // start new PostActivity and pass pid with Intent
                    Intent intent = new Intent(FindActivity.this, PostActivity.class);
                    intent.putExtra("PID", pid);
                    intent.putExtra("UID", uid);
                    startActivity(intent);
                }

            }
        });

        mDatabase.child("users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        collectUsernames((Map<String, Object>) dataSnapshot.getValue());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // handle error
                    }
                }
        );

        mDatabase.child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    if(userSnapshot.getValue() != null){
                        collectPosts((Map<String, Object>) userSnapshot.getValue());
                    }
                }
                feedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void collectPosts(Map<String,Object> posts){
            for (Map.Entry<String, Object> item : posts.entrySet()) {
                if(item != null){
                    postList.add(item);
                }
            }
    }

    private void collectUsernames(Map<String,Object> users) {
        for (Map.Entry<String, Object> item : users.entrySet()){
            //Get user map
            //entryList.add(item);
            allTempData.add(item);
        }
    }

    public void search(View view) {
        String text = editText.getText().toString();
        editText.setText("");
        if(text.length() > 0 && text.charAt(0)=='#'){
            listView.setAdapter(feedAdapter);
        } else {
            listView.setAdapter(adapter);
            if(!TextUtils.isEmpty(text) && !allTempData.isEmpty()){
                List<Map.Entry> temp = new ArrayList<>();
                for(Map.Entry m:allTempData){
                    Map singleUser = (Map) m.getValue();
                    String username = (String) singleUser.get("username");
                    if(!TextUtils.isEmpty(username) && username.toUpperCase().indexOf(text.toUpperCase()) != -1){
                        temp.add(m);
                    }
                }
                if(!temp.isEmpty()){
                    entryList.clear();
                    entryList.addAll(temp);
                    adapter.notifyDataSetChanged();
                }else {
                    entryList.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
