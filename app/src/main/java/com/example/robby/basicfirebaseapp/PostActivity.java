package com.example.robby.basicfirebaseapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostActivity extends AppCompatActivity {

    private TextView titleTextView;

    private String uid;
    private String pid;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        titleTextView = findViewById(R.id.titleTextView);

        uid = getIntent().getStringExtra("UID");
        pid = getIntent().getStringExtra("PID");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("posts").child(uid).child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                titleTextView.setText(post.title);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
