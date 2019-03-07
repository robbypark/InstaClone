package com.example.robby.basicfirebaseapp;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.robby.basicfirebaseapp.model.Comment;
import com.example.robby.basicfirebaseapp.model.Post;
import com.example.robby.basicfirebaseapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostActivity extends AppCompatActivity {

    private TextView titleTextView;
    private ImageView imageView;
    private Button likeButton;
    private TextView likeCountTextView;
    private Button postCommentButton;
    private EditText editText;
    private ListView commentListView;
    private CommentAdapter adapter;
    private TextView nameTextView;

    private FirebaseUser authUser;
    private String authUid;

    private String uid;
    private String pid;
    private DatabaseReference mDatabase;

    private ArrayList<Comment> commentList;


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
        nameTextView = findViewById(R.id.postUsernameTextView);

        commentList = new ArrayList<>();
        adapter =
                new CommentAdapter(this, R.layout.comment_list_item, commentList);
        commentListView.setAdapter(adapter);


        uid = getIntent().getStringExtra("UID");
        pid = getIntent().getStringExtra("PID");

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        authUid = authUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("posts").child(uid).child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    Post post = dataSnapshot.getValue(Post.class);
                    titleTextView.setText(post.getTitle());
                    Bitmap bitmap = ImageUtils.decodeBase64(post.getImage());
                    imageView.setImageBitmap(bitmap);
                }
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
                if(dataSnapshot.getValue() != null){
//                    collectComments((Map<String,Object>) dataSnapshot.getValue());
                    commentList.clear();
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        Comment comment = child.getValue(Comment.class);
                        commentList.add(comment);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // username on post
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameTextView.setText(user.getUsername());
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
                editText.setText("");
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
