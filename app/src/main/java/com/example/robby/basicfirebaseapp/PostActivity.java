package com.example.robby.basicfirebaseapp;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    private TextView titleTextView;
    private ImageView imageView;
    private Button likeButton;
    private TextView likeCountTextView;
    private Button postCommentButton;
    private EditText editText;
    private ListView commentListView;
    private ArrayAdapter<String> adapter;

    private FirebaseUser authUser;
    private String authUid;

    private String uid;
    private String pid;
    private DatabaseReference mDatabase;

    private ArrayList<String> commentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        titleTextView = findViewById(R.id.titleTextView);
        imageView = findViewById(R.id.postImageView);
        likeButton = findViewById(R.id.likeButton);
        likeCountTextView = findViewById(R.id.likeCountTextView);
        postCommentButton = findViewById(R.id.postCommentButton);
        editText = findViewById(R.id.commentEditText);
        commentListView = findViewById(R.id.commentListView);

        commentList = new ArrayList<>();
        adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, commentList);
        commentListView.setAdapter(adapter);


        uid = getIntent().getStringExtra("UID");
        pid = getIntent().getStringExtra("PID");

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        authUid = authUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("posts").child(uid).child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                titleTextView.setText(post.getTitle());
                Bitmap bitmap = ImageUtils.decodeBase64(post.getImage());
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // post like db listener
        mDatabase.child("likes").child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(authUid)){
                    likeButton.setText("unlike");
                } else {
                    likeButton.setText("like");
                }
                // get like count
                likeCountTextView.setText("Likes: " + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // post comment db listener
        mDatabase.child("comments").child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(PostActivity.this, "comment received", Toast.LENGTH_SHORT).show();
                if(dataSnapshot.getValue() != null){
//                    collectComments((Map<String,Object>) dataSnapshot.getValue());
                    commentList.clear();
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        Comment comment = child.getValue(Comment.class);
                        commentList.add(comment.getComment());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // post comment button
        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmt = editText.getText().toString();
                String cid = mDatabase.child("comments").child(pid).push().getKey();
                Comment comment = new Comment(authUid, cmt);
                mDatabase.child("comments").child(pid).child(cid).setValue(comment);
                // TODO: clear edit text
            }
        });

        // like post button
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(likeButton.getText().equals("like")){
                    // like post
                    mDatabase.child("likes").child(pid).child(authUid).setValue(true);
                } else if (likeButton.getText().equals("unlike")){
                    // unlike post
                    mDatabase.child("likes").child(pid).child(authUid).removeValue();
                }
            }
        });
    }

}
