package com.example.robby.basicfirebaseapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView emailTextView;
    private Button btnFollow;
    private Button btnUnFollow;

    private DatabaseReference mDatabase;
    private FirebaseUser authUser;
    private String authUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        btnFollow = findViewById(R.id.btnFollow);  //TODO check if already following
        btnUnFollow = findViewById(R.id.btnUnfollow);

        // get user info to display
        final String uid = getIntent().getStringExtra("UID");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameTextView.setText(user.getUsername());
                emailTextView.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // get authUser
        authUser = FirebaseAuth.getInstance().getCurrentUser();
        authUid = authUser.getUid();

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO check if already following
                // add uid to authUid following
                mDatabase.child("following").child(authUid).child(uid).setValue(true);
                // add authUid to uid followers
                mDatabase.child("followers").child(uid).child(authUid).setValue(true);
            }
        });

        btnUnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("following").child(authUid).child(uid).removeValue();
                mDatabase.child("followers").child(uid).child(authUid).removeValue();
            }
        });
    }
}
