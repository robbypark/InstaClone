package com.example.robby.basicfirebaseapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.robby.basicfirebaseapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView emailTextView;
    private Button btnFollow;
    private GridView gridView;
    private ImageView userImageView;

    private DatabaseReference mDatabase;
    private FirebaseUser authUser;
    private String authUid;
    private String uid;

    private PostAdapter adapter;
    private ArrayList<Map.Entry> postList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        btnFollow = findViewById(R.id.btnFollow);
        gridView = findViewById(R.id.userPostGridView);
        userImageView = findViewById(R.id.userImageView);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // get authUser
        authUser = FirebaseAuth.getInstance().getCurrentUser();
        authUid = authUser.getUid();
        // get user info to display
        uid = getIntent().getStringExtra("UID");

        // get postList
        postList = new ArrayList<>();
        adapter = new PostAdapter(UserActivity.this, R.layout.post_list_item, postList);
        gridView.setNumColumns(3);
        gridView.setAdapter(adapter);

        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameTextView.setText(user.getUsername());
                emailTextView.setText(user.getEmail());
                if(user.getImage() != null){
                    Bitmap bitmap = ImageUtils.decodeBase64(user.getImage());
                    userImageView.setImageBitmap(bitmap);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("followers").child(uid).child(authUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    // authUid is following
                    btnFollow.setText("unfollow");
                } else {
                    // authUid is not following
                    btnFollow.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnFollow.getText().toString().equals("follow")){
                    // follow
                    mDatabase.child("following").child(authUid).child(uid).setValue(true);
                    mDatabase.child("followers").child(uid).child(authUid).setValue(true);
                } else if (btnFollow.getText().toString().equals("unfollow")){
                    // unfollow
                    mDatabase.child("following").child(authUid).child(uid).removeValue();
                    mDatabase.child("followers").child(uid).child(authUid).removeValue();
                }
            }
        });

        mDatabase.child("posts").child(uid).addValueEventListener(
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
                Intent intent = new Intent(UserActivity.this, PostActivity.class);
                intent.putExtra("PID", pid);
                intent.putExtra("UID", uid);
                startActivity(intent);
            }
        });
    }

    private void collectPosts(Map<String,Object> posts) {
        // TODO: postList out of order?
        if(posts != null){
            postList.clear();
            for (Map.Entry<String, Object> item : posts.entrySet()){
                postList.add(item);
            }
        }
    }


}
