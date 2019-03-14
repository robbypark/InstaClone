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

    private static final int RC_LOAD_IMG = 10;
    private static final int REQUEST_IMAGE_CAPTURE = 15;
    private static final int RC_FILTER_IMAGE = 20;

    private FirebaseUser authUser;
    private String authUid;
    private DatabaseReference mDatabase;
    protected Bitmap selectedImage;

    private ImageView imageView;
    private Button btnUploadImage;
    private EditText editTextPost;
    private Button btnSubmit;
    private Button btnCamera;
    private Button filterButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        editTextPost = findViewById(R.id.editTextPost);
        btnSubmit = findViewById(R.id.btnSubmit);
        imageView = findViewById(R.id.createPostImageView);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnCamera = findViewById(R.id.cameraButton);

        filterButton = findViewById(R.id.filter);

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

        // filter
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImage != null){
                    Intent intent = new Intent(CreatePostActivity.this, FilterActivity.class);
//                    intent.putExtra("picture", selectedImage);
                    ImageUtils.storeBitmap(CreatePostActivity.this, selectedImage);
                    startActivityForResult(intent, RC_FILTER_IMAGE);
                } else {
                    Toast.makeText(CreatePostActivity.this, "Please add an image first.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO handle missing data
                if(!editTextPost.getText().toString().equals("")){
                    createPost();
                    // finish activity
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(CreatePostActivity.this, "Please enter a post name.", Toast.LENGTH_SHORT).show();
                }

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

        // TODO: implement hashtag here
        String hashtag = "";

        Post post = new Post(title, time, base64Image, authUid, hashtag);

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
        } else if (reqCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // get result of filter
            Bundle extras = data.getExtras();
            Bitmap image = (Bitmap) extras.getParcelable("data");
            // set selectedImage
            selectedImage = image;
            // set imageView
            imageView.setImageBitmap(selectedImage);
        } else if(reqCode == RC_FILTER_IMAGE && resultCode == RESULT_OK){
            // set selectedImage
            selectedImage = ImageUtils.retreiveBitmap(CreatePostActivity.this);
            // set imageView
            imageView.setImageBitmap(selectedImage);
        }
    }



}


