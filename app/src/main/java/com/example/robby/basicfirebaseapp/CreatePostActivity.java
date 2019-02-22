package com.example.robby.basicfirebaseapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreatePostActivity extends AppCompatActivity {

    private EditText editTextPost;
    private Button btnSubmit;

    private FirebaseUser authUser;
    private String authUid;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        authUid = authUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextPost = findViewById(R.id.editTextPost);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPost();
                // finish activity
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

    }

    // create post and upload it to Firebase
    private void createPost() {
        String title = editTextPost.getText().toString();
        long time = System.currentTimeMillis();
        Post post = new Post(title, time);

        // create post for current user
        String postId = mDatabase.child("posts").child(authUid).push().getKey();
        mDatabase.child("posts").child(authUid).child(postId).setValue(post);
    }
}
