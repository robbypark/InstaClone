package com.example.robby.basicfirebaseapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.example.robby.basicfirebaseapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {

    private FirebaseUser authUser;
    private String authUid;
    private DatabaseReference mDatabase;

    private CircleImageView profileImageView;
    private EditText nameEditText;
    private Button uploadButton;
    private Button saveButton;
    private Button urlButton;

    private Bitmap selectedImage;

    private BaseTarget baseTarget;


    private static final int RC_LOAD_IMG = 15;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        profileImageView = findViewById(R.id.editUserImageView);
        uploadButton = findViewById(R.id.editUploadImageButton);
        saveButton = findViewById(R.id.editSaveButton);
        nameEditText = findViewById(R.id.name);
        urlButton = findViewById(R.id.editUrlButton);

        authUser = FirebaseAuth.getInstance().getCurrentUser();
        authUid = authUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        nameEditText.setText(authUser.getDisplayName());

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RC_LOAD_IMG);
            }
        });

        urlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(EditActivity.this);
                new AlertDialog.Builder(EditActivity.this).setTitle("Input an image URL:")
                        .setView(et)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try{
                                    String url = et.getText().toString();
                                    Glide.with(EditActivity.this)
                                            .load(url)
                                            .into(baseTarget);
                                } catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("CANCEL",null).show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImage == null){
                    // do nothing
                    Toast.makeText(EditActivity.this, "Error: no image selected", Toast.LENGTH_SHORT).show();
                } else {
                    String name = nameEditText.getText().toString();

                    // update profile
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();
                    authUser.updateProfile(profileUpdates);

                    String base64Image = ImageUtils.encodeToBase64(selectedImage, Bitmap.CompressFormat.JPEG, 100);
                    User user = new User(name, authUser.getEmail(), base64Image);
                    mDatabase.child("users").child(authUid).setValue(user);
                    finish();
                }
            }
        });

        mDatabase.child("users").child(authUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if(currentUser.getImage() != null){
                    Bitmap bitmap = ImageUtils.decodeBase64(currentUser.getImage());
                    profileImageView.setImageBitmap(bitmap);
                    selectedImage = bitmap;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         baseTarget = new BaseTarget<BitmapDrawable>() {
             @Override
             public void onResourceReady(BitmapDrawable bitmap, Transition<? super BitmapDrawable> transition) {
                 // do something with the bitmap
                 // for demonstration purposes, let's set it to an imageview
                 profileImageView.setImageBitmap(bitmap.getBitmap());
                 selectedImage = bitmap.getBitmap();

             }

             @Override
             public void getSize(SizeReadyCallback cb) {
                 cb.onSizeReady(SIZE_ORIGINAL, SIZE_ORIGINAL);
             }

             @Override
             public void removeCallback(SizeReadyCallback cb) {
             }
         };
        }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == RC_LOAD_IMG && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                profileImageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(EditActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(EditActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }

    }

}
