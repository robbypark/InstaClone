package com.example.robby.basicfirebaseapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class FindActivity extends AppCompatActivity {

    //private FirebaseUser authUser;
    private EntryAdapter adapter;
    private ArrayList<Map.Entry> entryList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        //authUser = FirebaseAuth.getInstance().getCurrentUser();

        entryList = new ArrayList<>();

        adapter = new EntryAdapter(FindActivity.this, R.layout.map_list_item, entryList);
        listView = findViewById(R.id.find_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map.Entry<String, Object> item = (Map.Entry) listView.getItemAtPosition(position);
                String uid = item.getKey();
                // start new UserActivity and pass uid with Intent
                Intent intent = new Intent(FindActivity.this, UserActivity.class);
                intent.putExtra("UID", uid);
                startActivity(intent);
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addListenerForSingleValueEvent(
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
    }

    private void collectUsernames(Map<String,Object> users) {
        for (Map.Entry<String, Object> item : users.entrySet()){
            //Get user map
            entryList.add(item);
        }
    }
}
