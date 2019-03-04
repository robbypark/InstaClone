package com.example.robby.basicfirebaseapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.robby.basicfirebaseapp.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class CreatePostActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView imageView;
    private Button btnUploadImage;
    private EditText editTextPost;
    private Button btnSubmit;
    private Button btnCamera;

    private FirebaseUser authUser;
    private String authUid;
    private DatabaseReference mDatabase;

    private Bitmap selectedImage;
    private static final int RC_LOAD_IMG = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        editTextPost = findViewById(R.id.editTextPost);
        btnSubmit = findViewById(R.id.btnSubmit);
        imageView = findViewById(R.id.createPostImageView);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnCamera = findViewById(R.id.cameraButton);

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        authUid = authUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RC_LOAD_IMG);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO handle missing data
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
        // create a post
        String title = editTextPost.getText().toString();
        long time = System.currentTimeMillis();
        // convert from Bitmap to Base64
        String base64Image = ImageUtils.encodeToBase64(selectedImage, Bitmap.CompressFormat.JPEG, 100);

        Post post = new Post(title, time, base64Image, authUid);

        // add post for current user
        String postId = mDatabase.child("posts").child(authUid).push().getKey();
        mDatabase.child("posts").child(authUid).child(postId).setValue(post);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == RC_LOAD_IMG && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(CreatePostActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(CreatePostActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }

        if (reqCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            selectedImage = (Bitmap) extras.get("data");
            imageView.setImageBitmap(selectedImage);
        }

    }

}
