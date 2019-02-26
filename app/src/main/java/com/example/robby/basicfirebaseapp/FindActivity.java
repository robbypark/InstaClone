package com.example.robby.basicfirebaseapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
    private EntryAdapter adapter;
    private ArrayList<Map.Entry> entryList;
    private ArrayList<Map.Entry> allTempData;
    private ListView listView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        entryList = new ArrayList<>();
        allTempData = new ArrayList<>();

        adapter = new EntryAdapter(FindActivity.this, R.layout.map_list_item, entryList, "username");
        listView = findViewById(R.id.find_list);
        editText = findViewById(R.id.key);
        listView.setAdapter(adapter);

//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(s.toString().isEmpty()){
//                    entryList.clear();
//                    entryList.addAll(allTempData);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

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
            //entryList.add(item);
            allTempData.add(item);
        }
    }

    public void search(View view) {
        String key = editText.getText().toString();
        if(!TextUtils.isEmpty(key) && !allTempData.isEmpty()){
            List<Map.Entry> temp = new ArrayList<>();
            for(Map.Entry m:allTempData){
                Map singleUser = (Map) m.getValue();
                String username = (String) singleUser.get("username");
                if(!TextUtils.isEmpty(username) && username.toUpperCase().indexOf(key.toUpperCase()) != -1){
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
